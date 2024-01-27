package com.github.rakhmedovrs.avro.utils;

import com.github.rakhmedovrs.avro.ClientRequest;
import com.github.rakhmedovrs.avro.ClientMessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Ruslan Rakhmedov
 * @created 1/21/2024
 */
class AvroUtilsTest {
	private final static String CLIENT_GUESS_JSON_MESSAGE = "{\"clientId\":1,\"ClientMessageType\":\"GUESS\",\"guess\":1}";

	@Test
	void parseJson() throws IOException {
		ClientRequest expectedClientGuess = new ClientRequest(1, ClientMessageType.GUESS, 1);
		ClientRequest convertedClientGuess = AvroUtils.parseJson(CLIENT_GUESS_JSON_MESSAGE, ClientRequest.getClassSchema(), ClientRequest.class);
		Assertions.assertEquals(expectedClientGuess, convertedClientGuess);
	}

	@Test
	void testConvertingAvroToJsonString() throws IOException {
		ClientRequest clientGuess = new ClientRequest(1, ClientMessageType.GUESS, 1);
		String jsonString = AvroUtils.convertAvroToJsonString(clientGuess, ClientRequest.getClassSchema(), ClientRequest.class);
		Assertions.assertEquals(CLIENT_GUESS_JSON_MESSAGE, jsonString);
	}
}