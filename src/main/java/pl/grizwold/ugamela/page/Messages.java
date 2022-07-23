package pl.grizwold.ugamela.page;

import pl.grizwold.ugamela.UgamelaSession;

public class Messages extends Page {
    private static final String MESSAGES_PAGE = "messages.php";

    public Messages(UgamelaSession session) {
        super(session);
        if (!isUrlEndingWith(MESSAGES_PAGE)) {
            open(MESSAGES_PAGE);
        }
    }

    public SpyReports spyReports() {
        return new SpyReports(session);
    }
}
