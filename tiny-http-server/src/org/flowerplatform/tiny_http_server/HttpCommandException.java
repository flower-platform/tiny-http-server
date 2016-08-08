package org.flowerplatform.tiny_http_server;

/**
 * Exception class for any problem that might occur while running a
 * {@link IHttpCommand}  instance.
 * @author Andrei Taras
 */
public class HttpCommandException extends Exception {
	private static final long serialVersionUID = -6100701039991605836L;

	/**
	 * Optionally, user can give a specific http code to write.
	 */
	private Integer httpCode;
	
	public HttpCommandException() {
		super();
	}
	public HttpCommandException(String message, Throwable cause) {
		super(message, cause);
	}
	public HttpCommandException(String message) {
		super(message);
	}
	public HttpCommandException(Throwable cause) {
		super(cause);
	}
	public HttpCommandException(int httpCode) {
		this.httpCode = httpCode;
	}
	public HttpCommandException(int httpCode, String message, Throwable cause) {
		super(message, cause);
		this.httpCode = httpCode;
	}
	public HttpCommandException(int httpCode, String message) {
		super(message);
		this.httpCode = httpCode;
	}
	public HttpCommandException(int httpCode, Throwable cause) {
		super(cause);
		this.httpCode = httpCode;
	}

	public Integer getHttpCode() {
		return httpCode;
	}
}
