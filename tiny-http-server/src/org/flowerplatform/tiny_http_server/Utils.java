package org.flowerplatform.tiny_http_server;

import java.io.PrintStream;

/**
 * Collection of utility function.
 * 
 * @author Andrei Taras
 */
public class Utils {
	
	/**
	 * Some headers such as response encoding, and CORS headers are common through all
	 * responses sent; hence, we grouped them in this function which should be invoked from anybody 
	 * that sends back any kind of answer.
	 */
	public static void printCommonHeaders(PrintStream out) {
		out.println("Content-type: text/plain; charset=utf-8");
		out.println("Connection: close");
		out.println("Access-Control-Allow-Origin: *");
	}
}
