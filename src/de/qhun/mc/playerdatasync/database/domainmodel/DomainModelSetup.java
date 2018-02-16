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
import de.qhun.mc.playerdatasync.database.DatabaseAdapter;
import de.qhun.mc.playerdatasync.database.decorators.Entity;

/**
 * a class that will check if a model setup is required. eg. table creation
 *
 * @author Wrath
 */
public class DomainModelSetup {

    // the database adapter holder
    private final DatabaseAdapter databaseAdapter;
    
    public DomainModelSetup(DatabaseAdapter databaseAdapter) {
        
        this.databaseAdapter = databaseAdapter;
    }

    /**
     * setup all nessesary database things to get everything run
     *
     * @param entity
     */
    public void setupDomainModel(Class<?> entity) {
        
        // check if this is an entity
        if (!entity.isAnnotationPresent(Entity.class)) {
            
            Main.log.warning("Error in domain model setup for class " + entity.getName());
            return;
        }

        // query database
        try {
            this.databaseAdapter.query().createTableIfNotExists(
                    DecoratedDomainModel.getTableName(entity),
                    DecoratedDomainModel.getAttributes(entity)
            );

            // log creation statement
            Main.log.info("Successfully created table for DomainModel: " + entity.getSimpleName());
            
        } catch (Exception ex) {
            
            throw new Error("Could not setup domain model table for " + entity.getName(), ex);
        }
        
    }

    /**
     * setup multiple entities
     *
     * @param entities
     */
    public void setupDomainModel(Class<?>[] entities) {
        
        for (Class<?> entity : entities) {
            
            this.setupDomainModel(entity);
        }
    }
}
