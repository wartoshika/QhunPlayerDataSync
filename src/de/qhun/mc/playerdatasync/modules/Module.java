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
import de.qhun.mc.playerdatasync.config.ModuleConfiguration;

/**
 *
 * @author Wrath
 */
public interface Module {

    public boolean enable();

    public boolean disable();

    public void setConfiguration(ModuleConfiguration configuration);

    public void checkDependencies(DependencyManager dependencyManager);

    /**
     * the construct function is the effective constructor of the module and it
     * will be automaticly called after the dependency injection phase is
     * completed. the configuration will be available at this time.
     */
    public void construct();
}
