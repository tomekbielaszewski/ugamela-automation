package pl.grizwold.ugamela.routines;

import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.Fleet2;

import java.util.Optional;

public class FleetMovement {
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
}
