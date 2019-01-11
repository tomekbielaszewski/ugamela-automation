package pl.grizwold.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import lombok.extern.java.Log;
import pl.grizwold.Credentials;
import pl.grizwold.State;
import pl.grizwold.pageobj.*;
import pl.grizwold.pageobj.SpyReports.SpyReport;
import pl.grizwold.pageobj.model.Address;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Log
public class GameSteps extends Abstract {
    private final String STATE__SPY_REPORTS = "spyReports";

    private final String STATE__PLANET_ADDRESS = "address";

    private final String STATE__METAL = "metal";
    private final String STATE__CRYSTAL = "crystal";
    private final String STATE__DEUTERIUM = "deuterium";

    private final String STATE__CONTEXT = "context";

    private State state = new State();

    @Before
    public void init() {
        Credentials credentials = new Credentials();
        openOgame().login(credentials.login, credentials.password);
    }

    @After
    public void tearDown() {
        $.quit();
    }

    @Given("^open fleet window$")
    public void openFleetView() {
        Fleet1 fleet = new Fleet1($);
        state.put(STATE__CONTEXT, fleet);
    }

    @Given("^open spy reports$")
    public void openSpyReportsView() {
        SpyReports spyReports = new SpyReports($);
        state.put(STATE__CONTEXT, spyReports);
    }

    @Given("latest spy report has no defence")
    public void spyReportHasNoDefence() {
        Optional<SpyReports> spyReportsView = state.get(STATE__CONTEXT, SpyReports.class);
        assertTrue("No spy reports were loaded to test state", spyReportsView.isPresent());

        Optional<SpyReport> _latestSpyReport = spyReportsView.get().latest();
        assertTrue("No spy reports found", _latestSpyReport.isPresent());

        SpyReport latestSpyReport = _latestSpyReport.get();
        assertTrue("Defence row not visible: Insufficient amount of spy probes or spy tech too low", latestSpyReport.defenceRowVisible());
        assertFalse("Latest spy report has defence on the planet", latestSpyReport.hasDefence());
    }

    @Given("latest spy report has no fleet")
    public void spyReportHasNoFleet() {
        Optional<SpyReports> spyReportsView = state.get(STATE__CONTEXT, SpyReports.class);
        assertTrue("No spy reports were loaded to test state", spyReportsView.isPresent());

        Optional<SpyReport> _latestSpyReport = spyReportsView.get().latest();
        assertTrue("No spy reports found", _latestSpyReport.isPresent());

        SpyReport latestSpyReport = _latestSpyReport.get();
        assertTrue("Fleet row not visible: Insufficient amount of spy probes or spy tech too low", latestSpyReport.fleetRowVisible());
        assertFalse("Latest spy report has fleet on the planet", latestSpyReport.hasFleet());
    }

    @Given("remember resources amount on latest spy report")
    public void saveResourcesFromSpyReport() {
        Optional<SpyReports> spyReportsView = state.get(STATE__CONTEXT, SpyReports.class);
        assertTrue("No spy reports were loaded to test state", spyReportsView.isPresent());

        Optional<SpyReport> _latestSpyReport = spyReportsView.get().latest();
        assertTrue("No spy reports found", _latestSpyReport.isPresent());

        SpyReport latestSpyReport = _latestSpyReport.get();
        this.state.put(STATE__METAL, latestSpyReport.metal());
        this.state.put(STATE__CRYSTAL, latestSpyReport.cristal());
        this.state.put(STATE__DEUTERIUM, latestSpyReport.deuterium());
    }

    @Given("remember address on latest spy report")
    public void saveAddressFromSpyReport() {
        Optional<SpyReports> spyReportsView = state.get(STATE__CONTEXT, SpyReports.class);
        assertTrue("No spy reports were loaded to test state", spyReportsView.isPresent());

        Optional<SpyReport> _latestSpyReport = spyReportsView.get().latest();
        assertTrue("No spy reports found", _latestSpyReport.isPresent());

        SpyReport latestSpyReport = _latestSpyReport.get();
        this.state.put(STATE__PLANET_ADDRESS, latestSpyReport.address());
    }

    @Given("^using state:$")
    public void usingState(List<State.Pair> stateVars) {
        stateVars.forEach(pair -> this.state.put(pair));
    }

