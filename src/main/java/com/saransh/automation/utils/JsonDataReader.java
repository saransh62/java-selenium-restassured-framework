package com.saransh.automation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

/** Reads test data JSON from the classpath (src/test/resources/testdata) for @DataProviders. */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {
    }

    public static List<Map<String, String>> readList(String resourcePath) {
        try (InputStream in = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Test data not found: " + resourcePath);
            }
            return MAPPER.readValue(in, new TypeReference<>() { });
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot parse " + resourcePath, e);
        }
    }

    /** Converts a list of rows into TestNG's Object[][] with one Map argument per row. */
    public static Object[][] asDataProvider(String resourcePath) {
        return readList(resourcePath).stream().map(row -> new Object[]{row}).toArray(Object[][]::new);
    }
}
