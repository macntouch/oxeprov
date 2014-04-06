package br.eng.etech.oxeprov;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import org.apache.log4j.Logger;


import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;


/**
 * Provisionamento em batch, a partir de um arquivo CSV.
 * 
 * @author eduardo@etech.eng.br
 *
 */
public class Batch {
	/**
	 * Error on batch processing.
	 * 
	 * @author eduardo@etech.eng.br
	 */
	class Error implements ProcessingError {

		/**
		 * The String errorCode.
		 */
		private String errorCode;
		
		/**
		 * The associated exception, if any.
		 */
		private Exception exception;
		
		/**
		 * Get the associated exception.
		 * 
		 * @return the exception associated with the error.
		 */
		public Exception getException() {
			return exception;
		}
		
		@Override
		public String errorCode() {
			return errorCode;
		}
		
		/**
		 * Constructor with the errorCode and associated exception.
		 * 
		 * @param errorCode the error code in String format.
		 * @param exception the exception associated.
		 */
		Error(String errorCode, Exception exception) {
			this.errorCode = errorCode;
			this.exception = exception;
		}

		@Override
		public String errorMsg() {
			return "";
		}
	}
	
	/**
	 * Logger.
	 */
	Logger logger = Logger.getLogger(Batch.class);

	private String inputFilename;

	private String outputFilename; 
	
	/**
	 * Definição dos cabeçalhos.
	 */
	public static final String HEADER_ACAO = "ação";
	public static final String HEADER_MATRICULA = "matrícula";
	public static final String HEADER_ENTIDADE = "entidade";
	public static final String HEADER_CATEGORIA = "categoria";
	public static final String HEADER_PINCODE = "pincode";
	public static final String HEADER_RAMALVIRTUAL = "ramalvirtual";
	
	/**
	 * Headers for the CSV file.
	 */
	public static String[] csvHeader = { HEADER_ACAO, HEADER_MATRICULA, HEADER_ENTIDADE, HEADER_CATEGORIA, HEADER_PINCODE, HEADER_RAMALVIRTUAL};
	
//	/**
//	 * List of the clusters.
//	 */
//	private Map<String, Config.Cluster> clusters;
	
//	/**
//	 * List of commands for each cluster.
//	 */
//	private Map<String, SortedMap<Long, Mgr.Command>> commands = new Hashtable<String, SortedMap<Long, Mgr.Command>>();
//	private LinkedList<Mgr.Command> mgrCommands = new LinkedList<Mgr.Command>();

	
//	/**
//	 * List of errors association.
//	 */
//	private TreeMap<Long, ProcessingError> errorMap = new TreeMap<Long, ProcessingError>();
//	private LinkedList<ProcessingError> errors = new LinkedList<ProcessingError>();
	
