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
package de.qhun.mc.playerdatasync.database.domainmodel;

import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.database.decorators.Column;
import de.qhun.mc.playerdatasync.database.decorators.ColumnType;
import de.qhun.mc.playerdatasync.database.decorators.DecoratorAccessor;
import de.qhun.mc.playerdatasync.database.decorators.DecoratorGetter;
import de.qhun.mc.playerdatasync.database.decorators.NotNull;
import de.qhun.mc.playerdatasync.database.decorators.Primary;
import de.qhun.mc.playerdatasync.database.decorators.Table;
import de.qhun.mc.playerdatasync.util.DependencyInjection;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * a class that chaches decorator data from an entity by checking its decorator
 * data and provides functionslity like quick accessing table name and column
 * data type
 *
 * @author Wrath
 */
public class DecoratedDomainModel {

    // the table name cache
    private static final Map<Class<?>, String> tableNameCache = new HashMap<>();

    // the attribute cache
    private static final Map<Class<?>, List<DomainModelAttribute>> attributeMetadataCache = new HashMap<>();

    /**
     * get the table name for an entity
     *
     * @param <Entity>
     * @param entity
     * @return
     */
    public static <Entity extends Object> String getTableName(Class<Entity> entity) {

        // available in the cache?
        if (DecoratedDomainModel.tableNameCache.containsKey(entity)) {

            // yes available!
            return DecoratedDomainModel.tableNameCache.get(entity);
        }

        // not available, find by decorator!
        Table tableDecorator = DecoratorGetter.getClassDecoratorOrNull(Table.class, entity);

        // check if there is a name available
        String tableName = !"".equals(tableDecorator.name()) ? tableDecorator.name() : entity.getSimpleName().toLowerCase();

        // cache the value
        DecoratedDomainModel.tableNameCache.put(entity, tableName);

        return tableName;
    }

    /**
     * get metadata for all attributes of the given entity
     *
     * @param <Entity>
     * @param entity
     * @return
     */
    public static <Entity extends Object> List<DomainModelAttribute> getAttributes(Class<Entity> entity) {

        // allready in cache?
        if (DecoratedDomainModel.attributeMetadataCache.containsKey(entity)) {

            // yes available!
            return DecoratedDomainModel.attributeMetadataCache.get(entity);
        }

        // not in cache, get from the model itself
        List<DecoratorAccessor<Column>> fields = DecoratorGetter.getFieldsWithDecorator(Column.class, entity);
        List<DomainModelAttribute> attributes = new ArrayList<>();

        // iterate over the Fields
        fields.forEach(accessor -> {

            // get the real attribute name
            String realName = accessor.field.getName();

            // get the column name in the database
            String columnName = !"".equals(accessor.decorator.name()) ? accessor.decorator.name() : realName;

            // now the column type
            ColumnType columnType = accessor.decorator.type();

            // construct a DomainModelAttribute
            DomainModelAttribute attribute = new DomainModelAttribute();
            attribute.attributeName = realName;
            attribute.columnName = columnName;
            attribute.type = columnType;
            attribute.field = accessor.field;
            attribute.isPrimary = accessor.field.isAnnotationPresent(Primary.class);
            attribute.size = accessor.decorator.size();
            attribute.precision = accessor.decorator.precision();
            attribute.notNull = accessor.field.isAnnotationPresent(NotNull.class);
            attribute.modelClass = entity;

            // add it to the list
            attributes.add(attribute);
        });

        // cache all attributes
        DecoratedDomainModel.attributeMetadataCache.put(entity, attributes);

        return attributes;
    }

    /**
     * get all attributes including its value
     *
     * @param <Entity>
     * @param entity
     * @return
     */
    public static <Entity extends Object> List<DomainModelAttribute> getAttributesWithValues(Entity entity) {

        // first get all attributes
        List<DomainModelAttribute> attributes = DecoratedDomainModel.getAttributes(entity.getClass());

        // iterate over all attributes and get its value
        attributes.forEach(attribute -> {

            attribute.value = attribute.getFieldValue(entity);
        });

        // dont cache, just return!
        return attributes;
    }

    /**
     * creates an empty entity class
     *
     * @param <Entity>
     * @param entity
     * @return
     */
    public static <Entity extends Object> Entity createEmptyInstance(Class<Entity> entity) {

        return DependencyInjection.build(entity);
    }
}
