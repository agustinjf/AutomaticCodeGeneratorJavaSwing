package es.uma.lcc.iface.template.connector;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import es.uma.lcc.iface.template.data.SPIData;
import es.uma.lcc.iface.template.result.SPIResultData;

/**
 * Interfaz con los métodos que ofrece el servidor
 *
 * @author ajifernandez
 *
 */
public interface ISPIConnector extends Remote {

	/**
	 * Obtiene un dato dado un id
	 *
	 * @param id
	 * @return
	 */
	public SPIResultData get(String id) throws RemoteException;

	/**
	 * Obtiene todos los datos
	 *
	 * @return lista de datos
	 */
	public SPIResultData<List<SPIData>> get() throws RemoteException;

	/**
	 * Almacena un dato
	 *
	 * @param spiHostsData
	 * @return resultado de la operación
	 */
	public SPIResultData<SPIData> put(SPIData spiHostsData)
			throws RemoteException;

	/**
	 * Borra un dato
	 *
	 * @param spiHostsData
	 * @return resultado de la operación
	 */
	public SPIResultData<SPIData> delete(SPIData spiHostsData)
			throws RemoteException;

	/**
	 * Método que comprueba si el servidor está vivo
	 *
	 * @return
	 * @throws RemoteException
	 */
	public Boolean isAlive() throws RemoteException;
}
