package br.eng.etech.oxeprov;

import java.io.IOException;

public interface OxeConnection {
	public void connect(Config.Cluster.Machine machine) throws CouldNotConnect, IOException, NotAuthenticated;
	
	public void putFile(String data, String filename, String path) throws IOException;
	
	public void execCommand(String command) throws IOException;
	
	public String readLine() throws IOException;
	
	public String getStderrBuffer();
	
	public boolean hasMoreData() throws IOException;
	
	public void close();
}
