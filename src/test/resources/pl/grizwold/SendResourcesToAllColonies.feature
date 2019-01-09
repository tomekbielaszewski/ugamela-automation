Feature: Send specific amount of resources to each colony

  Scenario: Send specific amount of resources to each colony

    # resources to send to each colony
    Given using state:
      | key     | value     |
      | metal   | 368000000 |
      | cristal | 147000000  |
      | deuter  | 125000    |

    Given go to 1 planet on planet selector

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 2 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 3 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 4 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 5 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 6 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 7 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 8 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 9 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources

    Then open fleet window
    Then using just enough ships "Mega transporter" with capacity of 125000
    Then send them to 10 planet on fleet destination selector with speed 100
    Then send them on "Transport" mission with preconfigured amount of resources