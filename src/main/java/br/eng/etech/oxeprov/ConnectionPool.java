package br.eng.etech.oxeprov;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import br.eng.etech.oxeprov.Config.Cluster;

public class ConnectionPool {
	
	Logger logger = Logger.getLogger(ConnectionPool.class); 

	private Hashtable<String, Cluster> machines;
	
	private Hashtable<String, OxeConnection> connections = new Hashtable<String,OxeConnection>();

	public ConnectionPool(Hashtable<String, Cluster> machines) {
		this.machines = machines;
	}

	public OxeConnection getConnection(String clusterKey) throws CouldNotDoClusterConnection {
		OxeConnection conn = connections.get(clusterKey);
		if(null==conn) {
	 		Config.Cluster cluster = machines.get(clusterKey);
	 		
			conn = cluster.connect();		
		}
		
		return conn;
	}

	public void closeAll() {
		for(OxeConnection conn : connections.values()) {
			conn.close();
		}
	}
	
	/**
	 * Return the cluster key for a entity.
	 * 
	 * @param entity the entity to find.
	 * @return a String representing a cluster key.
	 */
	public String[] getClusterKey(int entity) {
		String clusterKey = null;
keyFind:	
		for(String key : machines.keySet()) {
			Config.Cluster cluster = machines.get(key);
			
			for(Config.Cluster.Range range : cluster.getRange()) {
				if(range.inRange(entity)) {
					clusterKey = key;
					break keyFind;
				}
			}
		}
		if(null==clusterKey) {
			logger.error("entity " + entity + " not found in any cluster");
			return new String[]{};
		}
		return new String[] {clusterKey};
	}
	
	public String[] getAllClusterKeys() {
		String[] clusterKey = new String[machines.size()];
		machines.keySet().toArray(clusterKey);
		
		return clusterKey;
	}
}
