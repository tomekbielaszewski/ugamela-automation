package pl.grizwold.pageobj;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Menu {
    private final WebDriver $;

    public Menu(WebDriver webDriver) {
        this.$ = webDriver;
    }

    public void overview() {
        if (notOnPage("overview.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(3) > td > a")).click();
    }

    public void empire() {
        if (notOnPage("empire.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(4) > td > a")).click();
    }

    public void buildings() {
        if (notOnPage("buildings.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(5) > td > a")).click();
    }

    public void resources() {
        if (notOnPage("resources.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(6) > td > a")).click();
    }

    public void research() {
        if (notOnPage("buildings.php?mode=research"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(7) > td > a")).click();
    }

    public void shipyard() {
        if (notOnPage("buildings.php?mode=fleet"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(8) > td > a")).click();
    }

    public Fleet1 fleet() {
        if (notOnPage("fleet.php")) {
            $.get("https://www.ugamela.pl/s1/fleet.php");
        }
        return new Fleet1(this.$);
    }

    public void technology() {
        if (notOnPage("techtree.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(10) > td > a")).click();
    }

    public void galaxy() {
        if (notOnPage("galaxy.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(11) > td > a")).click();
    }

    public void defence() {
        if (notOnPage("buildings.php?mode=defense"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(12) > td > a")).click();
    }

    public void teleport() {
        if (notOnPage("infos.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(13) > td > a")).click();
    }

    public void alliance() {
        if (notOnPage("alliance.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(15) > td > a")).click();
    }

    public void search() {
        if (notOnPage("search.php"))
            $.findElement(By.cssSelector("body > div:nth-child(7) > div.pf.l0.fl.w2.z1.style > table > tbody > tr:nth-child(16) > td > a")).click();
    }

    public Messages messages() {
        if (notOnPage("messages.php")) {
            $.findElement(By.cssSelector("#lm_msg")).click();
        }
        return new Messages($);
    }

    private boolean notOnPage(String page) {
        return !$.getCurrentUrl().contains(page);
    }
}
