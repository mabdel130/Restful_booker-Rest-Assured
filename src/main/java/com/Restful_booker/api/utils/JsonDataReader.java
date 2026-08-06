package com.Restful_booker.api.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Reads external JSON test data and deserializes it to any POJO type.
 * <p>
 * Every file is passed through {@link TemplateResolver} first, so the same file
 * yields fresh, unique data on every run — data-driven <em>and</em> dynamic.
 */
public final class JsonDataReader {

    private static final Logger LOG = LogManager.getLogger(JsonDataReader.class);
    private static final String TEST_DATA_DIR = "testdata/";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {
    }

    /** Reads a single object, e.g. one booking payload. */
    public static <T> T read(String fileName, Class<T> type) {
        String json = readResolvedJson(fileName);
        try {
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to map test data '" + fileName
                    + "' to " + type.getSimpleName(), e);
        }
    }

    /** Reads a JSON array into a list, for file-driven data providers. */
    public static <T> List<T> readList(String fileName, Class<T> type) {
        String json = readResolvedJson(fileName);
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to map test data list '" + fileName
                    + "' to " + type.getSimpleName(), e);
        }
    }

    /** Reads a JSON array of raw maps — useful for generic, schema-less data sets. */
    public static List<java.util.Map<String, Object>> readRows(String fileName) {
        String json = readResolvedJson(fileName);
        try {
            return MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read test data rows: " + fileName, e);
        }
    }

    /** Raw file content with all dynamic tokens already resolved. */
    public static String readResolvedJson(String fileName) {
        String resource = TEST_DATA_DIR + fileName;
        try (InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(resource)) {
            Objects.requireNonNull(stream, "Test data file not found on classpath: " + resource);
            String resolved = TemplateResolver.resolve(
                    new String(stream.readAllBytes(), StandardCharsets.UTF_8));
            LOG.info("Loaded test data '{}' with dynamic values resolved", fileName);
            Allure.addAttachment("Test data: " + fileName, "application/json", resolved, ".json");
            return resolved;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read test data file: " + resource, e);
        }
    }
}
