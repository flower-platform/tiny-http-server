package org.flowerplatform.tiny_http_server;

import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Claudiu Matei
 */
public interface RequestHandler {

	void processRequest(HttpServer server, String command, String requestData, PrintStream responseOutputStream) throws IOException;
	
}
