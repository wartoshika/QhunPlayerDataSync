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

import de.qhun.mc.playerdatasync.database.decorators.Column;
import de.qhun.mc.playerdatasync.database.decorators.ColumnType;
import de.qhun.mc.playerdatasync.database.decorators.Entity;
import de.qhun.mc.playerdatasync.database.decorators.NotNull;
import de.qhun.mc.playerdatasync.database.decorators.Primary;
import de.qhun.mc.playerdatasync.database.decorators.Table;
import java.util.UUID;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * the player's normal inventory
 *
 * @author Wrath
 */
@Entity
@Table
public class PlayerInventory {

    @Primary(index = 0)
    @Column(size = 36)
    @NotNull
    private UUID uuid;

    @Primary(index = 1)
    @Column(size = 24)
    @NotNull
    private String branch;

    @Column(type = ColumnType.Text)
    private String inventory;

    @Column(type = ColumnType.Text)
    private String enderChest;

    protected PlayerInventory() {
    }

    public PlayerInventory(UUID uuid, String branch) {

        this.uuid = uuid;
        this.branch = branch;
    }

    /**
     * get the uuid
     *
     * @return
     */
    public UUID getUuid() {

        return this.uuid;
    }

    /**
     * get players normal inventory
     *
     * @return
     */
    public Inventory getInventory() {

        return InventorySerializer.deserialize(this.inventory, InventoryType.PLAYER);
    }

    /**
     * get players ender chest
     *
     * @return
     */
    public Inventory getEnderChest() {

        return InventorySerializer.deserialize(this.enderChest, InventoryType.ENDER_CHEST);
    }

    /**
     * get the branch name
     *
     * @return
     */
    public String getBranch() {

        return this.branch;
    }

    /**
     * set the normal inventory for the player
     *
     * @param inventory
     */
    public void setInventory(Inventory inventory) {

        this.inventory = InventorySerializer.serialize(inventory, InventoryType.PLAYER);
    }

    /**
     * set the ender chest for the player
     *
     * @param enderChest
     */
    public void setEnderChest(Inventory enderChest) {

        this.enderChest = InventorySerializer.serialize(enderChest, InventoryType.ENDER_CHEST);
    }
}
