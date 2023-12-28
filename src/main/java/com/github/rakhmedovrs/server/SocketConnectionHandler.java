package com.github.rakhmedovrs.server;


import com.github.rakhmedovrs.Common;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Ruslan Rakhmedov
 * @created 12/19/2023
 */
@Slf4j
public class SocketConnectionHandler implements Runnable {

	private static final int RANDOM_RANGE = 10;
	private static final String STOP_COMMAND = "STOP";

	private final Socket incomingConnection;

	private final Map<String, ClientStats> stats;

	public SocketConnectionHandler(Socket incomingConnection, Map<String, ClientStats> stats) {
		this.incomingConnection = incomingConnection;
		this.stats = stats;
	}

	@Override
	public void run() {
		Random random = new Random();
		try {
			InputStream inputStream = incomingConnection.getInputStream();
			OutputStream outputStream = incomingConnection.getOutputStream();

			try (Scanner inputReader = new Scanner(inputStream, StandardCharsets.UTF_8);
				 PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {
				Integer randomNumber = random.nextInt(RANDOM_RANGE + 1); //upper bound is excluded
				logIfNeeded("Server decided to generate {} as a number to be guessed", randomNumber);
				output.println("Server generated random number in range [0," + RANDOM_RANGE + "]. Guess it");

				while (inputReader.hasNextLine()) {
					String input = inputReader.nextLine();
					String[] params = input.split("_");
					String clientId = params[0];
					ClientStats clientStats = stats.computeIfAbsent(clientId, ClientStats::new);

					if (input.equalsIgnoreCase(STOP_COMMAND)) {
						logIfNeeded("Client with id {} requested terminating connection", clientId);
						output.println("Stop command has been received, terminating connection");
						return;
					}

					try {
						int guess = Integer.parseInt(params[1]);
						if (guess < 0 || guess > RANDOM_RANGE) {
							clientStats.addIncorrectGuess();
							logIfNeeded("Client with id {} provided incorrect input", clientId);
							output.println("Your input is incorrect, it must be a number in range [0," + RANDOM_RANGE + "]. Guess it");
						}

						if (randomNumber.equals(guess)) {
							clientStats.addCorrectGuess();
							logIfNeeded("Client with id {} correctly guessed the number", clientId);
							randomNumber = random.nextInt(RANDOM_RANGE + 1); //upper bound is excluded
							logIfNeeded("Server decided to generate {} as a number to be guessed", randomNumber);
							output.println("Congrats you correctly guessed the number. " + "Server generated another random number in range [0," + RANDOM_RANGE + "]. Guess it");
						} else {
							clientStats.addIncorrectGuess();
							logIfNeeded("Client with id {} made incorrect guess {}", clientId, guess);
							output.println("Wrong guess");
						}
					} catch (NumberFormatException ignore) {
						clientStats.addIncorrectGuess();
						logIfNeeded("Client with id {} provided incorrect input", clientId);
						output.println("Your input is incorrect, it must be a number in range [0," + RANDOM_RANGE + "]. Guess it");
					}
				}
			}
		} catch (Exception e) {
			log.error("Error during interaction with the client", e);
		} finally {
			try {
				log.info("Attempting to close socket");
				incomingConnection.close();
				log.info("Socket was closed");
			} catch (IOException e) {
				log.error("Error during attempting to close socket", e);
			}
		}
	}

	private void logIfNeeded(String logString, Object... arguments) {
		if (Common.MAXIMUM_NUMBER_OF_ACTIVE_CLIENTS <= 10) {
			log.info(logString, arguments);
		}
	}
}
