package org.flowerplatform.tiny_http_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class ClientHandler implements Runnable {

	private Socket socket;

	private HttpServer server;
	
	private Logger logger = Logger.getGlobal();
	
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
			if (request == null) {
				return;
			}
			String method = request.substring(0, request.indexOf(' '));
			if (!method.equals("POST")) {
				out.println("HTTP/1.1 405 Method Not Allowed");
				Utils.printCommonHeaders(out);
				out.println();
				return;
			}
			int contentLength = 0;
			String s;
			while ((s = in.readLine()).length() > 0) {
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
			String requestData = sb.toString();
			
			server.getRequestHandler().processRequest(server, command, requestData, out);
			
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		} finally {
			try { 
				socket.close(); 
			} 
			catch (Throwable t) { 
				logger.log(Level.SEVERE, t.getMessage(), t);
			}
		}
	}

	/**
	 * Sometimes the content arrives as "" (empty string, but with quotes present). This is apparently valid json,
	 * and needs to be interpreted as such; This function checks if this is the case.
	 * @param emptyJson
	 * @author Andrei Taras
	 */
	private boolean isEmptyJson(StringBuilder emptyJson) {
		if (emptyJson.toString().equals("\"\"")) {
			return true;
		}
		
		return false;
	}
}
