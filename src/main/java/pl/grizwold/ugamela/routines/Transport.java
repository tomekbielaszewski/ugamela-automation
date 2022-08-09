package pl.grizwold.ugamela.routines;

import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.PlanetChooser;
import pl.grizwold.ugamela.page.ResourcePanel;
import pl.grizwold.ugamela.page.model.Resources;

public class Transport {
    private static final String TRANSPORT_SHIP = "Mega transporter";
    private static final long TRANSPORT_SHIP_CAPACITY = 125000;

    public void transport(int fromPlanet, int toPlanet, Resources transport, UgamelaSession session) {
        PlanetChooser planetChooser = new PlanetChooser(session);
        planetChooser.openPlanet(fromPlanet);

        ResourcePanel resourcePanel = new ResourcePanel(session);
        Resources availableResources = resourcePanel.resources();
        if(availableResources.isLargerThan(transport)) {
            throw new IllegalStateException("Not enough resources on the planet. Missing: " + availableResources.subtract(transport));
        }

        long amountOfTransportShips = transport.requiredCargo() / TRANSPORT_SHIP_CAPACITY;
        Fleet1 fleet1 = new Fleet1(session);
        new FleetMovement().chooseGivenAmountOfShips(amountOfTransportShips, TRANSPORT_SHIP, fleet1);

        if (!fleet1.canSendFleet())
            throw new IllegalStateException("Cannot send fleet - all slot taken");
        fleet1.next()
                .selectDestinationColony(toPlanet)
                .next()
                .loadResources(transport)
                .selectMission("Transport")
                .next();
    }
}
