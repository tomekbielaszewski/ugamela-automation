package pl.grizwold.steps;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import pl.grizwold.Credentials;
import pl.grizwold.pageobj.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class GameSteps extends Abstract {
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
        this.state.put("spyReports", new Menu($)
                .messages()
                .spyReports());
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
        int metal = Integer.parseInt((String) state.get("metal"));
        int cristal = Integer.parseInt((String) state.get("cristal"));
        int deuter = Integer.parseInt((String) state.get("deuter"));
        int shipsAmount = (metal + cristal + deuter) / capacity;

        fleet_step1_ships(shipsAmount, shipName);
    }

    @Given("^send them to (\\d+) planet on fleet destination selector with speed (\\d+)$")
    public void fleet_step2_destinationChoosing(int destinationIndex, int speed) {
        new Fleet2($)
                .selectSpeed(speed)
                .selectOwnDestination(destinationIndex)
                .next();
    }

    @Given("^send them on \"([^\"]*)\" mission with all resources$")
    public void fleet_step3_missionWithAllResources(String mission) {
        new Fleet3($)
                .selectMission(mission)
                .loadAllResources()
                .next();
    }

    @Given("^send them on \"([^\"]*)\" mission with preconfigured amount of resources$")
    public void fleet_step3_missionWithConfiguredResources(String mission) {
        int metal = Integer.parseInt((String) state.get("metal"));
        int cristal = Integer.parseInt((String) state.get("cristal"));
        int deuter = Integer.parseInt((String) state.get("deuter"));

        new Fleet3($)
                .selectMission(mission)
                .loadResources(metal, cristal, deuter)
                .next();
    }

    public class State {
        String key;
        String value;
    }
}
