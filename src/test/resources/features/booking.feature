@booking
Feature: Booking management
  End-to-end CRUD coverage of the /booking resource

  @auth @e2e
  Scenario Outline: Full booking lifecycle - create, read, update, delete using "<created>"
    When I create a booking from "<created>"
    Then the booking should be created successfully
    When I retrieve the created booking
    Then the retrieved booking should match the expected booking
    When I update the booking from "<updated>"
    Then the response status code should be 200
    When I retrieve the created booking
    Then the retrieved booking should match the expected booking
    When I delete the booking
    Then the response status code should be 201
    And the created booking should no longer exist

    Examples: Payload combinations
      | created                          | updated             |
      | create-booking.json              | update-booking.json |
      | booking-no-additional-needs.json | booking-long-stay.json |

  @data-driven
  Scenario Outline: Create a booking from payload "<payload>"
    When I create a booking from "<payload>"
    Then the booking should be created successfully
    And the response should match schema "create-booking-schema.json"

    Examples: Valid payload variants
      | payload                          |
      | create-booking.json              |
      | booking-no-additional-needs.json |
      | booking-long-stay.json           |

  @smoke
  Scenario: Create a booking with dynamically generated data
    When I create a booking with generated data
    Then the booking should be created successfully
    And the response should match schema "create-booking-schema.json"

  @auth @data-driven
  Scenario Outline: Partially update booking name to "<firstname> <lastname>"
    When I create a booking with generated data
    Then the booking should be created successfully
    When I partially update the booking with firstname "<firstname>" and lastname "<lastname>"
    Then the response status code should be 200
    When I retrieve the created booking
    Then the retrieved booking should match the expected booking

    Examples: Name variants
      | firstname | lastname   |
      | Mohamed   | AbdElghany |
      | Sara      | Ali        |
      | Jean-Luc  | O'Connor   |

  @negative @data-driven
  Scenario Outline: Retrieving a non-existent booking id <bookingId> returns 404
    When I retrieve a booking with id <bookingId>
    Then the response status code should be 404

    Examples: Unknown ids
      | bookingId  |
      | 0          |
      | 99999999   |
      | -1         |
