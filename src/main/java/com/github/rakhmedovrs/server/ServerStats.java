package com.github.rakhmedovrs.server;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Ruslan Rakhmedov
 * @created 12/25/2023
 * <p>
 * Class non thread-safe
 */
@ToString
@Getter
public class ServerStats {
	private int numberOfActiveClients;
	private int totalNumberOfCorrectGuesses;
	private int totalNumberOfIncorrectGuesses;

	public void addActiveClient() {
		numberOfActiveClients++;
	}

	public void addCorrectGuesses(int numberOfCorrectGuesses) {
		totalNumberOfCorrectGuesses += numberOfCorrectGuesses;
	}

	public void addIncorrectGuesses(int numberOfIncorrectGuesses) {
		totalNumberOfIncorrectGuesses += numberOfIncorrectGuesses;
	}


}
