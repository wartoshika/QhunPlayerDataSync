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
package de.qhun.mc.playerdatasync.modules.metadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Color;

/**
 * a class that can serialize potion effects
 *
 * @author Wrath
 */
public class PotionEffectSerializer {

    /**
     * serializes the given potion effects into a base64 string
     *
     * @param effects
     * @return
     */
    public static String serialize(List<PotionEffect> effects) {

        try {
            // prepare serialisation
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream objectStream = new BukkitObjectOutputStream(outputStream);

            // write the number of effects to the stream to allow
            // easier deserilisation
            objectStream.writeInt(effects.size());

            // every effect should be put into the output stream
            for (PotionEffect effect : effects) {

                // write each important type
                objectStream.writeObject(effect.getType().getName());
                objectStream.writeInt(effect.getDuration());
                objectStream.writeInt(effect.getAmplifier());
                objectStream.writeBoolean(effect.isAmbient());
                objectStream.writeBoolean(effect.hasParticles());
                objectStream.writeObject(effect.getColor());
            }

            // close the stream and convert to base64
            objectStream.close();
            return Base64.encodeBase64String(outputStream.toByteArray());

        } catch (Exception ex) {

            return "serialize error";
        }
    }

    /**
     * deserialize a list of effects from a base64 string
     *
     * @param base64SerializedEffectList
     * @return
     */
    public static List<PotionEffect> deserialize(String base64SerializedEffectList) {

        // create an empty list
        List<PotionEffect> effects = new ArrayList<>();

        try {
            // create a byte array input stream with the deserialised content
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(base64SerializedEffectList));
            BukkitObjectInputStream objectStream = new BukkitObjectInputStream(inputStream);

            // iterate over every effect in the stream
            // the first readInt is the amount of effects
            int effectAmount = objectStream.readInt();
            for (int i = 0; i < effectAmount; i++) {

                // try to read all data from the stream
                effects.add(new PotionEffect(
                        PotionEffectType.getByName((String) objectStream.readObject()),
                        objectStream.readInt(),
                        objectStream.readInt(),
                        objectStream.readBoolean(),
                        objectStream.readBoolean(),
                        (Color) objectStream.readObject())
                );

            }

            // close the stream and return the crafted inventory
            objectStream.close();

        } catch (Exception ex) {

            // data is corrupt... return an empty list
        }

        return effects;
    }
}
