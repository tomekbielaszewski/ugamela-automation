package pl.grizwold.ugamela;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.page.Galaxy;
import pl.grizwold.ugamela.page.model.Address;
import pl.grizwold.ugamela.routines.Farming;
import pl.grizwold.webdriver.MultiloginWebDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class Tester {
    private static final String MOTHERLAND = "Sol";
    private static final String[] COLONIES = new String[]{
            /*0*/ "Mercurius",
            /*1*/ "Venus",
            /*2*/ "Terra",
            /*3*/ "Mars",
            /*4*/ "Neptunus",
            /*5*/ "Uranus",
            /*6*/ "Jupiter",
            /*7*/ "Saturnus",
            /*8*/ "Pluto"
    };

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        WebDriver $ = connectToBrowser();

        UgamelaSession session = new UgamelaSession($).login();

        farmWholeGalaxy(session);

//        new Economy().collectResourcesFromColonies(session, "Mega transporter", MOTHERLAND, COLONIES);

//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[8], Buildings.Building.BUILDING_SOLAR_PLANT, 27, 30, session);
//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[8], Buildings.Building.BUILDING_CRYSTAL_MINE, 25, 30, session);
//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[8], Buildings.Building.BUILDING_METAL_MINE, 27, 30, session);

//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[3], Buildings.Building.BUILDING_LAB, 17, 19, session);
//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[4], Buildings.Building.BUILDING_LAB, 1, 19, session);
//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[5], Buildings.Building.BUILDING_LAB, 1, 19, session);
//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[6], Buildings.Building.BUILDING_LAB, 1, 19, session);
//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[7], Buildings.Building.BUILDING_LAB, 1, 19, session);
//        new Economy().sendResourcesForBuildingConstruction(MOTHERLAND, COLONIES[8], Buildings.Building.BUILDING_LAB, 1, 19, session);

//        for (String colony : COLONIES) {
//            new FleetMissions().transport(MOTHERLAND, colony, new Resources(0,0,10), session);
//        }
    }

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
                log.error(e.getMessage());
                e.printStackTrace();
                log.info("########################");
                Thread.sleep(10000);
            }
        }
    }

    private static void saveAddress(Address startAddress) throws IOException {
        Path path = Paths.get("./address");
        if(!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, startAddress.toString());
    }

    private static Address readAddress() throws IOException {
        String file = Files.readString(Paths.get("./address"));
        return new Address(file);
    }

    private static WebDriver connectToBrowser() {
        WebDriver $;

        String profileId = "5973191a-25d3-4047-b5d1-0803344b965f";
        MultiloginWebDriver multiloginWebDriver = new MultiloginWebDriver(profileId);
//        $ = multiloginWebDriver.get();
        $ = multiloginWebDriver.apply(35271);
        $.manage().window().maximize();
//        throw new IllegalStateException();
        return $;
    }
}
