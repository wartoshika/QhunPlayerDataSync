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

import de.qhun.mc.playerdatasync.database.DatabaseDialect;
import de.qhun.mc.playerdatasync.database.DialectFields;
import de.qhun.mc.playerdatasync.database.domainmodel.DomainModelAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Wrath
 */
public class MysqlDialect implements DatabaseDialect {

    private static final DialectFields fieldConverter = new MysqlFields();

    @Override
    public String createTable(String tableName, List<DomainModelAttribute> columns) {

        String baseFormat = "CREATE TABLE `%s` (%s%s) charset=utf8";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        columns.forEach(column -> {

            columnList.add(String.format(
                    "%s %s",
                    column.columnName,
                    fieldConverter.convertFromColumnType(column)
            ));
        });

        // search for primary existing
        List<DomainModelAttribute> primary = columns.stream()
                .filter(predicate -> predicate.isPrimary)
                .collect(Collectors.toList());

        String primaryKey = "";
        if (primary.size() > 0) {

            // get all column names from the primary keys
            primaryKey = "`";
            primaryKey += String.join("`,`", primary.stream().map(p -> p.columnName).collect(Collectors.toList()));
            primaryKey += "`";
        }

        // all together
        return String.format(
                baseFormat, tableName, String.join(",", columnList),
                !"".equals(primaryKey) ? ", PRIMARY KEY (" + primaryKey + ")" : ""
        );
    }

    @Override
    public String createTableIfNotExists(String tableName, List<DomainModelAttribute> columns) {

        String baseFormat = "CREATE TABLE IF NOT EXISTS `%s` (%s%s) charset=utf8";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        columns.forEach(column -> {

            columnList.add(String.format(
                    "%s %s",
                    column.columnName,
                    fieldConverter.convertFromColumnType(column)
            ));
        });

        // search for primary existing
        List<DomainModelAttribute> primary = columns.stream()
                .filter(predicate -> predicate.isPrimary)
                .collect(Collectors.toList());

        String primaryKey = "";
        if (primary.size() > 0) {

            // get all column names from the primary keys
            primaryKey = "`";
            primaryKey += String.join("`,`", primary.stream().map(p -> p.columnName).collect(Collectors.toList()));
            primaryKey += "`";
        }

        // all together
        return String.format(
                baseFormat, tableName, String.join(",", columnList),
                !"".equals(primaryKey) ? ", PRIMARY KEY (" + primaryKey + ")" : ""
        );
    }

    @Override
    public String insert(String tableName, List<DomainModelAttribute> values) {

        String baseFormat = "INSERT INTO `%s` (%s) VALUES (%s)";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        values.forEach(value -> {

            columnList.add(value.columnName);
        });

        // apply as many questionmarks as there are columns
        String valueQuestionmarks = String.join("", Collections.nCopies(columnList.size(), "?,"));

        // all together
        return String.format(
                baseFormat,
                tableName,
                String.join(",", columnList),
                // remove last char if available
                valueQuestionmarks.replaceFirst(".$", "")
        );
    }

    @Override
    public String update(String tableName, List<DomainModelAttribute> values) {

        return "";
    }

    @Override
    public String delete(String tableName, List<DomainModelAttribute> values) {

        // check if there are values
        if (values.isEmpty()) {

            throw new Error("There should be values to remove! If you want to truncate the table, use truncate instead!");
        }

        String baseFormat = "DELETE from `%s` WHERE %s";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        values.forEach(value -> {

            columnList.add("`" + value.columnName + "`=? AND");
        });

        // all together
        return String.format(
                baseFormat,
                tableName,
                // join all where clauses and remove the last AND chars
                String.join("", columnList).replaceFirst("....$", "")
        );
    }

    @Override
    public String replaceInto(String tableName, List<DomainModelAttribute> values) {

        String baseFormat = "REPLACE INTO `%s` (%s) VALUES (%s)";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        values.forEach(value -> {

            columnList.add(value.columnName);
        });

        // apply as many questionmarks as there are columns
        String valueQuestionmarks = String.join("", Collections.nCopies(columnList.size(), "?,"));

        // all together
        return String.format(
                baseFormat,
                tableName,
                String.join(",", columnList),
                // remove last char if available
                valueQuestionmarks.replaceFirst(".$", "")
        );
    }

    @Override
    public String get(String tableName, List<DomainModelAttribute> values) {

        String baseFormat = "SELECT * from `%s`";
        if (values.size() > 0) {
            baseFormat += " WHERE %s";
        }

        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        values.forEach(value -> {

            columnList.add("`" + value.columnName + "`=? AND ");
        });

        // all together
        return String.format(
                baseFormat,
                tableName,
                // join all where clauses and remove the last AND chars
                String.join("", columnList).replaceFirst(".....$", "")
        );
    }

}
