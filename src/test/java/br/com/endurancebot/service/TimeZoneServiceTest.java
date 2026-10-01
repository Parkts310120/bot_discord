package br.com.endurancebot.service;

import br.com.endurancebot.model.Availability;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeZoneServiceTest {

    private final TimeZoneService service = new TimeZoneService();

    @Test
    void convertsDriverLocalTimeToCanonicalInstants() {
        Availability availability = service.createAvailability(
                "race-1",
                "Driver",
                LocalDate.of(2026, 10, 1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 30),
                "America/Sao_Paulo"
        );

        assertEquals(
                Instant.parse("2026-10-01T13:00:00Z"),
                availability.getStart()
        );
        assertEquals(
                Instant.parse("2026-10-01T15:30:00Z"),
                availability.getEnd()
        );
        assertEquals(
                "America/Sao_Paulo",
                availability.getOriginalTimeZone()
        );
    }

    @Test
    void rollsOvernightIntervalIntoTheNextLocalDay() {
        Availability availability = service.createAvailability(
                "race-1",
                "Driver",
                LocalDate.of(2026, 10, 1),
                LocalTime.of(23, 30),
                LocalTime.of(1, 15),
                "Asia/Tokyo"
        );

        assertEquals(
                Instant.parse("2026-10-01T14:30:00Z"),
                availability.getStart()
        );
        assertEquals(
                Instant.parse("2026-10-01T16:15:00Z"),
                availability.getEnd()
        );
    }

    @Test
    void rejectsUnknownTimeZoneIdentifiers() {
        assertThrows(
                DateTimeException.class,
                () -> service.createAvailability(
                        "race-1",
                        "Driver",
                        LocalDate.of(2026, 10, 1),
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0),
                        "Mars/Olympus_Mons"
                )
        );
    }
}
