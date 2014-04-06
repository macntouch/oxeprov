package br.eng.etech.oxeprov;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Mapeamento de comandos do MGR, modo texto.
 * 
 * @author eduardo@etech.eng.br
 *
 */
public class Mgr {
	/**
	 * Contains the itens to create a command.
	 * 
	 * @author eduardo@etech.eng.br
	 *
	 */
	static class Command {
		/**
		 * Permitted actions.
		 *
		 * @author eduardo@etech.eng.br
		 */
		public enum Action {CREATE, SET, DELETE }
		
		/**
		 * The node of the command.
		 */
		private String node = "0";
		
		/**
		 * The entity associated to the command.
		 */
		private int entity;
		
		/**
		 * The action to execute.
		 */
		private Action action;
		
		/**
		 * The associated system object.
		 */
		private String sysObject;
		
		/**
		 * The object to act upon.
		 */
		private String object;
		
		/**
		 * The parameters of the command.
		 */
		private LinkedHashMap<String, Object> parameters;

		/**
		 * Constructor with the parameters to create the command.
		 * 
		 * @param node the node.
		 * @param action the action.
		 * @param sysObject the system object.
		 * @param object the object.
		 * @param parameters the parameters for the command.
		 */
		@Deprecated
		public Command(String node, Action action, String sysObject, String object, LinkedHashMap<String, Object> parameters) {
			this.node = node==null?"0":node;
			this.action = action;
			this.sysObject = sysObject;
			this.object = object;
			this.parameters = parameters;
		}

		/**
		 * Constructor with the parameters to create the command.
		 * 
		 * @param entity entity associated to the command.
		 * @param action the action.
		 * @param sysObject the system object.
		 * @param object the object.
		 * @param parameters the parameters for the command.
		 */
		public Command(int entity, Action action, String sysObject, String object, LinkedHashMap<String, Object> parameters) {
			this.entity = entity;
			this.action = action;
			this.sysObject = sysObject;
			this.object = object;
			this.parameters = parameters;
		}

		/**
		 * The node of the command.
		 */
		public String getNode() {
			return node;
		}
		
		/**
		 * The entity for the command.
		 */
		public int getEntity() {
			return entity;
		}
		
		/**
		 * The action to execute.
		 */
		public Action getAction() {
			return action;
		}
		
		/**
		 * The associated system object.
		 */
		public String getSysObject() {
			return sysObject;
		}
		
		/**
		 * The object to act upon.
		 */
		public String getObject() {
			return object;
		}
		
		/**
		 * The parameters of the command.
		 */
		public LinkedHashMap<String, Object> getParameters() {
			return parameters;
		}

		/**
		 * Creates the command to use at the mgr.
		 */
		@SuppressWarnings("unchecked")
		public String toString() {
			String cmd = action + " " + sysObject + " \"" + node + "\": \"" + object + "\" ";

			// Parameters if we have them
			if(null!=parameters && parameters.size()>0) {
				cmd += "{ ";
			
				// Each key transforms in a parameter
				java.util.Iterator<String> iKey = parameters.keySet().iterator();
				while(iKey.hasNext()) {
					String key = iKey.next();
					cmd += key + " = ";
					
					Object obj = parameters.get(key);
					if(obj instanceof String) {
						// If it is a string, this is a parameter with only one value
						cmd += "\"" + (String)obj + "\"";
					} else if(obj instanceof LinkedHashSet<?>) {
						// If there is a Set, the parameter has multiple values, in order of the set.
						LinkedHashSet<String> set = (LinkedHashSet<String>) obj;
						
						cmd += "{ ";
						
						java.util.Iterator<String> iItem = set.iterator();
						while(iItem.hasNext()) {
							cmd += "\"" + iItem.next() + "\"";
							if(iItem.hasNext())
								cmd += ", ";
						}
						
						cmd += " } ";
					}
					
					if(iKey.hasNext())
						cmd += ", ";
				}
				
				cmd += " } ";
			}
				
			return cmd;
		}
	}

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(Mgr.class);
	
