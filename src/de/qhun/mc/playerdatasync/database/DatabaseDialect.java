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

import de.qhun.mc.playerdatasync.database.domainmodel.DomainModelAttribute;
import java.util.List;

/**
 *
 * @author Wrath
 */
public interface DatabaseDialect {

    public String createTable(String tableName, List<DomainModelAttribute> columns);

    public String createTableIfNotExists(String tableName, List<DomainModelAttribute> columns);

    public String insert(String tableName, List<DomainModelAttribute> values);

    public String update(String tableName, List<DomainModelAttribute> values);

    public String delete(String tableName, List<DomainModelAttribute> values);

    public String replaceInto(String tableName, List<DomainModelAttribute> values);
    
    public String get(String tableName, List<DomainModelAttribute> values);

    //public Object getDatabaseRepresentation(Object value);
}
