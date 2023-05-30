Feature: Currency Exchange Rates API

    Background:
        Given the currency exchange rates CSV is loaded into memory

    Scenario: Retrieve the reference rate data for a given date for all available currencies
        Given I am an API client
        When I make a GET request to "/rates" with date parameter "2022-12-01"
        Then I should receive a 200 status code
        And the response should include the reference rate data for all available currencies on "2022-12-01"

    Scenario: Convert a given amount from one currency to another
        Given I am an API client
        When I make a GET request to "/convert" with parameters date "2022-12-01", source currency "JPY", target currency "GBP", and amount "1000"
        Then I should receive a 200 status code
        And the response should include the converted amount in target currency as it would have been on the given date

    Scenario: Retrieve the highest reference exchange rate for a given currency and period
        Given I am an API client
        When I make a GET request to "/highest_rate" with parameters start date "2022-01-01", end date "2022-12-31", and currency "USD"
        Then I should receive a 200 status code
        And the response should include the highest reference exchange rate that the USD achieved in the year 2022

    Scenario: Retrieve the average reference exchange rate for a given currency and period
        Given I am an API client
        When I make a GET request to "/average_rate" with parameters start date "2022-01-01", end date "2022-12-31", and currency "USD"
        Then I should receive a 200 status code
        And the response should include the average reference exchange rate of the USD for the year 2022
