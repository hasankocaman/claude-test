Feature: Amazon MacBook Pro Purchase Test
  As a customer
  I want to search for MacBook Pro on Amazon
  So that I can add the highest priced one to my cart

  Background:
    Given I am on Amazon homepage

  @smoke @macbook @priority-high
  Scenario: Search and add highest priced MacBook Pro to cart
    When I search for "MacBook Pro"
    And I sort the results by price high to low
    And I select the highest priced MacBook Pro
    Then I should see the product details page
    When I add the product to cart
    Then I should see the product in my cart
    And the product should be the MacBook Pro I selected

  @regression @macbook @price-validation
  Scenario: Verify MacBook Pro price sorting functionality
    When I search for "MacBook Pro"
    And I sort the results by price high to low
    Then the search results should be sorted by price in descending order
    And the highest priced MacBook Pro should be displayed first

  @smoke @macbook @search-validation
  Scenario: Verify MacBook Pro search returns relevant results
    When I search for "MacBook Pro"
    Then I should see search results
    And all visible products should contain "MacBook" or "Mac" in their titles
    And the search results should display product prices
    And the search results should display product ratings

  @regression @macbook @cart-functionality
  Scenario: Add multiple MacBook Pro models to cart
    When I search for "MacBook Pro"
    And I sort the results by price high to low
    And I add the first MacBook Pro to cart
    And I go back to search results
    And I add the second MacBook Pro to cart
    Then I should see 2 products in my cart
    And both products should be MacBook Pro models

  @macbook @product-comparison
  Scenario: Compare MacBook Pro product details
    When I search for "MacBook Pro"
    And I select the first MacBook Pro from results
    Then I should see detailed product information including:
      | Field              | Expected         |
      | Product Title      | contains MacBook |
      | Price             | greater than 0    |
      | Availability      | In Stock         |
      | Product Images    | at least 1       |
      | Product Features  | at least 3       |

  @macbook @cart-validation @end-to-end
  Scenario: Complete MacBook Pro purchase flow validation
    When I search for "MacBook Pro"
    And I sort the results by price high to low  
    And I select the highest priced MacBook Pro
    And I verify the product details are displayed correctly
    And I add the product to cart with quantity 1
    Then I should see the cart page
    And I should see 1 item in the cart
    And the cart subtotal should be greater than 0
    And the product in cart should match the selected MacBook Pro
    When I proceed to checkout
    Then I should be redirected to sign in or checkout page

  @macbook @negative-testing
  Scenario: Handle out of stock MacBook Pro
    When I search for "MacBook Pro"
    And I select a MacBook Pro that may be out of stock
    Then I should see the product details
    When I attempt to add the product to cart
    Then I should see appropriate availability message
    And the add to cart button should reflect the availability status

  @macbook @filter-testing
  Scenario Outline: Search MacBook Pro with different filters
    When I search for "MacBook Pro"
    And I apply the "<filter_type>" filter with value "<filter_value>"
    Then I should see filtered search results
    And all results should match the applied filter criteria

    Examples:
      | filter_type | filter_value |
      | Brand       | Apple        |
      | Price       | High to Low  |
      | Price       | Low to High  |

  @macbook @search-suggestions
  Scenario: Verify MacBook Pro search suggestions
    When I start typing "MacBook" in the search box
    Then I should see search suggestions
    And the suggestions should include "MacBook Pro" related terms
    When I select "MacBook Pro" from suggestions
    Then I should see MacBook Pro search results

  @macbook @responsive-testing @mobile
  Scenario: MacBook Pro search on mobile view
    Given I am using a mobile device viewport
    When I search for "MacBook Pro" 
    And I sort the results by price high to low
    And I select the highest priced MacBook Pro
    Then the product details should be displayed properly on mobile
    When I add the product to cart
    Then the cart should work correctly on mobile view

  @macbook @performance-testing
  Scenario: MacBook Pro search performance validation
    When I measure the time to search for "MacBook Pro"
    Then the search results should load within 10 seconds
    When I measure the time to load product details
    Then the product details page should load within 8 seconds
    When I measure the time to add product to cart
    Then the cart operation should complete within 5 seconds

  @macbook @data-driven
  Scenario Outline: Search for different MacBook Pro models
    When I search for "<macbook_model>"
    And I sort the results by price high to low
    And I select the highest priced product
    Then I should see the product details
    And the product title should contain "<expected_keyword>"
    When I add the product to cart
    Then the product should be successfully added to cart

    Examples:
      | macbook_model    | expected_keyword |
      | MacBook Pro      | MacBook          |
      | MacBook Pro 16   | MacBook          |
      | MacBook Pro M3   | MacBook          |
      | MacBook Pro 2024 | MacBook          |

  @macbook @accessibility
  Scenario: Verify MacBook Pro search accessibility
    When I search for "MacBook Pro"
    Then the search results should be accessible
    And all product links should have proper aria labels
    And images should have descriptive alt text
    When I navigate using keyboard only
    Then I should be able to select and add MacBook Pro to cart

  @macbook @error-handling
  Scenario: Handle MacBook Pro search errors gracefully
    When I search for "MacBook Pro" with special characters "MacBook Pro @#$%"
    Then I should see appropriate search results or error message
    When I encounter a network timeout during search
    Then I should see a user-friendly error message
    And I should be able to retry the search

  @macbook @security-testing
  Scenario: Verify MacBook Pro search security
    When I search for "MacBook Pro" with potential XSS payload
    Then the search should be handled securely
    And no script execution should occur
    And search results should display safely

  @macbook @integration-testing
  Scenario: MacBook Pro search with user account
    Given I am signed in to my Amazon account
    When I search for "MacBook Pro"
    And I add the highest priced MacBook Pro to cart
    Then the product should be saved to my account's cart
    And I should see personalized recommendations
    And my purchase history should be updated