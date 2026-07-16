package br.com.endurancebot.service;

import br.com.endurancebot.model.TimeZoneOption;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimeZoneCatalogService {

    private final List<TimeZoneOption> options;

    public TimeZoneCatalogService() {
        this.options = createDefaultOptions();
    }

    public List<TimeZoneOption> findAll() {
        return new ArrayList<>(options);
    }

    public List<TimeZoneOption> findByRegion(String region) {
        List<TimeZoneOption> result = new ArrayList<>();

        for (TimeZoneOption option : options) {
            if (option.getRegion().equalsIgnoreCase(region)) {
                result.add(option);
            }
        }

        return result;
    }

    public TimeZoneOption findByZoneId(String zoneId) {
        for (TimeZoneOption option : options) {
            if (option.getZoneId().equals(zoneId)) {
                return option;
            }
        }

        return null;
    }

    public boolean isValidZoneId(String zoneId) {
        try {
            ZoneId.of(zoneId);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private List<TimeZoneOption> createDefaultOptions() {
        List<TimeZoneOption> result = new ArrayList<>();

        result.add(new TimeZoneOption(
                "UTC",
                "UTC",
                "UTC"
        ));

        result.add(new TimeZoneOption(
                "São Paulo — BRT/BRST",
                "America/Sao_Paulo",
                "South America"
        ));

        result.add(new TimeZoneOption(
                "Buenos Aires — ART",
                "America/Argentina/Buenos_Aires",
                "South America"
        ));

        result.add(new TimeZoneOption(
                "New York — EST/EDT",
                "America/New_York",
                "North America"
        ));

        result.add(new TimeZoneOption(
                "Chicago — CST/CDT",
                "America/Chicago",
                "North America"
        ));

        result.add(new TimeZoneOption(
                "Los Angeles — PST/PDT",
                "America/Los_Angeles",
                "North America"
        ));

        result.add(new TimeZoneOption(
                "London — GMT/BST",
                "Europe/London",
                "Europe"
        ));

        result.add(new TimeZoneOption(
                "Brussels — CET/CEST",
                "Europe/Brussels",
                "Europe"
        ));

        result.add(new TimeZoneOption(
                "Paris — CET/CEST",
                "Europe/Paris",
                "Europe"
        ));

        result.add(new TimeZoneOption(
                "Berlin — CET/CEST",
                "Europe/Berlin",
                "Europe"
        ));

        result.add(new TimeZoneOption(
                "Tokyo — JST",
                "Asia/Tokyo",
                "Asia"
        ));

        result.add(new TimeZoneOption(
                "Singapore — SGT",
                "Asia/Singapore",
                "Asia"
        ));

        result.add(new TimeZoneOption(
                "Dubai — GST",
                "Asia/Dubai",
                "Asia"
        ));

        result.add(new TimeZoneOption(
                "Sydney — AEST/AEDT",
                "Australia/Sydney",
                "Oceania"
        ));

        result.sort(
                Comparator.comparing(TimeZoneOption::getRegion)
                        .thenComparing(TimeZoneOption::getLabel)
        );

        return result;
    }
}
