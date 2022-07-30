package pl.grizwold.ugamela.page.model;

public class MissionInfo {
    public final Status status;

    public MissionInfo(Status status) {
        this.status = status;
    }

    public enum Status {
        SENT,
        LIMIT_REACHED,
        NOT_ENOUGH_PROBES
    }
}
