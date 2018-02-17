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

import de.qhun.mc.playerdatasync.modules.AbstractModule;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

/**
 * the inventory module that stores multiple inventories in the database
 *
 * @author Wrath
 */
public class InventoryModule extends AbstractModule<InventoryConfiguration> {

    // the repository
    private PlayerInventoryRepository repository;

    private UUID eventReferenceJoin;
    private UUID eventReferenceQuit;

    @Override
    public void construct() {

        // setup the repository
        this.repository = new PlayerInventoryRepository();

        // setup domain models
        domainModelSetup.setupDomainModel(new Class<?>[]{
            PlayerInventory.class
        });
    }

    @Override
    public boolean enable() {

        // add player join event
        this.eventReferenceJoin = this.eventRegister.addEvent(PlayerJoinEvent.class, (event) -> this.onPlayerJoin(event));
        this.eventReferenceQuit = this.eventRegister.addEvent(PlayerQuitEvent.class, (event) -> this.onPlayerQuit(event));

        return true;
    }

    @Override
    public boolean disable() {

        // log disable 
        this.logInfoPrefixed("Module is disableing. Saving all inventory data for the online players.");

        // removes the events from the event register
        this.eventRegister.removeEvent(PlayerJoinEvent.class, this.eventReferenceJoin);
        this.eventRegister.removeEvent(PlayerQuitEvent.class, this.eventReferenceQuit);

        // save the inventory for all online players
        this.plugin.getServer().getOnlinePlayers().forEach(this::savePlayerInventory);

        // done
        return true;
    }

    /**
     * fires if a player is joining
     *
     * @param event
     */
    private void onPlayerJoin(PlayerJoinEvent event) {

        // first clear everything in the players inventory!
        // evil! isn't it?
        // this will prevent the user from eventually seeing items that
        // he/she does not own in the server network. this will
        // meight be visible when the server is laggy and there are not
        // many ticks per second
        Player player = event.getPlayer();
        player.getInventory().clear();

        // now queue a task to get the current inventory from the database
        this.createAsyncTask(() -> {

            // get every inventory for the player by searching by uuid
            UUID playerUuid = event.getPlayer().getUniqueId();
            PlayerInventory inventory = this.repository.findByPrimary(
                    // branch name and uuid are the keys! in that order!
                    Arrays.asList(playerUuid, this.configuration.getBranchName())
            );

            // check if the player is allready in the database
            if (inventory != null) {

                // now replace every inventory
                player.getInventory().setContents(inventory.getInventory().getContents());

                // replace ender chest
                if (this.configuration.isEnderChestEnabled()) {

                    player.getEnderChest().setContents(inventory.getEnderChest().getContents());
                }
            }

            // wait 10 ticks -> 500 ms
        }, 10L);
    }

    /**
     * fires if a player is quitting
     *
     * @param event
     */
    private void onPlayerQuit(PlayerQuitEvent event) {

        this.savePlayerInventory(event.getPlayer());
    }

    /**
     * saves the inventory
     *
     * @param player
     */
    private void savePlayerInventory(Player player) {

        // store all inventories
        // get player from db
        UUID playerUuid = player.getUniqueId();
        PlayerInventory inventory = this.repository.findByPrimary(
                Arrays.asList(playerUuid, this.configuration.getBranchName())
        );

        // check if a player instance exists
        if (inventory == null) {

            inventory = new PlayerInventory(
                    player.getUniqueId(),
                    this.configuration.getBranchName()
            );
        }

        // save inventory
        inventory.setInventory(player.getInventory());

        // ender chest
        if (this.configuration.isEnderChestEnabled()) {

            inventory.setEnderChest(player.getEnderChest());
        }

        // store the dataset
        this.repository.store(inventory);
    }

}
