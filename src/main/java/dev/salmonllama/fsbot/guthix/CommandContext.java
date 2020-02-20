/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.guthix;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import dev.salmonllama.fsbot.config.BotConfig;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CommandContext {
    private DiscordApi api;
    private Message message;
    private MessageAuthor author;
    private TextChannel channel;
    private Server server;
    private Command usedCommand;
    private String usedAlias;
    private String[] args;

    private CommandContext(CommandContextBuilder builder) {
        this.api = builder.api;
        this.message = builder.message;
        this.author = builder.author;
        this.channel = builder.channel;
        this.server = builder.server;
        this.usedCommand = builder.usedCommand;
        this.usedAlias = builder.usedAlias;
        this.args = builder.args;
    }

    public DiscordApi getApi() {
        return api;
    }

    public Message getMessage() {
        return message;
    }

    public MessageAuthor getAuthor() {
        return author;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public Server getServer() {
        return server;
    }

    public Command getUsedCommand() {
        return usedCommand;
    }

    public String getUsedAlias() {
        return usedAlias;
    }

    public String[] getArgs() {
        return args;
    }

    public User getUser() {
        if (author.asUser().isPresent()) {
            return author.asUser().get();
        } else {
            // Log something to discord, I dunno
        }
        return null;
    }

    public Collection<Role> getUserRoles() {
        User user = getUser();
        return user.getRoles(getServer());
    }

    public boolean isUserOwner() {
        return getUser().getIdAsString().equals(BotConfig.BOT_OWNER);
    }

    public CompletableFuture<Message> reply(String msg) {
        return channel.sendMessage(msg);
    }

    public CompletableFuture<Message> reply(EmbedBuilder embed) {
        return channel.sendMessage(embed);
    }

    public static class CommandContextBuilder {
        private DiscordApi api;
        private Message message;
        private MessageAuthor author;
        private TextChannel channel;
        private Server server;
        private Command usedCommand;
        private String usedAlias;
        private String[] args;

        public CommandContextBuilder(
                DiscordApi api,
                Message message,
                MessageAuthor author,
                TextChannel channel,
                Server server,
                Command usedCommand,
                String usedAlias,
                String[] args
        ) {
            this.api = api;
            this.message = message;
            this.author = author;
            this.channel = channel;
            this.server = server;
            this.usedCommand = usedCommand;
            this.usedAlias = usedAlias;
            this.args = args;
        }

        public CommandContext build() {
            return new CommandContext(this);
        }
    }
}