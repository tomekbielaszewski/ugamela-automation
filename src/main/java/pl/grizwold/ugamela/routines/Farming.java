package pl.grizwold.ugamela.routines;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.Galaxy;
import pl.grizwold.ugamela.page.MissionInfo;
import pl.grizwold.ugamela.page.SpyReports;
import pl.grizwold.ugamela.page.model.Address;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@Slf4j
public class Farming {

    public Address scanGalaxy(int systemsToScan, int maxScanRetries, Galaxy galaxy)
            throws InterruptedException {
        List<Galaxy.Planet> failedSpyAttempts = Collections.emptyList();

        for (int i = 0; i < systemsToScan; i++) {
            int retries = 0;
            do {
                if (retries > maxScanRetries) break;

                Stream<Galaxy.Planet> planetsToScan = failedSpyAttempts.isEmpty() ?
                        galaxy.getInhabitedPlanets() :
                        failedSpyAttempts.stream();

                failedSpyAttempts = planetsToScan
                        .filter(Galaxy.Planet::isEnemy)
                        .filter(Galaxy.Planet::isLongInactive)
                        .peek(p -> log.info("Scanning " + p.name + " at " + p.address))
                        .map(Galaxy.Planet::spy)
                        .filter(not(MissionInfo::isSuccess))
                        .peek(p -> log.info("Unsuccessful scan: " + p.getMessage()))
                        .peek(p -> {
                            if(p.getMessage().startsWith("Nie masz wystarczającej ilości miejsca na deuter!"))
                                throw new IllegalStateException(p.getMessage());
                        })
                        .map(MissionInfo::getPlanet)
                        .toList();
                galaxy.looseFocus();
                if (!failedSpyAttempts.isEmpty()) {
                    retries++;
                    long retryWaitSec = (long) Math.pow(retries, 2);
                    log.info("There were " + failedSpyAttempts.size() + " failed scans. Waiting " + retryWaitSec + "sec");
                    Thread.sleep(retryWaitSec * 1000);
                    continue;
                }
                galaxy.navigator.nextSystem();
            } while (!failedSpyAttempts.isEmpty());
        }
        return galaxy.address();
    }

    public void farmFromSpyReports(UgamelaSession session) {
        SpyReports spyReports = new SpyReports(session).open();
        do {
            Optional<SpyReports.SpyReport> latestReport = spyReports.latest();
            if (latestReport.isPresent()) {
                SpyReports.SpyReport report = latestReport.get();
                if (report.hasDefence() || report.hasFleet()) {
                    log.info(String.format("Report %s has defense or fleet - deleting", report.address()));
                    report.delete();
                    continue;
                }
                farmFromSpyReport(report);
                spyReports.open().deleteLatest();
            }
        } while (spyReports.latest().isPresent());
    }

    @SneakyThrows
    public void farmFromSpyReport(SpyReports.SpyReport spyReport) {
        long capacity = 125000;
        long minimumShips = 20;
        long warshipAmount = 200;
        String shipName = "Mega transporter";
        String warshipName = "Okręt wojenny";

        long resourcesSum = (spyReport.metal() + spyReport.cristal() + spyReport.deuterium());
        long loot = resourcesSum / 2;
        long shipsAmount = loot / capacity;

        if (shipsAmount == 0) {
            log.info("Ships amount to be sent is 0. Skipping this report.");
            return;
        }

        long waitingTime = 0;

        while (shipsAmount >= minimumShips) {
            log.info(String.format("Sending %d of %s ships on attack mission to %s", shipsAmount, shipName, spyReport.address()));

            try {
                Fleet1 fleet1 = spyReport.attack();

                FleetMissions fleetMovement = new FleetMissions();
                fleetMovement.chooseGivenAmountOfShips(shipsAmount, shipName, fleet1);
//                fleetMovement.chooseGivenAmountOfShips(warshipAmount, warshipName, fleet1);
                if (!fleet1.canSendFleet())
                    throw new IllegalStateException("Cannot send fleet - all slot taken");
                fleet1.next()
                        .next()
                        .selectMission("Attack")
                        .next();
            } catch (IllegalStateException e) {
                if (waitingTime > 30)
                    throw new IllegalStateException("Waited to long. Ships destroyed? Long missions?", e);
                if (e.getMessage().equalsIgnoreCase("Cannot send fleet - all slot taken")) {
                    log.info("All slots taken. Waiting 1min for free slot and retrying...");
                    Thread.sleep(1000 * 60);
                    waitingTime++;
                    continue;
                }
                if (e.getMessage().equalsIgnoreCase("There is not enough ships available \"" + shipName + "\" amount " + shipsAmount)) {
                    log.info("Not enough ships. Waiting 1min for fleet return...");
                    Thread.sleep(1000 * 60);
                    waitingTime++;
                    continue;
                }
            }

            waitingTime = 0;
            loot /= 2;
            shipsAmount = loot / capacity;
        }
        log.info(String.format("Finished farming %s there is %skk loot left which would require %s ships only.", spyReport.address(), loot / 1000000, shipsAmount));
    }
}
