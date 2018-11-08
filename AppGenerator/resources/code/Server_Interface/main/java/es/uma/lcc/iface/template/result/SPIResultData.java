package es.uma.lcc.iface.template.result;

import java.io.Serializable;

public class SPIResultData<T> implements Serializable {
	/** Serial Version */
	private static final long serialVersionUID = 1L;
	SPIErrorCode code;
	T data;
	Exception ex;

	/**
	 * @param code
	 * @param data
	 * @param ex
	 */
	public SPIResultData(SPIErrorCode code, T data, Exception ex) {
		super();
		this.code = code;
		this.data = data;
		this.ex = ex;
	}

	public SPIResultData(SPIErrorCode code) {
		super();
		this.code = code;
	}

	/**
	 * Devuelve el valor del atributo code
	 * 
	 * @return atributo code
	 */
	public SPIErrorCode getCode() {
		return code;
	}

	/**
	 * Establece el atributo code
	 * 
	 * @param code
	 *            atributo code a establecer
	 */
	public void setCode(SPIErrorCode code) {
		this.code = code;
	}

	/**
	 * Devuelve el valor del atributo data
	 * 
	 * @return atributo data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Establece el atributo data
	 * 
	 * @param data
	 *            atributo data a establecer
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * Devuelve el valor del atributo ex
	 * 
	 * @return atributo ex
	 */
	public Exception getEx() {
		return ex;
	}

	/**
	 * Establece el atributo ex
	 * 
	 * @param ex
	 *            atributo ex a establecer
	 */
	public void setEx(Exception ex) {
		this.ex = ex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SPIResultData [code=" + code + ", data=" + data + ", ex=" + ex
				+ "]";
	}

}
