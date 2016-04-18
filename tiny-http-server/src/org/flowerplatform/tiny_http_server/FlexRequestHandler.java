package org.flowerplatform.tiny_http_server;

import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Andrei Taras
 */
public class FlexRequestHandler implements RequestHandler {
	
	public void processRequest(HttpServer server, String command, String requestData, PrintStream responseOutputStream) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			IHttpCommand commandInstance = mapper.readValue(requestData, server.commands.get(command));
			Object result = commandInstance.run();
			
			writeResponse(responseOutputStream, 200, "OK", result);
		} catch (ReflectionException re) {
			// Special handler for this kind of exception; we prettily notify the client that
			// a reflection error has occurred, which usually means that this version of Arduino IDE
			// and this version of Flower Platform are no longer compatible.
			//
			// Please also note that we reply with 200 instead of 4XX/5XX; this is intentional 
			writeResponse(responseOutputStream, 200, "Reflection Error", new FlexResponse(FlexResponse.CODE_REFLECTION_ERROR, re.getMessage()));
		} catch (HttpCommandException hce) { 
			// Special handler for this kind of exception; we prettily notify the client
			// about the particular problem that occurred, by setting a custom error code.
			//
			// Please also note that we reply with 200 instead of 4XX/5XX; this is intentional 
			writeResponse(responseOutputStream, 200, "General Command Exec Error", new FlexResponse(FlexResponse.CODE_HTTP_COMMAND_EXECUTION_EXCEPTION, hce.getMessage()));
		} catch (Exception e) {
			// Internal error occurred.
			// Please note that we reply with 200 instead of 4XX/5XX; this is intentional 
			writeResponse(responseOutputStream, 200, "Internal Error", new FlexResponse(FlexResponse.CODE_HTTP_COMMAND_EXECUTION_EXCEPTION, e.getMessage()));
		}
	}
	
	private static void writeResponse(PrintStream out, int httpStatusCode, String httpStatusMessage, Object result) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		out.println(String.format("HTTP/1.1 %d %s", httpStatusCode, httpStatusMessage));
		out.println("Content-type: text/plain");
		out.println("Connection: close");
		out.println("Access-Control-Allow-Origin: *");
		out.println();
		if (result != null) {
			if (result instanceof byte[]) {
				out.write((byte[]) result);
			} else {
				out.write(mapper.writeValueAsString(result).getBytes());
			}
		}
	}

}
