package pl.grizwold.ugamela;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.page.*;
import pl.grizwold.ugamela.page.model.Address;
import pl.grizwold.webdriver.MultiloginWebDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@Log
public class Tester {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        WebDriver $ = connectToBrowser();

        UgamelaSession session = login($);

//        farmFromSpyReports(session);
//        chooseGivenAmountOfShips(100000, "Mega transporter", new Fleet1(session));

//        SpyReports.SpyReport spyReport = new SpyReports(session)
//                .latest()
//                .get();
//        System.out.println(spyReport.defenceRowVisible());
//        System.out.println(spyReport.hasDefence());
//        System.out.println(spyReport.fleetRowVisible());
//        System.out.println(spyReport.hasFleet());

        Address startAddress = new Address("[1:60:1]");

        while (true) {
            farmFromSpyReports(session);
            Galaxy.GALAXY_WAIT_TIMEOUT = 5;
            Galaxy galaxy = new Galaxy(session).goTo(startAddress);
            startAddress = scanGalaxy(10, 30, galaxy);
            log.info("Restarting the cycle from " + startAddress);
        }

//        long count = new SpyReports(session).open()
//                .all()
//                .stream()
//                .filter(not(SpyReports.SpyReport::hasFleet))
//                .count();
//        System.out.println(count);

//        Buildings buildings = new Buildings(session);
//        boolean isUpgradable;
//        do {
//            int plantLevel = buildings.getTotalLevel(Buildings.Building.BUILDING_SOLAR_PLANT);
//            int metalLevel = buildings.getTotalLevel(Buildings.Building.BUILDING_METAL_MINE);
//            int crystalLevel = buildings.getTotalLevel(Buildings.Building.BUILDING_CRYSTAL_MINE);
//
//            if (plantLevel >= metalLevel) isUpgradable = buildings.upgrade(Buildings.Building.BUILDING_METAL_MINE);
//            else if (plantLevel >= crystalLevel) isUpgradable = buildings.upgrade(Buildings.Building.BUILDING_CRYSTAL_MINE);
//            else isUpgradable = buildings.upgrade(Buildings.Building.BUILDING_SOLAR_PLANT);
//        } while (isUpgradable);
    }

    private static Address scanGalaxy(int systemsToScan, int maxScanRetries, Galaxy galaxy)
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

    private static WebDriver connectToBrowser() {
        WebDriver $;

        String profileId = "5973191a-25d3-4047-b5d1-0803344b965f";
        MultiloginWebDriver multiloginWebDriver = new MultiloginWebDriver(profileId);
//        $ = multiloginWebDriver.get();
        $ = multiloginWebDriver.apply(51819);
        $.manage().window().maximize();
        return $;
    }

    private static UgamelaSession login(WebDriver $) {
        Credentials credentials = new Credentials();
        UgamelaSession session = new UgamelaSession($);
        if (!session.isLoggedIn())
            session.login(credentials.login, credentials.password);
        return session;
    }

    private static void farmFromSpyReports(UgamelaSession session) {
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
    private static void farmFromSpyReport(SpyReports.SpyReport spyReport) {
        long capacity = 125000;
        long minimumShips = 200;
        String shipName = "Mega transporter";

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

                chooseGivenAmountOfShips((int) shipsAmount, shipName, fleet1)
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

    public static Fleet2 chooseGivenAmountOfShips(int shipAmount, String shipName, Fleet1 fleet) {
        Optional<Fleet1.AvailableFleet> availableShip = fleet.availableShips()
                .stream()
                .filter(f -> shipName.equals(f.shipName()))
                .filter(f -> shipAmount <= f.shipAmount())
                .findFirst();

        if (availableShip.isEmpty())
            throw new IllegalStateException("There is not enough ships available \"" + shipName + "\" amount " + shipAmount);
        availableShip.ifPresent(availableFleet -> availableFleet.select(shipAmount));

        if (fleet.canSendFleet())
            return fleet.next();
        throw new IllegalStateException("Cannot send fleet - all slot taken");
    }


}
