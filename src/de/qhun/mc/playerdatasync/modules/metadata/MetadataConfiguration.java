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
package de.qhun.mc.playerdatasync.modules.metadata;

import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.config.AbstractConfiguration;
import de.qhun.mc.playerdatasync.config.ModuleConfiguration;

/**
 * the metadata configuration file
 *
 * @author Wrath
 */
public class MetadataConfiguration extends AbstractConfiguration implements ModuleConfiguration {

    public MetadataConfiguration(Main plugin) {
        super(plugin);
    }

    /**
     * is the metadata sync enabled?
     *
     * @return
     */
    @Override
    public boolean isEnabled() {

        // only enable the module if at least one subcategory is available
        return this.getConfiguration().getBoolean("metadata.enabled")
                && this.isAtLeastOneCategoryEnabled();
    }

    /**
     * get the branch name
     *
     * @return
     */
    public String getBranchName() {

        return this.getConfiguration().getString("metadata.branch");
    }

    /**
     * is health sync enabled?
     *
     * @return
     */
    public boolean isHealthEnabled() {

        return this.getConfiguration().getBoolean("metadata.health");
    }

    /**
     * is xp sync enabled?
     *
     * @return
     */
    public boolean isXpEnabled() {

        return this.getConfiguration().getBoolean("metadata.xp");
    }

    /**
     * is food sync enabled?
     *
     * @return
     */
    public boolean isFoodEnabled() {

        return this.getConfiguration().getBoolean("metadata.xp");
    }

    /**
     * is gamemode sync enabled?
     *
     * @return
     */
    public boolean isGamemodeEnabled() {

        return this.getConfiguration().getBoolean("metadata.gamemode");
    }

    /**
     * is flight sync enabled?
     *
     * @return
     */
    public boolean isFlightEnabled() {

        return this.getConfiguration().getBoolean("metadata.flight");
    }

    /**
     * is potion effect sync enabled?
     *
     * @return
     */
    public boolean isPotionEffectEnabled() {

        return this.getConfiguration().getBoolean("metadata.potion_effects");
    }

    /**
     * is fire sync enabled?
     *
     * @return
     */
    public boolean isFireEnabled() {

        return this.getConfiguration().getBoolean("metadata.fire");
    }

    /**
     * is air sync enabled?
     *
     * @return
     */
    public boolean isAirEnabled() {

        return this.getConfiguration().getBoolean("metadata.air");
    }

    /**
     * checks if at least one category is enabled
     *
     * @return
     */
    private boolean isAtLeastOneCategoryEnabled() {

        return this.isHealthEnabled()
                || this.isXpEnabled()
                || this.isFoodEnabled()
                || this.isGamemodeEnabled()
                || this.isFlightEnabled()
                || this.isPotionEffectEnabled()
                || this.isFireEnabled()
                || this.isAirEnabled();
    }
}
