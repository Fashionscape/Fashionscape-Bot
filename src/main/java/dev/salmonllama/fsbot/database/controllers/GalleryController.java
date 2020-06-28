/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.database.controllers;

import dev.salmonllama.fsbot.database.FSDB;
import dev.salmonllama.fsbot.database.models.GalleryChannel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class GalleryController {
    public static CompletableFuture<Void> insert(GalleryChannel gallery) {
        return CompletableFuture.runAsync(() -> {
            try {
                insertExec(gallery);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Collection<GalleryChannel>> getGalleriesByServer(String serverId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getGalleriesByServerExec(serverId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<Boolean> galleryExists(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return galleryExistsExec(channelId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static CompletableFuture<String> getTag(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getTagExec(channelId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }
    // TODO: Refactor for proper Java-ing, add GalleryBuilder
    private static void insertExec(GalleryChannel gallery) throws SQLException {
        FSDB.get().insert("INSERT INTO galleries('server_id', 'server_name', 'channel_id', 'channel_name', 'tag', 'emoji')" +
                        "VALUES(?, ?, ?, ?, ?)",
                gallery.serverId,
                gallery.serverName,
                gallery.channelId,
                gallery.channelName,
                gallery.tag,
                gallery.emoji
        );
    }

    private static Collection<GalleryChannel> getGalleriesByServerExec(String serverId) throws SQLException { // TODO: What if the server has no galleries eh?
        ResultSet rs = FSDB.get().select("SELECT * FROM galleries WHERE server_id = ?", serverId);

        Collection<GalleryChannel> galleries = new ArrayList<>();
        while (rs.next()) {
            galleries.add(mapObject(rs));
        }

        FSDB.get().close(rs);
        return galleries;
    }

    private static boolean galleryExistsExec(String channelId) throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT EXISTS(SELECT 1 FROM galleries WHERE channel_id = ?) AS hmm", channelId);
        boolean exists = rs.getBoolean("hmm");

        FSDB.get().close(rs);
        return exists;
    }

    private static String getTagExec(String channelId) throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT * FROM galleries WHERE channel_id = ?", channelId);
        String tag = rs.getString("tag");

        FSDB.get().close(rs);
        return tag;
    }

    private static String getEmojiExec(String channelId) throws SQLException {
        ResultSet rs = FSDB.get().select("SELECT * FROM galleries WHERE channel_id = ?");
        String emoji = rs.getString("emoji");

        FSDB.get().close(rs);
        return emoji;
    }

    private static GalleryChannel mapObject(ResultSet rs) throws SQLException { // TODO: Builder this
        GalleryChannel gallery = new GalleryChannel();
        gallery.serverId = rs.getString("server_id");
        gallery.serverName = rs.getString("server_name");
        gallery.channelId = rs.getString("channel_id");
        gallery.channelName = rs.getString("channel_name");
        gallery.tag = rs.getString("tag");
        gallery.emoji = rs.getString("emoji");

        return gallery;
    }
}
