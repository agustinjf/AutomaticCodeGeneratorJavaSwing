package es.uma.lcc.gui.template.exception;

import es.uma.lcc.iface.template.result.SPIErrorCode;

/**
 * Excepcion del cliente
 *
 * @author ajifernandez
 *
 */
public class ClientException extends Exception {
	/** Serial Version */
	private static final long serialVersionUID = 1L;

	/** Código de error */
	private SPIErrorCode code;
	/** Mensaje de error */
	private String errorMsg;

	/**
	 * Constructor
	 *
	 * @param code
	 */
	public ClientException(SPIErrorCode code) {
		super();
		this.code = code;
	}

	/**
	 * Constructor
	 *
	 * @param code
	 */
	public ClientException(SPIErrorCode code, String msg) {
		super();
		this.code = code;
		this.setErrorMsg(msg);
	}

	/**
	 * Constructor
	 *
	 * @param code
	 */
	public ClientException(SPIErrorCode code, Exception e) {
		super(e);
		this.code = code;
	}

	/**
	 * Constructor
	 */
	public ClientException() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param arg0
	 * @param arg1
	 */
	public ClientException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor
	 *
	 * @param arg0
	 */
	public ClientException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor
	 *
	 * @param arg0
	 */
	public ClientException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Obtiene el valor de code
	 *
	 * @return valor de code
	 */
	public SPIErrorCode getCode() {
		return code;
	}

	/**
	 * Establece el valor de code
	 *
	 * @param code
	 *            a establecer
	 */
	public void setCode(SPIErrorCode code) {
		this.code = code;
	}

	/**
	 * Obtiene el valor de errorMsg
	 *
	 * @return valor de errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * Establece el valor de errorMsg
	 *
	 * @param errorMsg
	 *            a establecer
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "ClientException [code=" + code + ", errorMsg=" + errorMsg + "]";
	}
}
