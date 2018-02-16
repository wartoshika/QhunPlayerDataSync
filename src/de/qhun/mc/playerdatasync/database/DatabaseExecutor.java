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
import de.qhun.mc.playerdatasync.Main;
import de.qhun.mc.playerdatasync.database.decorators.ColumnType;
import de.qhun.mc.playerdatasync.database.domainmodel.DecoratedDomainModel;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    public boolean createTable(String tableName, List<DomainModelAttribute> columns) {

        String query = this.dialect.createTable(tableName, columns);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);

            // execute!
            return this.execute(statement) > 0;

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
    public boolean createTableIfNotExists(String tableName, List<DomainModelAttribute> columns) {

        String query = this.dialect.createTableIfNotExists(tableName, columns);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);

            // execute!
            return this.execute(statement) > 0;

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
    public boolean insert(String tableName, List<DomainModelAttribute> values) {

        String query = this.dialect.insert(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            return this.execute(statement) > 0;

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
    public boolean update(String tableName, List<DomainModelAttribute> values) {

        String query = this.dialect.update(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            return this.execute(statement) > 0;

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
    public boolean delete(String tableName, List<DomainModelAttribute> values) {

        String query = this.dialect.delete(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            return this.execute(statement) > 0;

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
    public boolean replaceInto(String tableName, List<DomainModelAttribute> values) {

        String query = this.dialect.replaceInto(tableName, values);

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            return this.execute(statement) > 0;

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * get from table
     *
     * @param <T>
     * @param tableName
     * @param entity
     * @param values
     * @return
     */
    public <T extends Object> List<List<DomainModelAttribute>> get(String tableName, Class<T> entity, List<DomainModelAttribute> values) {

        String query = this.dialect.get(tableName, values);
        List<List<DomainModelAttribute>> stack = new ArrayList<>();

        try {

            // prepare statement and insert variables
            PreparedStatement statement = this.databaseAdapter.getConnection().prepareStatement(query);
            this.prepareValues(statement, values);

            // execute!
            ResultSet result = this.executeGet(statement);

            while (result.next()) {

                // create a map
                List<DomainModelAttribute> attributeList = new ArrayList<>();
                ResultSetMetaData metadata = result.getMetaData();

                // iterate through the columns
                // STARTING BY COLUMN 1
                for (int i = 1; i <= metadata.getColumnCount(); i++) {

                    int[] comulm = {i};

                    DomainModelAttribute attribute = DecoratedDomainModel
                            .getAttributes(entity)
                            .stream().filter(predicate -> {
                                try {
                                    return predicate.columnName.equals(metadata.getColumnName(comulm[0]));
                                } catch (SQLException ex) {

                                    // log warning!
                                    Main.log.warning("Could not receive column data for result set!");
                                    Main.log.log(Level.WARNING, ex.getMessage(), ex);

                                }

                                return false;
                            })
                            .findFirst().get();

                    // get value, null check
                    if (attribute != null) {
                        attribute.value = this.getTransformedValue(attribute, result, comulm[0]);
                        attributeList.add(attribute);
                    }
                }

                // add to the list
                stack.add(attributeList);
            }

        } catch (SQLException ex) {

            // print error
            Main.log.log(Level.WARNING, ex.getMessage(), ex);
        }

        // empty stack on error
        return stack;
    }

    /**
     * executes the given statement
     *
     * @param statement
     * @return
     */
    private int execute(PreparedStatement statement) throws SQLException {

        Main.log.info(statement.toString());
        return statement.executeUpdate();
    }

    /**
     * executes the given statement
     *
     * @param statement
     * @return
     */
    private ResultSet executeGet(PreparedStatement statement) throws SQLException {

        Main.log.info(statement.toString());
        return statement.executeQuery();
    }

    /**
     * prepare all given values for the database statement
     *
     * @param statement
     * @param values
     */
    private void prepareValues(PreparedStatement statement, List<DomainModelAttribute> values) throws SQLException {

        // counter var
        // statements begin by 1
        int[] i = {1};

        // iterate through all values
        values.forEach(domainModelValue -> {

            ColumnType type = domainModelValue.type;
            Object value = domainModelValue.value;

            try {

                // if value is null, set null
                if (value == null) {

                    statement.setNull(i[0], 0);
                } else {

                    switch (type) {

                        case BigDecimal:
                            statement.setBigDecimal(i[0], new BigDecimal((double) value));
                            break;
                        case Double:
                            statement.setDouble(i[0], (double) value);
                            break;
                        case Integer:
                            statement.setInt(i[0], (int) value);
                            break;
                        case Date:
                            statement.setDate(i[0], (Date) value);
                            break;
                        default:
                        case String:
                            statement.setString(i[0], value.toString());
                            break;
                    }

                }

            } catch (SQLException ex) {

                // set null
                try {
                    statement.setNull(i[0], 0);
                } catch (SQLException exx) {

                    // do nothing
                }
            }

            // increment index counter
            i[0]++;

        });

    }

    /**
     * prepare values
     *
     * @param attribute
     * @param result
     * @param column
     * @return
     */
    private Object getTransformedValue(DomainModelAttribute attribute, ResultSet result, int column) {

        try {

            switch (attribute.type) {

                case BigDecimal:
                    return result.getBigDecimal(column);
                case Double:
                    return result.getDate(column);
                case Integer:
                    return result.getInt(column);
                case Date:
                    return result.getDate(column);
                default:
                case String:
                    return result.getString(column);
            }

        } catch (SQLException ex) {

            return null;
        }
    }

}
