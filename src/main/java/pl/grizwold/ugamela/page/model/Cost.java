package pl.grizwold.ugamela.page.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Cost {
    public long metal;
    public long crystal;
    public long deuterium;
    public long energy;
    public long antimatter;

    public Cost(long antimatter) {
        this(0, 0, 0, antimatter);
    }

    public Cost(long metal, long crystal, long deuterium) {
        this(metal, crystal, deuterium, 0, 0);
    }

    public Cost(long metal, long crystal, long deuterium, long energy) {
        this(metal, crystal, deuterium, energy, 0);
    }

    public Cost(long metal, long crystal, long deuterium, long energy, long antimatter) {
        this.metal = metal;
        this.crystal = crystal;
        this.deuterium = deuterium;
        this.energy = energy;
        this.antimatter = antimatter;
    }

    public Cost add(Cost cost) {
        return new Cost(
                this.metal + cost.metal,
                this.crystal + cost.crystal,
                this.deuterium + cost.deuterium
        );
    }
}
