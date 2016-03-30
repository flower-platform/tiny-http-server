package org.flowerplatform.tiny_http_server;

/**
 * Exception class for any problem that might occur while running a
 * {@link IHttpCommand}  instance.
 * @author Andrei Taras
 */
public class HttpCommandException extends Exception {
	private static final long serialVersionUID = -6100701039991605836L;

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
}
