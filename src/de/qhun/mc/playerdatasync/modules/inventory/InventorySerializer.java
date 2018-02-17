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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.Bukkit;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * a class that is capable of serializing a players inventory or a chest content
 * into a base64 string.
 *
 * idea from: https://gist.github.com/graywolf336/8153678
 *
 * @author Wrath
 */
public class InventorySerializer {

    /**
     * serializes the given inventory into a base64 string
     *
     * @param inventory
     * @param fallbackInventory
     * @return
     */
    public static String serialize(Inventory inventory, InventoryType fallbackInventory) {

        try {
            // prepare serialisation
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream objectStream = new BukkitObjectOutputStream(outputStream);

            // Write the size inventory type
            objectStream.writeObject(inventory.getType());

            // every item should be put into the output stream
            for (int i = 0; i < inventory.getSize(); i++) {

                // add this item
                objectStream.writeObject(inventory.getItem(i));
            }

            // close the stream and convert to base64
            objectStream.close();
            return Base64.encodeBase64String(outputStream.toByteArray());

        } catch (Exception ex) {

            // serialize an empty inventory
            try {

                return InventorySerializer
                        .serialize(Bukkit.getServer().createInventory(null, fallbackInventory), fallbackInventory);
            } catch (Exception exx) {

                // ok ... this is wired... throw an error
                throw new Error("Could not serialize an inventory!", exx);
            }
        }
    }

    /**
     * deserialize an inventory from a base64 string
     *
     * @param base64SerializedInventory
     * @param fallbackInventory
     * @return
     */
    public static Inventory deserialize(String base64SerializedInventory, InventoryType fallbackInventory) {

        try {
            // create a byte array input stream with the deserialised content
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(base64SerializedInventory));
            BukkitObjectInputStream objectStream = new BukkitObjectInputStream(inputStream);

            // read the inventory type from the stream
            InventoryType type = (InventoryType) objectStream.readObject();

            // create a bukkit input stream and an empty inventory to store the items in
            Inventory inventory = Bukkit.getServer().createInventory(null, type);

            // iterate over every items in the stream
            for (int i = 0; i < inventory.getSize(); i++) {

                // set the item
                inventory.setItem(i, (ItemStack) objectStream.readObject());
            }

            // close the stream and return the crafted inventory
            objectStream.close();
            return inventory;

        } catch (Exception ex) {

            // data is corrupt... return an empty inventory
            return Bukkit.getServer().createInventory(null, fallbackInventory);
        }
    }
}
