package br.com.endurancebot;

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
}
