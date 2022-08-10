package pl.grizwold.ugamela.routines;

import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.Buildings;
import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.PlanetChooser;
import pl.grizwold.ugamela.page.ResourcePanel;
import pl.grizwold.ugamela.page.model.Resources;

public class Economy {
    public void sendResourcesForBuildingConstruction(String fromPlanet, String toPlanet, Buildings.Building building, int fromLevel, int toLevel, UgamelaSession session) {
        PlanetChooser planetChooser = new PlanetChooser(session);
        Buildings buildings = new Buildings(session).open();
        ResourcePanel resourcePanel = new ResourcePanel(session);

        planetChooser.openPlanet(toPlanet);
        Resources availableResources = resourcePanel.resources();
        Resources buildingsCost = buildings.upgradeCost(Buildings.Building.BUILDING_NANITES_FACTORY, fromLevel, toLevel);
        Resources toTransport = buildingsCost.subtract(availableResources);
        FleetMissions mission = new FleetMissions();
        mission.transport(fromPlanet, toPlanet, toTransport, session);
    }

    public void collectResourcesFromColonies(UgamelaSession session, String shipName, String motherlandName, String... colonies) {
        PlanetChooser planetChooser = new PlanetChooser(session);
        FleetMissions fleetMissions = new FleetMissions();

        for (String colony : colonies) {
            planetChooser.openPlanet(colony);
            Fleet1 fleet1 = new Fleet1(session);
            fleetMissions.chooseAllShips(shipName, fleet1)
                    .next()
                    .selectDestinationColony(motherlandName)
                    .next()
                    .selectMission("Transport")
                    .loadAllResources()
                    .next();
        }
    }
}
