package br.com.endurancebot.service;

import br.com.endurancebot.model.TimeZoneOption;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeZoneCatalogServiceTest {

    private final TimeZoneCatalogService service =
            new TimeZoneCatalogService();

    @Test
    void validatesIanaTimeZoneIdentifiers() {
        assertTrue(service.isValidZoneId("America/Sao_Paulo"));
        assertFalse(service.isValidZoneId("Not/A_Zone"));
    }

    @Test
    void findsKnownZoneAndRegionOptions() {
        TimeZoneOption saoPaulo =
                service.findByZoneId("America/Sao_Paulo");

        assertNotNull(saoPaulo);
        assertEquals("South America", saoPaulo.getRegion());

        List<TimeZoneOption> europe =
                service.findByRegion("europe");

        assertEquals(4, europe.size());
        assertTrue(
                europe.stream()
                        .allMatch(
                                option -> option.getRegion()
                                        .equals("Europe")
                        )
        );
    }

    @Test
    void findAllReturnsADefensiveListCopy() {
        List<TimeZoneOption> snapshot = service.findAll();
        int expectedSize = snapshot.size();

        snapshot.clear();

        assertEquals(expectedSize, service.findAll().size());
    }
}
