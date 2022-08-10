package pl.grizwold.ugamela;

import lombok.extern.java.Log;
import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.page.Buildings;
import pl.grizwold.ugamela.page.Galaxy;
import pl.grizwold.ugamela.page.PlanetChooser;
import pl.grizwold.ugamela.page.ResourcePanel;
import pl.grizwold.ugamela.page.model.Address;
import pl.grizwold.ugamela.page.model.Resources;
import pl.grizwold.ugamela.routines.Economy;
import pl.grizwold.ugamela.routines.Farming;
import pl.grizwold.ugamela.routines.FleetMissions;
import pl.grizwold.webdriver.MultiloginWebDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log
public class Tester {
    private static final String MOTHERLAND = "Sol";
    private static final String[] COLONIES = new String[]{
            "Mercurius",
            "Venus",
            "Terra",
            "Mars",
            "Neptunus",
            "Uranus",
            "Jupiter",
            "Saturnus",
            "Pluto"
    };

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        WebDriver $ = connectToBrowser();

        UgamelaSession session = new UgamelaSession($).login();

//        farmWholeGalaxy(session);

        new Economy().collectResourcesFromColonies(session, "Mega transporter", MOTHERLAND, COLONIES);
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
                log.severe(e.getMessage());
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
