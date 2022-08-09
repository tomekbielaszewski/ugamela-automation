package pl.grizwold.ugamela.page;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.model.Cost;

import java.util.Optional;
import java.util.Scanner;

public class Buildings extends Page {
    private static final String BUILDINGS_PAGE = "buildings.php";

    public enum Building {
        BUILDING_METAL_MINE("El_1", 1.5, new Cost(60, 15, 0)),
        BUILDING_CRYSTAL_MINE("El_2", 1.6, new Cost(48, 24, 0)),
        BUILDING_DEUTERIUM_EXTRACTOR("El_3", 1.5, new Cost(225, 75, 0)),
        BUILDING_SOLAR_PLANT("El_4", 1.5, new Cost(75, 30, 0)),
        BUILDING_FUSION_POWER_PLANT("El_12", 1.8, new Cost(900, 360, 180)),
        BUILDING_ROBOT_FACTORY("El_14", 2, new Cost(400, 120, 200)),
        BUILDING_NANITES_FACTORY("El_15", 2, new Cost(1000000, 500000, 100000)),
        BUILDING_DOCKS("El_21", 2, new Cost(400, 200, 100)),
        BUILDING_METAL_STORAGE("El_22", 2, new Cost(2000, 0, 0)),
        BUILDING_CRISTAL_STORAGE("El_23", 2, new Cost(2000, 1000, 0)),
        BUILDING_DEUTERIUM_STORAGE("El_24", 2, new Cost(2000, 2000, 0)),
        BUILDING_LAB("El_31", 2, new Cost(200, 400, 200)),
        BUILDING_TERRAFORMER("El_33", 2, new Cost(0, 50000, 100000, 1000)),
        BUILDING_ALLY_DEPOSIT("El_34", 2, new Cost(20000, 40000, 0)),
        BUILDING_ROCKETS("El_44", 2, new Cost(20000, 20000, 1000)),
        BUILDING_QUANTUM_GATE("El_50", 2, new Cost(20));

        final String tab;
        final String info;
        final double factor;
        final Cost initialCost;

        Building(String id, double factor, Cost initialCost) {
            this.tab = "ss" + id;
            this.info = "nfo" + id;
            this.factor = factor;
            this.initialCost = initialCost;
        }
    }

    public Buildings(UgamelaSession session) {
        super(session);
        if (!isUrlEndingWith(BUILDINGS_PAGE)) {
            open(BUILDINGS_PAGE);
        }
    }

    public int getLevel(Building building) {
        WebElement buildingTab = getBuildingThumbnail(building);
        WebElement lvlContainer = buildingTab.findElement(By.cssSelector("div:nth-child(4)"));
        return Integer.parseInt(lvlContainer.getText());
    }

    public Cost upgradeCost(Building building, int toLevel) {
        return upgradeCost(building.initialCost, toLevel, building.factor);
    }

    public Cost upgradeCost(Cost startCost, int toLevel, double factor) {
        return upgradeCost(startCost, toLevel, toLevel, factor);
    }

    public Cost upgradeCost(Cost startCost, int fromLevel, int toLevel, double factor) {
        return new Cost(
                (long)(startCost.metal * Math.pow(factor, fromLevel - 1) * (Math.pow(factor, (toLevel - fromLevel + 1)) - 1) / (factor - 1)),
                (long)(startCost.crystal * Math.pow(factor, fromLevel - 1) * (Math.pow(factor, (toLevel - fromLevel + 1)) - 1) / (factor - 1)),
                (long)(startCost.deuterium * Math.pow(factor, fromLevel - 1) * (Math.pow(factor, (toLevel - fromLevel + 1)) - 1) / (factor - 1)),
                (long)(startCost.energy * Math.pow(factor, fromLevel - 1) * (Math.pow(factor, (toLevel - fromLevel + 1)) - 1) / (factor - 1)),
                (long)(startCost.antimatter * Math.pow(factor, fromLevel - 1) * (Math.pow(factor, (toLevel - fromLevel + 1)) - 1) / (factor - 1))
        );
    }

    public int getLevelsInQueue(Building building) {
        WebElement buildingTab = getBuildingThumbnail(building);
        int size = buildingTab.findElements(By.cssSelector("div:nth-child(5)")).size();
        if (size == 0) return 0;
        WebElement lvlContainer = buildingTab.findElement(By.cssSelector("div:nth-child(5)"));
        return Optional.ofNullable(lvlContainer.getText())
                .filter(StringUtils::isNoneBlank)
                .map(Integer::parseInt)
                .orElse(0);
    }

    public int getTotalLevel(Building building) {
        return getLevel(building) + getLevelsInQueue(building);
    }

    public boolean isUpgradable(Building building) {
        WebElement buildingTab = getBuildingThumbnail(building);
        return buildingTab.findElements(By.className("buildDo_Green")).size() > 0;
    }

    public boolean isTooExpensive(Building building) {
        WebElement buildingTab = getBuildingThumbnail(building);
        return buildingTab.findElements(By.className("buildDo_Orange")).size() > 0;
    }

    private WebElement getBuildingThumbnail(Building building) {
        return $().findElement(By.id(building.tab));
    }

    private WebElement getBuildingInfo(Building building) {
        return $().findElement(By.id(building.info));
    }

    public Buildings select(Building building) {
        getBuildingThumbnail(building).click();
        return this;
    }

    public boolean isSelected(Building building) {
        return getBuildingThumbnail(building).findElements(By.className(".ssSelect")).size() > 0;
    }

    public boolean upgrade(Building building) {
        if (isUpgradable(building)) {
            getBuildingThumbnail(building).findElement(By.cssSelector(".ssDo"))
                    .click();
            return true;
        }
        return false;
    }

    private Optional<Long> extractAmountFromResourceCostElement(Optional<WebElement> resourceCost) {
        return resourceCost.map(WebElement::getText)
                .map(t -> t.replaceAll("\\.", ""))
                .map(t -> t.replaceAll("\\(-", ""))
                .map(t -> t.replaceAll("\\)", ""))
                .filter(t -> new Scanner(t).hasNextLong())
                .map(t -> new Scanner(t).nextLong());
    }
}
