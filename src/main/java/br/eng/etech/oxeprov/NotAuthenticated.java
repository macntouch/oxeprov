package br.eng.etech.oxeprov;

import br.eng.etech.oxeprov.Config.Cluster;
import br.eng.etech.oxeprov.Config.Cluster.Machine;

/**
 * Exceção no caso de não autenticação.
 * 
 * @author eduardo@etech.eng.br
 *
 */
public class NotAuthenticated extends Exception {
	/**
	 * The version of the class. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Detalhe do erro de autenticação.
	 */
	private Cluster.Machine machine;
	
	/**
	 * Get the cluster associated with the error.
	 * 
	 * @return the cluster.
	 */
	public Cluster.Machine getMachine() {
		return machine;
	}

	/**
	 * Construtora com informações de conexão.
	 * 
	 * @param machine as configurações utilizadas para conexão.
	 */
	public NotAuthenticated(Machine machine) {
		super("could no authenticate with user");
		this.machine = machine;
	}

}