    @Given("^go to planet on index number = (\\d+) on planet selector$")
    public void planetSelector(int planetIndex) {
        new PlanetChooser($).openPlanet(planetIndex);
    }

    @Given("^on fleet step 1: use all ships \"([^\"]*)\"$")
    public void chooseAllShips(String shipName) {
        Fleet1 fleet = state.get(STATE__CONTEXT, Fleet1.class)
                .orElse(new Fleet1($));

        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .findFirst();
        assertTrue("There is no ship available \"" + shipName + "\"", availableShip.isPresent());
        availableShip.ifPresent(Fleet1.AvailableFleet::selectAll);

        Fleet2 next = fleet.next();
        state.put(STATE__CONTEXT, next);
    }

    @Given("^on fleet step 1: use (\\d+) ships \"([^\"]*)\"$")
    public void chooseGivenAmountOfShips(int shipAmount, String shipName) {
        Fleet1 fleet = state.get(STATE__CONTEXT, Fleet1.class)
                .orElse(new Fleet1($));

        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .filter(f -> shipAmount <= f.shipAmount())
                .findFirst();
        assertTrue("There is not enough ships available \"" + shipName + "\"", availableShip.isPresent());
        availableShip.ifPresent(availableFleet -> availableFleet.select(shipAmount));

        Fleet2 next = fleet.next();
        state.put(STATE__CONTEXT, next);
    }

    @Given("^on fleet step 1: use just enough ships \"([^\"]*)\" with capacity of (\\d+)$")
    public void chooseJustEnoughShips(String shipName, int capacity) {
        int metal = state.get(STATE__METAL, String.class).map(Integer::parseInt).get();
        int cristal = state.get(STATE__CRYSTAL, String.class).map(Integer::parseInt).get();
        int deuterium = state.get(STATE__DEUTERIUM, String.class).map(Integer::parseInt).get();
        int shipsAmount = (metal + cristal + deuterium) / capacity;

        chooseGivenAmountOfShips(shipsAmount, shipName);
    }

    @Given("^send multiple attacks to target on latest spy report using just enough \\(but not less than (\\d+)\\) ships \"([^\"]*)\" with capacity of (\\d+)$")
    public void spyReport_attackMultipleTimesWithTransport(int minimumShips, String shipName, int capacity) {
        SpyReports spyReportsView = state.get(STATE__CONTEXT, SpyReports.class)
                .orElse(new SpyReports($));

        Optional<SpyReport> _latestSpyReport = spyReportsView.latest();
        assertTrue("No spy reports found", _latestSpyReport.isPresent());

        SpyReport latestSpyReport = _latestSpyReport.get();
        long resourcesSum = (latestSpyReport.metal() + latestSpyReport.cristal() + latestSpyReport.deuterium());
        long loot = resourcesSum / 2;
        long shipsAmount = loot / capacity;

        do {
            log.info(String.format("Sending %d of %s ships on attack mission", shipsAmount, shipName));

            Fleet1 fleet1 = latestSpyReport.attack();

            state.put(STATE__CONTEXT, fleet1);
            chooseGivenAmountOfShips((int) shipsAmount, shipName);

            Fleet4 fleetSentView = state.get(STATE__CONTEXT, Fleet2.class)
                    .orElseThrow(() -> new IllegalStateException("First step of sending fleet did not finished properly"))
                    .next()
                    .selectMission("Attack")
                    .next();
            state.put(STATE__CONTEXT, fleetSentView);

            loot /= 2;
            shipsAmount = loot / capacity;
        } while (shipsAmount >= minimumShips);
    }

    @Given("^send multiple attacks on saved address using just enough \\(but not less than (\\d+)\\) ships \"([^\"]*)\" with capacity of (\\d+)$")
    public void attackMultipleTimesWithTransport(String shipName, int minimumShips, int capacity) {
        int metal = state.get(STATE__METAL, String.class).map(Integer::parseInt).get();
        int cristal = state.get(STATE__CRYSTAL, String.class).map(Integer::parseInt).get();
        int deuterium = state.get(STATE__DEUTERIUM, String.class).map(Integer::parseInt).get();
        int resourcesSum = (metal + cristal + deuterium);
        int loot = resourcesSum / 2;
        int shipsAmount = loot / capacity;

        do {
            log.info(String.format("Sending %d of %s ships on attack mission", shipsAmount, shipName));

            chooseGivenAmountOfShips(shipsAmount, shipName);
            directFleetToSavedPlanetAddress(100);
            sendFleetOnMission("Attack");

            loot /= 2;
            shipsAmount = loot / capacity;
        } while (shipsAmount >= minimumShips);
    }

