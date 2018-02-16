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
package de.qhun.mc.playerdatasync.database;

import java.util.UUID;

/**
 * a class that is capable of casting different types
 *
 * @author Wrath
 */
public class DatatypeCast {

    /**
     * casts a source generic object to a spefific class
     *
     * @param <T>
     * @param source
     * @param target
     * @return
     */
    public static <T extends Object> T cast(Object source, Class<T> target) {

        if (target.equals(UUID.class)) {

            return (T) UUID.fromString(source.toString());
        } else {

            // try a generic java cast
            return (T) source;
        }
    }

}
