package pl.grizwold.ugamela.page.model;

import lombok.AllArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class Address {
    private static final String ADDRESS_PATTERN = "\\[(?<galaxy>[\\d]+):(?<system>[\\d]+):(?<planet>[\\d]+)]"; // [1:123:3]
    public static final Address BEGINNING_OF_GALAXY = new Address("[1:1:1]");

    public final String galaxy;
    public final String system;
    public final String planet;

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
        return String.format("[%s:%s:%s]", galaxy, system, planet);
    }
}
