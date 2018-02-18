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
import de.qhun.mc.playerdatasync.config.ModuleConfiguration;
import de.qhun.mc.playerdatasync.modules.economy.EconomyConfiguration;
import de.qhun.mc.playerdatasync.modules.economy.EconomyModule;
import de.qhun.mc.playerdatasync.modules.inventory.InventoryConfiguration;
import de.qhun.mc.playerdatasync.modules.inventory.InventoryModule;
import de.qhun.mc.playerdatasync.modules.metadata.MetadataConfiguration;
import de.qhun.mc.playerdatasync.modules.metadata.MetadataModule;
import de.qhun.mc.playerdatasync.util.DependencyInjection;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // storage for all modules
    private final Map<Class<? extends Module>, Class<? extends ModuleConfiguration>> modules;

    // storage for all active modules
    private final Map<Class<? extends Module>, Module> activeModules;

    /**
     * constructor with given plugin instance
     *
     * @param plugin
     * @param dependencyManager
     */
    public ModuleComposer(
            JavaPlugin plugin,
            DependencyManager dependencyManager
    ) {

        // var storing
        this.dependencyManager = dependencyManager;
        this.plugin = plugin;

        // init the stack
        this.modules = new HashMap<>();
        this.activeModules = new HashMap<>();

        // ############################################
        // THE LIST OF MODULES THAT SHOULD BE ACTIVATED
        // ############################################
        this.modules.put(EconomyModule.class, EconomyConfiguration.class);
        this.modules.put(InventoryModule.class, InventoryConfiguration.class);
        this.modules.put(MetadataModule.class, MetadataConfiguration.class);
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
            Class<ModuleConfiguration> configurationClass = (Class<ModuleConfiguration>) this.modules.get(module);

            // build a dynamic constructor
            Constructor<ModuleConfiguration> ctor = configurationClass.getDeclaredConstructor(Main.class);

            // construct the configuration
            ModuleConfiguration configurationInstance = ctor.newInstance(this.plugin);

            // check if this module should be enabled
            if (!configurationInstance.isEnabled()) {
                return null;
            }

            // now construct the module
            Module moduleInstance = DependencyInjection.build(module);

            // attach the configuration
            moduleInstance.setConfiguration(configurationInstance);

            // call the constructor
            try {
                moduleInstance.construct();
            } catch (Exception ex) {

                // log the error
                Main.log.severe("Error while constructing module " + moduleInstance.getClass().getSimpleName());
                throw ex;
            }

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
        this.modules.entrySet().forEach((module) -> {
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
        });

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
        this.activeModules.entrySet().forEach((module) -> {

            // load that module
            this.disableModule(module.getKey());
        });
    }

    /**
     * get all active modules
     *
     * @return
     */
    public List<Module> getActiveModules() {

        return new ArrayList<>(this.activeModules.values());
    }
}
