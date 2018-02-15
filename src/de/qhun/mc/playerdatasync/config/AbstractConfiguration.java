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
package de.qhun.mc.playerdatasync.config;

import de.qhun.mc.playerdatasync.Main;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * general functions for explicit configuration classes
 *
 * @author Wartoshika
 */
public abstract class AbstractConfiguration {

    // the plugin instance
    protected Main plugin;

    /**
     * the constructor to get access to the plugin
     *
     * @param plugin
     */
    public AbstractConfiguration(Main plugin) {

        this.plugin = plugin;
    }

    /**
     * get the current active global configuration
     *
     * @return
     */
    protected FileConfiguration getConfiguration() {

        return this.plugin.getConfigurationManager().getConfiguration();
    }

}
