package pl.grizwold.ugamela.routines;

import lombok.extern.java.Log;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.PlanetChooser;
import pl.grizwold.ugamela.page.ResourcePanel;
import pl.grizwold.ugamela.page.model.Resources;

import java.util.Optional;

@Log
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

        log.info(String.format("%sx %s selected for mission", shipAmount, shipName));

        return fleet;
    }

    public Fleet1 chooseAllShips(String shipName, Fleet1 fleet) {
        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .findFirst();

        if (availableShip.isEmpty())
            throw new IllegalStateException("There is no such ships available \"" + shipName + "\"");
        availableShip.get().selectAll();
        int amount = availableShip.get().shipAmount();

        log.info(String.format("All %sx %s selected for mission", amount, shipName));

        return fleet;
    }

    public void transport(String fromPlanet, String toPlanet, Resources transport, UgamelaSession session) {
        PlanetChooser planetChooser = new PlanetChooser(session);
        planetChooser.openPlanet(fromPlanet);

        ResourcePanel resourcePanel = new ResourcePanel(session);
        Resources availableResources = resourcePanel.availableResources();
        if(transport.isLargerThan(availableResources)) {
            throw new IllegalStateException("Not enough resources on the planet. Missing: " + availableResources.subtractAllowingNegatives(transport));
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
        log.info(String.format("%sx %s sent on a transport mission from %s to %s. Cargo: %s", amountOfTransportShips, TRANSPORT_SHIP, fromPlanet, toPlanet, transport));
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
        log.info(String.format("%sx %s sent on a stationing mission from %s to %s", shipAmount, shipName, fromPlanet, toPlanet));
    }
}
