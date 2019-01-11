Feature: Send multiple attacks to planet on latest spy report

  Scenario: Send multiple attacks to planet on latest spy report

    Given go to planet on index number = 1 on planet selector
    Then send multiple attacks to target on latest spy report using just enough (but not less than 300) ships "Mega transporter" with capacity of 125000
    Then delete latest spy report

    Then send multiple attacks to target on latest spy report using just enough (but not less than 300) ships "Mega transporter" with capacity of 125000
    Then delete latest spy report

    Then send multiple attacks to target on latest spy report using just enough (but not less than 300) ships "Mega transporter" with capacity of 125000
    Then delete latest spy report

    Then send multiple attacks to target on latest spy report using just enough (but not less than 300) ships "Mega transporter" with capacity of 125000
    Then delete latest spy report
