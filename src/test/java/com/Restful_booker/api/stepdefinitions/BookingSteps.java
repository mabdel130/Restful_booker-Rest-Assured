package com.Restful_booker.api.stepdefinitions;

import com.Restful_booker.api.assertions.ApiAssertions;
import com.Restful_booker.api.config.ConfigProvider;
import com.Restful_booker.api.context.ScenarioContext;
import com.Restful_booker.api.models.request.Booking;
import com.Restful_booker.api.models.response.BookingResponse;
import com.Restful_booker.api.services.BookingService;
import com.Restful_booker.api.utils.DataGenerator;
import com.Restful_booker.api.utils.JsonDataReader;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class BookingSteps {

    private static final Logger LOG = LogManager.getLogger(BookingSteps.class);
    private static final String SCHEMA_DIR = "schemas/";

    private final ScenarioContext context;
    private final BookingService bookingService = new BookingService();

    public BookingSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I create a booking from {string}")
    public void iCreateABookingFrom(String testDataFile) {
        Booking booking = JsonDataReader.read(testDataFile, Booking.class);
        assertPayloadIsUsable(booking, "test data file " + testDataFile);
        createBooking(booking);
    }

    @When("I create a booking with generated data")
    public void iCreateABookingWithGeneratedData() {
        Booking booking = DataGenerator.randomBooking();
        assertPayloadIsUsable(booking, "generated data");
        createBooking(booking);
    }

    @Then("the booking should be created successfully")
    public void theBookingShouldBeCreatedSuccessfully() {
        ApiAssertions.assertThat(context.getLastResponse())
                .hasStatusCode(200)
                .hasJsonContentType()
                .hasNonEmptyBody()
                .hasField("bookingid")
                .respondedWithin(ConfigProvider.get().maxResponseTimeMs());

        BookingResponse created = context.getLastResponse().as(BookingResponse.class);
        Booking expected = context.getExpectedBooking();

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(created.getBookingid() > 0, "Booking id must be positive");
        soft.assertNotNull(created.getBooking(), "Response must echo the created booking");
        soft.assertEquals(created.getBooking().getFirstname(), expected.getFirstname(), "firstname mismatch");
        soft.assertEquals(created.getBooking().getLastname(), expected.getLastname(), "lastname mismatch");
        soft.assertEquals(created.getBooking().getTotalprice(), expected.getTotalprice(), "totalprice mismatch");
        soft.assertEquals(created.getBooking().getDepositpaid(), expected.getDepositpaid(), "depositpaid mismatch");
        soft.assertEquals(created.getBooking().getBookingdates(), expected.getBookingdates(), "bookingdates mismatch");
        soft.assertAll();

        LOG.info("Created booking id {}", created.getBookingid());
        context.setBookingId(created.getBookingid());
    }

    @When("I retrieve the created booking")
    public void iRetrieveTheCreatedBooking() {
        context.setLastResponse(bookingService.getBookingById(requireBookingId()));
        Assert.assertNotNull(context.getLastResponse(), "No response from get booking");
    }

    /** Explicit id lookup — used by negative Scenario Outlines for unknown ids. */
    @When("I retrieve a booking with id {int}")
    public void iRetrieveABookingWithId(int bookingId) {
        LOG.info("Retrieving booking by explicit id {}", bookingId);
        context.setLastResponse(bookingService.getBookingById(bookingId));
        Assert.assertNotNull(context.getLastResponse(), "No response from get booking");
    }

    @Then("the retrieved booking should match the expected booking")
    public void theRetrievedBookingShouldMatchTheExpectedBooking() {
        ApiAssertions.assertThat(context.getLastResponse())
                .hasStatusCode(200)
                .hasJsonContentType()
                .hasNonEmptyBody();

        Booking actual = context.getLastResponse().as(Booking.class);
        Booking expected = context.getExpectedBooking();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(actual.getFirstname(), expected.getFirstname(), "firstname mismatch");
        soft.assertEquals(actual.getLastname(), expected.getLastname(), "lastname mismatch");
        soft.assertEquals(actual.getTotalprice(), expected.getTotalprice(), "totalprice mismatch");
        soft.assertEquals(actual.getDepositpaid(), expected.getDepositpaid(), "depositpaid mismatch");
        soft.assertEquals(actual.getBookingdates(), expected.getBookingdates(), "bookingdates mismatch");
        soft.assertAll();
    }

    @When("I update the booking from {string}")
    public void iUpdateTheBookingFrom(String testDataFile) {
        Assert.assertNotNull(context.getToken(),
                "No auth token — tag the scenario with @auth so the token hook runs");
        Booking updated = JsonDataReader.read(testDataFile, Booking.class);
        assertPayloadIsUsable(updated, "test data file " + testDataFile);

        context.setLastResponse(
                bookingService.updateBooking(requireBookingId(), updated, context.getToken()));
        context.setExpectedBooking(updated);
        LOG.info("Updated booking {} from {}", context.getBookingId(), testDataFile);
    }

    @When("I partially update the booking with firstname {string} and lastname {string}")
    public void iPartiallyUpdateTheBooking(String firstname, String lastname) {
        Assert.assertNotNull(context.getToken(),
                "No auth token — tag the scenario with @auth so the token hook runs");
        Booking partial = Booking.builder().firstname(firstname).lastname(lastname).build();

        context.setLastResponse(
                bookingService.partialUpdateBooking(requireBookingId(), partial, context.getToken()));

        Booking expected = context.getExpectedBooking();
        expected.setFirstname(firstname);
        expected.setLastname(lastname);
    }

    @When("I delete the booking")
    public void iDeleteTheBooking() {
        Assert.assertNotNull(context.getToken(),
                "No auth token — tag the scenario with @auth so the token hook runs");
        context.setLastResponse(
                bookingService.deleteBooking(requireBookingId(), context.getToken()));
    }

    @Then("the created booking should no longer exist")
    public void theCreatedBookingShouldNoLongerExist() {
        int bookingId = requireBookingId();
        ApiAssertions.assertThat(bookingService.getBookingById(bookingId))
                .hasStatusCode(404);
        LOG.info("Confirmed booking {} is gone", bookingId);
        // Nothing left to clean up in the @After hook.
        context.setBookingId(null);
    }

    @Then("the response should match schema {string}")
    public void theResponseShouldMatchSchema(String schemaFile) {
        ApiAssertions.assertThat(context.getLastResponse()).hasJsonContentType();
        context.getLastResponse().then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(SCHEMA_DIR + schemaFile));
        LOG.info("Response conforms to schema {}", schemaFile);
    }

    private void createBooking(Booking booking) {
        context.setExpectedBooking(booking);
        context.setLastResponse(bookingService.createBooking(booking));
        Assert.assertNotNull(context.getLastResponse(), "No response from create booking");
    }

    /** Guards against a template/data mistake silently producing an empty payload. */
    private void assertPayloadIsUsable(Booking booking, String source) {
        SoftAssert soft = new SoftAssert();
        soft.assertNotNull(booking, "No booking payload built from " + source);
        soft.assertNotNull(booking.getFirstname(), "firstname missing in " + source);
        soft.assertNotNull(booking.getLastname(), "lastname missing in " + source);
        soft.assertNotNull(booking.getTotalprice(), "totalprice missing in " + source);
        soft.assertNotNull(booking.getBookingdates(), "bookingdates missing in " + source);
        soft.assertAll();
        LOG.debug("Payload from {}: {}", source, booking);
    }

    private int requireBookingId() {
        Assert.assertNotNull(context.getBookingId(), "No booking was created in this scenario");
        return context.getBookingId();
    }
}
