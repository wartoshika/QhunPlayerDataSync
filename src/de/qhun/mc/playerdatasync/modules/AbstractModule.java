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
import de.qhun.mc.playerdatasync.events.EventRegister;

/**
 * an abstraction layer for modules
 *
 * @author Wrath
 * @param <Config>
 */
public abstract class AbstractModule<Config extends AbstractConfiguration> implements Module {

    // the current configuration instance
    protected Config configuration;
    protected EventRegister eventRegister;

    public AbstractModule(EventRegister eventRegister) {

        this.logInfoPrefixed("Enable module.");
        this.eventRegister = eventRegister;
    }

    /**
     * set the current configuration
     *
     * @param configuration
     */
    @Override
    public void setConfiguration(AbstractConfiguration configuration) {

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
}
