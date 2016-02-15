package org.flowerplatform.tiny_http_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class HttpServer implements Runnable {

	Map<String, Class<? extends IHttpCommand>> commands;
	
	private ExecutorService threadPool;
	
	private ServerSocket serverSocket;

	boolean stopped = false;
	
	public HttpServer(int port) throws IOException {
		threadPool = Executors.newFixedThreadPool(2);
//		serverSocket = new ServerSocket(port, 0, InetAddress.getLoopbackAddress());
		serverSocket = new ServerSocket(port);
	
		commands = new HashMap<>();
	
		threadPool.submit(this);
	}

	public void registerCommand(String url, Class<? extends IHttpCommand> commandClass) {
		commands.put(url, commandClass);
	}
	
	public void run() {
		try {
			while (!stopped) {
				Socket clientSocket = serverSocket.accept();
				threadPool.submit(new ClientHandler(this, clientSocket));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try { 
				serverSocket.close(); 
			} catch (Throwable t) { 
				t.printStackTrace(); 
			} 
		}
	}
	
	public void stop() {
		stopped = true;
		try { 
			serverSocket.close(); 
		} catch (Throwable t) {
			t.printStackTrace();
		}
		threadPool.shutdownNow();
	}

}
