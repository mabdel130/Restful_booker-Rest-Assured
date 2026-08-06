@health @smoke
Feature: API health
  As a QA engineer
  I want to verify the API is up
  So that functional failures are not confused with outages

  Scenario: Ping health check responds
    When I ping the API
    Then the response status code should be 201
