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

import de.qhun.mc.playerdatasync.database.decorators.ColumnType;
import java.lang.reflect.Field;

/**
 * a simple class containung instances of column type and value pairs
 *
 * @author Wrath
 * @param <Value>
 */
public class DomainModelAttribute<Value extends Object> {

    // the class that this attribute belongs to
    public Class<? extends Object> modelClass;
    
    // the supported column datatype
    public ColumnType type = ColumnType.Object;

    // the effective value if the attribute
    public Value value;

    // the column name of the attribute
    public String columnName;

    // the name of the attribute on the domain model
    public String attributeName;

    // the field of the model to get access to its value later
    public Field field;
    
    // primary attribute?
    public boolean isPrimary = false;
    
    // not null constraint?
    public boolean notNull = false;
    
    // the size of the field. eg varchar(20) or int(6)
    public int size;
    
    // the numeric precision if available
    public int precision;

    /**
     * get the current value stored in the field of the entity
     *
     * @param <Entity>
     * @param object
     * @return
     */
    public <Entity extends Object> Value getFieldValue(Entity object) {

        Value fieldValue;

        // try getting its value
        try {
            this.field.setAccessible(true);
            fieldValue = (Value) this.field.get(object);
        } catch (IllegalAccessException | IllegalArgumentException ex) {

            fieldValue = null;
        }

        return fieldValue;
    }
}
