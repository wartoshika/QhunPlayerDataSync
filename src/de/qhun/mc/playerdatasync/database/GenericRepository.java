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

import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.database.decorators.Column;
import de.qhun.mc.playerdatasync.database.decorators.DecoratorAccessor;
import de.qhun.mc.playerdatasync.database.decorators.DecoratorGetter;
import de.qhun.mc.playerdatasync.database.decorators.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * a generic implementation of a repository
 *
 * @author Wrath
 * @param <Entity>
 * @param <Primary>
 */
public class GenericRepository<Entity, Primary> implements Repository<Entity, Primary> {

    // the database holder
    protected static DatabaseAdapter database;

    /**
     * set the current active database connection
     *
     * @param database
     */
    public static void setDatabaseAdapter(DatabaseAdapter database) {

        GenericRepository.database = database;
    }

    /**
     * saves an entity
     *
     * @param entity
     * @return
     */
    @Override
    public boolean store(Entity entity) {

        return GenericRepository.database.query().replaceInto(
                this.getTableName((Class<Entity>) entity.getClass()),
                this.getFields(entity)
        );
    }

    /**
     * deletes an entity
     *
     * @param entity
     * @return
     */
    @Override
    public boolean remove(Entity entity) {

        return GenericRepository.database.query().delete(
                this.getTableName((Class<Entity>) entity.getClass()),
                this.getFields(entity)
        );
    }

    /**
     * get all entities
     *
     * @return
     */
    @Override
    public List<Entity> findAll() {

        return new ArrayList<>();
    }

    /**
     * find one entity by its primary attribute
     *
     * @param entityClass
     * @param primary
     * @return
     */
    @Override
    public Entity findByPrimery(Class<Entity> entityClass, Primary primary) {

        // get primary column name
        Map<String, Primary> data = new HashMap<>();
        data.put(this.getPrimaryColumnName(entityClass, primary), primary);

        // search query!
        List<Map<String, Object>> rows = GenericRepository.database.query().get(
                this.getTableName(entityClass), (Map<String, Object>) data
        );

        // @todo: rows.get(0) may return error!
        return this.transformResultRowToEntity(rows.get(0), entityClass);
    }

    /**
     * find one entity by its primary attribute or creates an empty model
     *
     * @param entityClass
     * @param primary
     * @return
     */
    public Entity findByPrimaryOrCreate(Class<Entity> entityClass, Primary primary) {

        Entity entity = this.findByPrimery(entityClass, primary);
        if (entity == null) {

            // create a new entity
            try {
                return entityClass.newInstance();
            } catch (Exception ex) {
            }

            return null;
        }

        return entity;

    }

    /**
     * checks if the given entity is persent
     *
     * @param entityClass
     * @param primary
     * @return
     */
    @Override
    public boolean has(Class<Entity> entityClass, Primary primary) {

        return false;
    }

    /**
     * get the tablename for the given entity
     *
     * @param entity
     * @return
     */
    protected String getTableName(Class<Entity> entity) {

        // get the table decorator
        Table tableDecorator = DecoratorGetter.getClassDecoratorOrNull(Table.class, entity);

        // check if there is a name available
        return !"".equals(tableDecorator.name()) ? tableDecorator.name() : entity.getSimpleName().toLowerCase();
    }

    /**
     * get all fields that should be stored, updated ...
     *
     * @param entity
     * @return
     */
    protected Map<String, Object> getFields(Entity entity) {

        // get storable attributes
        List<DecoratorAccessor<Column>> fields = DecoratorGetter.getFieldsWithDecorator(Column.class, entity.getClass());

        // create a mapping table
        Map<String, Object> values = new HashMap<>();

        // iterate over the Fields
        fields.forEach(accessor -> {

            // get the column name
            String columnName = !"".equals(accessor.decorator.name()) ? accessor.decorator.name() : accessor.field.getName();

            // get tje column value
            Object value;
            try {

                // @todo: use getter methods to get the actual value
                value = accessor.field.get(entity);
            } catch (IllegalAccessException | IllegalArgumentException ex) {

                value = null;
            }

            // store it in the map
            values.put(columnName, value);
        });

        return values;
    }

    /**
     * searches for the primary attribute on the entity
     *
     * @param entity
     * @return
     */
    protected Primary getPrimaryValue(Entity entity) {

        // get fields with primary decorator
        List<DecoratorAccessor<de.qhun.mc.playerdatasync.database.decorators.Primary>> fields
                = DecoratorGetter.getFieldsWithDecorator(
                        de.qhun.mc.playerdatasync.database.decorators.Primary.class, entity.getClass()
                );

        // field count should be equal to 1!
        // @todo: add count check
        try {

            // get the primary value
            // @todo: use getter methods to get the actual value
            return (Primary) fields.get(0).field.get(entity);
        } catch (IllegalAccessException | IllegalArgumentException ex) {

            return null;
        }
    }

    /**
     * get the column name for the primary attribute on entity
     *
     * @param entityClass
     * @param primary
     * @return
     */
    protected String getPrimaryColumnName(Class<Entity> entityClass, Primary primary) {

        // get all columns
        List<DecoratorAccessor<Column>> fields = DecoratorGetter.getFieldsWithDecorator(Column.class, entityClass);

        // iterate over the Fields
        for (DecoratorAccessor<Column> accessor : fields) {

            // check if this field also has the primary decorator
            for (Annotation decorator : accessor.field.getAnnotations()) {

                if (decorator.annotationType().equals(de.qhun.mc.playerdatasync.database.decorators.Primary.class)) {

                    return !"".equals(accessor.decorator.name()) ? accessor.decorator.name() : accessor.field.getName();
                }
            }

        }

        Main.log.warning("Could not find entity primary attribute for entity " + entityClass.getName());
        return null;
    }

    /**
     * transforms a Map<string,object> to an entity object
     *
     * @param data
     * @param entityClass
     * @return
     */
    protected Entity transformResultRowToEntity(Map<String, Object> data, Class<Entity> entityClass) {

        try {

            // create empty entity
            Entity entity = entityClass.newInstance();

            // add fields
            for (Entry<String, Object> value : data.entrySet()) {

                // set value of the given field
                Field field = entityClass.getDeclaredField(value.getKey());
                field.setAccessible(true);
                field.set(entity, value.getValue());
            }

            return entity;

        } catch (Exception ex) {
        }

        return null;
    }
}
