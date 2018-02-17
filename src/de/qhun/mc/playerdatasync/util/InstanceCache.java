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
package de.qhun.mc.playerdatasync.util;

import java.util.HashMap;
import java.util.Map;

/**
 * a class that is capable of doing dependency injection and building instances
 * of a given class.
 *
 * @author Wrath
 */
public class InstanceCache {

    // the object cache
    private static final Map<Class<?>, Object> objectCache = new HashMap<>();

    /**
     * reads a value from the cache
     *
     * @param <T>
     * @param className
     * @return
     */
    public static <T extends Object> T getFromCache(Class<T> className) {

        // get an instance from the cache
        // @todo: add a warning when null is present!
        return (T) InstanceCache.objectCache.get(className);
    }

    /**
     * put an instance into the cache. any existing instance will be
     * overwritten.
     *
     * @param className
     * @param object
     */
    public static void setToCache(Class<?> className, Object object) {

        InstanceCache.objectCache.put(className, object);
    }

    /**
     * put several instances into the cache
     *
     * @param objects
     */
    public static void setToCache(Object[] objects) {

        for (Object o : objects) {

            // null check to access the class name
            if (o != null) {
                InstanceCache.setToCache(o.getClass(), o);
            }
        }
    }

    /**
     * puts several objects into the cache by naming its class/interface
     *
     * @param objectWithClassReference
     */
    public static void setToCache(Map<Class<?>, Object> objectWithClassReference) {

        objectWithClassReference.forEach((className, object) -> {

            InstanceCache.setToCache(className, object);
        });
    }
}
