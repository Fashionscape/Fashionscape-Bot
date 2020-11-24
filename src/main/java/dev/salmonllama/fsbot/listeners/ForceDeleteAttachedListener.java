/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.listeners;

import dev.salmonllama.fsbot.guthix.CommandContext;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class ForceDeleteAttachedListener implements MessageCreateListener {

    private CommandContext ctx;

    public ForceDeleteAttachedListener(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

    }
}
