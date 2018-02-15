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
package de.qhun.mc.playerdatasync.config;

import de.qhun.mc.playerdatasync.Main;

/**
 *
 * @author Wrath
 */
public class MysqlConfiguration extends AbstractConfiguration implements DatabaseConfiguration {

    public MysqlConfiguration(Main plugin) {
        super(plugin);
    }

    @Override
    public String getProtocol() {

        return "mysql";
    }

    @Override
    public String getHost() {

        return this.getConfiguration().getString("database.mysql.host");
    }

    @Override
    public int getPort() {

        return this.getConfiguration().getInt("database.mysql.port", 3306);
    }

    @Override
    public String getDatabase() {

        return this.getConfiguration().getString("database.mysql.database");
    }

    @Override
    public String getUsername() {

        return this.getConfiguration().getString("database.mysql.user");
    }

    @Override
    public String getPassword() {

        return this.getConfiguration().getString("database.mysql.password");
    }

    @Override
    public String getTablePrefix() {

        return this.getConfiguration().getString("database.mysql.table_prefix");
    }

    @Override
    public boolean getAutoReconnect() {

        return this.getConfiguration().getBoolean("database.mysql.auto_reconnect");
    }

}
