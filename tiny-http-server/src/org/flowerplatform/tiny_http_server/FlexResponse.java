package org.flowerplatform.tiny_http_server;

/**
 * Represents a basic response, with status, and status message fields. 
 * Currently doesn't do much apart from providing the status as a simple text; but can be extended in the 
 * future to provide a more complex status.
 * 
 * @author Andrei Taras
 */
public class FlexResponse {
	
	public static final int CODE_OK = 0;
	
	/**
	 * Status code indicating that a reflection error has occurred (i.e. Arduino IDE and Flowerino platform are
	 * not 100% compatible - new version perhaps).
	 */
	public static final int CODE_REFLECTION_ERROR = 1;
	
	/**
	 * A "regular" problem occurring during HTTP command execution.
	 */
	public static final int CODE_HTTP_COMMAND_EXECUTION_EXCEPTION = 2;

	/**
	 * Signals that an internal error has occurred.
	 */
	public static final int CODE_INTERNAL_ERROR = 3;
	
	private int code;
	private String message;

	public FlexResponse() {
		this(-1, null);
	}
	
	public FlexResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
