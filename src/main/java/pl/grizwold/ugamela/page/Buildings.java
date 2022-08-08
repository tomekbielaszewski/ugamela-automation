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
        BUILDING_METAL_MINE("El_1"),
        BUILDING_CRYSTAL_MINE("El_2"),
        BUILDING_DEUTERIUM_EXTRACTOR("El_3"),
        BUILDING_SOLAR_PLANT("El_4"),
        BUILDING_FUSION_POWER_PLANT("El_12"),
        BUILDING_ROBOT_FACTORY("El_14"),
        BUILDING_NANITES_FACTORY("El_15"),
        BUILDING_DOCKS("El_21"),
        BUILDING_METAL_STORAGE("El_22"),
        BUILDING_CRISTAL_STORAGE("El_23"),
        BUILDING_DEUTERIUM_STORAGE("El_24"),
        BUILDING_LAB("El_31"),
        BUILDING_TERRAFORMER("El_33"),
        BUILDING_ALLY_DEPOSIT("El_34"),
        BUILDING_ROCKETS("El_44"),
        BUILDING_QUANTUM_GATE("El_50");

        final String tab;

        final String info;

        Building(String id) {
            this.tab = "ss" + id;
            this.info = "nfo" + id;
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

    public Cost[] cost(Building building) {
        select(building);
        Optional<Cost> optionalCost = $().findElements(By.id(building.info))
                .stream().findFirst()
                .map(el -> el.findElements(By.className("infoResDiv")).stream()
                        .map(this::infoResourceElementToCost)
                        .reduce(new Cost(), Cost::add));
        Optional<Cost> optionalMissingResources = $().findElements(By.id(building.info))
                .stream().findFirst()
                .map(el -> el.findElements(By.className("infoResDiv")).stream()
                        .map(this::missingResourcesElementToCost)
                        .reduce(new Cost(), Cost::add));
        if(optionalCost.isEmpty()) {
            throw new IllegalStateException("Couldn't get cost of " + building.name());
        }
        return new Cost[] {
                optionalCost.get(),
                optionalMissingResources.orElse(new Cost())
        };
    }

    private Cost infoResourceElementToCost(WebElement infoResourceDiv) {
        Optional<Long> optAmount = extractAmountFromResourceCostElement(infoResourceDiv.findElements(By.tagName("span")).stream().findFirst());
        Cost cost = new Cost();
        if(optAmount.isEmpty())
            throw new IllegalStateException("Couldn't extract cost from the building info tab");
        if(isMetalInfo(infoResourceDiv)) {
            cost.metal = optAmount.get();
        }
        if(isCristalInfo(infoResourceDiv)) {
            cost.crystal = optAmount.get();
        }
        if(isDeuteriumInfo(infoResourceDiv)) {
            cost.deuterium = optAmount.get();
        }
        return cost;
    }

    private Cost missingResourcesElementToCost(WebElement infoResourceDiv) {
        Optional<Long> optAmountMissing = extractAmountFromResourceCostElement(infoResourceDiv.findElements(By.cssSelector("span:last-child")).stream().findFirst());
        Cost costMissing = new Cost();
        if(isMetalInfo(infoResourceDiv)) {
            costMissing.metal = optAmountMissing.orElse(0L);
        }
        if(isCristalInfo(infoResourceDiv)) {
            costMissing.crystal = optAmountMissing.orElse(0L);
        }
        if(isDeuteriumInfo(infoResourceDiv)) {
            costMissing.deuterium = optAmountMissing.orElse(0L);
        }
        return costMissing;
    }

    private boolean isMetalInfo(WebElement infoResourceDiv) {
        return infoResourceDiv.findElements(By.className("infoRes_metal")).size() > 0;
    }

    private boolean isCristalInfo(WebElement infoResourceDiv) {
        return infoResourceDiv.findElements(By.className("infoRes_crystal")).size() > 0;
    }

    private boolean isDeuteriumInfo(WebElement infoResourceDiv) {
        return infoResourceDiv.findElements(By.className("infoRes_deuterium")).size() > 0;
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
