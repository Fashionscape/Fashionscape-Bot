/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

// Heavily inspired by Kaaz's Emily database connection: https://github.com/Kaaz/DiscordBot/tree/master/src/main/java/emily/db
package dev.salmonllama.fsbot.database;

import dev.salmonllama.exceptions.UnknownParameterException;
import dev.salmonllama.fsbot.config.BotConfig;
import dev.salmonllama.fsbot.utilities.Constants;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.sql.*;

public class DatabaseProvider {
    private final String DB_ADDR;
    private final String DB_NAME;
    private Connection c;

    public DatabaseProvider(String dbName) {
        DB_NAME = dbName;
        DB_ADDR = "jdbc:sqlite:".concat(Constants.BOT_FOLDER).concat(BotConfig.DB_ADDR);
    }

    private Connection createConnection() {
        try {
            SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
            dataSource.setDatabaseName(DB_NAME);
            dataSource.setUrl(DB_ADDR);
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not connect to database, double check config values");
            System.exit(1);
        }
        return null;
    }

    public Connection getConnection() {
        if (c ==  null) {
            return createConnection();
        }
        return c;
    }

    private void resolveParameters(PreparedStatement query, Object... params) throws SQLException {
        int index = 1;
        for (Object p : params) {
            if (p instanceof String) {
                query.setString(index, (String) p);
            }
            else if (p instanceof Boolean) {
                query.setBoolean(index, (boolean) p);
            }
            else if (p instanceof Timestamp) {
                query.setTimestamp(index, (Timestamp) p);
            } else {
                throw new UnknownParameterException(p, index);
            }
            index++;
        }
    }

    public int query(String sql) throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    public int insert(String sql, Object... params) throws SQLException {
        try (PreparedStatement query = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            resolveParameters(query, params);
            query.executeUpdate();

            ResultSet rs = query.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return -1;
        }
    }

    public ResultSet select(String sql, Object... params) throws SQLException {
        PreparedStatement query;
        query = getConnection().prepareStatement(sql);
        resolveParameters(query, params);
        return query.executeQuery();
    }
}
