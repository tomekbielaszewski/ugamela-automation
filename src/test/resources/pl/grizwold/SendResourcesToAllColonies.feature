Feature: Send specific amount of resources to each colony

  Scenario: Send specific amount of resources to each colony

    # resources to send to each colony
    Given using state:
      | key       | value |
      | metal     | 100000  |
      | crystal   | 200000  |
      | deuterium | 300000 |

    Given go to planet on index number = 1 on planet selector

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 2
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 3
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 4
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 5
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 6
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 7
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 8
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 9
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources

    Then on fleet step 1: use just enough ships "Mega transporter" with capacity of 125000
    And on fleet step 2: direct ships to colony on index 10
    And on fleet step 3: send ships on "Transport" mission with saved amount of resources