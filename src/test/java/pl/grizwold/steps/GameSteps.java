package pl.grizwold.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import lombok.extern.java.Log;
import pl.grizwold.Credentials;
import pl.grizwold.pageobj.*;
import pl.grizwold.pageobj.model.Address;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

@Log
public class GameSteps extends Abstract {
    private final String STATE__SPY_REPORTS = "spyReports";

    private final String STATE__METAL = "metal";
    private final String STATE__CRYSTAL = "crystal";
    private final String STATE__DEUTERIUM = "deuterium";

    private final String STATE__PLANET_ADDRESS = "address";

    private Map<String, Object> state = new HashMap<>();

    @Before
    public void init() {
        Credentials credentials = new Credentials();
        super.openOgame()
                .login(credentials.login, credentials.password);
    }

    @After
    public void tearDown() {
        $.quit();
    }

    @Given("^open fleet window$")
    public void fleet() {
        new Menu($).fleet();
    }

    @Given("^open spy reports$")
    public void spyReports() {
        this.state.put(STATE__SPY_REPORTS, new Menu($)
                .messages()
                .spyReports());
    }

    @Given("latest spy report has no defence")
    public void spyReportHasNoDefence() {
        List<Messages.SpyReport> spyReports = (List<Messages.SpyReport>) this.state.get(STATE__SPY_REPORTS);

        assertNotNull("No spy reports were loaded to test state", spyReports);
        assertTrue("No spy reports found", spyReports.size() > 0);

        Messages.SpyReport latestSpyReport = spyReports.get(0);
        assertTrue("Defence row not visible: Insufficient amount of spy probes or spy tech too low", latestSpyReport.defenceRowVisible());
        assertFalse("Latest spy report has defence on the planet", latestSpyReport.hasDefence());
    }

    @Given("latest spy report has no fleet")
    public void spyReportHasNoFleet() {
        List<Messages.SpyReport> spyReports = (List<Messages.SpyReport>) this.state.get(STATE__SPY_REPORTS);

        assertNotNull("No spy reports were loaded to test state", spyReports);
        assertTrue("No spy reports found", spyReports.size() > 0);

        Messages.SpyReport latestSpyReport = spyReports.get(0);
        assertTrue("Fleet row not visible: Insufficient amount of spy probes or spy tech too low", latestSpyReport.fleetRowVisible());
        assertFalse("Latest spy report has fleet on the planet", latestSpyReport.hasFleet());
    }

    @Given("remember resources amount on latest spy report")
    public void saveResourcesFromSpyReport() {
        List<Messages.SpyReport> spyReports = (List<Messages.SpyReport>) this.state.get(STATE__SPY_REPORTS);

        assertNotNull("No spy reports were loaded to test state", spyReports);
        assertTrue("No spy reports found", spyReports.size() > 0);

        Messages.SpyReport latestSpyReport = spyReports.get(0);
        this.state.put(STATE__METAL, latestSpyReport.metal());
        this.state.put(STATE__CRYSTAL, latestSpyReport.cristal());
        this.state.put(STATE__DEUTERIUM, latestSpyReport.deuterium());
    }

    @Given("remember address on latest spy report")
    public void saveAddressFromSpyReport() {
        List<Messages.SpyReport> spyReports = (List<Messages.SpyReport>) this.state.get(STATE__SPY_REPORTS);

        assertNotNull("No spy reports were loaded to test state", spyReports);
        assertTrue("No spy reports found", spyReports.size() > 0);

        Messages.SpyReport latestSpyReport = spyReports.get(0);
        this.state.put(STATE__PLANET_ADDRESS, latestSpyReport.address());
    }

    @Given("^using state:$")
    public void fleet(List<State> stateVars) {
        stateVars.forEach(stateVar -> this.state.put(stateVar.key, stateVar.value));
    }

    @Given("^go to (\\d+) planet on planet selector$")
    public void fleet(int planetIndex) {
        new PlanetChooser($).openPlanet(planetIndex);
    }

