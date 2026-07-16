package br.com.endurancebot.model;

public class TimeZoneOption {

    private final String label;
    private final String zoneId;
    private final String region;

    public TimeZoneOption(
            String label,
            String zoneId,
            String region
    ) {
        this.label = label;
        this.zoneId = zoneId;
        this.region = region;
    }

    public String getLabel() {
        return label;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return label + " [" + zoneId + "]";
    }
}