	/**
	 * Conexão à máquina.
	 */
	private OxeConnection oxeConnection;
	
	/**
	 * Construtora com informações para autenticação.
	 * 
	 * @param oxeConnection the prebuilt connection to the machine.
	 * 
	 */
	public Mgr(OxeConnection oxeConnection) {
		this.oxeConnection = oxeConnection;
	}
	
	/**
	 * Permitted system objects.
	 * 
	 * @author eduardo@etech.eng.br
	 */
	public enum SysObject { Subscriber }
	

	/**
	 * Classe com informa��es separadas sobre os erros.
	 * 
	 * @author eduardo@etech.eng.br
	 */
	public class Error implements ProcessingError {
		/**
		 * O nome do arquivo.
		 */
		private String file;
		
		/**
		 * A linha do erro.
		 */
		private long line;
		
		/**
		 * A opera��o executada.
		 */
		private Mgr.Command.Action operation;
		
		/**
		 * O nome do objeto do sistema utilizado.
		 */
		private String objectName;
		
		/**
		 * O n�mero do n�.
		 */
		private String node;
		
		/**
		 * O nome do objeto.
		 */
		private String object;
		
		/**
		 * O c�digo de erro.
		 */
		private String cmisErr;
		
		/**
		 * Transforma as propriedades em texto.
		 */
		public String toString() {
			return file + ":" + line + " " + operation + " " + objectName + " \"" + node + "\":\"" + object + "\" = " + cmisErr;
		}

		/**
		 * Returns the name of the file batch where the error ocurred.
		 * 
		 * @return the name of the batch file.
		 */
		public String getFile() {
			return file;
		}
		
		/**
		 * Returns the line of the batch command that the error ocurred.
		 * 
		 * @return the line of the error.
		 */
		public long getLine() {
			return line;
		}
		
		/**
		 * Returns the operation execute when the error ocurred.
		 * 
		 * @return the operation execute;
		 */
		public Mgr.Command.Action getOperation() {
			return operation;
		}
		
		/**
		 * Returns the name of the system object on which the error ocurred.
		 * 
		 * @return the name of the system object.
		 */
		public String getObjectName() {
			return objectName;
		}
		
		/**
		 * Returns the node on which the error ocurred.
		 * 
		 * @return the node.
		 */
		public String getNode() {
			return node;
		}
		
		/**
		 * Returns the object on which the error ocurred.
		 * 
		 * @return the object.
		 */
		public String getObject() {
			return object;
		}
		
		/**
		 * Returns the CMIS error code of the error.
		 * 
		 * @return the CMIS error code.
		 */
		public String getCmisErr() {
			return cmisErr;
		}
		
		/**
		 * Sets the operation from a String.
		 * 
		 * @param operation the string of the operation.
		 */
		private void setOperation(String operation) {
			operation = operation.toUpperCase();
			
			if(operation.equals("CREATE"))
				this.operation = Mgr.Command.Action.CREATE;
			else if(operation.equals("DELETE"))
				this.operation = Mgr.Command.Action.DELETE;
			else if(operation.equals("SET"))
				this.operation = Mgr.Command.Action.SET;
		}

		@Override
		public String errorCode() {
			return getCmisErr();
		}

		@Override
		public String errorMsg() {
			String strOperation = "<comando desconhecido>";
			switch(operation) {
				case CREATE: strOperation = "criação"; break;
				case DELETE: strOperation = "deleção"; break;
				case SET: strOperation = "alteração"; break;
			}
			
			if(0 == cmisErr.compareTo("10")) {
				return "erro, " + strOperation + " de um PIN já existente";
			} else if(0 == cmisErr.compareTo("11")) {
				return "erro, " + strOperation + " de um ramal já existente";
			} else if(0 == cmisErr.compareTo("1")) {
				return "erro, " + strOperation + " de um ramal/pin não existente";
			}
			
			return "";
		}
	}
	
	public class GeneralError implements ProcessingError {
		/**
		 * O nome do arquivo.
		 */
		private String file;
		
		/**
		 * A linha do erro.
		 */
		private long line;

