package pl.grizwold.ugamela;

import com.google.gson.Gson;
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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;

@Log
public class Tester {

    public static void main(String[] args) throws IOException, URISyntaxException {
        WebDriver $;
//        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-debugging-port=32003");
//        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.99 Safari/537.36");
//        $ = new ChromeDriver(options);

        String profileId = "cc0b9187-c4e5-4e1f-8b52-29804bca47c7";
        String runningProfileAutomationUrl = getRunningProfileAutomationUrl(profileId);
        if (runningProfileAutomationUrl.equals("Profile " + profileId + " is not running or not automated"))
            runningProfileAutomationUrl = startProfile(profileId);
        $ = new RemoteWebDriver(new URI(runningProfileAutomationUrl).toURL(), new ChromeOptions());
        $.manage().window().maximize();

        Credentials credentials = new Credentials();
        UgamelaSession session = new UgamelaSession($);
        if (!session.isLoggedIn())
            session.login(credentials.login, credentials.password);

        farmFromSpyReports(session);

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

    private static void farmFromSpyReports(UgamelaSession session) {
        SpyReports spyReports = new SpyReports(session).open();
        do {
            Optional<SpyReports.SpyReport> latestReport = spyReports.latest();
            if (latestReport.isPresent()) {
                SpyReports.SpyReport report = latestReport.get();
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

    private static void farmFromSpyReport(SpyReports.SpyReport spyReport) {
        long capacity = 125000;
        long minimumShips = 200;
        String shipName = "Mega transporter";

        long resourcesSum = (spyReport.metal() + spyReport.cristal() + spyReport.deuterium());
        long loot = resourcesSum / 2;
        long shipsAmount = loot / capacity;

        do {
            log.info(String.format("Sending %d of %s ships on attack mission", shipsAmount, shipName));

            Fleet1 fleet1 = spyReport.attack();

            chooseGivenAmountOfShips((int) shipsAmount, shipName, fleet1)
                    .next()
                    .selectMission("Attack")
                    .next();

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

        return fleet.next();
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
