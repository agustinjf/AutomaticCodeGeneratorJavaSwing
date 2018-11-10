package es.uma.lcc.gui.template.connector;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uma.lcc.gui.template._Client;
import es.uma.lcc.gui.template.converter.ConverterManager;
import es.uma.lcc.gui.template.data.ClientData;
import es.uma.lcc.gui.template.exception.ClientException;
import es.uma.lcc.gui.template.process.ResourceManager;
import es.uma.lcc.iface.template.connector.ISPIConnector;
import es.uma.lcc.iface.template.data.Constant;
import es.uma.lcc.iface.template.data.SPIData;
import es.uma.lcc.iface.template.result.SPIErrorCode;
import es.uma.lcc.iface.template.result.SPIResultData;

/**
 * Clase que intercomunica el cliente con el servidor
 *
 * @author ajifernandez
 *
 */
public class ClientConnector {
	private static final int RETRY_TIME = 5000;
	// Interfaz remota
	ISPIConnector remote = null;
	// Manager de conversores
	ConverterManager converterManager;
	private ResourceManager resourceManger;

	/**
	 * Constructor
	 */
	public ClientConnector(ResourceManager resourceManager) {
		this.resourceManger = resourceManager;
		converterManager = new ConverterManager();
	}

	/**
	 * Crea la conexión con el servidor
	 */
	public void remoteConnection() {
		try {
			// Nos conectamos a la ip y puertos pasados por argumentos
			Registry registry = LocateRegistry.getRegistry(Constant.SERVER_IP,
					Constant.RMI_PORT);
			boolean error = false;
			boolean connected = false;
			// Nos quedamos hasta que se conecte
			while (!connected) {
				try {
					this.remote = (ISPIConnector) registry
							.lookup(Constant.RMI_ID);
					connected = true;
				} catch (NotBoundException e) {
					Logger.getLogger(_Client.class.getName()).log(
							Level.SEVERE,
							resourceManger.getString(
									"process.error.server.connection",
									new Object[] { RETRY_TIME }), e);
					error = true;
				} catch (RemoteException e) {
					Logger.getLogger(_Client.class.getName()).log(
							Level.SEVERE,
							resourceManger.getString(
									"process.error.server.connection",
									new Object[] { RETRY_TIME }), e);
					error = true;
				} finally {
					if (error) {
						try {
							Thread.sleep(RETRY_TIME);
						} catch (InterruptedException e1) {
							Logger.getLogger(_Client.class.getName()).log(
									Level.SEVERE,
									resourceManger.getString(
											"process.error.server.connection",
											new Object[] { RETRY_TIME }));
						}
					}
				}
			}
		} catch (RemoteException e) {
			Logger.getLogger(_Client.class.getName())
					.log(Level.SEVERE,
							resourceManger
									.getString("process.error.server.connection.running"),
							e);
		}
	}

	/**
	 * Obtiene la lista de datos
	 *
	 * @return lista de datos
	 * @throws ClientException
	 */
	@SuppressWarnings("unchecked")
	public List<ClientData> get() throws ClientException {
		List<ClientData> result = new ArrayList<ClientData>();
		try {
			SPIResultData<List<SPIData>> spiResultData = remote.get();
			if (SPIErrorCode.OK.equals(spiResultData.getCode())) {
				result = converterManager
						.convertSPIList((List<SPIData>) spiResultData
								.getData());
			} else {
				throw new ClientException(spiResultData.getCode(),
						resourceManger.getString("process.get.message.error"));
			}
			// List<SPIHostsData> list = remote.get();
		} catch (RemoteException e) {
			Logger.getLogger(_Client.class.getName()).log(Level.SEVERE,
					resourceManger.getString("process.get.message.error"), e);
		}
		return result;
	}

	/**
	 * Guarda un dato
	 *
	 * @param data
	 *            dato a guardar
	 * @return resultado del guardado
	 * @throws ClientException
	 */
	public ClientData save(ClientData data) throws ClientException {
		ClientData result = null;
		try {
			SPIData spiData = converterManager.convert(data);
			SPIResultData<SPIData> spiResultData = remote.put(spiData);
			if (SPIErrorCode.OK.equals(spiResultData.getCode())) {
				result = converterManager.convert((SPIData) spiResultData
						.getData());
			} else {
				throw new ClientException(spiResultData.getCode(),
						resourceManger.getString("process.save.message.error"));
			}
		} catch (RemoteException e) {
			Logger.getLogger(_Client.class.getName()).log(Level.SEVERE,
					resourceManger.getString("process.save.message.error"), e);
		}

		return result;
	}

	/**
	 * Borra un dato
	 *
	 * @param data
	 *            a borrar
	 * @return
	 * @throws ClientException
	 */
	public ClientData delete(ClientData data) throws ClientException {
		ClientData result = null;
		try {
			SPIData spiData = converterManager.convert(data);
			SPIResultData<SPIData> spiResultData = remote.delete(spiData);
			if (SPIErrorCode.OK.equals(spiResultData.getCode())) {
				result = converterManager.convert((SPIData) spiResultData
						.getData());
			} else {
				throw new ClientException(spiResultData.getCode(),
						resourceManger.getString("process.delete.message.ko"));
			}
		} catch (RemoteException e) {
			Logger.getLogger(_Client.class.getName()).log(Level.SEVERE,
					resourceManger.getString("process.delete.message.ko"), e);
		}

		return result;
	}

	/**
	 * Comprobamos si el servidor sigue conectado o no
	 *
	 * @return estado del servidor
	 * @throws RemoteException
	 * @throws ClientException
	 */
	public Boolean isAlive() throws RemoteException, ClientException {
		return remote.isAlive();
	}
}
