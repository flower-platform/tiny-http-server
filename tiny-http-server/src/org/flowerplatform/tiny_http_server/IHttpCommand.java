package org.flowerplatform.tiny_http_server;

public interface IHttpCommand {
	Object run() throws HttpCommandException;
}
