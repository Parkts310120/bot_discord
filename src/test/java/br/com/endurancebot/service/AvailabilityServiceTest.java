package br.com.endurancebot.service;

import br.com.endurancebot.model.Availability;
import br.com.endurancebot.model.Race;
import br.com.endurancebot.repository.AvailabilityRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvailabilityServiceTest {

    @Test
    void usesTheRaceDateAsSeenInTheDriversTimeZone() {
        AvailabilityRepository repository =
                new AvailabilityRepository();

        AvailabilityService service =
                new AvailabilityService(
                        new TimeZoneService(),
                        repository
                );

        Race race = new Race(
                "race-1",
                "Night Race",
                Instant.parse("2026-10-02T01:00:00Z"),
                Instant.parse("2026-10-02T07:00:00Z"),
                "UTC"
        );

        Availability availability =
                service.registerAvailability(
                        race,
                        "Driver",
                        LocalTime.of(21, 0),
                        LocalTime.of(23, 0),
                        "America/Sao_Paulo"
                );

        assertEquals(
                Instant.parse("2026-10-02T00:00:00Z"),
                availability.getStart()
        );
        assertEquals(
                Instant.parse("2026-10-02T02:00:00Z"),
                availability.getEnd()
        );
        assertEquals(1, service.listAvailabilities().size());
    }

    @Test
    void returnsOnlyAvailabilityForTheRequestedRace() {
        AvailabilityRepository repository =
                new AvailabilityRepository();

        AvailabilityService service =
                new AvailabilityService(
                        new TimeZoneService(),
                        repository
                );

        repository.save(availability("race-a", "Driver A"));
        repository.save(availability("race-b", "Driver B"));
        repository.save(availability("race-a", "Driver C"));

        List<Availability> raceA =
                service.listByRace("race-a");

        assertEquals(2, raceA.size());
        assertEquals(
                List.of("Driver A", "Driver C"),
                raceA.stream()
                        .map(Availability::getDriverName)
                        .toList()
        );
    }

    private Availability availability(
            String raceId,
            String driverName
    ) {
        return new Availability(
                raceId,
                driverName,
                Instant.parse("2026-10-01T10:00:00Z"),
                Instant.parse("2026-10-01T12:00:00Z"),
                "UTC"
        );
    }
}