	private Mgr.Command readCommand(CsvReader csvInput) throws IOException {
		// Gets the action
		Mgr.Command.Action action;
		int acao;
		try {
			acao = Integer.parseInt(csvInput.get(HEADER_ACAO));
		} catch (Exception e) {
			return null;
		}
		
		switch(acao) {
		case 1:
			action = Mgr.Command.Action.CREATE;
			break;
		case 2:
			action = Mgr.Command.Action.DELETE;
			break;
		case 3:
			action = Mgr.Command.Action.SET;
			break;
		default:
			logger.error("ação " + csvInput.get(HEADER_ACAO) + " is not mapped");
			return null;
		}

		// Search the machine from the entity
//		int entity = Integer.parseInt(csv.get(HEADER_ENTIDADE));
//		String clusterKey = getClusterKey(entity);

		// Create the MGR command
		//Config.Cluster cluster = clusters.get(clusterKey);
		
		LinkedHashMap<String,Object> map = null;
		
		if(action != Mgr.Command.Action.DELETE) {
			map = new LinkedHashMap<String,Object>();
			map.put("Annu_Name", csvInput.get(HEADER_MATRICULA));
			map.put("Display_Name", csvInput.get(HEADER_MATRICULA));
			map.put("Entity_Number", csvInput.get(HEADER_ENTIDADE));
			map.put("Public_Network_Category_Id", csvInput.get(HEADER_CATEGORIA));
			LinkedHashSet<String> pinSet = new LinkedHashSet<String>();
			pinSet.add(csvInput.get(HEADER_PINCODE));
			pinSet.add("FALSE");
			pinSet.add("PIN_Universal_Access");
			pinSet.add("1");
			map.put("PIN", pinSet);
			map.put("Valid_For_Call_By_Name", "No");
		}

		Mgr.Command command = new Mgr.Command(Integer.parseInt(csvInput.get(HEADER_ENTIDADE)), action, "Subscriber", csvInput.get(HEADER_RAMALVIRTUAL), map);
		logger.debug("Command created: " + command);
		
		return command;
	}
	
//	private void createCommandList() throws IOException {
//		CsvReader csv = new CsvReader(csvInput, ',');
//		csv.setHeaders(csvHeader);
//		
//		while(csv.readRecord()) {
//			// Gets the action
//			Mgr.Command.Action action;
//			int acao;
//			try {
//				acao = Integer.parseInt(csv.get(HEADER_ACAO));
//			} catch (Exception e) {
//				break;
//			}
//			
//			switch(acao) {
//			case 1:
//				action = Mgr.Command.Action.CREATE;
//				break;
//			case 2:
//				action = Mgr.Command.Action.DELETE;
//				break;
//			case 3:
//				action = Mgr.Command.Action.SET;
//				break;
//			default:
//				logger.error("ação " + csv.get(HEADER_ACAO) + " is not mapped");
//				continue;
//			}
//
//			// Search the machine from the entity
////			int entity = Integer.parseInt(csv.get(HEADER_ENTIDADE));
////			String clusterKey = getClusterKey(entity);
//
//			// Create the MGR command
//			//Config.Cluster cluster = clusters.get(clusterKey);
//			
//			LinkedHashMap<String,Object> map = null;
//			
//			if(action != Mgr.Command.Action.DELETE) {
//				map = new LinkedHashMap<String,Object>();
//				map.put("Annu_Name", csv.get(HEADER_MATRICULA));
//				map.put("Display_Name", csv.get(HEADER_MATRICULA));
//				map.put("Entity_Number", csv.get(HEADER_ENTIDADE));
//				map.put("Public_Network_Category_Id", csv.get(HEADER_CATEGORIA));
//				LinkedHashSet<String> pinSet = new LinkedHashSet<String>();
//				pinSet.add(csv.get(HEADER_PINCODE));
//				pinSet.add("FALSE");
//				pinSet.add("PIN_Universal_Access");
//				pinSet.add("1");
//				map.put("PIN", pinSet);
//				map.put("Valid_For_Call_By_Name", "No");
//			}
//
//			Mgr.Command command = new Mgr.Command(Integer.parseInt(csv.get(HEADER_ENTIDADE)), action, "Subscriber", csv.get(HEADER_RAMALVIRTUAL), map);
//			logger.debug("Command created: " + command);
//			
//			// Associate the command to the cluster
//			addCommand(command);
////			addCommand(clusterKey, csv.getCurrentRecord()+1, command);
//		}
//		
//		csv.close();
//	}

//	/**
//	 * Return the cluster key for a entity.
//	 * 
//	 * @param entity the entity to find.
//	 * @return a String representing a cluster key.
//	 */
//	private String getClusterKey(int entity) {
//		String clusterKey = null;
//keyFind:	
//		for(String key : clusters.keySet()) {
//			Config.Cluster cluster = clusters.get(key);
//			
//			for(Config.Cluster.Range range : cluster.getRange()) {
//				if(range.inRange(entity)) {
//					clusterKey = key;
//					break keyFind;
//				}
//			}
//		}
//		if(null==clusterKey) {
//			logger.error("entity " + entity + " not found in any cluster");
//		}
//		return clusterKey;
//	}

//	/**
//	 * Add a command to the list of commands to be executed.
//	 * 
//	 * @param clusterKey the key of the cluster to associate to.
//	 * @param csvLine line number of the CSV file.
//	 * @param command the command to associate.
//	 */
//	private void addCommand(String clusterKey, long csvLine, Mgr.Command command) {
//		SortedMap<Long, Mgr.Command> commandList = commands.get(clusterKey);
//		if(null==commandList) {
//			commandList = new TreeMap<Long, Mgr.Command>();
//			commands.put(clusterKey, commandList);
//		}
//		commandList.put(csvLine, command);
//	}
	
//	/**
//	 * Add a command to the list of commands to be executed.
//	 * 
//	 * @param command the command to be added to the list.
//	 */
//	private void addCommand(Mgr.Command command) {
//		mgrCommands.add(command);
//	}
	
