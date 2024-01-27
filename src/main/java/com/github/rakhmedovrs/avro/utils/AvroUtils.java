package com.github.rakhmedovrs.avro.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Ruslan Rakhmedov
 * @created 1/21/2024
 */
public class AvroUtils {
	public static <T extends GenericRecord> T parseJson(String jsonString, Schema schema, Class<T> clazz)
			throws IOException {
		SpecificDatumReader<T> reader = new SpecificDatumReader<>(clazz);
		JsonDecoder jsonDecoder = DecoderFactory.get().jsonDecoder(schema, jsonString);
		return reader.read(null, jsonDecoder);
	}

	public static <T extends GenericRecord> String convertAvroToJsonString(T avroObject, Schema schema, Class<T> clazz) throws IOException {
		SpecificDatumWriter<T> writer = new SpecificDatumWriter<>(clazz);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().jsonEncoder(schema, outputStream);
		writer.write(avroObject, encoder);
		encoder.flush();
		return outputStream.toString();
	}
}
