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
package de.qhun.mc.playerdatasync.database.decorators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * a utility class to get class, method, field annotations in a generic way
 *
 * @author Wrath
 */
public class DecoratorGetter {

    /**
     * get all fields with the given decorator and return the infos incl.
     * decorator data
     *
     * @param <D> the decorator type
     * @param <DC> the decorator class
     * @param decorator
     * @param entity the object to test on
     * @return
     */
    public static <D extends Annotation, DC extends Class<D>> List<DecoratorAccessor<D>>
            getFieldsWithDecorator(DC decorator, Class<?> entity) {

        Field[] fields = entity.getDeclaredFields();
        List<DecoratorAccessor<D>> accessorList = new ArrayList<>();

        for (Field field : fields) {

            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(decorator)) {

                    DecoratorAccessor accessor = new DecoratorAccessor();

                    // add values to the accessor
                    accessor.decorator = annotation;
                    accessor.field = field;

                    // stack ip up
                    accessorList.add(accessor);
                }
            }
        }

        // check if there is a super class
        if (!entity.getSuperclass().equals(Object.class)) {

            // yes, add these fields via recursive function call
            accessorList.addAll(DecoratorGetter.getFieldsWithDecorator(decorator, entity.getSuperclass()));
        }

        return accessorList;
    }

    /**
     * get a specific decorator from the class of entity or null
     *
     * @param <D>
     * @param <DC>
     * @param decorator
     * @param entity
     * @return
     */
    public static <D extends Annotation, DC extends Class<D>> D
            getClassDecoratorOrNull(DC decorator, Class<?> entity) {

        // iterate through all decorators
        for (Annotation definedDecorator : entity.getDeclaredAnnotations()) {

            if (definedDecorator.annotationType().equals(decorator)) {

                return (D) definedDecorator;
            }
        }

        return null;
    }

}
