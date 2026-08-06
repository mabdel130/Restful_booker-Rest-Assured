package com.Restful_booker.api.hooks;

import com.Restful_booker.api.context.ScenarioContext;
import com.Restful_booker.api.logging.LogCollector;
import com.Restful_booker.api.services.AuthService;
import com.Restful_booker.api.services.BookingService;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);

    private final ScenarioContext context;
    private final AuthService authService = new AuthService();
    private final BookingService bookingService = new BookingService();

    public Hooks(ScenarioContext context) {
        this.context = context;
    }

    /** Fresh log buffer per scenario so attachments never mix across threads. */
    @Before(order = 0)
    public void startScenario(Scenario scenario) {
        LogCollector.clear();
        LOG.info("### SCENARIO START: {}", scenario.getName());
    }

    /** Scenarios tagged @auth get a fresh token before they start. */
    @Before(value = "@auth", order = 10)
    public void createAuthToken() {
        LOG.info("Obtaining auth token for an @auth scenario");
        context.setToken(authService.createToken());
    }

    /** Best-effort cleanup: remove any booking a scenario created. */
    @After(order = 10)
    public void cleanUpCreatedBooking() {
        if (context.getBookingId() == null) {
            return;
        }
        try {
            LOG.info("Cleaning up booking {}", context.getBookingId());
            String token = context.getToken() != null ? context.getToken() : authService.createToken();
            bookingService.deleteBooking(context.getBookingId(), token);
        } catch (Exception e) {
            LOG.warn("Cleanup of booking {} failed: {}", context.getBookingId(), e.getMessage());
        }
    }

    /**
     * Runs last (lowest order) so the attachment contains the whole scenario,
     * cleanup included. This is what puts the run log inside the Allure report.
     */
    @After(order = 0)
    public void attachExecutionLog(Scenario scenario) {
        LOG.info("### SCENARIO END: {} -> {}", scenario.getName(), scenario.getStatus());
        String logs = LogCollector.drain();
        if (!logs.isBlank()) {
            Allure.addAttachment("Execution log", "text/plain", logs, ".log");
        }
    }
}
