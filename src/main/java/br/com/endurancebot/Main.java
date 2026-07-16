package br.com.endurancebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isBlank()) {
            System.out.println("Endurance Bot core is ready.");
            System.out.println("DISCORD_TOKEN is not configured.");
            System.out.println("The bot will not connect to Discord.");
            return;
        }

        try {
            JDA jda = JDABuilder
                    .createDefault(token)
                    .addEventListeners(new BotListener())
                    .build();

            jda.awaitReady();

            jda.updateCommands()
                    .addCommands(
                            Commands.slash(
                                    "ping",
                                    "Checks whether the bot is online"
                            )
                    )
                    .queue(
                            success -> System.out.println(
                                    "[INFO] Slash commands registered."
                            ),
                            error -> System.out.println(
                                    "[ERROR] Could not register commands: "
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
