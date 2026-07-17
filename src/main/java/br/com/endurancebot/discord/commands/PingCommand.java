package br.com.endurancebot.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand {

    public void execute(SlashCommandInteractionEvent event) {
        event.reply("🏁 Endurance Bot is online!")
                .setEphemeral(true)
                .queue();
    }

}
