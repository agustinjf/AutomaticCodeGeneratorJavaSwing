package es.uma.lcc.iface.template.data;

import java.io.Serializable;

/**
 * Module Data
 * 
 * @author ajifernandez
 *
 */
public class SPIData implements Serializable {

	/** Serial version */
	private static final long serialVersionUID = 1L;

	/** Identificador */
	private String id;
	/** Host */
	private String host;
	/** Descripción */
	private String description;
	/** Estado */
	private Boolean isActive;

	/**
	 * Constructor
	 */
	public SPIData() {
		super();
	}

	/**
	 * Devuelve el valor del atributo id
	 *
	 * @return atributo id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Establece el atributo id
	 *
	 * @param id atributo id a establecer
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Devuelve el valor del atributo host
	 *
	 * @return atributo host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Establece el atributo host
	 *
	 * @param host atributo host a establecer
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Devuelve el valor del atributo description
	 *
	 * @return atributo description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Establece el atributo description
	 *
	 * @param description atributo description a establecer
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Devuelve el valor del atributo isActive
	 *
	 * @return atributo isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Establece el atributo isActive
	 *
	 * @param isActive atributo isActive a establecer
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SPIData [id=" + id + ", host=" + host + ", description=" + description + ", isActive=" + isActive + "]";
	}

}
