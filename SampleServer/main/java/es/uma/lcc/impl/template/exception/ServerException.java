package es.uma.lcc.impl.template.exception;

import es.uma.lcc.iface.template.result.SPIErrorCode;

public class ServerException extends Exception {

	private SPIErrorCode code;
	private String errorMsg;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerException [code=" + code + ", errorMsg=" + errorMsg + "]";
	}

	/**
	 * @param code
	 */
	public ServerException(SPIErrorCode code) {
		super();
		this.code = code;
	}

	/**
	 * @param code
	 */
	public ServerException(SPIErrorCode code, String msg) {
		super();
		this.code = code;
		this.setErrorMsg(msg);
	}

	/**
	 * @param code
	 */
	public ServerException(SPIErrorCode code, Exception e) {
		super(e);
		this.code = code;
	}

	/** Serial Version */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public ServerException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ServerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public ServerException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ServerException(Throwable arg0) {
		super(arg0);
	}

	public SPIErrorCode getCode() {
		return code;
	}

	public void setCode(SPIErrorCode code) {
		this.code = code;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
