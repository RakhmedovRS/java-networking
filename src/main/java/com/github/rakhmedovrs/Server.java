package com.github.rakhmedovrs;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ruslan Rakhmedov
 * @created 12/19/2023
 */
public class Server implements Runnable {


	private static final int DEFAULT_PORT = 8189;
	private static final int MAXIMUM_NUMBER_OF_ACTIVE_CLIENTS = 10;

	private final int port;

	private final ExecutorService executorService;

	public Server() {
		port = DEFAULT_PORT;
		executorService = Executors.newFixedThreadPool(MAXIMUM_NUMBER_OF_ACTIVE_CLIENTS);
	}

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (!serverSocket.isClosed()) {
				executorService.submit(new SocketConnectionHandler(serverSocket.accept()));
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			executorService.shutdownNow();
		}
	}
}
