package com.github.rakhmedovrs.avro.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Ruslan Rakhmedov
 * @created 1/21/2024
 */
public class AvroUtils {
	public static <T extends GenericRecord> T parseJson(String jsonString, Schema schema)
			throws IOException {
		Decoder decoder = DecoderFactory.get().jsonDecoder(schema, jsonString);
		DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
		GenericRecord genericRecord = reader.read(null, decoder);
		return (T) SpecificData.get().deepCopy(schema, genericRecord);
	}

	public static <T extends GenericRecord> String convertAvroToJsonString(T avroObject, Schema schema) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Encoder encoder = EncoderFactory.get().jsonEncoder(schema, outputStream);
		DatumWriter<T> writer = new GenericDatumWriter<>(schema);
		writer.write(avroObject, encoder);
		encoder.flush();
		return outputStream.toString();
	}
}
