package br.com.endurancebot;

import br.com.endurancebot.model.TimeZoneOption;
import br.com.endurancebot.service.TimeZoneCatalogService;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Endurance Bot...");
        System.out.println();

        TimeZoneCatalogService timeZoneCatalogService =
                new TimeZoneCatalogService();

        List<TimeZoneOption> timeZones =
                timeZoneCatalogService.findAll();

        System.out.println("Available time zones:");

        String currentRegion = "";

        for (TimeZoneOption timeZone : timeZones) {
            if (!timeZone.getRegion().equals(currentRegion)) {
                currentRegion = timeZone.getRegion();

                System.out.println();
                System.out.println("=== " + currentRegion + " ===");
            }

            System.out.println(
                    timeZone.getLabel()
                            + " -> "
                            + timeZone.getZoneId()
            );
        }

        System.out.println();
        System.out.println(
                "Endurance Bot core started successfully."
        );
    }
}
