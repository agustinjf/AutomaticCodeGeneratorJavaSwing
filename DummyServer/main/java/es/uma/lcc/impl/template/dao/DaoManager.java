package es.uma.lcc.impl.template.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import es.uma.lcc.iface.template.result.SPIErrorCode;
import es.uma.lcc.impl.template.dao.data.FileType;
import es.uma.lcc.impl.template.data.ConfigData;
import es.uma.lcc.impl.template.data.ServerData;
import es.uma.lcc.impl.template.exception.ServerException;

/**
 * Clase encargada de guardar y recuperar los datos del proveedor de datos
 * elegido
 *
 * @author ajifernandez
 *
 */
public class DaoManager {

	private static final String CONFIG = "config";
	private static final String EXTENSION = ".xml";
	private static final String ENCODING = "UTF-8";
	private static final String XML_HEADER_TAG = "<?xml version=\"1.0\"?>\n";
	public static String FILE_PATH = "./";
	private String dao_id;
	private XStream xstream = null;
	private FileOutputStream fos = null;

	/** Índice por el que se realizan las inserciones nuevas */
	private int index = 0;

	/** Configuración del servidor */
	private ConfigData configData = null;

	/**
	 * Constructor
	 *
	 * @param id
	 *            Identificador del modelo
	 */
	public DaoManager(String id) {
		this.xstream = new XStream(new DomDriver());
		this.xstream.alias("ServerData", ServerData.class);
		this.xstream.alias("ConfigData", ConfigData.class);

		this.dao_id = id;

		configure();
	}

	/**
	 * Configura parámetros de configuración
	 */
	private void configure() {
		if (!existsFile(FileType.CONFIG)) {
			configData = new ConfigData(String.valueOf(index), FILE_PATH);
			saveConfig();
		} else {
			InputStream in = getInputString(FileType.CONFIG);
			configData = (ConfigData) xstream.fromXML(in);
			index = Integer.valueOf(configData.getIndex()) + 1;
			FILE_PATH = configData.getFilePath();
		}

	}

