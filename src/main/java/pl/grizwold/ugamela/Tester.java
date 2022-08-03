package pl.grizwold.ugamela;

import lombok.extern.java.Log;
import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.page.Galaxy;
import pl.grizwold.ugamela.page.model.Address;
import pl.grizwold.ugamela.routines.Farming;
import pl.grizwold.webdriver.MultiloginWebDriver;

import java.io.IOException;
import java.net.URISyntaxException;

@Log
public class Tester {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        WebDriver $ = connectToBrowser();

        UgamelaSession session = new UgamelaSession($).login();

        Address startAddress = new Address("[1:60:1]");
        Farming farming = new Farming();

        while (true) {
            farming.farmFromSpyReports(session);
            Galaxy.GALAXY_WAIT_TIMEOUT = 5;
            Galaxy galaxy = new Galaxy(session).goTo(startAddress);
            startAddress = farming.scanGalaxy(10, 30, galaxy);
            log.info("Restarting the cycle from " + startAddress);
        }

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

    private static WebDriver connectToBrowser() {
        WebDriver $;

        String profileId = "5973191a-25d3-4047-b5d1-0803344b965f";
        MultiloginWebDriver multiloginWebDriver = new MultiloginWebDriver(profileId);
//        $ = multiloginWebDriver.get();
        $ = multiloginWebDriver.apply(51819);
        $.manage().window().maximize();
        return $;
    }
}
