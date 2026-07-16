package br.com.endurancebot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println(
                "Logged in as: "
                        + event.getJDA().getSelfUser().getName()
        );
    }

    @Override
    public void onSlashCommandInteraction(
            SlashCommandInteractionEvent event
    ) {
        if (event.getName().equals("ping")) {
            event.reply("🏁 Endurance Bot is online!")
                    .setEphemeral(true)
                    .queue();

            System.out.println(
                    "[INFO] /ping used by "
                            + event.getUser().getName()
            );
        }
    }
}
