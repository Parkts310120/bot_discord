package br.com.endurancebot.repository;

import br.com.endurancebot.model.Availability;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AvailabilityRepositoryTest {

    @Test
    void findsDriverCaseInsensitivelyAndFiltersByRace() {
        AvailabilityRepository repository = new AvailabilityRepository();

        repository.save(availability("race-a", "Joao"));
        repository.save(availability("race-b", "Maria"));

        Availability found = repository.findByDriverName("JOAO");
        List<Availability> raceA = repository.findByRaceId("race-a");

        assertNotNull(found);
        assertEquals("Joao", found.getDriverName());
        assertEquals(1, raceA.size());
        assertEquals("race-a", raceA.getFirst().getRaceId());
    }

    @Test
    void findAllReturnsADefensiveListCopy() {
        AvailabilityRepository repository = new AvailabilityRepository();
        repository.save(availability("race-a", "Joao"));

        List<Availability> snapshot = repository.findAll();
        snapshot.clear();

        assertEquals(1, repository.findAll().size());
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
