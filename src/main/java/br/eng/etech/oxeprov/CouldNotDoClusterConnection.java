package br.eng.etech.oxeprov;

public class CouldNotDoClusterConnection extends Exception {
	/**
	 * Version of the class. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Detalhe do erro de autentica��o.
	 */
	private Config.Cluster cluster;

	/**
	 * Gets the cluster which the error is associated.
	 * 
	 * @return the associated cluster.
	 */
	public Config.Cluster getCluster() {
		return cluster;
	}
	
	/**
	 * Construtora com informações de conexão.
	 * 
	 * @param machine as configurações utilizadas para conexão.
	 */
	public CouldNotDoClusterConnection(Config.Cluster cluster) {
		super("could no connect to the cluster");
		this.cluster = cluster;
	}
}
