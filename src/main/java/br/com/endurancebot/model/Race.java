package br.com.endurancebot.model;

import java.time.Instant;

public class Race {

    private final String id;
    private final String name;
    private final Instant start;
    private final Instant end;
    private final String officialTimeZone;

    public Race(
            String id,
            String name,
            Instant start,
            Instant end,
            String officialTimeZone
    ) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.officialTimeZone = officialTimeZone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public String getOfficialTimeZone() {
        return officialTimeZone;
    }
}
