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
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * a class that performs database queries over different dialects
 *
 * @author Wrath
 */
public class DatabaseExecutor {

    // the current dialect and database adapter
    private final DatabaseDialect dialect;
    private final DatabaseAdapter databaseAdapter;

    public DatabaseExecutor(DatabaseAdapter databaseAdapter, DatabaseDialect dialect) {

        this.dialect = dialect;
        this.databaseAdapter = databaseAdapter;
    }

    /**
     * create the table
     *
     * @param tableName
     * @param columns
     * @return
     */
    public boolean createTable(String tableName, Map<String, Object> columns) {

        String query = this.dialect.createTable(tableName, columns);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);

            // execute!
            ResultSet result = this.execute(statement);

            while (result.next()) {
                return true;
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * create the table if not exists
     *
     * @param tableName
     * @param columns
     * @return
     */
    public boolean createTableIfNotExists(String tableName, Map<String, Object> columns) {

        String query = this.dialect.createTableIfNotExists(tableName, columns);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);

            // execute!
            ResultSet result = this.execute(statement);

            while (result.next()) {
                return true;
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * insert into table
     *
     * @param tableName
     * @param values
     * @return
     */
    public boolean insert(String tableName, Map<String, Object> values) {

        String query = this.dialect.insert(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            ResultSet result = this.execute(statement);

            while (result.next()) {
                return true;
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * updates onto table
     *
     * @param tableName
     * @param values
     * @return
     */
    public boolean update(String tableName, Map<String, Object> values) {

        String query = this.dialect.update(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            ResultSet result = this.execute(statement);

            while (result.next()) {
                return true;
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * deletes from table
     *
     * @param tableName
     * @param values
     * @return
     */
    public boolean delete(String tableName, Map<String, Object> values) {

        String query = this.dialect.delete(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            ResultSet result = this.execute(statement);

            while (result.next()) {
                return true;
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * replaces into table
     *
     * @param tableName
     * @param values
     * @return
     */
    public boolean replaceInto(String tableName, Map<String, Object> values) {

        String query = this.dialect.replaceInto(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            ResultSet result = this.execute(statement);

            while (result.next()) {
                return true;
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * executes the given statement
     *
     * @param statement
     * @return
     */
    private ResultSet execute(PreparedStatement statement) throws SQLException {

        Main.log.info(statement.toString());
        return statement.executeQuery();
    }

    /**
     * get from table
     *
     * @param <T>
     * @param tableName
     * @param values
     * @return
     */
    public <T extends Object> List<Map<String, Object>> get(String tableName, Map<String, Object> values) {

        String query = this.dialect.get(tableName, values);
        List<Map<String, Object>> stack = new ArrayList<>();

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            ResultSet result = this.execute(statement);

            while (result.next()) {

                // create a map
                Map<String, Object> map = new HashMap<>();
                ResultSetMetaData metadata = result.getMetaData();

                // iterate through the columns
                // STARTING BY COLUMN 1
                for (int i = 1; i <= metadata.getColumnCount(); i++) {

                    map.put(metadata.getColumnName(i), result.getObject(i));
                }

                // add to the list
                stack.add(map);
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        // empty stack on error
        return stack;
    }

    /**
     * prepare all given values for the database statement
     *
     * @param statement
     * @param values
     */
    private void prepareValues(PreparedStatement statement, Map<String, Object> values) throws SQLException {

        // iterate through all values
        int i = 1;
        for (Map.Entry<String, Object> entry : values.entrySet()) {

            Object value = entry.getValue();

            // ugly type check
            if (value instanceof Integer) {

                statement.setInt(i, (int) value);
            } else if (value instanceof Double || value instanceof BigDecimal) {

                statement.setDouble(i, (double) value);
            } else if (value instanceof Date) {

                statement.setDate(i, (Date) value);
            } else {

                statement.setString(i, value.toString());
            }
        }

        // increment index counter
        i++;
    }

}
