package org.flowerplatform.tiny_http_server;

/**
 * Used to create and populate command instances
 * 
 * @author Claudiu Matei
 *
 */
public interface CommandFactory {


	/**
	 * 
	 * @param commandClass 
	 * @param data	Data to be loaded into the newly created command instance. Could be a JSON or XML string, or other object
	 * @return
	 */
	Object createCommandInstance(Class<? extends IHttpCommand> commandClass, Object data);
	
}
