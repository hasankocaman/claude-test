Feature: Sample Web Testing Feature

  Background:
    Given the user navigates to the application

  @smoke
  Scenario: Verify page title
    When the user is on the home page
    Then the page title should contain "Example"

  @regression
  Scenario: Search functionality
    When the user searches for "selenium"
    Then search results should be displayed
    And results should contain "selenium" keyword

  @smoke @regression
  Scenario Outline: Login with different credentials
    Given the user is on the login page
    When the user enters username "<username>" and password "<password>"
    And clicks the login button
    Then the login result should be "<expected_result>"

    Examples:
      | username    | password    | expected_result |
      | valid_user  | valid_pass  | success         |
      | invalid_user| invalid_pass| failure         |