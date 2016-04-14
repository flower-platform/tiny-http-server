package org.flowerplatform.tiny_http_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class ClientHandler implements Runnable {

	private Socket socket;

	private HttpServer server;
	
	public ClientHandler(HttpServer server, Socket socket) {
		this.socket = socket;
		this.server = server;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream out = new PrintStream(socket.getOutputStream());
			String request = in.readLine();
//			System.out.println(request);
			String method = request.substring(0, request.indexOf(' '));
			if (!method.equals("POST")) {
				out.println("HTTP/1.1 405 Method Not Allowed");
				out.println("Content-type: text/plain");
				out.println("Connection: close");
				out.println("Access-Control-Allow-Origin: *");
				out.println();
				return;
			}
			int contentLength = 0;
			String s;
			while ((s = in.readLine()).length() > 0) {
//				System.out.println(s);
				if (s.toLowerCase().startsWith("content-length")) {
					contentLength = Integer.parseInt(s.substring(s.indexOf(' ') + 1));
				}
			}
			String command = request.substring(request.indexOf('/') + 1,  request.lastIndexOf(' '));
			StringBuilder sb = new StringBuilder();
			char[] buf = new char[256];
			while (contentLength > 0) {
				int k = in.read(buf);
				sb.append(buf, 0, k);
				contentLength -= k;
			}

			Class<? extends IHttpCommand> commandClass = server.commands.get(command);
			if (commandClass == null) {
				throw new RuntimeException(String.format("Invalid command: %s", command));
			}
			
			if (sb.length() == 0 || isEmptyJson(sb)) {
				sb = new StringBuilder("{}");
			}
			
			try {
				ObjectMapper mapper = new ObjectMapper();
				IHttpCommand commandInstance = mapper.readValue(sb.toString(), server.commands.get(command));
				Object result = commandInstance.run();
				
				writeResponse(out, 200, "OK", result);
			} catch (ReflectionException re) {
				// Special handler for this kind of exception; we prettily notify the client that
				// a reflection error has occurred, which usually means that this version of Arduino IDE
				// and this version of Flower Platform are no longer compatible.
				//
				// Please also note that we reply with 200 instead of 4XX/5XX; this is intentional 
				writeResponse(out, 200, "Reflection Error", new DefaultResponse(DefaultResponse.CODE_REFLECTION_ERROR, re.getMessage()));
			} catch (HttpCommandException hce) { 
				// Special handler for this kind of exception; we prettily notify the client
				// about the particular problem that occurred, by setting a custom error code.
				//
				// Please also note that we reply with 200 instead of 4XX/5XX; this is intentional 
				writeResponse(out, 200, "General Command Exec Error", new DefaultResponse(DefaultResponse.CODE_HTTP_COMMAND_EXECUTION_EXCEPTION, hce.getMessage()));
			} catch (Exception e) {
				// Internal error occurred.
				// Please note that we reply with 200 instead of 4XX/5XX; this is intentional 
				writeResponse(out, 200, "Internal Error", new DefaultResponse(DefaultResponse.CODE_HTTP_COMMAND_EXECUTION_EXCEPTION, e.getMessage()));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try { 
				socket.close(); 
			} 
			catch (Throwable t) { 
				t.printStackTrace(); 
			}
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
	
	/**
	 * Sometimes the content arrives as "" (empty string, but with quotes present). This is apparently valid json,
	 * and needs to be interpreted as such; This function checks if this is the case.
	 * @param emptyJson
	 */
	private boolean isEmptyJson(StringBuilder emptyJson) {
		if (emptyJson.toString().equals("\"\"")) {
			return true;
		}
		
		return false;
	}
}
