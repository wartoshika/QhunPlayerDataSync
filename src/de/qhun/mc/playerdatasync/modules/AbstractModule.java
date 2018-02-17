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
package de.qhun.mc.playerdatasync.modules;

import de.qhun.mc.playerdatasync.DependencyManager;
import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.config.AbstractConfiguration;
import de.qhun.mc.playerdatasync.database.domainmodel.DomainModelSetup;
import de.qhun.mc.playerdatasync.events.EventRegister;
import de.qhun.mc.playerdatasync.util.Autoload;

/**
 * an abstraction layer for modules
 *
 * @author Wrath
 * @param <Config>
 */
public abstract class AbstractModule<Config extends AbstractConfiguration> implements Module {

    // the current configuration instance
    protected Config configuration;

    @Autoload
    protected EventRegister eventRegister;

    @Autoload
    protected Main plugin;

    @Autoload
    protected DomainModelSetup domainModelSetup;

    public AbstractModule() {

        this.logInfoPrefixed("Enable module.");
    }

    /**
     * set the current configuration
     *
     * @param configuration
     */
    @Override
    public final void setConfiguration(AbstractConfiguration configuration) {

        this.configuration = (Config) configuration;
    }

    /**
     * check if all dependencies are available
     *
     * @param dependencyManager
     */
    @Override
    public void checkDependencies(DependencyManager dependencyManager) {

        // all fine for non dependency modules
    }

    /**
     * prints an info statement with module name prefix
     *
     * @param message
     */
    protected final void logInfoPrefixed(String message) {

        Main.log.info(
                String.format(
                        "[%s] %s",
                        this.getClass().getSimpleName(),
                        message
                )
        );
    }

    /**
     * run an asynchronous task with the given delay
     *
     * @param r
     * @param delayInTicks
     */
    protected void createAsyncTask(Runnable r, long delayInTicks) {

        // call the bukkit task scheduler api
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, r, delayInTicks);
    }
}
