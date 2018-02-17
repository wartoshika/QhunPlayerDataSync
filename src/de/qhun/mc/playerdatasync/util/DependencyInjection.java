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

import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.database.decorators.DecoratorAccessor;
import de.qhun.mc.playerdatasync.database.decorators.DecoratorGetter;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Level;

/**
 * a class that will autoload all dependencies from @Autoload into the instance
 *
 * @author Wrath
 */
public class DependencyInjection {

    /**
     * constructs an instance and apply all instances to available @Autoload
     * decorators
     *
     * @param <T>
     * @param instanceClassName
     * @return
     */
    public static <T extends Object> T build(Class<T> instanceClassName) {

        T instance = null;

        // create the empty instance
        for (Constructor<?> ctor : instanceClassName.getDeclaredConstructors()) {

            // get 0 parameter constructor
            if (ctor.getParameterCount() == 0) {

                ctor.setAccessible(true);
                try {
                    instance = (T) ctor.newInstance();
                    break;
                } catch (Exception ex) {

                    Main.log.severe("Try to instantiate a class without 0 parameter constructor!");
                    Main.log.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }

        // null check
        if (instance == null) {
            return instance;
        }

        // get all fields that are decorated with @Autoload
        List<DecoratorAccessor<Autoload>> fields = DecoratorGetter.getFieldsWithDecorator(Autoload.class, instanceClassName);

        // iterate over the autoload fields
        for (DecoratorAccessor<Autoload> field : fields) {

            // load from cache
            try {
                field.field.setAccessible(true);
                field.field.set(
                        instance,
                        InstanceCache.getFromCache(field.field.getType())
                );
            } catch (IllegalAccessException | IllegalArgumentException ex) {

                // log error
                Main.log.severe("Error while @Autoload!");
                Main.log.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        return instance;
    }

    /**
     * register an object at the DI system
     *
     * @param className
     * @param object
     */
    public static void register(Class<?> className, Object object) {
        
        InstanceCache.setToCache(className, object);
    }

    /**
     * register an object at the DI system
     *
     * @param object
     */
    public static void register(Object object) {

        DependencyInjection.register(object.getClass(), object);
    }
}
