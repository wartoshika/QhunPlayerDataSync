/*
 * Copyright (C) 2018 Wrath
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.qhun.mc.playerdatasync.database;

import de.qhun.mc.playerdatasync.Main;
import org.bukkit.plugin.java.JavaPlugin;
import de.qhun.mc.playerdatasync.config.DatabaseConfiguration;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * general database handling
 *
 * @author Wrath
 */
public abstract class DatabaseAdapter {

    // plugin holder
    protected JavaPlugin plugin;
    protected DatabaseConfiguration configuration;
    protected Connection connection;
    protected DatabaseExecutor databaseExecutor;

    public DatabaseAdapter(JavaPlugin plugin) {

        this.plugin = plugin;
        this.databaseExecutor = new DatabaseExecutor(this, this.getDatabaseDialect());
    }

    public abstract Class<? extends DatabaseConfiguration> getConfigurationClass();

    public abstract Class<? extends Driver> getJdbcDriverClass();

    protected abstract DatabaseDialect getDatabaseDialect();

    /**
     * set the database configuration class
     *
     * @param configuration
     */
    public void setConfiguration(DatabaseConfiguration configuration) {

        this.configuration = configuration;
    }

    /**
     * connects to the configured database
     *
     * @return
     */
    public Connection connectToDatabase() {

        // check if there is a configuration
        if (this.configuration == null) {

            throw new Error("There is no database configuration class available!");
        }

        try {

            // try to runtime load the driver for jdbc
            // if it fails, an exception will be thrown
            Class.forName(this.getJdbcDriverClass().getName());

            // run against the database bachend
            String connectionUrl = String.format(
                    "jdbc:%s://%s:%s/%s?user=%s&password=%s"
                    + "&autoReconnect=%s"
                    + "&useUnicode=true&characterEncoding=UTF-8",
                    this.configuration.getProtocol(),
                    this.configuration.getHost(),
                    this.configuration.getPort(),
                    this.configuration.getDatabase(),
                    this.configuration.getUsername(),
                    this.configuration.getPassword(),
                    this.configuration.getAutoReconnect() ? "true" : "false"
            );
            this.connection = DriverManager.getConnection(connectionUrl);

            return this.connection;
        } catch (ClassNotFoundException ex) {

            // driver not found!
            throw new Error("Could not find database driver for jdbc: " + this.getJdbcDriverClass().getName(), ex);
        } catch (SQLException ex) {

            // connection error
            throw new Error("Error while connecting to the database", ex);
        } catch (Exception ex) {

            throw new Error("Unknown error occured while setting up the database connection", ex);
        }
    }

    /**
     * disconnects from the configured database
     *
     * @return
     */
    public boolean disconnectFromDatabase() {

        try {

            // log the disconnect
            Main.log.info("Disconnecting from the database backend");

            // close connection
            this.connection.close();
            return true;
        } catch (SQLException ex) {

            // print error
            throw new Error("error while disconnecting from the database", ex);
        }
    }

    /**
     * get the current database connection instance
     *
     * @return
     */
    public Connection getConnection() {

        return this.connection;
    }

    /**
     * get access to a specific database dialect and perform queries
     *
     * @return
     */
    public DatabaseExecutor query() {

        return this.databaseExecutor;
    }
}
