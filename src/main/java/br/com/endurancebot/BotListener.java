package br.com.endurancebot;

import br.com.endurancebot.discord.commands.AvailabilityCommand;
import br.com.endurancebot.discord.commands.PingCommand;
import br.com.endurancebot.discord.commands.RaceCreateCommand;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

    private final PingCommand pingCommand;
    private final RaceCreateCommand raceCreateCommand;
    private final AvailabilityCommand availabilityCommand;

    public BotListener() {
        this.pingCommand = new PingCommand();
        this.raceCreateCommand =
                new RaceCreateCommand();

        this.availabilityCommand =
                new AvailabilityCommand(
                        raceCreateCommand
                );
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println(
                "Logged in as: "
                        + event.getJDA()
                        .getSelfUser()
                        .getName()
        );
    }

    @Override
    public void onSlashCommandInteraction(
            SlashCommandInteractionEvent event
    ) {
        switch (event.getName()) {
            case "ping":
                pingCommand.execute(event);
                break;

            case RaceCreateCommand.COMMAND_NAME:
                raceCreateCommand.execute(event);
                break;

            default:
                event.reply("Unknown command.")
                        .setEphemeral(true)
                        .queue();
        }
    }

    @Override
    public void onButtonInteraction(
            ButtonInteractionEvent event
    ) {
        if (availabilityCommand.isAvailabilityButton(
                event.getComponentId()
        )) {
            availabilityCommand.handleButton(event);
        }
    }

    @Override
    public void onStringSelectInteraction(
            StringSelectInteractionEvent event
    ) {
        if (event.getComponentId()
                .equals(
                        RaceCreateCommand.TIME_ZONE_MENU_ID
                )) {
            raceCreateCommand
                    .handleTimeZoneSelection(event);

            return;
        }

        if (event.getComponentId()
                .equals(
                        AvailabilityCommand.TIME_ZONE_MENU_ID
                )) {
            availabilityCommand
                    .handleTimeZoneSelection(event);
        }
    }

    @Override
    public void onModalInteraction(
            ModalInteractionEvent event
    ) {
        if (event.getModalId()
                .startsWith(
                        RaceCreateCommand.MODAL_ID_PREFIX
                )) {
            raceCreateCommand.handleModal(event);
            return;
        }

        if (event.getModalId()
                .startsWith(
                        AvailabilityCommand.MODAL_ID
                )) {
            availabilityCommand.handleModal(event);
        }
    }
}
