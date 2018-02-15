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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

// bukkit/spigot imports
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * A class that handles all YAML file based configuration.
 *
 * @author Wartoshika
 */
public class ConfigurationManager {

    // the main config file name with extension included
    private final String configFileName = "config.yml";

    // current plugin instance
    private final JavaPlugin plugin;

    // the path to the plugin folder
    private final String pluginPath;

    // the current active configuration
    private FileConfiguration configuration;

    /**
     * constructor of the configuration manager
     *
     * @param plugin the current minecraft plugin
     */
    public ConfigurationManager(JavaPlugin plugin) {

        this.plugin = plugin;
        this.pluginPath = "plugins" + File.separator + plugin.getName();
    }

    /**
     * loads the current configuration into the ram
     *
     * @return success flag
     */
    public boolean loadConfiguration() {

        this.configuration = YamlConfiguration.loadConfiguration(
                new File(this.getConfigPath())
        );

        return this.configuration instanceof YamlConfiguration;
    }

    /**
     * saves the current active configuration to the filesystem
     *
     * @return success flag
     */
    public boolean saveConfiguration() {

        try {

            this.configuration.save(
                    new File(this.getConfigPath())
            );
        } catch (IOException ex) {

            // log the exception
            Main.log.severe("Error while saving the current active configuration. Look at the exception:");
            Main.log.log(Level.SEVERE, ex.getMessage(), ex);

            return false;
        }

        return true;
    }

    /**
     * get the currently active configuration
     *
     * @return
     */
    public FileConfiguration getConfiguration() {

        return this.configuration;
    }

    /**
     * handles basic config setup and created directories if there isn't a
     * plugin config file
     *
     * @throws java.io.IOException
     */
    public void deployDefaultConfigIfNessesary() throws IOException {

        // create plugin config folger if nessenary
        new File(this.pluginPath).mkdir();

        // deploy config if there isn't one available
        File pluginConfig = new File(this.getConfigPath());
        if (!pluginConfig.exists()) {

            Main.log.info("I haven't found a config.yml file. Creating one!");

            // deploy using resourceStreams!
            Files.copy(
                    this.plugin.getResource("resources/" + this.configFileName),
                    pluginConfig.toPath()
            );

            Main.log.info("You can now use the created config file at:");
            Main.log.info(pluginConfig.getAbsolutePath());
        }
    }

    /**
     * returns the absolute config path
     *
     * @return config path
     */
    private String getConfigPath() {

        return this.pluginPath + File.separator + this.configFileName;
    }
}
