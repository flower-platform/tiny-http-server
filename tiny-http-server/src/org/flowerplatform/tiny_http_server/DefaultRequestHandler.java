package org.flowerplatform.tiny_http_server;

import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class DefaultRequestHandler implements RequestHandler {

	/**
	 * The default HTTP status code that we send back when an error is encountered.
	 */
	private static final int DEFAULT_ERROR_HTTP_CODE = 500;
	
	public void processRequest(HttpServer server, String command, String requestData, PrintStream responseOutputStream) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			IHttpCommand commandInstance = null;
			if (server.getCommandFactory() == null) {
				commandInstance = mapper.readValue(requestData, server.commands.get(command));
			} else {
				commandInstance = (IHttpCommand) server.getCommandFactory().createCommandInstance(server.commands.get(command), requestData);
			}
			
			Object result = commandInstance.run();
			
			responseOutputStream.println("HTTP/1.1 200 OK");
			Utils.printCommonHeaders(responseOutputStream);
			responseOutputStream.println();
			if (result != null) {
				if (result instanceof byte[]) {
					responseOutputStream.write((byte[]) result);
				} else {
					mapper.writeValue(responseOutputStream, result);
				}
			}
		} catch (HttpCommandException hce) {
			Integer httpCode = hce.getHttpCode() != null ? hce.getHttpCode() : DEFAULT_ERROR_HTTP_CODE; 
			sendError(httpCode, hce, responseOutputStream);
		} catch (Throwable th) {
			sendError(DEFAULT_ERROR_HTTP_CODE, th, responseOutputStream);
		}
	}
	
	private void sendError(int httpCode, Throwable th, PrintStream responseOutputStream) {
		responseOutputStream.println("HTTP/1.1 " + httpCode + " Internal Server Error");
		Utils.printCommonHeaders(responseOutputStream);
		responseOutputStream.println();
		responseOutputStream.println(th.getMessage());		
	}
	
}
