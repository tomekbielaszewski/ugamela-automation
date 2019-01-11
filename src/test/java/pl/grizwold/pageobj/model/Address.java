package pl.grizwold.pageobj.model;

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

    private int planet;
    private int system;
    private int galaxy;

    public Address(String address) {
        Pattern pattern = Pattern.compile(ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(address);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Planet address " + address + " does not match address pattern");
        }

        String _galaxy = matcher.group("galaxy");
        String _system = matcher.group("system");
        String _planet = matcher.group("planet");

        this.galaxy = Integer.parseInt(_galaxy);
        this.system = Integer.parseInt(_system);
        this.planet = Integer.parseInt(_planet);
    }
}
