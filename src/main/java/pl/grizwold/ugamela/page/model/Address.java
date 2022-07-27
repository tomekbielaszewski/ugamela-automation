package pl.grizwold.ugamela.page.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
@AllArgsConstructor
public class Address {
    private static final String ADDRESS_PATTERN = "\\[(?<galaxy>[\\d]+):(?<system>[\\d]+):(?<planet>[\\d]+)]";

    private String planet;
    private String system;
    private String galaxy;

    public Address(String address) {
        Pattern pattern = Pattern.compile(ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(address);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Planet address " + address + " does not match address pattern");
        }

        this.galaxy = matcher.group("galaxy");
        this.system = matcher.group("system");
        this.planet = matcher.group("planet");
    }

    public String toString() {
        return String.format("%s:%s:%s", galaxy, system, planet);
    }
}
