/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.commands.staff;

import com.vdurmont.emoji.EmojiParser;
import dev.salmonllama.fsbot.config.BotConfig;
import dev.salmonllama.fsbot.database.controllers.OutfitController;
import dev.salmonllama.fsbot.guthix.Command;
import dev.salmonllama.fsbot.guthix.CommandContext;
import dev.salmonllama.fsbot.guthix.CommandPermission;
import dev.salmonllama.fsbot.guthix.PermissionType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import dev.salmonllama.fsbot.utilities.database.DatabaseUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class RemoveOutfitCommand extends Command {
    @Override public String name() { return "Remove Outfit"; }
    @Override public String description() { return "Removes an outfit from the database given an id"; }
    @Override public String usage() { return "remove <String id>"; }
    @Override public String category() { return "Staff"; }
    @Override public CommandPermission permission() { return new CommandPermission(PermissionType.ROLE, BotConfig.STAFF_ROLE); }
    @Override public Collection<String> aliases() { return new ArrayList<>(Arrays.asList("removeoutfit", "remove")); }

    @Override
    public void onCommand(CommandContext ctx) {
        String[] args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        long authorId = ctx.getUser().getId();

        if (args.length != 1) {
            channel.sendMessage("You must supply a valid outfit ID.");
            return;
        }

        // get the outfit, confirm deletion through reaction.
        String outfitId = args[0];
        OutfitController.findById(outfitId).thenAcceptAsync(possibleOutfit -> {
           possibleOutfit.ifPresentOrElse(outfit -> {
               // Send outfit info, react with selectors, add a listener to the message
               EmbedBuilder embed = new EmbedBuilder()
                       .setTitle("Confirm Outfit Deletion")
                       .setThumbnail(outfit.getLink())
                       .setAuthor(ctx.getApi().getYourself())
                       .setUrl(outfit.getLink())
                       .setFooter(String.format("Tag: %s", outfit.getTag()))
                       .addField("Added", outfit.getCreated().toString(), true)
                       .addField("Updated", outfit.getUpdated().toString(), true)
                       .addField("Submitted by:", ctx.getApi().getUserById(outfit.getSubmitter()).join().getDiscriminatedName())
                       .addField("Deleted", outfit.isDeleted() ? "True" : "False", true)
                       .addField("Featured", outfit.isFeatured() ? "True" : "False", true);

               ctx.reply(embed).thenAcceptAsync(msg -> {
                   msg.addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                   msg.addReaction(EmojiParser.parseToUnicode(":octagonal_sign:"));

                   msg.addReactionAddListener(event -> {
                       if (event.getUser().getId() != authorId) {
                           return;
                       }
                       
                       if (event.getEmoji().equalsEmoji(EmojiParser.parseToUnicode(":white_check_mark:"))) {
                           // Delete the outfit
                           OutfitController.delete(outfit.getId());

                           EmbedBuilder response = new EmbedBuilder()
                                   .setTitle("Deletion Successful!")
                                   .setDescription(String.format("Outfit %s marked as deleted", outfit.getId()));
                           ctx.reply(response);

                       } else if (event.getEmoji().equalsEmoji(EmojiParser.parseToUnicode(":octagonal_sign:"))) {
                           // Do nothing
                           EmbedBuilder response = new EmbedBuilder()
                                   .setTitle("Deletion Aborted")
                                   .setDescription(String.format("No modifications were made to %s", outfit.getId()));

                           ctx.reply(response);
                       }
                   });
               });
           }, () -> {
               EmbedBuilder response = new EmbedBuilder()
                       .setTitle("Outfit not Found")
                       .setDescription(String.format("ID %s does not exist", outfitId));

               ctx.reply(response);
           });
        });
    }
}
