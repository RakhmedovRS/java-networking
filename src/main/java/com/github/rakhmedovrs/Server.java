package com.github.rakhmedovrs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Ruslan Rakhmedov
 * @created 12/19/2023
 */
public class Server implements Runnable {

	private static final int RANDOM_RANGE = 10;
	private static final String STOP_COMMAND = "STOP";
	private static final int DEFAULT_PORT = 8189;

	private int port;

	public Server() {
		port = DEFAULT_PORT;
	}

	public Server(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		Random random = new Random();
		try (ServerSocket serverSocket = new ServerSocket(port);
			 Socket incomingConnection = serverSocket.accept()) {

			InputStream inputStream = incomingConnection.getInputStream();
			OutputStream outputStream = incomingConnection.getOutputStream();

			try (Scanner inputReader = new Scanner(inputStream, StandardCharsets.UTF_8);
				 PrintWriter output = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {
				Integer randomNumber = random.nextInt(RANDOM_RANGE + 1); //upper bound is excluded
				output.println("Server generated random number in range 0 .. " + RANDOM_RANGE + ". Guess it");

				while (inputReader.hasNextLine()) {
					String input = inputReader.nextLine();
					try {
						int guess = Integer.parseInt(input);
						if (guess < 0 || guess > RANDOM_RANGE) {
							output.println("Your input is incorrect, it must be a number in range 0 .. " + RANDOM_RANGE + ". Guess it");
						}

						if (randomNumber.equals(guess)) {
							randomNumber = random.nextInt(RANDOM_RANGE + 1); //upper bound is excluded
							output.println("Congrats you correctly guessed the number. " + "Server generated another random number in range 0 .. " + RANDOM_RANGE + ". Guess it");
						}
						else {
							output.println("Wrong guess");
						}
					}
					catch (NumberFormatException ignore) {
						if (input.equalsIgnoreCase(STOP_COMMAND)) {
							output.println("Stop command has been received, terminating connection");
							return;
						}

						output.println("Your input is incorrect, it must be a number in range 0 .. " + RANDOM_RANGE + ". Guess it");
					}
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
