package br.com.endurancebot.model;

import java.time.Instant;

public class Availability {

    private final String raceId;
    private final String driverName;
    private final Instant start;
    private final Instant end;
    private final String originalTimeZone;

    public Availability(
            String raceId,
            String driverName,
            Instant start,
            Instant end,
            String originalTimeZone
    ) {
        this.raceId = raceId;
        this.driverName = driverName;
        this.start = start;
        this.end = end;
        this.originalTimeZone = originalTimeZone;
    }

    public String getRaceId() {
        return raceId;
    }

    public String getDriverName() {
        return driverName;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public String getOriginalTimeZone() {
        return originalTimeZone;
    }
}
