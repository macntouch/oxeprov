package br.eng.etech.oxeprov;

import br.eng.etech.oxeprov.Config.Cluster.Machine;

public class CouldNotConnect extends Exception {
	/**
	 * Version of the class. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Detalhe do erro de autentica��o.
	 */
	private Config.Cluster.Machine machine;

	/**
	 * Gets the cluster which the error is associated.
	 * 
	 * @return the associated cluster.
	 */
	public Config.Cluster.Machine getMachine() {
		return machine;
	}
	
	/**
	 * Construtora com informações de conexão.
	 * 
	 * @param machine as configurações utilizadas para conexão.
	 */
	public CouldNotConnect(Machine machine) {
		super("could no connect to machine");
		this.machine = machine;
	}
}