	/**
	 * Constructor with information for Batchs.
	 * 
	 * @param clusters list of clusters.
	 * @param file the filename with the commands to process.
	 * @throws IOException  error reading the file.
	 */
	public Batch(File file) throws IOException {		
//		this.clusters = Config.global.getMachines();
		this.inputFilename = file.getPath();

		this.outputFilename = Config.global.getOutputFolder() + File.separator + file.getName();

		SimpleDateFormat formatter = new SimpleDateFormat("-yyyyMMdd-HHmmssSSS");
		this.outputFilename = this.outputFilename.replaceFirst("(\\..*$)", formatter.format(new Date()) + "$1");

		// Cria a lista de comandos
//		createCommandList();
	}
	
//	/**
//	 * Runs the batch for a Cluster.
//	 * @throws IOException 
//	 */
//	public void run() {
//		// For each set of commands...
//		for(String clusterKey : commands.keySet()) {
//			// Get the associated machine
//			Config.Cluster cluster = clusters.get(clusterKey);
//			
//			Mgr mgr = null;
//			
//			// Creates the specific connection
//			OxeConnection conn;
//			try {
//				conn = cluster.connect();
//			} catch (CouldNotDoClusterConnection e) {
//				logger.error(e + " connecting to cluster " + clusterKey);
//				addAllAsError(commands.get(clusterKey), "OXEPROV_1", e);
//				continue;
//			}
//			
//			mgr = new Mgr(conn);
//
//			// Executes the batch
//			//errors = mgr.executeBatch(commands.get(clusterKey).values());
//			Map<Long, Mgr.Command> commandList = commands.get(clusterKey);
//			
//			for(Long line : commandList.keySet()) {
//				try {
//					Mgr.Error error = mgr.execute(commandList.get(line));
//					if(null != error) errorMap.put(line, error);
//				} catch(IOException e) {
//					logger.error(e + " " + " executing to cluster " + clusterKey);
//					errorMap.put(line, new Error("OXEPROV_1", e));
//				}
//			}
//
//			if(null!=mgr) mgr.close();			
//		}
//	}
	
