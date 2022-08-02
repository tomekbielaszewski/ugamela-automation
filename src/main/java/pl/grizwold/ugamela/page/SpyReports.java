package pl.grizwold.ugamela.page;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pl.grizwold.ugamela.UgamelaSession;
import pl.grizwold.ugamela.page.model.Address;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public class SpyReports extends Page {
    private static final String MESSAGES_PAGE = "messages.php";
    private static final String SPY_REPORTS_PAGE = "messages.php?mode=show&messcat=0";
    private static final String SPY_REPORTS_PAGE_CATEGORY = "messcat=0";
    private static final String MESSAGES_SELECTOR = "#msgCont > tbody > tr > td.msgrow.tleft";
    private static final String DELETE_MESSAGE_SELECTOR = "#msgCont > tbody > tr > td > span.fRigh > span:nth-child(3) > a.delete";

    public SpyReports(UgamelaSession session) {
        super(session);
    }

    public SpyReports open() {
        if (!isUrlLike(SPY_REPORTS_PAGE)) {
            open(SPY_REPORTS_PAGE);
        }
        return this;
    }

    public List<SpyReport> all() {
        validateState();
        List<WebElement> spyReports = $().findElements(By.cssSelector(MESSAGES_SELECTOR));
        return spyReports.stream()
                .map(s -> new SpyReport(s, this))
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
        if (isUrlLike(MESSAGES_PAGE) && isUrlLike(SPY_REPORTS_PAGE_CATEGORY))
            return;
        open();
    }

    public class SpyReport {
        private static final String ADDRESS_SELECTOR = "table.sth.nohide > tbody > tr > td > span > a.orange";
        private static final String METAL_SELECTOR = "table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2)";
        private static final String CRISTAL_SELECTOR = "table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(5)";
        private static final String DEUTERIUM_SELECTOR = "table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(2)";
        private static final String FLEET_SELECTOR = "";
        private static final String DEFENCE_SELECTOR = "";
        private static final String ATTACK_LINK_SELECTOR = "table:nth-child(9) > tbody > tr > td > a";

        private final SpyReports spyReports;
        private final WebElement spyReport;
        private final Address address;
        private final String attackLink;
        private final long metal;
        private final long cristal;
        private final long deuterium;

        public SpyReport(WebElement spyReport, SpyReports spyReports) {
            this.spyReports = spyReports;
            this.spyReport = spyReport;
            this.address = new Address(spyReport.findElement(By.cssSelector(ADDRESS_SELECTOR)).getText());
            this.attackLink = this.spyReport.findElement(By.linkText("Napadaj")).getAttribute("href");
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
            return spyReport.findElements(By.xpath(".//td[contains(text(),'Flota')]")).size() > 0;
        }

        public boolean hasFleet() {
            if(!fleetRowVisible()) return false;
            return spyReport.findElements(By.xpath(".//td[contains(text(),'Flota')]/../../child::*"))
                    .size() > 1;
        }

        public Map<String, Integer> getDefence() {
            Stream<String> names = spyReport.findElements(By.xpath(".//td[contains(.,'Obrona')]/../../child::*/td[@class='stl']")).stream().map(WebElement::getText);
            Stream<Integer> amounts = spyReport.findElements(By.xpath(".//td[contains(.,'Obrona')]/../../child::*/td[@class='stv']")).stream().map(WebElement::getText).map(n->n.replaceAll("\\.", "")).map(Integer::parseInt);
            return Streams.zip(names, amounts, Maps::immutableEntry)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        public boolean defenceRowVisible() {
            return spyReport.findElements(By.xpath(".//td[contains(text(),'Obrona')]")).size() > 0;
        }

        public boolean defenceRowEmpty() {
            return spyReport.findElements(By.xpath(".//td[contains(text(),'Obrona')]/../../child::*"))
                    .size() == 1;
        }

        public boolean hasDefence() {
            if(!defenceRowVisible()) return false;
            if(defenceRowEmpty()) return false;
            return getDefence().keySet().stream()
                    .filter(not("Rakieta międzyplanetarna"::equals))
                    .count() > 0;
        }

        public Fleet1 attack() {
            $().get(this.attackLink);
            return new Fleet1(SpyReports.this.session);
        }

        public SpyReports delete() {
            return spyReports.open()
                    .delete(this);
        }

        private long getResource(String resourceSelector) {
            String resource = this.spyReport.findElement(By.cssSelector(resourceSelector)).getText();
            resource = resource.replaceAll("\\.", "");
            return Long.parseLong(resource);
        }
    }

    private SpyReports delete(SpyReport spyReport) {
        spyReport.spyReport
                .findElement(By.xpath("../preceding-sibling::tr[1]"))
                .findElement(By.cssSelector(".delete")).click();
        return this;
    }
}
