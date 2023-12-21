package com.github.rakhmedovrs.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ruslan Rakhmedov
 * @created 12/19/2023
 */
@Slf4j
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
		log.info("Starting server socket on port {}", port);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (!serverSocket.isClosed()) {
				log.info("Server socket on port {} has been successfully stated", port);
				Socket socket = serverSocket.accept();
				log.info("New incoming connection request");
				executorService.submit(new SocketConnectionHandler(socket));
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			log.info("Shutting down executorService");
			executorService.shutdownNow();
			log.info("executorService has been shutdown");
		}
	}
}
