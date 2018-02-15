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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Wrath
 */
public class MysqlDialect implements DatabaseDialect {

    @Override
    public String createTable(String tableName, Map<String, Object> columns) {

        String baseFormat = "CREATE TABLE `%s` (%s)";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        for (Entry<String, Object> column : columns.entrySet()) {

            columnList.add(
                    String.format(
                            "%s %s",
                            column.getKey(),
                            "VARCHAR(30)"
                    )
            );
        }

        // all together
        return String.format(baseFormat, tableName, String.join(",", columnList));
    }

    @Override
    public String createTableIfNotExists(String tableName, Map<String, Object> columns) {

        String baseFormat = "CREATE TABLE IF NOT EXISTS `%s` (%s)";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        for (Entry<String, Object> column : columns.entrySet()) {

            columnList.add(
                    String.format(
                            "%s %s",
                            column.getKey(),
                            "VARCHAR(30)"
                    )
            );
        }

        // all together
        return String.format(baseFormat, tableName, String.join(",", columnList));
    }

    @Override
    public String insert(String tableName, Map<String, Object> values) {

        String baseFormat = "INSERT INTO `%s` (%s) VALUES (%s)";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        for (Entry<String, Object> column : values.entrySet()) {

            columnList.add(column.getKey());
        }

        // apply as many questionmarks as there are columns
        String valueQuestionmarks = String.join("", Collections.nCopies(columnList.size(), "?,"));

        // all together
        return String.format(
                baseFormat,
                tableName,
                String.join(",", columnList),
                // remove last char if available
                valueQuestionmarks.replaceFirst(".$", valueQuestionmarks)
        );
    }

    @Override
    public String update(String tableName, Map<String, Object> values) {

        return "";

    }

    @Override
    public String delete(String tableName, Map<String, Object> values) {

        return "";
    }

    @Override
    public String replaceInto(String tableName, Map<String, Object> values) {

        String baseFormat = "REPLACE INTO `%s` (%s) VALUES (%s)";
        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        for (Entry<String, Object> column : values.entrySet()) {

            columnList.add(column.getKey());
        }

        // apply as many questionmarks as there are columns
        String valueQuestionmarks = String.join("", Collections.nCopies(columnList.size(), "?,"));

        // all together
        return String.format(
                baseFormat,
                tableName,
                String.join(",", columnList),
                // remove last char if available
                valueQuestionmarks.replaceFirst(".$", valueQuestionmarks)
        );
    }

    @Override
    public String get(String tableName, Map<String, Object> values) {

        String baseFormat = "SELECT * from %s";
        if (values.size() > 0) {
            baseFormat += " WHERE %s";
        }

        List<String> columnList = new ArrayList<>();

        // iterate through all columns
        for (Entry<String, Object> column : values.entrySet()) {

            columnList.add(column.getKey() + "= ? AND");
        }
        
        // all together
        return String.format(
                baseFormat,
                tableName,
                // join all where clauses and remove the last AND chars
                String.join("", columnList).replaceFirst("....$", "")
        );
    }

}
