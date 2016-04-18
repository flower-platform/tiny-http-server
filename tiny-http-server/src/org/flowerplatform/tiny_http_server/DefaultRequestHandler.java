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
			IHttpCommand commandInstance = mapper.readValue(requestData, server.commands.get(command));
			Object result = commandInstance.run();
			responseOutputStream.println("HTTP/1.1 200 OK");
			responseOutputStream.println("Content-type: text/plain");
			responseOutputStream.println("Connection: close");
			responseOutputStream.println("Access-Control-Allow-Origin: *");
			responseOutputStream.println();
			if (result != null) {
				if (result instanceof byte[]) {
					responseOutputStream.write((byte[]) result);
				} else {
					responseOutputStream.write(mapper.writeValueAsString(result).getBytes());
				}
			}
		} catch (Exception e) {
			responseOutputStream.println("HTTP/1.1 500 Internal Server Error");
			responseOutputStream.println("Content-type: text/plain");
			responseOutputStream.println("Connection: close");
			responseOutputStream.println("Access-Control-Allow-Origin: *");
			responseOutputStream.println();
			responseOutputStream.println(e.getMessage());
		}
	}
	
}