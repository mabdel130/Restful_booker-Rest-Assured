@auth-api
Feature: Authentication
  Token generation against the /auth resource

  # Credentials come from config via ${...} placeholders — resolved at runtime by
  # PlaceholderResolver, so no secret is ever hard-coded in a feature file.
  @smoke
  Scenario Outline: Generate a token with valid credentials
    When I request an auth token with username "<username>" and password "<password>"
    Then the response status code should be 200
    And a valid auth token should be returned

    Examples:
      | username          | password          |
      | ${auth.username}  | ${auth.password}  |

  @negative
  Scenario Outline: Authentication is rejected for "<case>"
    When I request an auth token with username "<username>" and password "<password>"
    Then the response status code should be 200
    And the auth response reason should be "Bad credentials"

    Examples: Invalid credential combinations
      | case              | username         | password    |
      | wrong password    | ${auth.username} | wrongpass   |
      | unknown user      | intruder         | password123 |
      | empty credentials |                  |             |
      | sql injection     | admin' OR '1'='1 | anything    |
