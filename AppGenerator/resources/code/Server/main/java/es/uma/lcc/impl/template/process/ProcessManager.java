package es.uma.lcc.impl.template.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uma.lcc.impl.template.dao.DaoManager;
import es.uma.lcc.impl.template.data.ConfigData;
import es.uma.lcc.impl.template.data.ServerData;
import es.uma.lcc.impl.template.exception.ServerException;

/**
 * Clase principal del servidor
 *
 * @author ajifernandez
 *
 */
public class ProcessManager {

	/** Identificador del modelo de datos */
	private final static String DAO_ID = "DATA";

	/** Manager de acceso a datos */
	DaoManager daoManager = null;

	Map<String, ServerData> cache = null;

	/**
	 * Constructor
	 */
	public ProcessManager() {
		daoManager = new DaoManager(DAO_ID);

		// Inicializamos la cache principal
		cache = new HashMap<String, ServerData>();
		initializeCache();

		if (daoManager.getConfigData() == null) {
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Config file doesn't exists");
			daoManager.setConfigData(new ConfigData("0", DaoManager.FILE_PATH));
			daoManager.saveConfig();
		}
	};

	/**
	 * Inicializa la cache
	 */
	private void initializeCache() {
		List<ServerData> dataList = new ArrayList<ServerData>();
		try {
			dataList = find();
		} catch (ServerException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error creating cache data", e);
		}

		for (ServerData data : dataList) {
			cache.put(data.getId(), data);
		}

	}

	/**
	 * Obtiene un host según el id
	 *
	 * @param id
	 * @return host
	 * @throws ServerException
	 */
	public ServerData find(String id) throws ServerException {
		Logger.getLogger(getClass().getName()).log(Level.INFO, id);
		ServerData result = null;
		try {
			// Primero miramos si tenemos datos en la caché
			if (cache != null && !cache.isEmpty()) {
				Logger.getLogger(getClass().getName()).log(Level.INFO, "HIT cache");
				result = cache.get(id);
			} else {
				Logger.getLogger(getClass().getName()).log(Level.INFO, "FAIL cache");
				List<ServerData> dataList = find();
				for (ServerData data : dataList) {
					if (data.getId().equals(id)) {
						result = data;
						break;
					}
				}
			}

			if (result == null) {
				Logger.getLogger(getClass().getName()).log(Level.INFO, "No results");
			}
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Result: " + result);
		} catch (ServerException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error getting data", e);
			throw e;
		}
		return result;
	}

	/**
	 * Guarda un host
	 *
	 * @param hostsData
	 * @return
	 */
	public Boolean save(ServerData data) {
		Logger.getLogger(getClass().getName()).log(Level.INFO, data.toString());
		boolean result = false;
		result = daoManager.put(new ArrayList<ServerData>(cache.values()), data);
		if (result) {
			// Actualizamos la cache
			cache.remove(data.getId());
			cache.put(data.getId(), data);
		}
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Result: " + result);
		return result;
	}

	/**
	 * Borra un host
	 *
	 * @param hostsData
	 * @return
	 */
	public Boolean delete(ServerData data) {
		Logger.getLogger(getClass().getName()).log(Level.INFO, data.toString());
		boolean result = false;
		result = daoManager.delete(new ArrayList<ServerData>(cache.values()), data);
		if (result) {
			// Actualizamos la cache
			cache.remove(data.getId());
		}
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Result: " + result);
		return result;
	}

	/**
	 * Obtiene la lista de host
	 *
	 * @return lista de host
	 * @throws ServerException
	 */
	public List<ServerData> find() throws ServerException {
		List<ServerData> result = null;
		try {
			Logger.getLogger(getClass().getName()).log(Level.INFO, "");
			if (cache != null && !cache.isEmpty()) {
				Logger.getLogger(getClass().getName()).log(Level.INFO, "HIT cache");
				result = new ArrayList<ServerData>(cache.values());
			} else {
				Logger.getLogger(getClass().getName()).log(Level.INFO, "FAIL cache");
				result = daoManager.get();
			}
			Logger.getLogger(getClass().getName()).log(Level.INFO, "Result: " + result);
		} catch (ServerException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error getting data", e);
			throw e;
		}
		return result;
	}
}
