package br.com.endurancebot.discord.commands;

import br.com.endurancebot.model.Race;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailabilityCommand {

    public static final String BUTTON_ID = "submit-availability";
    public static final String TIME_ZONE_MENU_ID = "availability-time-zone";
    public static final String ADD_INTERVAL_BUTTON_ID = "availability-add";
    public static final String REMOVE_INTERVAL_BUTTON_ID = "availability-remove";
    public static final String FINISH_BUTTON_ID = "availability-finish";
    public static final String MODAL_ID = "availability-interval-modal";

    private final RaceCreateCommand raceCreateCommand;

    private final Map<String, AvailabilitySession> activeSessions =
            new HashMap<>();

    private final Map<String, AvailabilitySession> submittedAvailabilities =
            new HashMap<>();

    public AvailabilityCommand(
            RaceCreateCommand raceCreateCommand
    ) {
        this.raceCreateCommand = raceCreateCommand;
    }

    public void handleButton(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case BUTTON_ID:
                startAvailability(event);
                break;

            case ADD_INTERVAL_BUTTON_ID:
                openIntervalModal(event);
                break;

            case REMOVE_INTERVAL_BUTTON_ID:
                removeLastInterval(event);
                break;

            case FINISH_BUTTON_ID:
                finishAvailability(event);
                break;

            default:
                break;
        }
    }

    private void startAvailability(
            ButtonInteractionEvent event
    ) {
        Race activeRace = raceCreateCommand.getActiveRace();

        if (activeRace == null) {
            event.reply("❌ There is no active race.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        StringSelectMenu timeZoneMenu = createTimeZoneMenu();

        event.reply("🌍 **Select your time zone:**")
                .addComponents(ActionRow.of(timeZoneMenu))
                .setEphemeral(true)
                .queue();
    }

    public void handleTimeZoneSelection(
            StringSelectInteractionEvent event
    ) {
        Race activeRace = raceCreateCommand.getActiveRace();

        if (activeRace == null) {
            event.reply("❌ There is no active race.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String userId = event.getUser().getId();
        String selectedTimeZone =
                event.getValues().getFirst();

        AvailabilitySession session =
                new AvailabilitySession(
                        userId,
                        getDriverName(event),
                        selectedTimeZone,
                        activeRace.getId()
                );

        activeSessions.put(userId, session);

        event.reply(buildSessionMessage(session))
                .addComponents(createSessionButtons(session))
                .setEphemeral(true)
                .queue();
    }

    private void openIntervalModal(
            ButtonInteractionEvent event
    ) {
        AvailabilitySession session =
                activeSessions.get(event.getUser().getId());

        if (session == null) {
            event.reply(
                            "❌ Start again using the race button."
                    )
                    .setEphemeral(true)
                    .queue();
            return;
        }

        TextInput availableFrom = TextInput
                .create(
                        "available-from",
                        TextInputStyle.SHORT
                )
                .setPlaceholder("Example: 10:00")
                .setMinLength(5)
                .setMaxLength(5)
                .setRequired(true)
                .build();

        TextInput availableUntil = TextInput
                .create(
                        "available-until",
                        TextInputStyle.SHORT
                )
                .setPlaceholder("Example: 12:00")
                .setMinLength(5)
                .setMaxLength(5)
                .setRequired(true)
                .build();

        Modal modal = Modal
                .create(MODAL_ID, "Add Availability Interval")
                .addComponents(
                        Label.of(
                                "Available from",
                                availableFrom
                        ),
                        Label.of(
                                "Available until",
                                availableUntil
                        )
                )
                .build();

        event.replyModal(modal).queue();
    }

    public void handleModal(
            ModalInteractionEvent event
    ) {
        AvailabilitySession session =
                activeSessions.get(event.getUser().getId());

        if (session == null) {
            event.reply(
                            "❌ Your availability session expired. "
                                    + "Start again from the race panel."
                    )
                    .setEphemeral(true)
                    .queue();
            return;
        }

        try {
            DateTimeFormatter timeFormat =
                    DateTimeFormatter.ofPattern("HH:mm");

            LocalTime start = LocalTime.parse(
                    getValue(event, "available-from"),
                    timeFormat
            );

            LocalTime end = LocalTime.parse(
                    getValue(event, "available-until"),
                    timeFormat
            );

            session.intervals().add(
                    new AvailabilityInterval(start, end)
            );

            event.reply(
                            "✅ Interval added.\n\n"
                                    + buildSessionMessage(session)
                    )
                    .addComponents(createSessionButtons(session))
                    .setEphemeral(true)
                    .queue();

        } catch (Exception exception) {
            event.reply(
                            "❌ Invalid time. Use `HH:mm`, "
                                    + "for example `10:00`."
                    )
                    .setEphemeral(true)
                    .queue();
        }
    }

    private void removeLastInterval(
            ButtonInteractionEvent event
    ) {
        AvailabilitySession session =
                activeSessions.get(event.getUser().getId());

        if (session == null) {
            event.reply(
                            "❌ Start again from the race panel."
                    )
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (session.intervals().isEmpty()) {
            event.reply("❌ There are no intervals to remove.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        session.intervals().remove(
                session.intervals().size() - 1
        );

        event.reply(
                        "🗑️ Last interval removed.\n\n"
                                + buildSessionMessage(session)
                )
                .addComponents(createSessionButtons(session))
                .setEphemeral(true)
                .queue();
    }

    private void finishAvailability(
            ButtonInteractionEvent event
    ) {
        String userId = event.getUser().getId();

        AvailabilitySession session =
                activeSessions.get(userId);

        Race activeRace =
                raceCreateCommand.getActiveRace();

        if (session == null || activeRace == null) {
            event.reply(
                            "❌ Start again from the race panel."
                    )
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (session.intervals().isEmpty()) {
            event.reply(
                            "❌ Add at least one interval first."
                    )
                    .setEphemeral(true)
                    .queue();
            return;
        }

        submittedAvailabilities.put(userId, session);
        activeSessions.remove(userId);

        event.reply(
                        buildFinalConfirmation(
                                session,
                                activeRace
                        )
                )
                .setEphemeral(true)
                .queue();

        System.out.println(
                "[INFO] Availability submitted by "
                        + session.driverName()
                        + " with "
                        + session.intervals().size()
                        + " interval(s)"
        );
    }

    private String buildSessionMessage(
            AvailabilitySession session
    ) {
        StringBuilder message = new StringBuilder();

        message.append("📅 **Your availability**\n\n");
        message.append("🌍 Time zone: `")
                .append(session.timeZone())
                .append("`\n\n");

        if (session.intervals().isEmpty()) {
            message.append(
                    "No intervals added yet.\n"
            );
        } else {
            message.append("**Intervals:**\n");

            for (
                    int index = 0;
                    index < session.intervals().size();
                    index++
            ) {
                AvailabilityInterval interval =
                        session.intervals().get(index);

                message.append(index + 1)
                        .append(". `")
                        .append(interval.start())
                        .append("–")
                        .append(interval.end())
                        .append("`\n");
            }
        }

        message.append(
                "\nUse **Add interval** to include "
                        + "another available period."
        );

        return message.toString();
    }

    private String buildFinalConfirmation(
            AvailabilitySession session,
            Race race
    ) {
        ZoneId driverZone = ZoneId.of(session.timeZone());
        ZoneId raceZone =
                ZoneId.of(race.getOfficialTimeZone());

        LocalDate raceDateInDriverZone =
                race.getStart()
                        .atZone(driverZone)
                        .toLocalDate();

        DateTimeFormatter outputFormat =
                DateTimeFormatter.ofPattern(
                        "dd MMM HH:mm z"
                );

        StringBuilder message = new StringBuilder();

        message.append("✅ **Availability submitted!**\n\n");
        message.append("👤 Driver: **")
                .append(session.driverName())
                .append("**\n");
        message.append("🌍 Time zone: `")
                .append(session.timeZone())
                .append("`\n\n");

        message.append("**Your local intervals:**\n");

        for (
                int index = 0;
                index < session.intervals().size();
                index++
        ) {
            AvailabilityInterval interval =
                    session.intervals().get(index);

            ZonedDateTime localStart =
                    ZonedDateTime.of(
                            raceDateInDriverZone,
                            interval.start(),
                            driverZone
                    );

            ZonedDateTime localEnd =
                    ZonedDateTime.of(
                            raceDateInDriverZone,
                            interval.end(),
                            driverZone
                    );

            if (!interval.end().isAfter(interval.start())) {
                localEnd = localEnd.plusDays(1);
            }

            message.append(index + 1)
                    .append(". ")
                    .append(localStart.format(outputFormat))
                    .append(" → ")
                    .append(localEnd.format(outputFormat))
                    .append("\n");

            message.append("   Race time: ")
                    .append(
                            localStart
                                    .withZoneSameInstant(raceZone)
                                    .format(outputFormat)
                    )
                    .append(" → ")
                    .append(
                            localEnd
                                    .withZoneSameInstant(raceZone)
                                    .format(outputFormat)
                    )
                    .append("\n");
        }

        return message.toString();
    }

    private ActionRow createSessionButtons(
            AvailabilitySession session
    ) {
        Button addInterval = Button.primary(
                ADD_INTERVAL_BUTTON_ID,
                "Add interval"
        );

        Button removeInterval = Button.danger(
                REMOVE_INTERVAL_BUTTON_ID,
                "Remove last"
        ).withDisabled(session.intervals().isEmpty());

        Button finish = Button.success(
                FINISH_BUTTON_ID,
                "Finish"
        ).withDisabled(session.intervals().isEmpty());

        return ActionRow.of(
                addInterval,
                removeInterval,
                finish
        );
    }

    private StringSelectMenu createTimeZoneMenu() {
        return StringSelectMenu
                .create(TIME_ZONE_MENU_ID)
                .setPlaceholder("Select your time zone")
                .setRequiredRange(1, 1)
                .addOption("UTC", "UTC", "Coordinated Universal Time")
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
    }

    private String getDriverName(
            StringSelectInteractionEvent event
    ) {
        if (event.getMember() != null) {
            return event.getMember().getEffectiveName();
        }

        return event.getUser().getName();
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

    public boolean isAvailabilityButton(
            String componentId
    ) {
        return componentId.equals(BUTTON_ID)
                || componentId.equals(ADD_INTERVAL_BUTTON_ID)
                || componentId.equals(REMOVE_INTERVAL_BUTTON_ID)
                || componentId.equals(FINISH_BUTTON_ID);
    }

    private record AvailabilityInterval(
            LocalTime start,
            LocalTime end
    ) {
    }

    private record AvailabilitySession(
            String userId,
            String driverName,
            String timeZone,
            String raceId,
            List<AvailabilityInterval> intervals
    ) {
        private AvailabilitySession(
                String userId,
                String driverName,
                String timeZone,
                String raceId
        ) {
            this(
                    userId,
                    driverName,
                    timeZone,
                    raceId,
                    new ArrayList<>()
            );
        }
    }
}
