package pl.grizwold.ugamela;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.page.Buildings;
import pl.grizwold.ugamela.page.Galaxy;
import pl.grizwold.ugamela.page.PlanetChooser;
import pl.grizwold.ugamela.page.ResourcePanel;
import pl.grizwold.ugamela.page.model.Address;
import pl.grizwold.ugamela.page.model.Resources;
import pl.grizwold.ugamela.routines.Farming;
import pl.grizwold.webdriver.MultiloginWebDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class TheBot {
    private static final String MOTHERLAND = "Sol";
    private static String[] COLONIES = new String[]{
            /*0*/ "Mercurius",
            /*1*/ "Venus",
            /*2*/ "Terra",
            /*3*/ "Mars",
            /*4*/ "Jupiter",
            /*5*/ "Saturnus",
            /*6*/ "Uranus",
            /*7*/ "Neptunus",
            /*8*/ "Pluto"
    };

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        WebDriver $ = connectToBrowser();
        UgamelaSession session = new UgamelaSession($)
                .selectUniversum("Universum 1")
                .login();

        new Buildings(session).open();
        new PlanetChooser(session).openPlanet(MOTHERLAND);
        Resources resourcesBefore = new ResourcePanel(session).availableResources();

        farmWholeGalaxy(session);

//        new Economy().collectResourcesFromColonies(session, "Mega transporter", MOTHERLAND, COLONIES);
//        new FleetMissions().transport(MOTHERLAND, COLONIES[8], new Resources(0, 10 * 1000000, 0), session);

//            int desiredLevel = 22;
//            String colony = COLONIES[8];
//            new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, colony, Buildings.Building.BUILDING_METAL_MINE, desiredLevel, desiredLevel, session);
//            new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, colony, Buildings.Building.BUILDING_CRYSTAL_MINE, desiredLevel, desiredLevel, session);
//            new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, colony, Buildings.Building.BUILDING_DEUTERIUM_EXTRACTOR, desiredLevel, desiredLevel, session);
//            new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, colony, Buildings.Building.BUILDING_SOLAR_PLANT, desiredLevel, desiredLevel, session);
//            new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, colony, Buildings.Building.BUILDING_LAB, desiredLevel, desiredLevel, session);

//        COLONIES = Arrays.copyOfRange(COLONIES, 6, COLONIES.length);
//        for (String colony : COLONIES) {
//            log.info("Now supplying " + colony);
//            new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, colony, Buildings.Building.BUILDING_METAL_MINE, 44, session);
//        }

//        for (String colony : COLONIES) {
//            new FleetMissions().transport(MOTHERLAND, colony, new Resources(0,25000000,0), session);
//        }

        new PlanetChooser(session).openPlanet(MOTHERLAND);
        Resources resourcesAfter = new ResourcePanel(session).availableResources();
        log.info("");
        log.info("Resources on Sol now: {}", resourcesAfter);
        log.info("Resources after bot activity: {}", resourcesAfter.subtractAllowingNegatives(resourcesBefore));
    }

    @SneakyThrows
    private static void farmWholeGalaxy(UgamelaSession session) throws InterruptedException {
        Address startAddress;

        try {
            startAddress = readAddress();
        } catch (IOException e) {
            e.printStackTrace();
            startAddress = new Address("[6:344:1]");
        }

        Farming farming = new Farming();

        while (true) {
            try {
                log.info("Starting the cycle from " + startAddress);
                farming.farmFromSpyReports(session);
                Galaxy.GALAXY_WAIT_TIMEOUT = 5;
                Galaxy galaxy = new Galaxy(session).goTo(startAddress);
                startAddress = farming.scanGalaxy(10, 30, galaxy);
                log.info("Ended the cycle on " + startAddress);
                saveAddress(startAddress);
            } catch (Exception e) {
                if (e.getMessage().equals("Nie masz wystarczającej ilości miejsca na deuter!")) {
                    Address beginningOfGalaxy = new Address("[1:1:1]");
                    saveAddress(beginningOfGalaxy);
                    startAddress = beginningOfGalaxy;
                }
                log.error(e.getMessage());
                e.printStackTrace();
                log.info("########################");
                Thread.sleep(10000);
            }
        }
    }

    private static Address readAddress() throws IOException {
        Path path = Paths.get("./address");
        if (!Files.exists(path)) {
            saveAddress(new Address("[1:1:1]"));
        }
        String contents = Files.readString(path);
        return new Address(contents);
    }

    private static void saveAddress(Address startAddress) throws IOException {
        Path path = Paths.get("./address");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, startAddress.toString());
    }

    private static int readPort() throws IOException {
        Path path = Paths.get("./webdriver-port");
        if (!Files.exists(path)) {
            savePort(10000);
        }
        String contents = Files.readString(path);
        return Integer.parseInt(contents);
    }

    private static void savePort(int port) throws IOException {
        Path path = Paths.get("./webdriver-port");
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, String.valueOf(port));
    }

    @SneakyThrows
    private static WebDriver connectToBrowser() {
        String profileId = "5973191a-25d3-4047-b5d1-0803344b965f";
        MultiloginWebDriver mla = new MultiloginWebDriver(profileId);
        int port = readPort();

        WebDriver $ = mla.connect(port)
                .orElseGet(mla::startAndConnect);

        savePort(mla.port);

        return $;
    }
}
