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
import java.util.logging.Level;

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
        DomainModelAttribute<Primary> primaryAttribute = DecoratedDomainModel
                .getAttributes(entityClass)
                .stream().filter(attribute -> attribute.isPrimary)
                .findFirst().get();

        // add the primary value
        primaryAttribute.value = primary;

        // search query!
        List<List<DomainModelAttribute>> rows = GenericRepository.database.query().get(
                DecoratedDomainModel.getTableName(entityClass),
                entityClass,
                Arrays.asList(primaryAttribute)
        );

        // null for an empty result set
        if (rows.isEmpty()) {
            return null;
        }

        return this.transformResultToEntity(rows.get(0), entityClass);
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
     * transforms a Map<string,object> to an entity object
     *
     * @param attributes
     * @param entityClass
     * @return
     */
    protected Entity transformResultToEntity(List<DomainModelAttribute> attributes, Class<Entity> entityClass) {

        try {

            // create empty entity
            Entity entity = entityClass.newInstance();

            // add fields
            attributes.forEach(attribute -> {

                // set value of the given field
                try {
                    attribute.field.setAccessible(true);
                    attribute.field.set(entity, attribute.value);
                } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {

                    Main.log.warning("Cannot set the domain model field!");
                    Main.log.log(Level.WARNING, ex.getMessage(), ex);
                }
            });

            return entity;

        } catch (IllegalAccessException | InstantiationException ex) {

            Main.log.warning("Could not create domain model");
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return null;
    }
}
