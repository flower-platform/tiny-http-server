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
		} catch (Exception e) {
			responseOutputStream.println("HTTP/1.1 500 Internal Server Error");
			Utils.printCommonHeaders(responseOutputStream);
			responseOutputStream.println();
			responseOutputStream.println(e.getMessage());
		}
	}
	
}
