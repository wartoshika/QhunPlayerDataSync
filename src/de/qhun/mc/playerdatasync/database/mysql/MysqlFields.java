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
package de.qhun.mc.playerdatasync.database.mysql;

import de.qhun.mc.playerdatasync.database.DialectFields;
import de.qhun.mc.playerdatasync.database.domainmodel.DomainModelAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * converts datatypes to fields
 *
 * @author Wrath
 */
public class MysqlFields implements DialectFields {

    /**
     * get the mysql field type for the column type
     *
     * @param attribute
     * @return
     */
    @Override
    public String convertFromColumnType(DomainModelAttribute attribute) {

        List<String> types = new ArrayList<>();

        switch (attribute.type) {

            case Double:
            case BigDecimal:
                types.add("DOUBLE(" + attribute.size + "," + attribute.precision + ")");
                break;
            case Date:
                types.add("Datetime");
                break;
            case Integer:
                types.add("INT(" + attribute.size + ")");
                break;
            case Object:
                types.add("BLOB");
                break;
            case Text:
                types.add("TEXT");
                break;
            default:
            case String:
                types.add("VARCHAR(" + attribute.size + ")");
                break;
        }

        // not null?
        if (attribute.notNull) {

            types.add("NOT NULL");
        }

        // join everything together
        return String.join(" ", types);
    }
}
