package org.flowerplatform.tiny_http_server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Claudiu Matei
 *
 */
public class HttpServer implements Runnable {

	Map<String, Class<? extends IHttpCommand>> commands;
	
	private ExecutorService threadPool;
	
	private ServerSocket serverSocket;

	private CommandFactory commandFactory;
	
	private RequestHandler requestHandler = new DefaultRequestHandler();
	
	boolean stopped = false;
	
	private Logger logger = Logger.getGlobal();
	
	public HttpServer(int port) throws IOException {
		this(port, true);
	}

	public HttpServer(int port, boolean localhostOnly) throws IOException {
		threadPool = Executors.newFixedThreadPool(5);
		
		serverSocket = localhostOnly ? new ServerSocket(port, 0, InetAddress.getLoopbackAddress()) : new ServerSocket(port);
	
		commands = new HashMap<>();
	
		threadPool.submit(this);
	}

	public RequestHandler getRequestHandler() {
		return requestHandler;
	}

	public void setRequestHandler(RequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}
	
	public CommandFactory getCommandFactory() {
		return commandFactory;
	}

	public void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
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
		} catch (SocketException se) {
			// Note that we don't print this exception if server is stopped. It is to be expected 
			// then.
			if (!stopped) {
				logger.log(Level.SEVERE, se.getMessage(), se);
			}
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		} finally {
			try { 
				serverSocket.close(); 
			} catch (Throwable t) { 
				logger.log(Level.SEVERE, t.getMessage(), t);
			} 
		}
	}
	
	public void stop() {
		stopped = true;
		try { 
			serverSocket.close(); 
		} catch (Throwable t) {
			logger.log(Level.SEVERE, t.getMessage(), t);
		}
		threadPool.shutdownNow();
	}

}
