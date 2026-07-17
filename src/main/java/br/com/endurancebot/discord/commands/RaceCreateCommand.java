package br.com.endurancebot.discord.commands;

import br.com.endurancebot.model.Race;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class RaceCreateCommand {

    public static final String COMMAND_NAME = "race-create";
    public static final String TIME_ZONE_MENU_ID = "race-time-zone";
    public static final String MODAL_ID_PREFIX = "race-create-modal:";

    private Race activeRace;

    public void execute(SlashCommandInteractionEvent event) {
        StringSelectMenu timeZoneMenu = StringSelectMenu
                .create(TIME_ZONE_MENU_ID)
                .setPlaceholder("Select the official race time zone")
                .setRequiredRange(1, 1)

                .addOption(
                        "UTC",
                        "UTC",
                        "Coordinated Universal Time"
                )

                .addOption(
                        "São Paulo — BRT",
                        "America/Sao_Paulo",
                        "Brazil"
                )

                .addOption(
                        "Buenos Aires — ART",
                        "America/Argentina/Buenos_Aires",
                        "Argentina"
                )

                .addOption(
                        "New York — EST/EDT",
                        "America/New_York",
                        "United States"
                )

                .addOption(
                        "Chicago — CST/CDT",
                        "America/Chicago",
                        "United States"
                )

                .addOption(
                        "Los Angeles — PST/PDT",
                        "America/Los_Angeles",
                        "United States"
                )

                .addOption(
                        "London — GMT/BST",
                        "Europe/London",
                        "United Kingdom"
                )

                .addOption(
                        "Brussels — CET/CEST",
                        "Europe/Brussels",
                        "Belgium"
                )

                .addOption(
                        "Paris — CET/CEST",
                        "Europe/Paris",
                        "France"
                )

                .addOption(
                        "Berlin — CET/CEST",
                        "Europe/Berlin",
                        "Germany"
                )

                .addOption(
                        "Tokyo — JST",
                        "Asia/Tokyo",
                        "Japan"
                )

                .addOption(
                        "Singapore — SGT",
                        "Asia/Singapore",
                        "Singapore"
                )

                .addOption(
                        "Dubai — GST",
                        "Asia/Dubai",
                        "United Arab Emirates"
                )

                .addOption(
                        "Sydney — AEST/AEDT",
                        "Australia/Sydney",
                        "Australia"
                )

                .build();

                event.reply(
                                "🌍 **Select the your time zone:**"
                        )
                        .addComponents(ActionRow.of(timeZoneMenu))
                        .setEphemeral(true)
                        .queue();
    }

    public void handleTimeZoneSelection(
            StringSelectInteractionEvent event
    ) {
        if (!event.getComponentId().equals(TIME_ZONE_MENU_ID)) {
            return;
        }

        String selectedTimeZone =
                event.getValues().getFirst();

        Modal modal = createRaceModal(selectedTimeZone);

        event.replyModal(modal).queue();
    }

    private Modal createRaceModal(String selectedTimeZone) {
        TextInput raceName = TextInput
                .create("race-name", TextInputStyle.SHORT)
                .setPlaceholder("Example: 24 Hours of Spa")
                .setMinLength(2)
                .setMaxLength(80)
                .setRequired(true)
                .build();

        TextInput raceDate = TextInput
                .create("race-date", TextInputStyle.SHORT)
                .setPlaceholder("dd/MM/yyyy")
                .setMinLength(10)
                .setMaxLength(10)
                .setRequired(true)
                .build();

        TextInput startTime = TextInput
                .create("start-time", TextInputStyle.SHORT)
                .setPlaceholder("HH:mm")
                .setMinLength(5)
                .setMaxLength(5)
                .setRequired(true)
                .build();

        TextInput duration = TextInput
                .create("duration", TextInputStyle.SHORT)
                .setPlaceholder("Example: 24")
                .setMaxLength(3)
                .setRequired(true)
                .build();

        return Modal
                .create(
                        MODAL_ID_PREFIX + selectedTimeZone,
                        "Create Endurance Race"
                )
                .addComponents(
                        Label.of("Race name", raceName),
                        Label.of("Race date", raceDate),
                        Label.of("Start time", startTime),
                        Label.of("Duration in hours", duration)
                )
                .build();
    }

    public void handleModal(ModalInteractionEvent event) {
        if (!event.getModalId().startsWith(MODAL_ID_PREFIX)) {
            return;
        }

        try {
            String selectedTimeZone =
                    event.getModalId()
                            .substring(MODAL_ID_PREFIX.length());

            String name = getValue(event, "race-name");
            String dateText = getValue(event, "race-date");
            String timeText = getValue(event, "start-time");
            String durationText = getValue(event, "duration");

            DateTimeFormatter dateFormat =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy");

            DateTimeFormatter timeFormat =
                    DateTimeFormatter.ofPattern("HH:mm");

            LocalDate date =
                    LocalDate.parse(dateText, dateFormat);

            LocalTime time =
                    LocalTime.parse(timeText, timeFormat);

            long durationHours =
                    Long.parseLong(durationText);

            if (durationHours <= 0 || durationHours > 168) {
                throw new IllegalArgumentException(
                        "Duration must be between 1 and 168 hours."
                );
            }

            ZoneId zoneId = ZoneId.of(selectedTimeZone);

            ZonedDateTime localStart =
                    ZonedDateTime.of(date, time, zoneId);

            activeRace = new Race(
                    UUID.randomUUID().toString(),
                    name,
                    localStart.toInstant(),
                    localStart.plusHours(durationHours).toInstant(),
                    selectedTimeZone
            );

            DateTimeFormatter outputFormat =
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy 'at' HH:mm z"
                    );

            String response =
                    "✅ **Race created successfully!**\n\n"
                            + "🏁 **" + activeRace.getName() + "**\n"
                            + "📅 Start: "
                            + activeRace.getStart()
                            .atZone(zoneId)
                            .format(outputFormat)
                            + "\n"
                            + "🏁 End: "
                            + activeRace.getEnd()
                            .atZone(zoneId)
                            .format(outputFormat)
                            + "\n"
                            + "🌍 Time zone: `"
                            + activeRace.getOfficialTimeZone()
                            + "`\n"
                            + "⏱ Duration: "
                            + durationHours
                            + " hours";

                        Button submitAvailabilityButton = Button.primary(
                                "submit-availability",
                                "Submit Availability"
                        );

                        event.reply(response)
                                .addComponents(
                                        ActionRow.of(submitAvailabilityButton)
                                )
                                .setEphemeral(false)
                                .queue();

            System.out.println(
                    "[INFO] Race created: "
                            + activeRace.getName()
            );

        } catch (DateTimeParseException exception) {
            event.reply(
                            "❌ Invalid date or time. Use `dd/MM/yyyy` "
                                    + "for the date and `HH:mm` for the time."
                    )
                    .setEphemeral(true)
                    .queue();

        } catch (NumberFormatException exception) {
            event.reply(
                            "❌ Duration must be a whole number."
                    )
                    .setEphemeral(true)
                    .queue();

        } catch (Exception exception) {
            event.reply(
                            "❌ Could not create the race.\n"
                                    + "Reason: "
                                    + exception.getMessage()
                    )
                    .setEphemeral(true)
                    .queue();
        }
    }

    public Race getActiveRace() {
        return activeRace;
    }

    private String getValue(
            ModalInteractionEvent event,
            String fieldId
    ) {
        if (event.getValue(fieldId) == null) {
            throw new IllegalArgumentException(
                    "Missing field: " + fieldId
            );
        }

        return event.getValue(fieldId)
                .getAsString()
                .trim();
    }
}
