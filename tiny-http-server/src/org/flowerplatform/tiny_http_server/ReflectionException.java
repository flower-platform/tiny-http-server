package org.flowerplatform.tiny_http_server;

/**
 * Exception class; signals that an exception occurred due to reflection issues (i.e. this editor and 
 * Flower platform are no longer compatible).
 * 
 * @author Andrei Taras
 */
public class ReflectionException extends HttpCommandException {
	private static final long serialVersionUID = 2756816656898498410L;

	public ReflectionException() {
		super();
	}

	public ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReflectionException(String message) {
		super(message);
	}

	public ReflectionException(Throwable cause) {
		super(cause);
	}
}