		/**
		 * O c�digo de erro.
		 */
		private String error;
		
		/**
		 * Returns the name of the file batch where the error ocurred.
		 * 
		 * @return the name of the batch file.
		 */
		public String getFile() {
			return file;
		}
		
		/**
		 * Returns the line of the batch command that the error ocurred.
		 * 
		 * @return the line of the error.
		 */
		public long getLine() {
			return line;
		}
		
		/**
		 * Transforma as propriedades em texto.
		 */
		public String toString() {
			return file + ":" + line + "\" = " + error;
		}
		
		/**
		 * Returns the error code of the error.
		 * 
		 * @return the error code.
		 */
		public String getError() {
			return error;
		}
		
		@Override
		public String errorCode() {
			return getError();
		}

		@Override
		public String errorMsg() {
			return "";
		}
		
	}
	
	/**
	 * Pattern de expressão geral de erro.
	 */
	public static final Pattern pErrGeneral = Pattern.compile("\\?\\?\\?\\s*Error\\s*:\\s*file\\s*\"([^\"]*)\",\\s*line\\s*(\\d*)\\s*:\\s*(.*)");
	
	/**
	 * Pattern de express�o regular para detectar o nome do comando.
	 */
	public static final Pattern pErrCommand = Pattern.compile("!!!\\s*Command\\s*:\\s*file\\s*\"([^\"]*)\",\\s*line\\s*(.*)");
	
	/**
	 * Pattern de express�o regular para detectar a opera��o.
	 */
	public static final Pattern pErrOperation = Pattern.compile("!!!\\s*Operation\\s*:\\s*(\\S*)");
	
	/**
	 * Pattern de express�o regular para detectar o nome do objeto do sistema.
	 */
	public static final Pattern pErrObjectName = Pattern.compile("!!!\\s*Object\\s+Name\\s*:\\s*(\\S*)");
	
	/**
	 * Pattern de express�o regular para detectar o n� ou o objeto (aparece nesta ordem).
	 */
	public static final Pattern pErrNodeObject = Pattern.compile("!!!\\s*:\\s*(\\S*)");
	
	/**
	 * Pattern de express�o regular para detectar o erro.
	 */
	public static final Pattern pErrCMIS = Pattern.compile("CMISERR_(\\S*)");
	
	/**
	 * L� os erros da sa�da do comando executado.
	 * 
	 * @return uma listagem dos erros.
	 * @throws IOException se ocorreu algum erro na leitura do stream.
	 */
	private List<Error> readErrors() throws IOException {
		// Cria a listagem
		List<Error> errors = new LinkedList<Error>();
		
		// O primeiro objeto
		Error error = new Error();
		
		// Enquanto houver coisas para ler...
		do {
			String line = oxeConnection.readLine();
			logger.trace("Error from OXE: " + line);
			
			// Se tem uma nova linha, dever� ser tratada
			if(null != line) {
				Matcher m = pErrCommand.matcher(line);
				if(m.find()) {
					error = new Error();
					
					error.file = m.group(1);
					error.line = Integer.parseInt(m.group(2));
				}
				
				m = pErrOperation.matcher(line);
				if(m.find()) {
					error.setOperation(m.group(1));
				}
				
				m = pErrObjectName.matcher(line);
				if(m.find()) {
					error.objectName = m.group(1);
				}
				
				m = pErrNodeObject.matcher(line);
				if(m.find()) {
					// Se o n� ainda n�o tiver sido lido, � o n�, sen�o, o objeto.
					if(null == error.node)
						error.node = m.group(1);
					else
						error.object = m.group(1);
				}
				
				m = pErrCMIS.matcher(line);
				if(m.find()) {
					error.cmisErr = m.group(1);
					errors.add(error);
					logger.debug(error);
				}
				
				m = pErrGeneral.matcher(line);
				if(m.find()) {
					GeneralError gError = new GeneralError();
					gError.file = m.group(1);
					gError.line = Integer.parseInt(m.group(2));
					gError.error = m.group(3);
					
					errors.add(error);
					logger.debug(error);
				}
			}
		} while(oxeConnection.hasMoreData());
		
		// Se houve strings na sa�da de erro, loga.
		String stderr = oxeConnection.getStderrBuffer(); 
		if(stderr.length()>0)
			logger.error("Erros: " + stderr);
		
		return errors;
	}

