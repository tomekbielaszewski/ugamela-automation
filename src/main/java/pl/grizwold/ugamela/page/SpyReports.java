package pl.grizwold.ugamela.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpyReports extends Page {
    private static final String SPY_REPORTS_PAGE = "messages.php?mode=show&messcat=0";
    private static final String MESSAGES_SELECTOR = "#msgCont > tbody > tr > td.msgrow.tleft";
    private static final String DELETE_MESSAGE_SELECTOR = "#msgCont > tbody > tr > td > span.fRigh > span:nth-child(3) > a.delete";

    public SpyReports(UgamelaSession session) {
        super(session);
        if (!isUrlEndingWith(SPY_REPORTS_PAGE)) {
            open(SPY_REPORTS_PAGE);
        }
    }

    public List<SpyReport> all() {
        validateState();
        List<WebElement> spyReports = $().findElements(By.cssSelector(MESSAGES_SELECTOR));
        return spyReports.stream()
                .map(SpyReport::new)
                .collect(Collectors.toList());
    }

    public Optional<SpyReport> latest() {
        return all().stream().findFirst();
    }

    public void deleteLatest() {
        validateState();
        $().findElement(By.cssSelector(DELETE_MESSAGE_SELECTOR)).click();
    }

    private void validateState() {
        String currentUrl = $().getCurrentUrl();
        if (currentUrl.endsWith(SPY_REPORTS_PAGE))
            return;
        throw new IllegalStateException("Not on spy reports page!\nCurrent page: " + currentUrl + "\nExpected page: " + SPY_REPORTS_PAGE);
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
        private final String attackLink;
        private final long metal;
        private final long cristal;
        private final long deuterium;

        public SpyReport(WebElement spyReport) {
            this.spyReport = spyReport;
            this.address = new Address(spyReport.findElement(By.cssSelector(ADDRESS_SELECTOR)).getText());
            this.attackLink = this.spyReport.findElement(By.cssSelector(ATTACK_LINK_SELECTOR)).getAttribute("href");
            this.metal = getResource(METAL_SELECTOR);
            this.cristal = getResource(CRISTAL_SELECTOR);
            this.deuterium = getResource(DEUTERIUM_SELECTOR);
        }

        public Address address() {
            return address;
        }

        public long metal() {
            return metal;
        }

        public long cristal() {
            return cristal;
        }

        public long deuterium() {
            return deuterium;
        }

        public boolean fleetRowVisible() {
            return true;
        }

        public boolean hasFleet() {
            return false;
        }

        public boolean defenceRowVisible() {
            return true;
        }

        public boolean hasDefence() {
            return false;
        }

        public Fleet1 attack() {
            $().get(this.attackLink);
            return new Fleet1(SpyReports.this.session);
        }

        private long getResource(String resourceSelector) {
            String resource = this.spyReport.findElement(By.cssSelector(resourceSelector)).getText();
            resource = resource.replaceAll("\\.", "");
            return Long.parseLong(resource);
        }
    }
}
