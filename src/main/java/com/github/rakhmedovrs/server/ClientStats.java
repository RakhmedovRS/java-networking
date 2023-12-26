package com.github.rakhmedovrs.server;

import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ruslan Rakhmedov
 * @created 12/25/2023
 * <p>
 * Class is thread-safe
 */
@ToString
public class ClientStats {
	private final String clientId;
	private final AtomicInteger correctGuesses = new AtomicInteger();
	private final AtomicInteger incorrectGuesses = new AtomicInteger();

	public ClientStats(String clientId) {
		this.clientId = clientId;
	}

	public void addCorrectGuess() {
		correctGuesses.incrementAndGet();
	}

	public void addIncorrectGuess() {
		incorrectGuesses.incrementAndGet();
	}

	public int getCorrectGuesses() {
		return correctGuesses.get();
	}

	public int getIncorrectGuesses() {
		return incorrectGuesses.get();
	}
}
