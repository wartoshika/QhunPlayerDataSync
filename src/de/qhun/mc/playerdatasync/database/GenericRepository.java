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
import de.qhun.mc.playerdatasync.database.domainmodel.DomainModelAttribute;
import de.qhun.mc.playerdatasync.database.domainmodel.DecoratedDomainModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * a generic implementation of a repository
 *
 * @author Wrath
 * @param <Entity>
 * @param <Primary>
 */
public abstract class GenericRepository<Entity, Primary> implements Repository<Entity, Primary> {

    // the database holder
    protected static DatabaseAdapter database;

    /**
     * get the current class of entity
     *
     * @return
     */
    protected abstract Class<Entity> getEntityClass();

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
                DecoratedDomainModel.getTableName((Class<Entity>) entity.getClass()),
                DecoratedDomainModel.getAttributesWithValues(entity)
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
                DecoratedDomainModel.getTableName((Class<Entity>) entity.getClass()),
                DecoratedDomainModel.getAttributesWithValues(entity)
        );
    }

    /**
     * updates an entity
     *
     * @param entity
     * @return
     */
    @Override
    public boolean update(Entity entity) {

        return GenericRepository.database.query().update(
                DecoratedDomainModel.getTableName((Class<Entity>) entity.getClass()),
                DecoratedDomainModel.getAttributesWithValues(entity)
        );
    }

    /**
     * get all entities
     *
     * @return
     */
    @Override
    public List<Entity> findAll() {

        // get all entities
        List<List<DomainModelAttribute>> rows = GenericRepository.database.query().get(
                DecoratedDomainModel.getTableName(this.getEntityClass()),
                this.getEntityClass()
        );

        // eneity list
        List<Entity> entities = new ArrayList<>();

        // null for an empty result set
        if (rows.isEmpty()) {
            return entities;
        }

        // transform all
        rows.forEach(row -> {

            entities.add(this.transformResultToEntity(row));
        });

        return entities;
    }

    /**
     * find one entity by its primary attribute/attributes
     *
     * @param primary
     * @return
     */
    @Override
    public Entity findByPrimary(Primary primary) {

        List<DomainModelAttribute> queryList = new ArrayList<>();

        // primary can be a normal object or a list
        // if it is a list, the primary contains more values
        // we should extract them
        if (primary instanceof List) {

            // cast to list
            List<?> primaryList = (List<?>) primary;

            // get primary columns and
            // fill the values. the primary index of the decorator is the
            // index of the list!
            queryList = DecoratedDomainModel
                    .getAttributes(this.getEntityClass())
                    .stream().filter(attribute -> attribute.isPrimary)
                    .map(primaryAttribute -> {

                        // get the index from the decorator and use this
                        // index to get the list item as value
                        primaryAttribute.value = primaryList.get(
                                primaryAttribute.field.getAnnotation(
                                        de.qhun.mc.playerdatasync.database.decorators.Primary.class
                                ).index()
                        );

                        return primaryAttribute;
                    })
                    .collect(Collectors.toList());

        } else {

            // get primary column name
            DomainModelAttribute<Primary> primaryAttribute = DecoratedDomainModel
                    .getAttributes(this.getEntityClass())
                    .stream().filter(attribute -> attribute.isPrimary)
                    .findFirst().get();

            // add the primary value
            primaryAttribute.value = primary;

            // stack this up
            queryList.add(primaryAttribute);
        }

        // search query!
        List<List<DomainModelAttribute>> rows = GenericRepository.database.query().get(
                DecoratedDomainModel.getTableName(this.getEntityClass()),
                this.getEntityClass(),
                queryList
        );

        // null for an empty result set
        if (rows.isEmpty()) {
            return null;
        }

        return this.transformResultToEntity(rows.get(0));
    }

    /**
     * find one entity by its primary attribute/attributes or creates an empty
     * model
     *
     * @param primary
     * @return
     */
    public Entity findByPrimaryOrCreate(Primary primary) {

        Entity entity = this.findByPrimary(primary);
        if (entity == null) {

            // create a new entity
            return DecoratedDomainModel.createEmptyInstance(this.getEntityClass());
        }

        return entity;
    }

    /**
     * checks if the given entity is persent
     *
     * @param primary
     * @return
     */
    @Override
    public boolean has(Primary primary) {

        return false;
    }

    /**
     * transforms a Map<string,object> to an entity object
     *
     * @param attributes
     * @return
     */
    protected Entity transformResultToEntity(List<DomainModelAttribute> attributes) {

        // create empty entity
        Entity entity = DecoratedDomainModel.createEmptyInstance(this.getEntityClass());

        // null check
        if (entity == null) {
            return entity;
        }

        // add fields
        attributes.forEach(attribute -> {

            // set value of the given field
            try {
                Class<?> attributeType = attribute.field.getType();

                attribute.field.setAccessible(true);
                attribute.field.set(entity, DatatypeCast.cast(attribute.value, attributeType));
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {

                Main.log.warning("Cannot set the domain model field!");
                Main.log.log(Level.WARNING, ex.getMessage(), ex);
            }
        });

        return entity;
    }
}
