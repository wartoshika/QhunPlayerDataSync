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
package de.qhun.mc.playerdatasync.events;

import org.bukkit.plugin.PluginManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * a class that is capable of redirecting events to all modules that required an
 * ingame event
 *
 * @author Wrath
 */
public class EventRegister {

    // the event stack
    private Map<Class<? extends Event>, List<Consumer<Event>>> eventStack;

    // plugin storage
    private final JavaPlugin plugin;

    public EventRegister(JavaPlugin plugin) {

        this.plugin = plugin;

        // create empty stack
        this.eventStack = new HashMap<>();
    }

    /**
     * registers all available events into the bukkit/spigot event system
     */
    public void registerAvailableBukkitEvents() {

        // get the plugin manager
        PluginManager pluginManager = this.plugin.getServer().getPluginManager();

        // now setup all events
        pluginManager.registerEvents(new PlayerConnectionEvent(this), this.plugin);
    }

    /**
     * registers an event
     *
     * @param bukkitEvent the org.bukkit.event.Event
     * @param callback the executed callback function
     */
    public void addEvent(Class<? extends Event> bukkitEvent, Consumer<? extends Event> callback) {

        // check if the event allready exists. if not, create a List of callable
        // to avoid nullpointer exeption
        if (this.eventStack.get(bukkitEvent) == null) {

            // create a new list
            this.eventStack.put(bukkitEvent, new ArrayList<>());
        }

        // add the event to the list
        List<Consumer<Event>> callableStack = this.eventStack.get(bukkitEvent);
        callableStack.add((Consumer<Event>) callback);
        this.eventStack.put(bukkitEvent, callableStack);
    }

    /**
     * executes all registered events
     *
     * @param bukkitEvent
     */
    public void executeEvent(Event bukkitEvent) {

        // only iterate if there is a stack
        if (this.eventStack.get(bukkitEvent.getClass()) == null) {

            return;
        }
        // get the list
        List<Consumer<Event>> callableStack = this.eventStack.get(bukkitEvent.getClass());

        // execute each function
        callableStack.forEach((callable) -> {
            try {

                // execute the callback with the bukkit event
                callable.accept(bukkitEvent);
            } catch (Exception ex) {
            }
        });
    }
}
