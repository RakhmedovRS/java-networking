package com.github.rakhmedovrs.client;

import com.github.rakhmedovrs.avro.ClientGuessRequest;
import com.github.rakhmedovrs.avro.GuessResult;
import com.github.rakhmedovrs.avro.RequestIntention;
import com.github.rakhmedovrs.avro.ServerGuessResponse;
import com.github.rakhmedovrs.avro.utils.AvroUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Ruslan Rakhmedov
 * @created 12/20/2023
 */
@Slf4j
public class Client implements Runnable {

	private static final String SERVER_URL = "localhost";
	private static final int DEFAULT_PORT = 8189;

	private final int clientId;

	public Client(int clientId) {
		this.clientId = clientId;
	}

	@Override
	public void run() {
		log.info("Attempting to establish connection to {} on port {}", SERVER_URL, DEFAULT_PORT);

		try (Socket socket = new Socket(SERVER_URL, DEFAULT_PORT)) {
			log.info("Connection has been established");
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			try (Scanner inputReader = new Scanner(inputStream, StandardCharsets.UTF_8);
				 PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

				Random random = new Random();

				int randomNumber = 0;
				while (inputReader.hasNextLine()) {
					ServerGuessResponse response = AvroUtils.parseJson(inputReader.nextLine(), ServerGuessResponse.getClassSchema());
					if (GuessResult.INCORRECT == response.getGuessResult()) {
						log.info("Incorrect guess {}", randomNumber);
					} else {
						if (GuessResult.CORRECT == response.getGuessResult()) {
							log.info("Number {} was guessed successfully", randomNumber);
						}

						log.info("Attempting to guess number in range [{},{}]", response.getLowerRangeBoundary(), response.getUpperRangeBoundary());
					}
					randomNumber = random.nextInt(response.getUpperRangeBoundary() + 1);

					ClientGuessRequest clientGuess = new ClientGuessRequest(clientId, RequestIntention.MAKE_A_GUESS, randomNumber);
					output.println(AvroUtils.convertAvroToJsonString(clientGuess, ClientGuessRequest.getClassSchema()));

					Thread.sleep(500);
				}
			}
		} catch (Exception e) {
			log.error("Error during establishing connection");
			throw new RuntimeException(e);
		}
	}
}
