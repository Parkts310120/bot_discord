package br.com.endurancebot.service;

import br.com.endurancebot.model.Availability;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeZoneService {

    public Availability createAvailability(
            String raceId,
            String driverName,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String timeZone
    ) {
        ZoneId driverZone = ZoneId.of(timeZone);

        ZonedDateTime localStart =
                ZonedDateTime.of(date, startTime, driverZone);

        ZonedDateTime localEnd =
                ZonedDateTime.of(date, endTime, driverZone);

        if (!endTime.isAfter(startTime)) {
            localEnd = localEnd.plusDays(1);
        }

        return new Availability(
                raceId,
                driverName,
                localStart.toInstant(),
                localEnd.toInstant(),
                timeZone
        );
    }
}
