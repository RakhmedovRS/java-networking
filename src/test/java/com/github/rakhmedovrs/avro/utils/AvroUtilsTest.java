package com.github.rakhmedovrs.avro.utils;

import com.github.rakhmedovrs.avro.ClientGuessRequest;
import com.github.rakhmedovrs.avro.RequestIntention;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Ruslan Rakhmedov
 * @created 1/21/2024
 */
class AvroUtilsTest {
	private final static String CLIENT_GUESS_JSON_MESSAGE = "{\"clientId\":1,\"requestIntention\":\"MAKE_A_GUESS\",\"guess\":1}";

	@Test
	void parseJson() throws IOException {
		ClientGuessRequest expectedClientGuess = new ClientGuessRequest(1, RequestIntention.MAKE_A_GUESS, 1);
		ClientGuessRequest convertedClientGuess = AvroUtils.parseJson(CLIENT_GUESS_JSON_MESSAGE, ClientGuessRequest.getClassSchema());
		Assertions.assertEquals(expectedClientGuess, convertedClientGuess);
	}

	@Test
	void testConvertingAvroToJsonString() throws IOException {
		ClientGuessRequest clientGuess = new ClientGuessRequest(1, RequestIntention.MAKE_A_GUESS, 1);
		String jsonString = AvroUtils.convertAvroToJsonString(clientGuess, ClientGuessRequest.getClassSchema());
		Assertions.assertEquals(CLIENT_GUESS_JSON_MESSAGE, jsonString);
	}
}