    @Given("^using all ships \"([^\"]*)\"$")
    public void fleet_step1_allShips(String shipName) {
        Fleet1 fleet = new Fleet1($);
        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .findFirst();
        assertTrue("There is no ship available \"" + shipName + "\"", availableShip.isPresent());
        availableShip.ifPresent(Fleet1.AvailableFleet::selectAll);
        fleet.next();
    }

    @Given("^using (\\d+) ships \"([^\"]*)\"$")
    public void fleet_step1_ships(int shipAmount, String shipName) {
        Fleet1 fleet = new Fleet1($);
        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .filter(f -> shipAmount <= f.shipAmount())
                .findFirst();
        assertTrue("There is not enough ships available \"" + shipName + "\"", availableShip.isPresent());
        availableShip.ifPresent(availableFleet -> availableFleet.select(shipAmount));
        fleet.next();
    }

    @Given("^using just enough ships \"([^\"]*)\" with capacity of (\\d+)$")
    public void fleet_step1_enaughShips(String shipName, int capacity) {
        int metal = Integer.parseInt((String) state.get(STATE__METAL));
        int cristal = Integer.parseInt((String) state.get(STATE__CRYSTAL));
        int deuterium = Integer.parseInt((String) state.get(STATE__DEUTERIUM));
        int shipsAmount = (metal + cristal + deuterium) / capacity;

        fleet_step1_ships(shipsAmount, shipName);
    }

    @Given("^send multiple attacks on saved address using just enough \\(but not less than (\\d+)\\) ships \"([^\"]*)\" with capacity of (\\d+)$")
    public void attackMultipleTimesWithTransport(String shipName, int minimumShips, int capacity) {
        int metal = Integer.parseInt((String) state.get(STATE__METAL));
        int cristal = Integer.parseInt((String) state.get(STATE__CRYSTAL));
        int deuterium = Integer.parseInt((String) state.get(STATE__DEUTERIUM));
        int resourcesSum = (metal + cristal + deuterium);
        int loot = resourcesSum / 2;
        int shipsAmount = loot / capacity;

        do {
            log.info(String.format("Sending %d of %s ships on attack mission", shipsAmount, shipName));
            fleet_step1_ships(shipsAmount, shipName);
            fleet_step2_savedDestination(100);
            fleet_step3_mission("Attack");
            loot /= 2;
            shipsAmount = loot / capacity;
        } while (shipsAmount < minimumShips);
    }

    @Given("^send them to saved destination with speed (\\d+)$")
    public void fleet_step2_savedDestination(int speed) {
        Address address = (Address) this.state.get(STATE__PLANET_ADDRESS);
        new Fleet2($)
                .selectSpeed(speed)
                .selectDestination(address)
                .next();
    }

    @Given("^send them to (\\d+) planet on fleet destination selector with speed (\\d+)$")
    public void fleet_step2_destinationChoosing(int destinationIndex, int speed) {
        new Fleet2($)
                .selectSpeed(speed)
                .selectDestinationColony(destinationIndex)
                .next();
    }

    @Given("^send them on \"([^\"]*)\" mission with all resources$")
    public void fleet_step3_missionWithAllResources(String mission) {
        new Fleet3($)
                .selectMission(mission)
                .loadAllResources()
                .next();
    }

    @Given("^send them on \"([^\"]*)\" mission$")
    public void fleet_step3_mission(String mission) {
        new Fleet3($)
                .selectMission(mission)
                .next();
    }

    @Given("^send them on \"([^\"]*)\" mission with preconfigured amount of resources$")
    public void fleet_step3_missionWithConfiguredResources(String mission) {
        int metal = Integer.parseInt((String) state.get(STATE__METAL));
        int cristal = Integer.parseInt((String) state.get(STATE__CRYSTAL));
        int deuterium = Integer.parseInt((String) state.get(STATE__DEUTERIUM));

        new Fleet3($)
                .selectMission(mission)
                .loadResources(metal, cristal, deuterium)
                .next();
    }

    public class State {
        String key;
        String value;
    }
}
