package es.uma.lcc.impl.template.data;

public class ConfigData {
	private String index;
	private String filePath;

	public ConfigData(String index, String filePath) {
		super();
		this.index = index;
		this.filePath = filePath;
	}

	/**
	 * Devuelve el valor del atributo index
	 * 
	 * @return atributo index
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * Establece el atributo index
	 * 
	 * @param index
	 *            atributo index a establecer
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfigData [index=" + index + ", filePath=" + filePath + "]";
	}

	/**
	 * Devuelve el valor del atributo filePath
	 * 
	 * @return atributo filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Establece el atributo filePath
	 * 
	 * @param filePath
	 *            atributo filePath a establecer
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
