package es.uma.lcc.gui.appgenerator.process.data;

/**
 * Clase que indica el tipo de cambio a realizar
 *
 * @author ajifernandez
 *
 */
public enum ChangeMode {
	Normal, // Cambios básicos a nivel general
	ListView, // Cambios de visualización
	ConsultView, // Cambios de visualización y establecimiento/Obtención de
					// datos
	EditView, // Cambios de visualización y establecimiento/Obtención de datos
	DaoManager, // Cambios en proceso de almacenaje
	ConverterGui, // Cambios en conversión de gui a spi
	ConverterImpl, // Cambios en conversión de spi a impl
	DataModelGui, //Modelo de datos de cliente
	DataModelImpl, //Modelo de datos de servidor
	DataModelIface, //Modelo de datos de intercambio
	Resources, //Fichero de recursos por defecto
	ResourcesES, //Fichero de recursos en español
	ResourcesEN, //Fichero de recursos en ingles
	ClientProcess, //Procesador del client
	ResourceManager //Manager de recursos
}
