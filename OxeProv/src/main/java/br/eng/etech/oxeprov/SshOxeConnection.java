package br.eng.etech.oxeprov;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPOutputStream;
import ch.ethz.ssh2.Session;
import br.eng.etech.oxeprov.Config.Cluster;

public class SshOxeConnection implements OxeConnection {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(SshOxeConnection.class);
	
	/**
	 * Conex��o �� m��quina.
	 */
	private Connection connection;

	/**
	 * Sess��o de comunica����o na m��quina.
	 */
	private Session session;

	@Override
	public void connect(Cluster.Machine machine) throws CouldNotConnect, IOException, NotAuthenticated {
		// Realiza a autentica���o.
		ConnectionInfo connectionInfo = null;

		logger.debug("connection to " + machine.getHost() + ":" + machine.getPort());
		connection = new Connection(machine.getHost(), machine.getPort());
		try {
			connectionInfo = connection.connect();
		} catch (IOException e) {
			logger.warn("could no connect to " + machine.getHost() + ":" + machine.getPort());
		}

		if(null==connectionInfo) {
			connection.close();
			throw new CouldNotConnect(machine);
		}
		
		logger.debug("authenticating");
		if(!connection.authenticateWithPassword(machine.getUsername(), machine.getPassword())) {
			connection.close();
			logger.error("could not authenticate with " + machine.getUsername());
			throw new NotAuthenticated(machine);
		}
		logger.debug("ssh sessions authenticated");
		
		// Abre a sess��o, mas n��o consome nada.
		logger.debug("opening session");
		try {
			session = connection.openSession();
		} catch(IOException e) {
			connection.close();
			throw e;
		}
	}

	@Override
	public void execCommand(String command) throws IOException {
		// Executa o comando
		session.execCommand(command);
	}

	@Override
	public void putFile(String data, String filename, String path) throws IOException {
		// Cria a comunica������o SCP
		logger.debug("creating scp communication");
		SCPClient scp = new SCPClient(connection);
		
		// Envia o comando para a m���quina
		logger.debug("sending the file to the machine");
		//TODO: Usar o charset
		// Old code:
		// scp.put(data.getBytes(), filename, path);
		// New code (not tested):
		byte[] dataBytes = data.getBytes();
		
		SCPOutputStream out = scp.put(filename, dataBytes.length, path, "0444");
		try {
			out.write(dataBytes);
		} finally {
			try {
				out.close();
			} catch(IOException e) {
				
			}
		}
	}

	// Onde iremos guardar as informa������es de erro do comando chamado
	private StringBuffer stderr = new StringBuffer();
	
	// The channel condition
	private int condition;
	
	@Override
	public String readLine() throws IOException {
		// Onde guardaremos a linha at��� encontrarmos um ENTER
		StringBuffer line = new StringBuffer();

		// While there is no problem...
		boolean hasLine = false;
		
		do {
			// Aguarda a disponibilidade de uma das condi������es no SSH
			condition = session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.STDERR_DATA | ChannelCondition.STDOUT_DATA | ChannelCondition.TIMEOUT, 30000);
			
			// Se houver dados de stderr, leia o caracter e guarda sem tratamento
			if( ( condition & ChannelCondition.STDERR_DATA ) != 0) {
				InputStream is = session.getStderr();
				while(is.available() > 0) {
					stderr.append((char)is.read());
				}
			} 
			
			// Se for uma sa���da padr���o, l��� caracter por caracter et��� EOF ou um ENTER
			if( ( condition & ChannelCondition.STDOUT_DATA ) != 0) {
				InputStream is = session.getStdout();
				while(is.available() > 0) {
					int chr = is.read();
					if(-1 == chr || chr == '\n') {
						hasLine = true;
						break;
					} else {
						line.append((char)chr);
					}
				}
			} 
			
			// Se for EOF ou stream FECHADO, e n���o houver dados, sai...
			if( ( ( condition & ChannelCondition.EOF) != 0 || ( condition & ChannelCondition.CLOSED) != 0 ) && ( condition & ChannelCondition.STDOUT_DATA ) == 0 && ( condition & ChannelCondition.STDERR_DATA ) == 0) {
				break;
			}
			
			// Se houve timeout, sai...
			if( ( condition & ChannelCondition.TIMEOUT ) != 0) {
				logger.error("timeout waiting for remote data");
				break;
			}
		} while(!hasLine);
		
		return hasLine ? line.toString() : null;
	}

	@Override
	public String getStderrBuffer() {
		return stderr.toString();
	}

	@Override
	public boolean hasMoreData() {
		 return (condition & ChannelCondition.STDOUT_DATA) != 0 ||
		 (condition & ChannelCondition.STDERR_DATA) != 0 ||
		 ( (condition & ChannelCondition.TIMEOUT ) == 0 &&
		   (condition & ChannelCondition.EOF ) == 0 &&
		   (condition & ChannelCondition.CLOSED) == 0
		 );
	}

	@Override
	public void close() {
		if(null!=session)
			session.close();
		if(null!=connection)
			connection.close();
		
		session = null;
		connection = null;
	}

}
