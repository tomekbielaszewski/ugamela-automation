package pl.grizwold.ugamela.routines;

import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.PlanetChooser;
import pl.grizwold.ugamela.page.ResourcePanel;
import pl.grizwold.ugamela.page.model.Resources;

import java.util.Optional;

public class FleetMissions {
    private static final String TRANSPORT_SHIP = "Mega transporter";
    private static final long TRANSPORT_SHIP_CAPACITY = 125000;

    public Fleet1 chooseGivenAmountOfShips(long shipAmount, String shipName, Fleet1 fleet) {
        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .filter(f -> shipAmount <= f.shipAmount())
                .findFirst();

        if (availableShip.isEmpty())
            throw new IllegalStateException("There is not enough ships available \"" + shipName + "\" amount " + shipAmount);
        availableShip.ifPresent(availableFleet -> availableFleet.select(shipAmount));

        return fleet;
    }

    public Fleet1 chooseAllShips(String shipName, Fleet1 fleet) {
        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .findFirst();

        if (availableShip.isEmpty())
            throw new IllegalStateException("There is no such ships available \"" + shipName + "\"");
        availableShip.ifPresent(Fleet1.AvailableFleet::selectAll);

        return fleet;
    }

    public void transport(String fromPlanet, String toPlanet, Resources transport, UgamelaSession session) {
        PlanetChooser planetChooser = new PlanetChooser(session);
        planetChooser.openPlanet(fromPlanet);

        ResourcePanel resourcePanel = new ResourcePanel(session);
        Resources availableResources = resourcePanel.resources();
        if(availableResources.isLargerThan(transport)) {
            throw new IllegalStateException("Not enough resources on the planet. Missing: " + availableResources.subtract(transport));
        }

        long amountOfTransportShips = (long) Math.ceil(((double) transport.requiredCargo()) / TRANSPORT_SHIP_CAPACITY);
        Fleet1 fleet1 = new Fleet1(session);
        this.chooseGivenAmountOfShips(amountOfTransportShips, TRANSPORT_SHIP, fleet1);

        if (!fleet1.canSendFleet())
            throw new IllegalStateException("Cannot send fleet - all slot taken");
        fleet1.next()
                .selectDestinationColony(toPlanet)
                .next()
                .loadResources(transport)
                .selectMission("Transport")
                .next();
    }

    public void station(String fromPlanet, String toPlanet, int shipAmount, String shipName, UgamelaSession session) {
        PlanetChooser planetChooser = new PlanetChooser(session);
        planetChooser.openPlanet(fromPlanet);

        Fleet1 fleet1 = new Fleet1(session);
        this.chooseGivenAmountOfShips(shipAmount, shipName, fleet1);

        if (!fleet1.canSendFleet())
            throw new IllegalStateException("Cannot send fleet");
        fleet1.next()
                .selectDestinationColony(toPlanet)
                .next()
                .selectMission("Station")
                .next();
    }
}
