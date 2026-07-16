package br.com.endurancebot.service;

import br.com.endurancebot.model.Availability;
import br.com.endurancebot.model.Race;
import br.com.endurancebot.repository.AvailabilityRepository;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class AvailabilityService {

    private final TimeZoneService timeZoneService;
    private final AvailabilityRepository availabilityRepository;

    public AvailabilityService(
            TimeZoneService timeZoneService,
            AvailabilityRepository availabilityRepository
    ) {
        this.timeZoneService = timeZoneService;
        this.availabilityRepository = availabilityRepository;
    }

    public Availability registerAvailability(
            Race race,
            String driverName,
            LocalTime startTime,
            LocalTime endTime,
            String timeZone
    ) {
        ZonedDateTime raceStartInDriverZone =
                race.getStart().atZone(ZoneId.of(timeZone));

        Availability availability =
                timeZoneService.createAvailability(
                        race.getId(),
                        driverName,
                        raceStartInDriverZone.toLocalDate(),
                        startTime,
                        endTime,
                        timeZone
                );

        availabilityRepository.save(availability);

        return availability;
    }

    public List<Availability> listAvailabilities() {
        return availabilityRepository.findAll();
    }

    public List<Availability> listByRace(String raceId) {
        return availabilityRepository.findByRaceId(raceId);
    }

    public Availability findByDriverName(String driverName) {
        return availabilityRepository.findByDriverName(driverName);
    }
}
