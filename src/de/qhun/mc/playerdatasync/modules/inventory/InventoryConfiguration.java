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
import de.qhun.mc.playerdatasync.config.ModuleConfiguration;

/**
 *
 * @author Wrath
 */
public class InventoryConfiguration extends AbstractConfiguration implements ModuleConfiguration {

    public InventoryConfiguration(Main plugin) {
        super(plugin);
    }

    /**
     * is the inventory sync enabled?
     *
     * @return
     */
    @Override
    public boolean isEnabled() {

        return this.getConfiguration().getBoolean("inventory.enabled");
    }

    /**
     * should the ender chest be synced?
     *
     * @return
     */
    public boolean isEnderChestEnabled() {

        return this.getConfiguration().getBoolean("inventory.ender_chest");
    }

    /**
     * get the branch name
     *
     * @return
     */
    public String getBranchName() {

        return this.getConfiguration().getString("inventory.branch");
    }
}
