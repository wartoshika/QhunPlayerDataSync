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

import de.qhun.mc.playerdatasync.Main;
import org.bukkit.plugin.PluginManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
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
    private final Map<Class<? extends Event>, Map<UUID, Consumer<Event>>> eventStack;

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
     * @param <E> the org.bukkit.event.Event
     * @param bukkitEvent the org.bukkit.event.Event
     * @param callback the executed callback function
     * @return the reference to remoce this event
     */
    public <E extends Event> UUID addEvent(Class<E> bukkitEvent, Consumer<E> callback) {

        // check if the event allready exists. if not, create a List of callable
        // to avoid nullpointer exeption
        if (this.eventStack.get(bukkitEvent) == null) {

            // create a new list
            this.eventStack.put(bukkitEvent, new HashMap<>());
        }

        // add the event to the list
        Map<UUID, Consumer<Event>> callableStack = this.eventStack.get(bukkitEvent);

        // generate a reference uuid
        UUID reference = UUID.randomUUID();

        callableStack.put(reference, (Consumer<Event>) callback);
        this.eventStack.put(bukkitEvent, callableStack);

        // return the reference
        return reference;
    }

    /**
     * removes give given event by naming its reference
     *
     * @param <E>
     * @param bukkitEvent
     * @param reference
     */
    public <E extends Event> void removeEvent(Class<E> bukkitEvent, UUID reference) {

        // get the callback list
        Map<UUID, Consumer<Event>> callbackList = this.eventStack.get(bukkitEvent);

        // remove the given reference
        callbackList.remove(reference);
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
        Map<UUID, Consumer<Event>> callableStack = this.eventStack.get(bukkitEvent.getClass());

        // execute each function
        callableStack.values().forEach((callable) -> {
            try {

                // execute the callback with the bukkit event
                callable.accept(bukkitEvent);
            } catch (Exception ex) {

                // print error
                Main.log.warning("Error while Executing an Event callback!");
                Main.log.log(Level.WARNING, ex.getMessage(), ex);
            }
        });
    }
}
