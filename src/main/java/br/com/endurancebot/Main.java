package br.com.endurancebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isBlank()) {
            System.out.println("DISCORD_TOKEN is not configured.");
            return;
        }

        try {
            JDA jda = JDABuilder
                    .createDefault(token.trim())
                    .addEventListeners(new BotListener())
                    .build();

            jda.awaitReady();

            jda.updateCommands()
                    .addCommands(
                            Commands.slash(
                                    "ping",
                                    "Checks whether the bot is online"
                            ),
                            Commands.slash(
                                            "race-create",
                                            "Creates a new endurance race"
                                    )
                                    .setDefaultPermissions(
                                            DefaultMemberPermissions.enabledFor(
                                                    Permission.MANAGE_SERVER
                                            )
                                    )
                    )
                    .queue(
                            success -> System.out.println(
                                    "[INFO] Slash commands registered."
                            ),
                            error -> System.out.println(
                                    "[ERROR] Command registration failed: "
                                            + error.getMessage()
                            )
                    );

            System.out.println(
                    "Endurance Bot connected successfully."
            );

        } catch (Exception exception) {
            System.out.println("Could not start the bot.");
            System.out.println(
                    "Reason: " + exception.getMessage()
            );
        }
    }
}
