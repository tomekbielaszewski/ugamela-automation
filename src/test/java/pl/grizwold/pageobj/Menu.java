package pl.grizwold.pageobj;

import org.openqa.selenium.WebDriver;

public class Menu {
    private final WebDriver $;

    public Menu(WebDriver webDriver) {
        this.$ = webDriver;
    }
    
    public Fleet1 fleet() {
        return new Fleet1(this.$);
    }

    public Messages messages() {
        return new Messages($);
    }
}
