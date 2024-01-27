package com.github.rakhmedovrs.server;

import com.github.rakhmedovrs.Common;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ruslan Rakhmedov
 * @created 12/19/2023
 */
@Slf4j
public class Server implements Runnable {
	private final int port;
	private final ExecutorService executorService;
	private final ConcurrentHashMap<Integer, ClientStats> stats;

	public Server() {
		port = Common.DEFAULT_PORT;
		executorService = Executors.newFixedThreadPool(Common.MAXIMUM_NUMBER_OF_ACTIVE_CLIENTS + 1); // 1 additional thread for printing starts
		stats = new ConcurrentHashMap<>();

		executorService.submit(() -> {
			while (!executorService.isTerminated()) {
				try {
					ServerStats serverStats = new ServerStats();
					stats.forEach(((clientId, clientStats) -> {
						serverStats.addActiveClient();
						serverStats.addCorrectGuesses(clientStats.getCorrectGuesses());
						serverStats.addIncorrectGuesses(clientStats.getIncorrectGuesses());
					}));

					double totalNumberOfCorrectGuesses = serverStats.getTotalNumberOfCorrectGuesses();
					double totalNumberOfIncorrectGuesses = serverStats.getTotalNumberOfIncorrectGuesses();
					log.info(serverStats.toString());
					log.info("Ratio of correct guesses {}", totalNumberOfCorrectGuesses / totalNumberOfIncorrectGuesses);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public void run() {
		log.info("Starting server socket on port {}", port);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (!serverSocket.isClosed()) {
				log.info("Server socket on port {} has been successfully stated", port);
				Socket socket = serverSocket.accept();
				log.info("New incoming connection request");
				executorService.submit(new SocketConnectionHandler(socket, stats));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			log.info("Shutting down executorService");
			executorService.shutdownNow();
			log.info("executorService has been shutdown");
		}
	}
}
