package es.uma.lcc.gui.appgenerator.data;

/**
 * Modelo de datos de la información general de la aplicación
 *
 * @author ajifernandez
 *
 */
public class GeneralData {
	/** Nombre de la aplicación */
	String appName;
	/** Paquetería utilizada */
	String packageName;
	/** Usuario que crea la aplicación */
	String userName;
	/** Nombreo del modelo de datos */
	String dataModelName;

	/**
	 * Devuelve el valor del atributo appName
	 *
	 * @return atributo appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * Establece el atributo appName
	 *
	 * @param appName
	 *            atributo appName a establecer
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * Devuelve el valor del atributo packageName
	 *
	 * @return atributo packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Establece el atributo packageName
	 *
	 * @param packageName
	 *            atributo packageName a establecer
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Devuelve el valor del atributo userName
	 *
	 * @return atributo userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Establece el atributo userName
	 *
	 * @param userName
	 *            atributo userName a establecer
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Devuelve el valor del atributo dataModelName
	 *
	 * @return atributo dataModelName
	 */
	public String getDataModelName() {
		return dataModelName;
	}

	/**
	 * Establece el atributo dataModelName
	 *
	 * @param dataModelName
	 *            atributo dataModelName a establecer
	 */
	public void setDataModelName(String dataModelName) {
		this.dataModelName = dataModelName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GeneralData [appName=" + appName + ", packageName=" + packageName + ", userName=" + userName + ", dataModelName=" + dataModelName
				+ "]";
	}

}
