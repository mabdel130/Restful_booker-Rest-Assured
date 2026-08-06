package com.Restful_booker.api.logging;

/**
 * Per-thread log buffer. Because Cucumber runs one scenario per thread, the
 * buffer holds exactly that scenario's log lines, which the hooks then attach
 * to the Allure report. No shared state, so parallel runs stay clean.
 */
public final class LogCollector {

    private static final ThreadLocal<StringBuilder> BUFFER = ThreadLocal.withInitial(StringBuilder::new);

    private LogCollector() {
    }

    static void append(String line) {
        BUFFER.get().append(line);
    }

    /** Returns everything logged by the current scenario and resets the buffer. */
    public static String drain() {
        String logs = BUFFER.get().toString();
        clear();
        return logs;
    }

    public static void clear() {
        BUFFER.get().setLength(0);
    }
}
