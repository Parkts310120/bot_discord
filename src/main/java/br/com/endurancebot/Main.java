package br.com.endurancebot;

import br.com.endurancebot.model.Availability;
import br.com.endurancebot.model.Race;
import br.com.endurancebot.repository.AvailabilityRepository;
import br.com.endurancebot.service.AvailabilityService;
import br.com.endurancebot.service.TimeZoneService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        TimeZoneService timeZoneService =
                new TimeZoneService();

        AvailabilityRepository availabilityRepository =
                new AvailabilityRepository();

        AvailabilityService availabilityService =
                new AvailabilityService(
                        timeZoneService,
                        availabilityRepository
                );

        DateTimeFormatter dateFormat =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DateTimeFormatter timeFormat =
                DateTimeFormatter.ofPattern("HH:mm");

        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z");

        System.out.println("=== Admin Race Setup ===");

        System.out.print("Race name: ");
        String raceName = scanner.nextLine();

        System.out.print("Race date (dd/MM/yyyy): ");
        LocalDate raceDate = LocalDate.parse(
                scanner.nextLine(),
                dateFormat
        );

        System.out.print("Race start time (HH:mm): ");
        LocalTime raceStartTime = LocalTime.parse(
                scanner.nextLine(),
                timeFormat
        );

        System.out.print("Race duration in hours: ");
        long durationHours =
                Long.parseLong(scanner.nextLine());

        System.out.print(
                "Official time zone, for example Europe/London: "
        );
        String officialTimeZone = scanner.nextLine();

        ZoneId raceZone = ZoneId.of(officialTimeZone);

        ZonedDateTime localRaceStart =
                ZonedDateTime.of(
                        raceDate,
                        raceStartTime,
                        raceZone
                );

        Race race = new Race(
                UUID.randomUUID().toString(),
                raceName,
                localRaceStart.toInstant(),
                localRaceStart
                        .plusHours(durationHours)
                        .toInstant(),
                officialTimeZone
        );

        System.out.println();
        System.out.println("Race created successfully.");
        System.out.println("Race: " + race.getName());
        System.out.println(
                "Official start: "
                        + race.getStart()
                        .atZone(raceZone)
                        .format(outputFormat)
        );
        System.out.println(
                "Official end: "
                        + race.getEnd()
                        .atZone(raceZone)
                        .format(outputFormat)
        );

        boolean continueRegistration = true;

        while (continueRegistration) {
            try {
                System.out.println();
                System.out.println("=== Driver Availability ===");

                System.out.print("Driver name: ");
                String driverName = scanner.nextLine();

                System.out.print("Available from (HH:mm): ");
                LocalTime availableFrom = LocalTime.parse(
                        scanner.nextLine(),
                        timeFormat
                );

                System.out.print("Available until (HH:mm): ");
                LocalTime availableUntil = LocalTime.parse(
                        scanner.nextLine(),
                        timeFormat
                );

                System.out.print(
                        "Driver time zone, for example America/Sao_Paulo: "
                );
                String driverTimeZone = scanner.nextLine();

                availabilityService.registerAvailability(
                        race,
                        driverName,
                        availableFrom,
                        availableUntil,
                        driverTimeZone
                );

                System.out.println(
                        "Availability registered successfully."
                );

                System.out.print(
                        "Register another driver? (y/n): "
                );

                continueRegistration =
                        scanner.nextLine().equalsIgnoreCase("y");

            } catch (Exception exception) {
                System.out.println(
                        "Error: " + exception.getMessage()
                );

                System.out.print(
                        "Try again? (y/n): "
                );

                continueRegistration =
                        scanner.nextLine().equalsIgnoreCase("y");
            }
        }

        List<Availability> availabilities =
                availabilityService.listByRace(race.getId());

        System.out.println();
        System.out.println("=== Admin Availability Summary ===");

        if (availabilities.isEmpty()) {
            System.out.println(
                    "No driver availability was registered."
            );
        }

        for (Availability availability : availabilities) {
            ZonedDateTime startUtc =
                    availability.getStart()
                            .atZone(ZoneId.of("UTC"));

            ZonedDateTime endUtc =
                    availability.getEnd()
                            .atZone(ZoneId.of("UTC"));

            ZonedDateTime startOfficial =
                    availability.getStart()
                            .atZone(raceZone);

            ZonedDateTime endOfficial =
                    availability.getEnd()
                            .atZone(raceZone);

            Duration duration =
                    Duration.between(
                            availability.getStart(),
                            availability.getEnd()
                    );

            System.out.println();
            System.out.println(
                    "Driver: "
                            + availability.getDriverName()
            );
            System.out.println(
                    "Original time zone: "
                            + availability.getOriginalTimeZone()
            );
            System.out.println(
                    "Availability in race time zone: "
                            + startOfficial.format(outputFormat)
                            + " to "
                            + endOfficial.format(outputFormat)
            );
            System.out.println(
                    "Availability in UTC: "
                            + startUtc.format(outputFormat)
                            + " to "
                            + endUtc.format(outputFormat)
            );
            System.out.println(
                    "Available duration: "
                            + duration.toHours()
                            + " hours"
            );
        }

        scanner.close();
    }
}
