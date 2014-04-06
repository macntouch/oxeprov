package br.eng.etech.oxeprov;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * Controle de configurações.
 * 
 * O arquivo de configuração deverá ter:
 *   entity.<numero>.host;
 *   entity.<numero>.port (padrão é 22);
 *   entity.<numero>.username (padrão é mtcl);
 *   entity.<numero>.password (padrão é mtcl);
 * 
 * @author eduardo@etech.eng.br
 *
 */
public class Config {
	Logger logger = Logger.getLogger(Config.class);
	
	/**
	 * Configurações de acesso à uma máquina.
	 * 
	 * @author eduardo@etech.eng.br
	 *
	 */
	public class Cluster {
		/**
		 * Ranges de entidades atendidos.
		 * 
		 * @author eduardo@etech.eng.br
		 */
		class Range implements Comparable<Range> {
			/**
			 * Entidade inicial.
			 */
			private int initialEntity;
			
			/**
			 * Entidade final.
			 */
			private int finalEntity;
			
			/**
			 * Construtora com uma única entidade.
			 * 
			 * @param entity the entity number.
			 */
			public Range(int entity) {
				this.initialEntity = this.finalEntity = entity;
			}
			
			/**
			 * Constructor with a entity range.
			 * 
			 * @param initialEntity the initial entity number.
			 * @param finalEntity the final entity number, inclusive.
			 */
			public Range(int initialEntity, int finalEntity) {
				this.initialEntity = initialEntity;
				this.finalEntity = finalEntity;
			}

			/**
			 * Verifies with the entity is in this range.
			 * 
			 * @param entity the entity number.
			 * @return true if the entity is in the range.
			 */
			public boolean inRange(int entity) {
				return (initialEntity <= entity && finalEntity >= entity);
			}

			@Override
			public int compareTo(Range o) {
				return this.initialEntity - o.initialEntity;
			}
		}

		/**
		 * Defini��o do objeto que atende � esta m�quina.
		 * 
		 * @author eduardo@etech.eng.br
		 */
		class Machine {
			private Cluster enclosing;
			
			/**
			 * Nome ou IP da máquina.
			 */
			private String host;
			
			/**
			 * Porta de conexão.
			 */
			private Integer port = null;
			
			/**
			 * Nome do usuário para conexão.
			 */
			private String username = null;
			
			/**
			 * Senha para conexão.
			 */
			private String password = null;

			/**
			 * Classe de conexão.
			 */
			private String connectionClass = null;

			Machine(Cluster enclosing) {
				this.enclosing = enclosing;
			}
			
			/**
			 * Retorna o hostname.
			 * 
			 * @return uma string com o nome ou IP do host.
			 */
			public String getHost() {
				return host;
			}

			/**
			 * Retorna a porta de conexão.
			 * 
			 * @return a porta para conexão.
			 */
			public int getPort() {
				return null==port ? enclosing.port : port;
			}

			/**
			 * Retorna o nome do usuário para conexão.
			 * 
			 * @return o nome do usuário.
			 */
			public String getUsername() {
				return null==username ? enclosing.username : username;
			}

			/**
			 * Retorna a senha do usuário para conexão.
			 * 
			 * @return a senha do usuário.
			 */
			public String getPassword() {
				return null==password ? enclosing.password : password;
			}

			/**
			 * Retorna a classe de conexão.
			 * 
			 * @return a string da classe de conexão.
			 */
			public String getConnectionClass() {
				return null==connectionClass ? enclosing.connectionClass : connectionClass;
			}
		}
		
		/**
		 * Porta de conexão para qualquer m�quina.
		 */
		private int port = 22;
		
		/**
		 * Nome do usuário para conexão para qualquer m�quina.
		 */
		private String username = "mtcl";
		
		/**
		 * Senha para conexão para qualquer m�quina.
		 */
		private String password = "mtcl";

		/**
		 * Node number to create the objects.
		 */
		private String node = null;
		
		/**
		 * Range de entidades atendidas.
		 */
		private SortedSet<Range> range = new TreeSet<Range>();
		
		/**
		 * Lista de m�quinas atendidas.
		 */
		private List<Machine> machines = new LinkedList<Machine>();
		
		/**
		 * Classe de conexão.
		 */
		private String connectionClass = "br.eng.etech.oxeprov.SshOxeConnection";
		
		/**
		 * Retorna os ranges que esta m�quina atende.
		 * 
		 * @return um set com os ranges.
		 */
		public Set<Range> getRange() {
			return range;
		}
		
		/**
		 * Return the list of machines.
		 * 
		 * @return a list with all the machines configured.
		 */
		public List<Machine> getMachines() {
			return machines;
		}

		/**
		 * Returns the machine node.
		 * 
		 * @return the node number.
		 */
		public String getNode() {
			return node;
		}

