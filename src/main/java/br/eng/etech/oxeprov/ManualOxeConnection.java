package br.eng.etech.oxeprov;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

import br.eng.etech.oxeprov.Config.Cluster;
import br.eng.etech.oxeprov.Config.Cluster.Machine;
import br.eng.etech.oxeprov.ui.CommandDialog;
import br.eng.etech.oxeprov.ui.ShowFileContentsDialog;

public class ManualOxeConnection implements OxeConnection {
	
	/**
	 * A máquina conectada.
	 */
	@SuppressWarnings("unused")
	private Cluster.Machine machine;
	
	/**
	 * Dados das respostas.
	 */
	private BufferedReader response = null;
	
	/**
	 * Última linha lida.
	 */
	private String lastLine = "";

	@Override
	public void connect(Machine machine) throws CouldNotConnect, IOException,
			NotAuthenticated {
		this.machine = machine;
	}

	@Override
	public void putFile(String data, String filename, String path)
			throws IOException {
		ShowFileContentsDialog dialog = new ShowFileContentsDialog(data, filename, path);
		dialog.setModal(true);
		dialog.setVisible(true);		
	}

	@Override
	public void execCommand(String command) throws IOException {
		CommandDialog dialog = new CommandDialog(command);
		dialog.setModal(true);
		dialog.setVisible(true);
		
		response = new BufferedReader(new StringReader(dialog.getResponse()));
	}

	@Override
	public String readLine() throws IOException {
		lastLine = response.readLine();
		return lastLine;
	}

	@Override
	public String getStderrBuffer() {
		return "";
	}

	@Override
	public boolean hasMoreData() throws IOException {
		return response.ready() && lastLine!=null;
	}

	@Override
	public void close() {
		if(null!=response) {
			try {
				response.close();
			} catch (IOException e) {
			}
		}
	}

}
