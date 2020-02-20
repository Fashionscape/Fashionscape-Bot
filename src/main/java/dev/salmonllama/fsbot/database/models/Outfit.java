/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.database.models;

import dev.salmonllama.fsbot.database.DatabaseModel;

import java.sql.Timestamp;

public class Outfit extends DatabaseModel {
    public String id = "";
    public String link = "";
    public String submitter = "";
    public String tag = "";
    public Timestamp created = null;
    public Timestamp updated = null;
    public boolean deleted = false;
    public boolean featured = false;
    public int displayCount = 0;
    public String deletionHash = "";

    public Outfit() {

    }

    public static String schema() {
        return "CREATE TABLE IF NOT EXISTS outfits (" +
                "id TEXT, " +
                "link TEXT," +
                "submitter TEXT," +
                "tag TEXT," +
                "created TEXT," +
                "updated TEXT," +
                "deleted TEXT," +
                "featured TEXT," +
                "display_count INT," +
                "deletion_hash TEXT)";
    }

    @Override
    public String toString() {
        return String.format("Outfit: [id: %s, link: %s, submitter: %s, tag: %s, created: %s, updated: %s, deleted: %s, featured: %s, display count: %s, deletion hash: %s]",
                id, link, submitter, tag, created, updated, deleted, featured, displayCount, deletionHash
        );
    }
}
