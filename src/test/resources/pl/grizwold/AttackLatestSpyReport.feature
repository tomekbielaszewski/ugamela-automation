Feature: Attack planet on latest spy report

  Scenario:
    Given open spy reports
    And latest spy report has no defence
    And latest spy report has no fleet
    And remember resources amount on latest spy report
    And remember address on latest spy report
    Then send multiple attacks on saved address using just enough ships "Mega Transporter" with capacity of 125000