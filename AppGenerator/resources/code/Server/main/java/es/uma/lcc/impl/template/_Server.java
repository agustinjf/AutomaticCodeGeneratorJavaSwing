package es.uma.lcc.impl.template;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import es.uma.lcc.iface.template.data.Constant;
import es.uma.lcc.impl.template.connector.SPIConnector;

/**
 * Clase principal encargada de levantar el servidor
 *
 * @author ajifernandez
 *
 */
public class _Server {

	public static void main(String[] args) {
		try {
			// Obtenemos el fichero de log para configurarlo
			LogManager.getLogManager().readConfiguration(
					_Server.class.getClassLoader().getResourceAsStream(
							"logging.properties"));

			Logger.getLogger(_Server.class.getName()).log(Level.INFO,
					"Server starting");
			Logger.getLogger(_Server.class.getName()).log(Level.INFO,
					"Attempting to create RMI connection");
			SPIConnector impl = new SPIConnector();
			Registry registry = LocateRegistry.createRegistry(Integer
					.valueOf(Constant.RMI_PORT));
			registry.bind(Constant.RMI_ID, impl);

			Logger.getLogger(_Server.class.getName()).log(Level.INFO,
					"RMI connection created");
			Logger.getLogger(_Server.class.getName()).log(Level.INFO,
					"Server running");
		} catch (RemoteException e) {
			Logger.getLogger("_Server").log(Level.SEVERE,
					"Error starting server", e);
			System.exit(-1);
		} catch (AlreadyBoundException e) {
			Logger.getLogger("_Server").log(Level.SEVERE,
					"Error starting server", e);
			System.exit(-1);
		} catch (SecurityException e) {
			Logger.getLogger("_Server").log(Level.SEVERE,
					"Error starting server", e);
			System.exit(-1);
		} catch (FileNotFoundException e) {
			Logger.getLogger("_Server").log(Level.SEVERE,
					"Error starting server", e);
			System.exit(-1);
		} catch (IOException e) {
			Logger.getLogger("_Server").log(Level.SEVERE,
					"Error starting server", e);
			System.exit(-1);
		}
	}

}
