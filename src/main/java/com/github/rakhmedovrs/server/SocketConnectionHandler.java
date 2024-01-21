package com.github.rakhmedovrs.server;


import com.github.rakhmedovrs.Common;
import com.github.rakhmedovrs.avro.ClientGuessRequest;
import com.github.rakhmedovrs.avro.GuessResult;
import com.github.rakhmedovrs.avro.RequestIntention;
import com.github.rakhmedovrs.avro.ServerGuessResponse;
import com.github.rakhmedovrs.avro.utils.AvroUtils;
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

	private final Socket incomingConnection;

	private final Map<Integer, ClientStats> stats;

	public SocketConnectionHandler(Socket incomingConnection, Map<Integer, ClientStats> stats) {
		this.incomingConnection = incomingConnection;
		this.stats = stats;
	}

	@Override
	public void run() {
		Random random = new Random();
		Integer clientId = null;
		try {
			InputStream inputStream = incomingConnection.getInputStream();
			OutputStream outputStream = incomingConnection.getOutputStream();

			try (Scanner inputReader = new Scanner(inputStream, StandardCharsets.UTF_8);
				 PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {
				Integer randomNumber = random.nextInt(RANDOM_RANGE + 1); //upper bound is excluded
				logIfNeeded("Server decided to generate {} as a number to be guessed", randomNumber);
				output.println(generateServerGuessResponseAsString(GuessResult.UNDEFINED));

				while (inputReader.hasNextLine()) {
					ClientGuessRequest clientGuess = AvroUtils.parseJson(inputReader.nextLine(), ClientGuessRequest.getClassSchema());

					clientId = clientGuess.getClientId();
					ClientStats clientStats = stats.computeIfAbsent(clientId, ClientStats::new);

					if (clientGuess.getRequestIntention() == RequestIntention.DROP_CONNECTION) {
						logIfNeeded("Client with id {} requested terminating connection", clientId);
						output.println("Stop command has been received, terminating connection");
						return;
					}

					try {
						int guess = clientGuess.getGuess();
						if (guess < 0 || guess > RANDOM_RANGE) {
							clientStats.addIncorrectGuess();
							logIfNeeded("Client with id {} provided incorrect input", clientId);
							output.println(generateServerGuessResponseAsString(GuessResult.INCORRECT));
						}

						if (randomNumber.equals(guess)) {
							clientStats.addCorrectGuess();
							logIfNeeded("Client with id {} correctly guessed the number", clientId);
							randomNumber = random.nextInt(RANDOM_RANGE + 1); //upper bound is excluded
							logIfNeeded("Server decided to generate {} as a number to be guessed", randomNumber);
							output.println(generateServerGuessResponseAsString(GuessResult.CORRECT));
						} else {
							clientStats.addIncorrectGuess();
							logIfNeeded("Client with id {} made incorrect guess {}", clientId, guess);
							output.println(generateServerGuessResponseAsString(GuessResult.INCORRECT));
						}
					} catch (NumberFormatException ignore) {
						clientStats.addIncorrectGuess();
						logIfNeeded("Client with id {} provided incorrect input", clientId);
						output.println(generateServerGuessResponseAsString(GuessResult.INCORRECT));
					}
				}
			}
		} catch (Exception e) {
			log.error("Error during interaction with the client", e);
		} finally {
			try {
				log.info("Attempting to close socket for clientId {}", clientId);
				incomingConnection.close();
				log.info("Socket for clientId {} was closed", clientId);
			} catch (IOException e) {
				log.error("Error during attempting to close socket for clientId {}", clientId, e);
			}
		}
	}

	private static String generateServerGuessResponseAsString(GuessResult guessResult) throws IOException {
		ServerGuessResponse response = new ServerGuessResponse(guessResult, 0, RANDOM_RANGE);
		return AvroUtils.convertAvroToJsonString(response, ServerGuessResponse.getClassSchema());
	}

	private void logIfNeeded(String logString, Object... arguments) {
		if (Common.MAXIMUM_NUMBER_OF_ACTIVE_CLIENTS <= 10) {
			log.info(logString, arguments);
		}
	}
}
