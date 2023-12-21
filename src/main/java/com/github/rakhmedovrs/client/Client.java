package com.github.rakhmedovrs.client;

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

	private class Range {
		private final int start;
		private final int end;

		public Range(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

	private static final String SERVER_URL = "localhost";
	private static final int DEFAULT_PORT = 8189;

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

				Range range = null;
				int randomNumber = 0;
				while (inputReader.hasNextLine()) {
					String input = inputReader.nextLine();
					if ("Wrong guess".equals(input)) {
						log.info("Incorrect guess {}", randomNumber);
					}
					else {
						if (input.contains("Congrats you correctly guessed the number")) {
							log.info("Number {} was guessed successfully", randomNumber);
						}

						range = parseRange(input);
						log.info("Attempting to guess number in range [{},{}]", range.start, range.end);
					}
					randomNumber = random.nextInt(range.end + 1);
					output.println(randomNumber);

					Thread.sleep(500);
				}
			}
		}
		catch (Exception e) {
			log.error("Error during establishing connection");
			throw new RuntimeException(e);
		}
	}

	private Range parseRange(String input) {
		String rangeSubString = input.substring(input.indexOf('[') + 1, input.indexOf(']'));
		String[] range = rangeSubString.split(",");
		return new Range(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
	}
}
