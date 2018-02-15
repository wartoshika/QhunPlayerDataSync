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

import de.qhun.mc.playerdatasync.ConfigurationManager;
import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.config.DatabaseConfiguration;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.concurrent.Executors;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * selects the configured database backend and enabled the database connection
 *
 * @author Wrath
 */
public class DatabaseBackendManager {

    // stores the configuration manager
    private final ConfigurationManager configurationManager;
    private final JavaPlugin plugin;

    // the current selected backend
    private DatabaseAdapter backendAdapter;

    // the current database connection
    private Connection connection;

    public DatabaseBackendManager(JavaPlugin plugin, ConfigurationManager configurationManager) {

        this.configurationManager = configurationManager;
        this.plugin = plugin;
    }

    /**
     * get the current database adapter
     *
     * @return
     */
    public DatabaseAdapter getDatabaseAdapter() {

        return this.backendAdapter;
    }

    /**
     * connect to the configured backend and prepare everything to start
     *
     * @return
     */
    public DatabaseAdapter connectToDatabase() {

        // setup max database threads
        int maxThreads = this.configurationManager.getConfiguration().getInt("database.max_thread_count", 4);
        Executors.newFixedThreadPool(maxThreads);

        // get the backend adapter class for the configuration
        String adapterClassName = this.configurationManager.getConfiguration().getString("database.adapter");

        // build a dynamic constructor
        try {

            Class<DatabaseAdapter> adapterClass = (Class<DatabaseAdapter>) Class.forName(adapterClassName);
            Constructor<DatabaseAdapter> ctor = adapterClass.getDeclaredConstructor(JavaPlugin.class);

            // construct the configuration
            this.backendAdapter = ctor.newInstance(this.plugin);

            // create the configuration
            Constructor<? extends DatabaseConfiguration> ctorConfig = this.backendAdapter.getConfigurationClass()
                    .getDeclaredConstructor(Main.class);

            // add the config
            this.backendAdapter.setConfiguration(ctorConfig.newInstance(this.plugin));

            // create configuration
        } catch (Exception ex) {

            throw new Error("Error while creating the database adapter and configuration!", ex);
        }

        // now connect to the database
        this.connection = this.backendAdapter.connectToDatabase();

        return this.backendAdapter;
    }

}
