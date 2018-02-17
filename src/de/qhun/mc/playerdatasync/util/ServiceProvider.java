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
package de.qhun.mc.playerdatasync.util;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * a class that provides bukkit services to modules
 *
 * @author Wrath
 */
public class ServiceProvider {

    // the plugin holder
    private final JavaPlugin plugin;

    public ServiceProvider(JavaPlugin plugin) {

        this.plugin = plugin;
    }

    /**
     * get a class from the service provider
     *
     * @param <T>
     * @param className
     * @return
     */
    public <T> T get(Class<T> className) {

        return this.plugin.getServer().getServicesManager().getRegistration(className).getProvider();
    }

}
