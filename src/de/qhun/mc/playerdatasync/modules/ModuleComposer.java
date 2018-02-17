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
import de.qhun.mc.playerdatasync.modules.economy.EconomyConfiguration;
import de.qhun.mc.playerdatasync.database.domainmodel.DomainModelSetup;
import de.qhun.mc.playerdatasync.events.EventRegister;
import de.qhun.mc.playerdatasync.modules.economy.EconomyModule;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * a class that handles all available modules and composes them into a fully
 * functional class stack
 *
 * @author Wrath
 */
public class ModuleComposer {

    // the plugin and dependency management holder
    private final JavaPlugin plugin;
    private final DependencyManager dependencyManager;
    private final EventRegister eventRegister;
    private final DomainModelSetup domainModelSetup;

    // storage for all modules
    private final Map<Class<? extends Module>, Class<? extends AbstractConfiguration>> modules;

    // storage for all active modules
    private final Map<Class<? extends Module>, Module> activeModules;

    /**
     * constructor with given plugin instance
     *
     * @param plugin
     * @param dependencyManager
     * @param eventRegister
     * @param domainModelSetup
     */
    public ModuleComposer(
            JavaPlugin plugin,
            DependencyManager dependencyManager,
            EventRegister eventRegister,
            DomainModelSetup domainModelSetup
    ) {

        // var storing
        this.dependencyManager = dependencyManager;
        this.plugin = plugin;
        this.eventRegister = eventRegister;
        this.domainModelSetup = domainModelSetup;

        // init the stack
        this.modules = new HashMap<>();
        this.activeModules = new HashMap<>();

        // ECONOMY MODULE
        this.modules.put(EconomyModule.class, EconomyConfiguration.class);
        
    }

    /**
     * loads the given module with its configuration
     *
     * @param module the module to load
     * @return the loaded module
     */
    public Module loadModule(Class<? extends Module> module) {

        // @todo: check if the module is allready loaded
        try {

            // get the configuration constructor from the map stack
            Class<AbstractConfiguration> configurationClass = (Class<AbstractConfiguration>) this.modules.get(module);

            // build a dynamic constructor
            Constructor<AbstractConfiguration> ctor = configurationClass.getDeclaredConstructor(Main.class);

            // construct the configuration
            AbstractConfiguration configurationInstance = ctor.newInstance(this.plugin);

            // now construct the module
            Module moduleInstance = module.getConstructor(EventRegister.class, DomainModelSetup.class, JavaPlugin.class)
                    .newInstance(this.eventRegister, this.domainModelSetup, this.plugin);

            // attach the configuration
            moduleInstance.setConfiguration(configurationInstance);

            // check if the module's dependencies are all present
            moduleInstance.checkDependencies(this.dependencyManager);

            // enable the module
            moduleInstance.enable();

            // store the loaded module
            this.activeModules.put(module, moduleInstance);

            // all fine!
            return moduleInstance;
            
        } catch (Exception ex) {

            // throw error
            throw new Error(ex.getMessage(), ex);
        }
    }

    /**
     * load all modules. does not check module activity!
     */
    public void loadAllModules() {

        // iterate through all available modules
        for (Map.Entry<Class<? extends Module>, Class<? extends AbstractConfiguration>> module
                : this.modules.entrySet()) {
            
            try {

                // load that module
                this.loadModule(module.getKey());
            } catch (Error error) {

                // error while loading this module
                Main.log.severe(
                        String.format(
                                "[%s] Error while loading this module! Error: %s",
                                module.getKey().getSimpleName(),
                                error.getMessage()
                        )
                );
                Main.log.log(Level.SEVERE, error.getMessage(), error);
            }
        }
        
    }

    /**
     * disables that module
     *
     * @param module
     * @return
     */
    public boolean disableModule(Class<? extends Module> module) {
        
        return this.activeModules.get(module).disable();
    }

    /**
     * disables all modules
     */
    public void disableAllModules() {

        // iterate through all available modules
        for (Map.Entry<Class<? extends Module>, Module> module
                : this.activeModules.entrySet()) {

            // load that module
            this.disableModule(module.getKey());
        }
    }
}
