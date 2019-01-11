package pl.grizwold.pageobj;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class Messages {
    private static final String MESSAGES_SELECTOR = "#msgCont > tbody > tr > td.msgrow.tleft";

    private final WebDriver $;

    public Messages(WebDriver $) {
        this.$ = $;
    }

    public List<SpyReport> spyReports() {
        validateState();
        List<WebElement> spyReports = $.findElements(By.cssSelector(MESSAGES_SELECTOR));
        return spyReports.stream()
                .map(SpyReport::new)
                .collect(Collectors.toList());
    }

    private void validateState() {
        assertTrue($.getCurrentUrl().endsWith("messages.php?mode=show&messcat=0"));
    }

    public class SpyReport {
        private static final String METAL_SELECTOR = "table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2)";
        private static final String CRISTAL_SELECTOR = "";
        private static final String DEUTERIUM_SELECTOR = "";
        private static final String DEFENCE_SELECTOR = "";
        private static final String FLEET_SELECTOR = "";
        private static final String ATTACK_LINK_SELECTOR = "";
        private WebElement spyReport;

        public SpyReport(WebElement spyReport) {
            this.spyReport = spyReport;
        }

        public Fleet1 attack() {
            return new Fleet1(Messages.this.$);
        }

        public boolean hasFleet() {
            return false;
        }

        public boolean hasDefence() {
            return false;
        }

        public int metal() {
            return 0;
        }

        public int cristal() {
            return 0;
        }

        public int deuterium() {
            return 0;
        }
    }
}