	public void run() throws IOException {
		CsvReader csvInput = new CsvReader(inputFilename, ',');
		csvInput.setHeaders(csvHeader);
		
		CsvWriter csvOutput = new CsvWriter(outputFilename);
		csvOutput.setDelimiter(',');
		
		ConnectionPool connectionPool = new ConnectionPool(Config.global.getMachines());;
		
		try {
			while(csvInput.readRecord()) {
				Mgr.Command command  = readCommand(csvInput);
				if(null==command) {
					writeOutput(csvInput, csvOutput, "linha de comando fora dos parâmetros");
					continue;
				}
		
				String[] clusterKeys;
				
				if(command.getAction()!=Mgr.Command.Action.DELETE) {
					clusterKeys = connectionPool.getClusterKey(command.getEntity());
				} else {
					clusterKeys = connectionPool.getAllClusterKeys();
				}
				
				if(clusterKeys.length==0) {
					writeOutput(csvInput, csvOutput, "entidade não está associada a cluster algum");
					continue;
				}				
				
				String strOutputErr = null;
				String strOutputSuccess = null;
				
				for(String clusterKey : clusterKeys) {
					OxeConnection conn = null;
					
					try {
						conn = connectionPool.getConnection(clusterKey);
					} catch(CouldNotDoClusterConnection e) {
						logger.error(e + " connecting to cluster " + clusterKey);
						strOutputErr = "não foi possível conectar no cluster " + clusterKey;
						continue;
					}
					
					Mgr mgr = new Mgr(conn);
					
					Mgr.Error error = null;
					try {
						error = mgr.execute(command);
					} catch(IOException e) {
						logger.error(e + " executing to cluster " + clusterKey);
						strOutputErr = "não foi possível executar o comando no cluster " + clusterKey;
						continue;
					}
					
					if(null!=error) {
						strOutputErr = error.errorCode() + ": " + error.errorMsg();
					} else {
						switch(command.getAction()) {
							case CREATE:
								strOutputSuccess = "OK, criação de novo ramal/PIN";
								break;
							case DELETE:
								strOutputSuccess = "OK, deleção de ramal/PIN";
								break;
							case SET:
								strOutputSuccess = "OK, alteração realizada";
								break;
						}					
					}
				}
				
				if(null!=strOutputSuccess) {
					writeOutput(csvInput, csvOutput, strOutputSuccess);
				} else {
					writeOutput(csvInput, csvOutput, strOutputErr);
				}
			}
		} finally {
			connectionPool.closeAll();
			csvInput.close();
			csvOutput.close();
		}
	}

//	private void addAllAsError(SortedMap<Long, Mgr.Command> sortedMap, String errorCode, Exception e) {
//		for(Long key : sortedMap.keySet()) {
//			errorMap.put(key, new Error(errorCode, e));
//		}
//	}

	public void writeOutput(CsvReader csvInput, CsvWriter csvOutput, String str)  {
		
		try {
			ArrayList<String> record = new ArrayList<String>(Arrays.asList(csvInput.getValues()));
			
			record.add(str);
			
			String[] outputRecord = new String[record.size()];
			record.toArray(outputRecord);
			csvOutput.writeRecord(outputRecord);
		} catch(IOException e) {
			logger.error("error writing output " + e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
//	/**
//	 * Write the errors to the output folder.
//	 * 
//	 * @throws IOException any error with the files. 
//	 */
//	public void writeErrors() throws IOException {
//		// Open the CSVs
//		CsvReader r = new CsvReader(csvInput, ',');
//		CsvWriter w = new CsvWriter(csvOutput);
//		w.setDelimiter(',');
//		
//		// Puts all the commands in only a Mapping
//		TreeMap<Long, Mgr.Command> allCommands = new  TreeMap<Long, Mgr.Command>();
//		for(SortedMap<Long, Mgr.Command> cmd : commands.values()) {
//			allCommands.putAll(cmd);
//		}
//		
//		// General error
//		ProcessingError gError = null;
//		
//		// Get each line of the input CSV
//		while(r.readRecord()) {
//			LinkedList<String> record = new LinkedList<String>(Arrays.asList(r.getValues()));
//			
//			ProcessingError error = errorMap.get(r.getCurrentRecord()+1);
//			Mgr.Command cmd = allCommands.get(r.getCurrentRecord()+1);
//			
//			if(error instanceof Mgr.GeneralError)
//				gError = error;
//			
//			if(null==error && null==gError) {
//				if(null==cmd) {
//					record.add("ERRO, cluster inexistente?");
//				} else {
//					switch(cmd.getAction()) {
//						case CREATE:
//							record.add("OK, criação de novo ramal/PIN");
//							break;
//						case DELETE:
//							record.add("OK, deleção de ramal/PIN");
//							break;
//						case SET:
//							record.add("OK, alteração realizada");
//							break;
//					}
//				}
//			} else if(null!=error) {
//				record.add(error.errorCode() + ": " + error.errorMsg());
//			} else if(null!=gError) {
//				record.add(gError.errorCode() + ": " + gError.errorMsg());
//			}
//	
//			String[] outputRecord = new String[record.size()];
//			record.toArray(outputRecord);
//			w.writeRecord(outputRecord);
//		}
//	
//		// Close the streams
//		r.close();
//		w.close();
//	}
}
