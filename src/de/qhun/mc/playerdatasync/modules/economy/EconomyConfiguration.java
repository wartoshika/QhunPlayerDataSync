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
package de.qhun.mc.playerdatasync.modules.economy;

import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.config.AbstractConfiguration;
import de.qhun.mc.playerdatasync.config.ModuleConfiguration;

/**
 * a class that represents the current configuration state for economy things
 *
 * @author Wartoshika
 */
public class EconomyConfiguration extends AbstractConfiguration implements ModuleConfiguration {

    public EconomyConfiguration(Main plugin) {
        super(plugin);
    }

    /**
     * is the economy sync enabled?
     *
     * @return
     */
    @Override
    public boolean isEnabled() {

        return this.getConfiguration().getBoolean("economy.enabled");
    }

    /**
     * is the sync enabled?
     *
     * @return
     */
    public boolean isSyncEnabled() {

        return this.getConfiguration().getBoolean("economy.sync.enabled");
    }

    /**
     * get the sync interval in seconds
     *
     * @return
     */
    public int getSyncInterval() {

        return this.getConfiguration().getInt("economy.sync.interval");
    }

}
