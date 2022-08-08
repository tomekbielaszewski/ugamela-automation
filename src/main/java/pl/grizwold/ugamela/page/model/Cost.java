package pl.grizwold.ugamela.page.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Cost {
    public long metal;
    public long crystal;
    public long deuterium;

    public Cost add(Cost cost) {
        return new Cost(
                this.metal + cost.metal,
                this.crystal + cost.crystal,
                this.deuterium + cost.deuterium
        );
    }
}
