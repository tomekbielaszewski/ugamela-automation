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

    public boolean isLargerThan(Resources than) {
        return this.metal > than.metal &&
                this.crystal > than.crystal &&
                this.deuterium > than.deuterium &&
                this.energy > than.energy &&
                this.antimatter > than.antimatter;
    }

    public Resources add(Resources add) {
        return new Resources(
                this.metal + add.metal,
                this.crystal + add.crystal,
                this.deuterium + add.deuterium
        );
    }

    public Resources subtract(Resources sub) {
        return new Resources(
                this.metal - sub.metal,
                this.crystal - sub.crystal,
                this.deuterium - sub.deuterium
        ).normalize();
    }

    public Resources subtractAllowingNegatives(Resources sub) {
        return new Resources(
                this.metal - sub.metal,
                this.crystal - sub.crystal,
                this.deuterium - sub.deuterium
        );
    }

    public long requiredCargo() {
        return metal + crystal + deuterium;
    }

    @Override
    public String toString() {
        if (antimatter > 0)
            return String.format("antimatter: %s", antimatter);
        if (energy > 0)
            return String.format("metal: %s | crystal %s | deuterium %s | energy %s", metal, crystal, deuterium, energy);
        return String.format("metal: %s | crystal %s | deuterium %s", metal, crystal, deuterium);
    }

    private Resources normalize() {
        return new Resources(
                this.metal < 0 ? 0 : this.metal,
                this.crystal < 0 ? 0 : this.crystal,
                this.deuterium < 0 ? 0 : this.deuterium,
                this.energy < 0 ? 0 : this.energy,
                this.antimatter < 0 ? 0 : this.antimatter
        );
    }
}