    @Given("^on fleet step 2: direct ships to saved destination with speed (\\d+)$")
    public void directFleetToSavedPlanetAddress(int speed) {
        Optional<Address> _address = this.state.get(STATE__PLANET_ADDRESS, Address.class);
        assertTrue("Address was not saved before", _address.isPresent());

        Address address = _address.get();
        log.info("Saved address: " + address.toString());

        Fleet2 fleet2 = state.get(STATE__CONTEXT, Fleet2.class)
                .orElseThrow(() -> new IllegalStateException("First step of sending fleet was not done"));

        Fleet3 fleet3 = fleet2.selectSpeed(speed)
                .selectDestination(address)
                .next();

        state.put(STATE__CONTEXT, fleet3);
    }

    @Given("^on fleet step 2: direct ships to saved destination$")
    public void directFleetToSavedPlanetAddress() {
        directFleetToSavedPlanetAddress(100);
    }

    @Given("^on fleet step 2: direct ships to colony on index (\\d+) with speed (\\d+)$")
    public void directFleetToColony(int destinationIndex, int speed) {
        Fleet2 fleet2 = state.get(STATE__CONTEXT, Fleet2.class)
                .orElseThrow(() -> new IllegalStateException("First step of sending fleet was not done"));

        Fleet3 fleet3 = fleet2.selectSpeed(speed)
                .selectDestinationColony(destinationIndex)
                .next();

        state.put(STATE__CONTEXT, fleet3);
    }

    @Given("^on fleet step 2: direct ships to colony on index (\\d+)$")
    public void directFleetToColony(int destinationIndex) {
        directFleetToColony(destinationIndex, 100);
    }

    @Given("^on fleet step 3: send ships on \"([^\"]*)\" mission with all resources$")
    public void sendFleetOnMissionWithAllResources(String mission) {
        Fleet3 fleet3 = state.get(STATE__CONTEXT, Fleet3.class)
                .orElseThrow(() -> new IllegalStateException("Second step of sending fleet was not done"));

        Fleet4 fleet4 = fleet3.selectMission(mission)
                .loadAllResources()
                .next();

        state.put(STATE__CONTEXT, fleet4);
    }

    @Given("^on fleet step 3: send ships on \"([^\"]*)\" mission$")
    public void sendFleetOnMission(String mission) {
        Fleet3 fleet3 = state.get(STATE__CONTEXT, Fleet3.class)
                .orElseThrow(() -> new IllegalStateException("Second step of sending fleet was not done"));

        Fleet4 fleet4 = fleet3.selectMission(mission)
                .next();

        state.put(STATE__CONTEXT, fleet4);
    }

    @Given("^on fleet step 3: send ships on \"([^\"]*)\" mission with saved amount of resources$")
    public void sendFleetOnMissionWithSavedAmountOfResources(String mission) {
        int metal = state.get(STATE__METAL, String.class).map(Integer::parseInt).get();
        int cristal = state.get(STATE__CRYSTAL, String.class).map(Integer::parseInt).get();
        int deuterium = state.get(STATE__DEUTERIUM, String.class).map(Integer::parseInt).get();

        Fleet3 fleet3 = state.get(STATE__CONTEXT, Fleet3.class)
                .orElseThrow(() -> new IllegalStateException("Second step of sending fleet was not done"));

        Fleet4 fleet4 = fleet3.selectMission(mission)
                .loadResources(metal, cristal, deuterium)
                .next();

        state.put(STATE__CONTEXT, fleet4);
    }

    @Given("^delete latest spy report$")
    public void deleteLatestSpyReport() {
        SpyReports spyReportsView = state.get(STATE__CONTEXT, SpyReports.class)
                .orElse(new SpyReports($));

        Optional<SpyReport> _latestSpyReport = spyReportsView.latest();
        assertTrue("No spy reports found", _latestSpyReport.isPresent());

        spyReportsView.deleteLatest();
        state.put(STATE__CONTEXT, spyReportsView);
    }
}
