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
package de.qhun.mc.playerdatasync.modules.inventory;

import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.config.AbstractConfiguration;

/**
 *
 * @author Wrath
 */
public class InventoryConfiguration extends AbstractConfiguration {

    public InventoryConfiguration(Main plugin) {
        super(plugin);
    }

    /**
     * is the inventory sync enabled?
     *
     * @return
     */
    public boolean isEnabled() {

        return this.getConfiguration().getBoolean("inventory.enabled");
    }
}
