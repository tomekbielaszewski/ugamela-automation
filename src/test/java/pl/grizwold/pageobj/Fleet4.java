package pl.grizwold.pageobj;

import static org.junit.Assert.assertTrue;

public class Fleet4 extends Page {

    public Fleet4(Fleet3 parent) {
        super(parent.$);
    }

    private void validateState() {
        assertTrue($.getCurrentUrl().endsWith("fleet3.php"));
    }
}
