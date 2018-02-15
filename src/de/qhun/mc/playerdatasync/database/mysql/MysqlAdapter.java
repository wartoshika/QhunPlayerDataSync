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

import de.qhun.mc.playerdatasync.config.MysqlConfiguration;
import de.qhun.mc.playerdatasync.database.DatabaseAdapter;
import de.qhun.mc.playerdatasync.database.DatabaseDialect;
import java.sql.Driver;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * implements the mysql dialect for database transactions
 *
 * @author Wrath
 */
public class MysqlAdapter extends DatabaseAdapter {

    public MysqlAdapter(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * override the configuration file return from the default adapter
     *
     * @return
     */
    @Override
    public Class<MysqlConfiguration> getConfigurationClass() {

        return MysqlConfiguration.class;
    }

    /**
     * use the mysql driver
     *
     * @return
     */
    @Override
    public Class<? extends Driver> getJdbcDriverClass() {

        return com.mysql.jdbc.Driver.class;
    }

    /**
     * get the mysql database dialect
     *
     * @return
     */
    @Override
    protected DatabaseDialect getDatabaseDialect() {

        return new MysqlDialect();
    }
}
