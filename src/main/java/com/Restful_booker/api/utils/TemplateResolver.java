package com.Restful_booker.api.utils;

import com.Restful_booker.api.config.ConfigProvider;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves {@code ${...}} tokens found in JSON test-data files and Gherkin
 * Examples tables. This is what keeps the framework free of static data: the
 * files describe the <em>shape</em> of a payload, values are produced per run.
 *
 * <table>
 *   <tr><th>Token</th><th>Resolves to</th></tr>
 *   <tr><td>{@code ${faker.Name.firstName}}</td><td>any Datafaker expression</td></tr>
 *   <tr><td>{@code ${random.int(50,2000)}}</td><td>random int in range (inclusive)</td></tr>
 *   <tr><td>{@code ${random.bool}}</td><td>true / false</td></tr>
 *   <tr><td>{@code ${uuid}}</td><td>random UUID</td></tr>
 *   <tr><td>{@code ${date.today}} {@code ${date.today+30}} {@code ${date.today-7}}</td><td>ISO date</td></tr>
 *   <tr><td>{@code ${config.base.uri}} or {@code ${auth.username}}</td><td>configuration value</td></tr>
 * </table>
 */
public final class TemplateResolver {

    private static final Pattern TOKEN = Pattern.compile("\\$\\{([^}]+)}");
    private static final Pattern RANDOM_INT = Pattern.compile("random\\.int\\((-?\\d+)\\s*,\\s*(-?\\d+)\\)");
    private static final Pattern DATE_OFFSET = Pattern.compile("date\\.today\\s*([+-])\\s*(\\d+)");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Faker FAKER = new Faker();

    private TemplateResolver() {
    }

    public static String resolve(String raw) {
        if (raw == null || !raw.contains("${")) {
            return raw;
        }
        Matcher matcher = TOKEN.matcher(raw);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String resolved = resolveToken(matcher.group(1).trim());
            matcher.appendReplacement(out, Matcher.quoteReplacement(resolved));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private static String resolveToken(String token) {
        if ("uuid".equals(token)) {
            return UUID.randomUUID().toString();
        }
        if ("random.bool".equals(token)) {
            return String.valueOf(ThreadLocalRandom.current().nextBoolean());
        }
        if ("date.today".equals(token)) {
            return LocalDate.now().format(ISO_DATE);
        }
        if (token.startsWith("faker.")) {
            return FAKER.expression("#{" + token.substring("faker.".length()) + "}");
        }

        Matcher randomInt = RANDOM_INT.matcher(token);
        if (randomInt.matches()) {
            int min = Integer.parseInt(randomInt.group(1));
            int max = Integer.parseInt(randomInt.group(2));
            return String.valueOf(ThreadLocalRandom.current().nextInt(min, max + 1));
        }

        Matcher dateOffset = DATE_OFFSET.matcher(token);
        if (dateOffset.matches()) {
            long days = Long.parseLong(dateOffset.group(2));
            LocalDate date = "+".equals(dateOffset.group(1))
                    ? LocalDate.now().plusDays(days)
                    : LocalDate.now().minusDays(days);
            return date.format(ISO_DATE);
        }

        // Anything else is a configuration key, with or without the "config." prefix.
        String key = token.startsWith("config.") ? token.substring("config.".length()) : token;
        String value = ConfigProvider.get().getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Cannot resolve template token: ${" + token + "}");
        }
        return value;
    }
}
