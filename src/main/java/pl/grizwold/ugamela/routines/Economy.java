package pl.grizwold.ugamela.routines;

import lombok.extern.java.Log;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.Buildings;
import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.PlanetChooser;
import pl.grizwold.ugamela.page.model.Resources;

@Log
public class Economy {
    public void sendResourcesForBuildingConstruction(String fromPlanet, String toPlanet, Buildings.Building building, int fromLevel, int toLevel, UgamelaSession session) {
        Buildings buildings = new Buildings(session).open();

        Resources buildingsCost = buildings.upgradeCost(building, fromLevel, toLevel);
        log.info(String.format("Upgrade of %s from %s to %s will cost: %s", building, fromLevel, toLevel, buildingsCost));
        FleetMissions mission = new FleetMissions();
        mission.transport(fromPlanet, toPlanet, buildingsCost, session);
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