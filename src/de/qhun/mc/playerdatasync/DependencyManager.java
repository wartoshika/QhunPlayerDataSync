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

// spigot/bukkit imports
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

// other imports
import net.milkbowl.vault.economy.Economy;

/**
 * provides functions to check if a certain depency module is available
 *
 * @author Wartoshika
 */
public class DependencyManager {

    private final JavaPlugin plugin;

    /**
     * takes the plugin instance to store bukkit relevant infos
     *
     * @param plugin the current plugin
     */
    public DependencyManager(JavaPlugin plugin) {

        this.plugin = plugin;
    }

    /**
     * checks if vault and a hooked economy system is available
     *
     * @return
     */
    public boolean isEconomyAvailable() {

        // first check if vault is a registered plugin in the pluginmanager
        if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        // check if an economy class is available at the service manager
        RegisteredServiceProvider<Economy> rsp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        return (Economy) rsp.getProvider() != null;
    }
}
