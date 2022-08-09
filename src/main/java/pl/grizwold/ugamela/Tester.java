package pl.grizwold.ugamela;

import lombok.extern.java.Log;
import org.openqa.selenium.WebDriver;
import pl.grizwold.ugamela.page.ResourcePanel;
import pl.grizwold.ugamela.page.model.Address;
import pl.grizwold.ugamela.page.model.Resources;
import pl.grizwold.webdriver.MultiloginWebDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log
public class Tester {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        WebDriver $ = connectToBrowser();

        UgamelaSession session = new UgamelaSession($).login();

        ResourcePanel r = new ResourcePanel(session);
//        buildings.open();
        Resources cost = new Resources(r.metal(), r.crystal(), r.deuterium());
        Resources cap = new Resources(r.metalCapacity(), r.crystalCapacity(), r.deuteriumCapacity());
//        Cost cost = buildings.upgradeCost(Buildings.Building.BUILDING_CRYSTAL_MINE, 47);
        System.out.printf("Current: metal: %s, crystal: %s, deuterium: %s%n", cost.metal, cost.crystal, cost.deuterium);
        System.out.printf("Capacit: metal: %s, crystal: %s, deuterium: %s%n", cap.metal, cap.crystal, cap.deuterium);
//        System.out.printf("metal: %s, crystal: %s, deuterium: %s%n", cost[0].metal, cost[0].crystal, cost[0].deuterium);
//        System.out.printf("missing: metal: %s, crystal: %s, deuterium: %s%n", cost[1].metal, cost[1].crystal, cost[1].deuterium);

//        System.out.println(buildings.isUpgradable(Buildings.Building.BUILDING_FUSION_POWER_PLANT));
//        ;

//        Address startAddress;
//
//        try {
//            startAddress = readAddress();
//        } catch (IOException e) {
//            e.printStackTrace();
//            startAddress = new Address("[6:344:1]");
//        }
//
//        Farming farming = new Farming();
//
//        while (true) {
//            try {
//                log.info("Starting the cycle from " + startAddress);
//                farming.farmFromSpyReports(session);
//                Galaxy.GALAXY_WAIT_TIMEOUT = 5;
//                Galaxy galaxy = new Galaxy(session).goTo(startAddress);
//                startAddress = farming.scanGalaxy(10, 30, galaxy);
//                log.info("Ended the cycle on " + startAddress);
//                saveAddress(startAddress);
//            } catch (Exception e) {
//                log.severe(e.getMessage());
//                e.printStackTrace();
//                log.info("########################");
//                Thread.sleep(10000);
//            }
//        }

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
        $ = multiloginWebDriver.apply(28326);
        $.manage().window().maximize();
        return $;
    }
}
