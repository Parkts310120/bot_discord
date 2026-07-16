package br.com.endurancebot.repository;

import br.com.endurancebot.model.Availability;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityRepository {

    private final List<Availability> availabilities;

    public AvailabilityRepository() {
        this.availabilities = new ArrayList<>();
    }

    public void save(Availability availability) {
        availabilities.add(availability);
    }

    public List<Availability> findAll() {
        return new ArrayList<>(availabilities);
    }

    public Availability findByDriverName(String driverName) {
        for (Availability availability : availabilities) {
            if (availability.getDriverName()
                    .equalsIgnoreCase(driverName)) {
                return availability;
            }
        }

        return null;
    }

    public List<Availability> findByRaceId(String raceId) {
        List<Availability> result = new ArrayList<>();

        for (Availability availability : availabilities) {
            if (availability.getRaceId().equals(raceId)) {
                result.add(availability);
            }
        }

        return result;
    }
}
