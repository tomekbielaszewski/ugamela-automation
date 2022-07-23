package pl.grizwold.ugamela.page;

import pl.grizwold.ugamela.UgamelaSession;

public class Menu extends Page {

    public Menu(UgamelaSession session) {
        super(session);
    }

    public Fleet1 fleet() {
        return new Fleet1(session);
    }

    public Messages messages() {
        return new Messages(session);
    }
}
