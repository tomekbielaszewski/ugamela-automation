package pl.grizwold.pageobj;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pl.grizwold.pageobj.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class SpyReports extends Page {
    private static final String SPY_REPORTS_PAGE = "messages.php?mode=show&messcat=0";
    private static final String MESSAGES_SELECTOR = "#msgCont > tbody > tr > td.msgrow.tleft";

    public SpyReports(WebDriver $) {
        super($);
        if (!onUrlEndingWith(SPY_REPORTS_PAGE)) {
            open(SPY_REPORTS_PAGE);
        }
    }

    public List<SpyReport> all() {
        validateState();
        List<WebElement> spyReports = $.findElements(By.cssSelector(MESSAGES_SELECTOR));
        return spyReports.stream()
                .map(SpyReport::new)
                .collect(Collectors.toList());
    }

    public Optional<SpyReport> latest() {
        return all().stream().findFirst();
    }

    private void validateState() {
        assertTrue($.getCurrentUrl().endsWith(SPY_REPORTS_PAGE));
    }

    public class SpyReport {
        private static final String ADDRESS_SELECTOR = "table.sth.nohide > tbody > tr > td > span > a.orange";
        private static final String METAL_SELECTOR = "table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2)";
        private static final String CRISTAL_SELECTOR = "table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(5)";
        private static final String DEUTERIUM_SELECTOR = "table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(2)";
        private static final String FLEET_SELECTOR = "";
        private static final String DEFENCE_SELECTOR = "";
        private static final String ATTACK_LINK_SELECTOR = "table:nth-child(9) > tbody > tr > td > a";

        private final WebElement spyReport;
        private final Address address;

        public SpyReport(WebElement spyReport) {
            this.spyReport = spyReport;
            this.address = new Address(spyReport.findElement(By.cssSelector(ADDRESS_SELECTOR)).getText());
        }

        public Fleet1 attack() {
            String attackSubpageLink = this.spyReport.findElement(By.cssSelector(ATTACK_LINK_SELECTOR)).getAttribute("href");
            open(attackSubpageLink);
            return new Fleet1(SpyReports.this.$);
        }

        public boolean hasFleet() {
            return false;
        }

        public boolean hasDefence() {
            return false;
        }

        public long metal() {
            String metalStr = this.spyReport.findElement(By.cssSelector(METAL_SELECTOR)).getText();
            metalStr = metalStr.replaceAll("\\.", "");
            return Long.parseLong(metalStr);
        }

        public long cristal() {
            String cristalStr = this.spyReport.findElement(By.cssSelector(CRISTAL_SELECTOR)).getText();
            cristalStr = cristalStr.replaceAll("\\.", "");
            return Integer.parseInt(cristalStr);
        }

        public long deuterium() {
            String deuteriumStr = this.spyReport.findElement(By.cssSelector(DEUTERIUM_SELECTOR)).getText();
            deuteriumStr = deuteriumStr.replaceAll("\\.", "");
            return Integer.parseInt(deuteriumStr);
        }

        public boolean defenceRowVisible() {
            return true;
        }

        public boolean fleetRowVisible() {
            return true;
        }

        public Address address() {
            return address;
        }
    }
}