	/**
	 * Obtiene el FileInputSream en función del tipo de fichero que se pase por
	 * argumentos
	 *
	 * @param fileType
	 * @return
	 */
	private FileInputStream getInputString(FileType fileType) {
		String path = getPathFile(fileType);
		try {
			return new FileInputStream(path);
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"File doesn't exists for " + fileType);
		}
		return null;
	}

	/**
	 * Guarda el objeto de forma individual y lo añade al resto de elementos
	 *
	 * @param data
	 * @return resultado de la operacion
	 */
	public Boolean put(List<ServerData> dataList, ServerData data) {
		if (data.getId() != null && !data.getId().equals("")) {
			return update(dataList, data);
		} else {
			return create(dataList, data);
		}
	}

	/**
	 * Crea un nuevo elemento
	 *
	 * @param dataList
	 *
	 * @param data
	 *            Elemento a crear
	 */
	private boolean create(List<ServerData> dataList, ServerData data) {

		String xml = null;

		// El objeto no tiene id, por lo que es un elemento nuevo
		data.setId(String.valueOf(getIndex()));

		// Ya que tenemos el dato con su índice, procedemos a la inserción
		// en el listado
		dataList.add(data);

		// Generamos el xml
		xml = xstream.toXML(dataList);

		// Comprobamos si existe o no el fichero de listado
		if (existsFile(FileType.LIST)) {
			// al existir, lo eliminamos
			if (deleteFile(FileType.LIST)) {
				Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
						"Succesful delete listFile");
			}
		}

		// Creamos un fichero con el xml
		createFile(FileType.LIST, xml);

		// Actualizamos el valor del index
		updateConfigFile();

		return true;
	}

	/**
	 * Actualizamos el valor del index
	 */
	private void updateConfigFile() {
		// Creamos fichero nuevo del listado
		InputStream in;
		try {
			// Recuperamos valores originales del fichero y los modificamos
			in = new FileInputStream(getConfigPath());
			ConfigData configData = (ConfigData) xstream.fromXML(in);
			setIndex(index + 1);
			configData.setIndex(String.valueOf(index));
			configData.setFilePath(FILE_PATH);

			// Construimos el xml
			String xml = xstream.toXML(configData);

			// Borramos el fichero
			deleteFile(FileType.CONFIG);
			// Creamos el nuevo fichero
			createFile(FileType.CONFIG, xml);
		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error updating index", e);
		}
	}

	/**
	 * Obtiene la ruta del fichero de configuración
	 *
	 * @return Path del fichero
	 */
	private String getConfigPath() {
		return FILE_PATH + CONFIG + EXTENSION;
	}

	/**
	 * Actualiza un elemento
	 *
	 * @param data
	 *            Elemento a actualizar
	 * @return
	 */
	private boolean update(List<ServerData> dataList, ServerData data) {

		String xml = null;
		byte[] bytes;

		try {
			// Borramos el fichero antiguo
			File f = new File(getListPath());
			if (f.delete()) {
				for (ServerData dataL : dataList) {
					if (dataL.getId().equals(data.getId())) {
						//FILL_IN_GENERATOR
					}
				}

				// Creamos fichero nuevo del listado
				fos = new FileOutputStream(getListPath());
				fos.write(XML_HEADER_TAG.getBytes(ENCODING));
				xml = xstream.toXML(dataList);
				bytes = xml.getBytes(ENCODING);
				fos.write(bytes);
				fos.close();
			} else {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"Data cannot be updated data");
				return false;
			}
		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error saving data", e);
			return false;
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error saving data", e);
			return false;
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error saving data", e);
			return false;
		}
		return true;
	}

	/**
	 * Obtiene todos los elementos
	 *
	 * @return lista con todos los elementos
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ServerData> get() throws ServerException {
		List<ServerData> dataList = new ArrayList<ServerData>();
		try {
			// Obtenemos la lista de elementos (en caso de exista) e
			// insertamos el nuevo elemento
			File listFile = new File(getListPath());
			if (listFile.exists()) {
				InputStream in = new FileInputStream(getListPath());
				dataList = (List<ServerData>) xstream.fromXML(in);
			} else {
				dataList = new ArrayList<ServerData>();
			}

		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error getting data", e);
			throw new ServerException(SPIErrorCode.DATABASE_ERROR, e);
		}

		return dataList;
	}

	/**
	 * Obtiene la ruta del fichero xml que corresponde con la lista de objetos
	 *
	 * @return Path del fichero
	 */
	private String getListPath() {
		return FILE_PATH + dao_id + EXTENSION;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Guarda la configuración
	 */
	public void saveConfig() {
		String xml = xstream.toXML(configData);
		createFile(FileType.CONFIG, xml);
	}

	/**
	 * Obtiene el valor del ConfigData
	 *
	 * @return ConfigData
	 */
	public ConfigData getConfigData() {
		return configData;
	}

	/**
	 * Establece el valor del configData
	 *
	 * @param configData
	 */
	public void setConfigData(ConfigData configData) {
		this.configData = configData;
	}

	/**
	 * Comprueba la existencia o no del fichero de listado
	 *
	 * @return si existe o no el fichero de listado
	 */
	public boolean existsFile(FileType fileType) {
		String path = getPathFile(fileType);
		File fList = new File(path);
		return fList.exists();
	}

	/**
	 * Borra el fichero de listado
	 *
	 * @return
	 */
	public boolean deleteFile(FileType fileType) {
		String path = getPathFile(fileType);

		File fList = new File(path);
		return fList.delete();
	}

	/**
	 * Obtiene la ruta del fichero enfunción del tipo que sea
	 *
	 * @param fileType
	 *            Tipo de fichero
	 * @return path del fichero
	 */
	private String getPathFile(FileType fileType) {
		String path = "";
		switch (fileType) {
		case CONFIG:
			path = getConfigPath();
			break;
		case LIST:
			path = getListPath();
			break;
		default:
			path = null;
			break;
		}
		return path;
	}

	/**
	 * Crea el fichero de listado con el xml pasado como argumento
	 *
	 * @param xml
	 * @return
	 */
	public boolean createFile(FileType fileType, String xml) {
		boolean result = false;
		try {
			// Obtenemos la ruta del fichero
			String path = getPathFile(fileType);

			// Escribimos el contenido
			fos = new FileOutputStream(path);
			fos.write(XML_HEADER_TAG.getBytes(ENCODING));
			byte[] bytes = xml.getBytes(ENCODING);
			fos.write(bytes);
			fos.close();
			result = true;
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Error creating ListFile", e);
			result = false;
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Error creating ListFile", e);
			result = false;
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Error creating ListFile", e);
			result = false;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DaoManager [dao_id=" + dao_id + ", xstream=" + xstream
				+ ", fos=" + fos + ", index=" + index + ", configData="
				+ configData + "]";
	}

	public boolean delete(ArrayList<ServerData> dataList, ServerData data) {

		String xml = null;
		byte[] bytes;

		try {
			// Borramos el fichero antiguo
			File f = new File(getListPath());
			if (f.delete()) {
				for (int i = dataList.size() - 1; i >= 0; i--) {
					ServerData dataL = dataList.get(i);
					if (dataL.getId().equals(data.getId())) {
						dataList.remove(i);
					}
				}

				// Creamos fichero nuevo del listado
				fos = new FileOutputStream(getListPath());
				fos.write(XML_HEADER_TAG.getBytes(ENCODING));
				xml = xstream.toXML(dataList);
				bytes = xml.getBytes(ENCODING);
				fos.write(bytes);
				fos.close();
			} else {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"Data cannot be updated data");
				return false;
			}
		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error saving data", e);
			return false;
		} catch (UnsupportedEncodingException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error saving data", e);
			return false;
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"Error saving data", e);
			return false;
		}
		return true;
	}
}