		/**
		 * Connects to this cluster.
		 * 
		 * @return the connection class.
		 * @throws CouldNotDoClusterConnection could not connect to this cluster of machines.
		 */
		public OxeConnection connect() throws CouldNotDoClusterConnection {
			// Realiza a autentica�o.
			Config.Cluster.Machine machine = null;
			ListIterator<Config.Cluster.Machine> li = getMachines().listIterator();
			OxeConnection conn = null;
			
			while(li.hasNext() && null == conn) {
				machine = li.next();
				
				logger.debug("connection with " + machine.getConnectionClass() + " to " + machine.getHost() + ":" + machine.getPort());
				try {
					conn = (OxeConnection) Class.forName(machine.getConnectionClass()).newInstance();
				} catch (InstantiationException e) {
					logger.warn(e + " " + machine.getHost());
					conn = null;
					continue;
				} catch (IllegalAccessException e) {
					logger.warn(e + " " + machine.getHost());
					conn = null;
					continue;
				} catch (ClassNotFoundException e) {
					logger.warn(e + " " + machine.getHost());
					conn = null;
					continue;
				}
				
				try {
					conn.connect(machine);
				} catch (CouldNotConnect e) {
					logger.warn(e + " " + machine.getHost());
					conn.close();
					conn = null;
					continue;
				} catch (IOException e) {
					logger.warn(e + " " + machine.getHost());
					conn.close();
					conn = null;
					continue;
				} catch (NotAuthenticated e) {
					logger.warn(e + " " + machine.getHost());
					conn.close();
					conn = null;
					continue;
				}
			}

			if(null==conn) {
				throw new CouldNotDoClusterConnection(this);
			}
			
			return conn;
		}
	}
	
	/**
	 * Pattern de expressão regular para pegar os campos de uma entidade.
	 */
	static public Pattern pEntity = Pattern.compile("^cluster\\.(.+)\\.(.*)$");
	
	/**
	 * Pattern de expressão regular para separar os ranges.
	 */
	static public Pattern pRange = Pattern.compile("^\\s*(\\d+)\\s*(?:-\\s*(\\d+))?\\s*$");
	
	/**
	 * As propriedades originais.
	 */
	private Properties properties;
	
	/**
	 * Propriedades extendidas.
	 */
	private ExtendedProperties extProperties;
	
	/**
	 * Listagem das máquinas.
	 */
	private Hashtable<String,Cluster> clusters = new Hashtable<String, Cluster>();
	
	/**
	 * Returna a lista de máquinas.
	 * 
	 * @return a lista de máquinas.
	 */
	public Hashtable<String,Cluster> getMachines() {
		return clusters;
	}

	/**
	 * Returns the timeout to wait for batch command output.
	 * 
	 * @return the timeout in miliseconds.
	 */
	public long getCmdTimeout() {
		return Long.parseLong(properties.getProperty("cmd.timeout", "30"))*1000;
	}
	
	/**
	 * Get the folder to search for files.
	 * 
	 * @return the folder to search for files.
	 */
	public String getFolderSearch() {
		return properties.getProperty("folder.search");
	}

	/**
	 * Get the output folder.
	 * 
	 * @return the output folder.
	 */
	public String getOutputFolder() {
		return properties.getProperty("folder.output");
	}

	/**
	 * Construtora com o arquivo de configuração.
	 * 
	 * @param arqConfig o arquivo de configuração.
	 * @throws IOException erro na leitura do arquivo.
	 * @throws FileNotFoundException arquivo não foi encontrado.
	 */
	public Config(File configFile) throws FileNotFoundException, IOException {
		// Cria as propriedades e lê o arquivo.
		properties = new Properties();
		properties.load(new FileReader(configFile));
		PropertyConfigurator.configure(properties);
		
		// Cria as propriedades extendidas e lê o arquivo
		extProperties = new ExtendedProperties(configFile.getPath());
		
		// Cria as classes relacionadas às configurações.
		for(Object nameObj : extProperties.keySet()) {
			// Verifica se é uma informação de máquina.
			Matcher mEntity = pEntity.matcher((String)nameObj);
			
			if(mEntity.matches()) {
				// Pega ou cria o objeto da máquina.
				Cluster cluster = clusters.get(mEntity.group(1).toLowerCase());
				if(null==cluster) {
					logger.debug("creating cluster object for cluster " + mEntity.group(1));
					cluster = new Cluster();
					clusters.put(mEntity.group(1).toLowerCase(), cluster);
				}
				
				// Altera o atributo determinado.
				// Não funciona o JSE 6 machine.getClass().getDeclaredField(mEntity.group(2)).set(machine, properties.get(nameObj));
				// Dá acesso negado
				if(mEntity.group(2).equals("host")) {
					for(String address : extProperties.getStringArray((String)nameObj)) {
						Cluster.Machine m = cluster.new Machine(cluster);
						m.host = address;
						cluster.machines.add(m);
						
						logger.debug("added machine " + address + " to cluster " + mEntity.group(1).toLowerCase());
					}
				} else if(mEntity.group(2).equals("port"))
					cluster.port = (Integer)properties.get(nameObj);
				else if(mEntity.group(2).equals("username"))
					cluster.username = (String)properties.get(nameObj);
				else if(mEntity.group(2).equals("password"))
					cluster.password = (String)properties.get(nameObj);
				else if(mEntity.group(2).equals("range")) {
					for(String range : extProperties.getStringArray((String)nameObj)) {
						Matcher mRange = pRange.matcher(range);
						if(mRange.matches()) {
							logger.debug(mRange.groupCount() + " groups found for range in cluster " + mEntity.group(1).toLowerCase());
							for(int i=0; i<mRange.groupCount()+1; ++i) {
								logger.debug("group " + i + ": " + mRange.group(i));
							}
							if(null != mRange.group(2)) {
								cluster.range.add(cluster.new Range(Integer.parseInt(mRange.group(1)), Integer.parseInt(mRange.group(2))));
							} else {
								cluster.range.add(cluster.new Range(Integer.parseInt(mRange.group(1))));
							}
						}
					}
				} else if(mEntity.group(2).equals("node"))
					cluster.node = (String)properties.get(nameObj);
				else if(mEntity.group(2).equals("class"))
					cluster.connectionClass = (String)properties.get(nameObj);
			}
		}
	}
	
	/**
	 * Place to put the global config.
	 */
	public static Config global;
}