	/**
	 * L� os erros da sa�da do comando executado.
	 * 
	 * @return uma listagem dos erros.
	 * @throws IOException se ocorreu algum erro na leitura do stream.
	 */
	private Error readError() throws IOException {
		// O primeiro objeto
		Error error = new Error();
		
		// Enquanto houver coisas para ler...
		do {
			String line = oxeConnection.readLine();
			logger.trace("Error from OXE: " + line);
			
			// Se tem uma nova linha, dever� ser tratada
			if(null != line) {
				Matcher m = pErrCommand.matcher(line);
				if(m.find()) {
					error = new Error();
					
					error.file = m.group(1);
					error.line = Integer.parseInt(m.group(2));
				}
				
				m = pErrOperation.matcher(line);
				if(m.find()) {
					error.setOperation(m.group(1));
				}
				
				m = pErrObjectName.matcher(line);
				if(m.find()) {
					error.objectName = m.group(1);
				}
				
				m = pErrNodeObject.matcher(line);
				if(m.find()) {
					// Se o n� ainda n�o tiver sido lido, � o n�, sen�o, o objeto.
					if(null == error.node)
						error.node = m.group(1);
					else
						error.object = m.group(1);
				}
				
				m = pErrCMIS.matcher(line);
				if(m.find()) {
					error.cmisErr = m.group(1);
					logger.debug(error);
					return error;
				}
				
				m = pErrGeneral.matcher(line);
				if(m.find()) {
					GeneralError gError = new GeneralError();
					gError.file = m.group(1);
					gError.line = Integer.parseInt(m.group(2));
					gError.error = m.group(3);
					
					logger.debug(error);
					return error;
				}
			}
		} while(oxeConnection.hasMoreData());
		
		// Se houve strings na sa�da de erro, loga.
		String stderr = oxeConnection.getStderrBuffer(); 
		if(stderr.length()>0)
			logger.error("Erros: " + stderr);
		
		return null;
	}
	
	/**
	 * Executa a lista de comandos.
	 * 
	 * @param cmds a lista de comandos a ser executado.
	 * @return uma lista de erros.
	 * @throws IOException se houve algum erro de escrita ou leitura nos streams.
	 */
	public List<Error> executeBatch(Collection<Command> cmds) throws IOException {
		// Cria o arquivo de comando batch
		logger.debug("creating the batch file");
		StringBuffer cmdFile = new StringBuffer();
		for(Command cmd : cmds) {
			cmdFile.append(cmd);
			cmdFile.append("\n");
		}
		
		// Envia o arquivo
		oxeConnection.putFile(cmdFile.toString(), "cmd.txt", ".");

		// Executa o comando
		logger.debug("executing the batch file");
		oxeConnection.execCommand("mgr -nodico -X cmd.txt");

		// Pega os erros
		logger.debug("getting the errors");
		return readErrors();
	}

	/**
	 * Executa a lista de comandos.
	 * 
	 * @param cmds a lista de comandos a ser executado.
	 * @return uma lista de erros.
	 * @throws IOException se houve algum erro de escrita ou leitura nos streams.
	 */
	public Error execute(Command cmd) throws IOException {
		// Cria o arquivo de comando batch
		logger.debug("creating the batch file");
		StringBuffer cmdFile = new StringBuffer();

		cmdFile.append(cmd);
		cmdFile.append("\n");
		
		// Envia o arquivo
		oxeConnection.putFile(cmdFile.toString(), "cmd.txt", ".");

		// Executa o comando
		logger.debug("executing the batch file");
		oxeConnection.execCommand("mgr -nodico -X cmd.txt");

		// Pega os erros
		logger.debug("getting the errors");
		return readError();
	}
	
	/**
	 * Closes the communication with MGR.
	 */
	public void close() {
		//oxeConnection.close();
	}
}
