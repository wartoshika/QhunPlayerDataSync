/*
 * Copyright (C) 2018 Wartoshika <dev@qhun.de>
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
package de.qhun.mc.playerdatasync;

// java imports
import de.qhun.mc.playerdatasync.config.AbstractConfiguration;
import de.qhun.mc.playerdatasync.database.DatabaseBackendManager;
import de.qhun.mc.playerdatasync.modules.Module;
import de.qhun.mc.playerdatasync.modules.ModuleComposer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// spigot/bukkit imports
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * Main class containing the integration with the spigot/bukkit system.
 *
 * @author Wartoshika
 */
public final class Main extends JavaPlugin {

    public static Logger log;
    private DependencyManager dependencyManager;
    private ConfigurationManager configurationManager;
    private HashMap<Class<AbstractConfiguration>, Class<Module>> modules;
    private ModuleComposer moduleComposer;
    private DatabaseBackendManager databaseBackendManager;

    /**
     * will be called during plugin creation
     */
    @Override
    public void onEnable() {

        // get plugin and logger instances
        Main.log = getLogger();

        // get plugin manager 
        PluginManager pluginManager = Bukkit.getPluginManager();

        // construct the dependency manager and configuration manager
        this.configurationManager = new ConfigurationManager(this);
        this.moduleComposer = new ModuleComposer(this, new DependencyManager(this));
        this.databaseBackendManager = new DatabaseBackendManager(this, configurationManager);

        // setup routines
        try {

            // load current configuration
            if (!this.configurationManager.loadConfiguration()) {

                throw new Error("Configuration load error. Check syntax!");
            }

            // config
            this.configurationManager.deployDefaultConfigIfNessesary();
            
            // enable database connection
            this.databaseBackendManager.connectToDatabase();

            // load all modules
            this.moduleComposer.loadAllModules();

            //pm.registerEvents(new PlayerJoinListener(this), this);
        } catch (Exception ex) {

            // printing error
            Main.log.severe("Error during plugin startup phase.");
            Main.log.log(Level.SEVERE, ex.getMessage(), ex);

            // disable plugin
            pluginManager.disablePlugin(this);
        }
    }

    /**
     * will be called on plugin destruction
     */
    @Override
    public void onDisable() {

        // disable all modules
        this.moduleComposer.disableAllModules();
        
        // disable database connection
        this.databaseBackendManager.getDatabaseAdapter().disconnectFromDatabase();
    }

    /**
     * getter for the configuration manager
     *
     * @return
     */
    public ConfigurationManager getConfigurationManager() {

        return this.configurationManager;
    }

}
