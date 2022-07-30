package pl.grizwold.ugamela;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import pl.grizwold.ugamela.page.Fleet1;
import pl.grizwold.ugamela.page.Fleet2;
import pl.grizwold.ugamela.page.SpyReports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Log
public class Tester {

    public static void main(String[] args) throws IOException, URISyntaxException {
        WebDriver $ = connectToBrowser();

        UgamelaSession session = login($);

//        farmFromSpyReports(session);
        chooseGivenAmountOfShips(100000, "Mega transporter", new Fleet1(session));

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

    private static WebDriver connectToBrowser() throws IOException, URISyntaxException {
        WebDriver $;
//        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-debugging-port=32003");
//        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
//        $ = new ChromeDriver(options);

        String profileId = "5973191a-25d3-4047-b5d1-0803344b965f";
//        String runningProfileAutomationUrl = getRunningProfileAutomationUrl(profileId);
//        if (runningProfileAutomationUrl.equals("Profile " + profileId + " is not running or not automated"))
//            runningProfileAutomationUrl = startProfile(profileId);
        String runningProfileAutomationUrl = "http://127.0.0.1:40938";
//        String runningProfileAutomationUrl = startProfile(profileId);
        log.info("Connecting to: " + runningProfileAutomationUrl);
        ChromeOptions options = new ChromeOptions();
        $ = new RemoteWebDriver(new URI(runningProfileAutomationUrl).toURL(), options);
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
                if(report.hasDefence() || report.hasFleet()) {
                    log.info(String.format("Report %s has defense or fleet - deleting", report.address()));
                    report.delete();
                    continue;
                }
                farmFromSpyReport(report);
                spyReports.open().deleteLatest();
            }
        } while (spyReports.latest().isPresent());
//        new SpyReports(session)
//                .open()
//                .all()
//                .stream()
//                .filter(SpyReports.SpyReport::defenceRowVisible)
//                .filter(SpyReports.SpyReport::fleetRowVisible)
//                .filter(not(SpyReports.SpyReport::hasDefence))
//                .filter(not(SpyReports.SpyReport::hasFleet))
//                .forEach(Tester::farmFromSpyReport);
    }

    @SneakyThrows
    private static void farmFromSpyReport(SpyReports.SpyReport spyReport) {
        long capacity = 125000;
        long minimumShips = 200;
        String shipName = "Mega transporter";

        long resourcesSum = (spyReport.metal() + spyReport.cristal() + spyReport.deuterium());
        long loot = resourcesSum / 2;
        long shipsAmount = loot / capacity;

        if(shipsAmount == 0) {
            log.info("Ships amount to be sent is 0. Skipping this report.");
            return;
        }

        long waitingTime = 0;

        do {
            log.info(String.format("Sending %d of %s ships on attack mission to %s", shipsAmount, shipName, spyReport.address()));

            try {
                Fleet1 fleet1 = spyReport.attack();

                chooseGivenAmountOfShips((int) shipsAmount, shipName, fleet1)
                        .next()
                        .selectMission("Attack")
                        .next();
            } catch (IllegalStateException e) {
                if(waitingTime > 30) throw new IllegalStateException("Waited to long. Ships destroyed? Long missions?", e);
                if(e.getMessage().equalsIgnoreCase("Cannot send fleet - all slot taken")) {
                    log.info("All slots taken. Waiting 1min for free slot and retrying...");
                    Thread.sleep(1000 * 60);
                    log.info("Retrying now!");
                    waitingTime++;
                    continue;
                }
                if(e.getMessage().equalsIgnoreCase("There is not enough ships available \"" + shipName + "\" amount " + shipsAmount)) {
                    log.info("Not enough ships. Waiting 1min for fleet return...");
                    Thread.sleep(1000 * 60);
                    waitingTime++;
                    continue;
                }
            }

            waitingTime = 0;
            loot /= 2;
            shipsAmount = loot / capacity;
        } while (shipsAmount >= minimumShips);
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

        if(fleet.canSendFleet())
            return fleet.next();
        throw new IllegalStateException("Cannot send fleet - all slot taken");
    }

    private static String getRunningProfileAutomationUrl(String profileId) throws IOException {
        /*Send GET request to start the browser profile by profileId. Returns response in the following format:
        '{"status":"OK","value":"http://127.0.0.1:XXXXX"}', where XXXXX is the localhost port on which browser profile is
        launched. Please make sure that you have Multilogin listening port set to 35000. Otherwise please change the port
        value in the url string*/
        String url = "http://127.0.0.1:32002/api/v1/profile/selenium?profileId=" + profileId;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //Get JSON text from the response and return the value by key "value"
        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(response.toString(), HashMap.class);
        return hashMap.get("value").toString();
    }

    private static String startProfile(String profileId) throws IOException {
        /*Send GET request to start the browser profile by profileId. Returns response in the following format:
        '{"status":"OK","value":"http://127.0.0.1:XXXXX"}', where XXXXX is the localhost port on which browser profile is
        launched. Please make sure that you have Multilogin listening port set to 35000. Otherwise please change the port
        value in the url string*/
        String url = "http://127.0.0.1:32002/api/v1/profile/start?automation=true&profileId=" + profileId;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //Get JSON text from the response and return the value by key "value"
        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(response.toString(), HashMap.class);
        return hashMap.get("value").toString();
    }
}
