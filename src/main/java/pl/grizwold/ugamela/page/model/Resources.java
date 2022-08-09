package pl.grizwold.ugamela.page.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Resources {
    public long metal;
    public long crystal;
    public long deuterium;
    public long energy;
    public long antimatter;

    public Resources(long antimatter) {
        this(0, 0, 0, antimatter);
    }

    public Resources(long metal, long crystal, long deuterium) {
        this(metal, crystal, deuterium, 0, 0);
    }

    public Resources(long metal, long crystal, long deuterium, long energy) {
        this(metal, crystal, deuterium, energy, 0);
    }

    public Resources(long metal, long crystal, long deuterium, long energy, long antimatter) {
        this.metal = metal;
        this.crystal = crystal;
        this.deuterium = deuterium;
        this.energy = energy;
        this.antimatter = antimatter;
    }

    public Resources add(Resources cost) {
        return new Resources(
                this.metal + cost.metal,
                this.crystal + cost.crystal,
                this.deuterium + cost.deuterium
        );
    }
}
