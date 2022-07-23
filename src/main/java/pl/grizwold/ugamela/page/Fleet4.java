package pl.grizwold.ugamela.page;

public class Fleet4 extends Page {
    private static final String FLEET_4_PAGE = "fleet3.php";

    public Fleet4(Fleet3 parent) {
        super(parent.session);
    }

    private void validateState() {
        String currentUrl = $().getCurrentUrl();
        if (currentUrl.endsWith(FLEET_4_PAGE))
            return;
        throw new IllegalStateException("Not on fleet4 page.\nCurrent page: " + currentUrl + "\nExpected page: " + FLEET_4_PAGE);
    }
}
