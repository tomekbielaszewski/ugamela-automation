package pl.grizwold.pageobj;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Messages {
    private final WebDriver $;

    public Messages(WebDriver $) {
        this.$ = $;
    }

    public List<SpyReport> spyReports() {
        return null;
    }

    public class SpyReport {
        private WebElement spyReport;

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

        public int deuter() {
            return 0;
        }
    }
}
