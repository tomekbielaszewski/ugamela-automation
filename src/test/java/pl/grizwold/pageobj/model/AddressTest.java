package pl.grizwold.pageobj.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AddressTest {

    @Test
    public void addressTest() {
        String addressText = "[123:456:789]";
        Address address = new Address(addressText);

        assertThat(address.getGalaxy(), is(123));
        assertThat(address.getSystem(), is(456));
        assertThat(address.getPlanet(), is(789));
    }
}
