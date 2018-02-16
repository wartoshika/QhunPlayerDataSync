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
package de.qhun.mc.playerdatasync.modules.economy;

import de.qhun.mc.playerdatasync.database.decorators.Column;
import de.qhun.mc.playerdatasync.database.decorators.ColumnType;
import de.qhun.mc.playerdatasync.database.decorators.Entity;
import de.qhun.mc.playerdatasync.database.decorators.NotNull;
import de.qhun.mc.playerdatasync.database.decorators.Primary;
import de.qhun.mc.playerdatasync.database.decorators.Table;
import java.util.UUID;

/**
 * a domain model for player's economy balance
 *
 * @author Wrath
 */
@Entity
@Table(name = "account")
public class PlayerAccount {

    @Primary
    @Column(size = 36)
    private UUID uuid;

    @Column(type = ColumnType.Double)
    @NotNull
    private double balance;

    public UUID getUuid() {

        return this.uuid;
    }

    public double getBalance() {

        return this.balance;
    }

    public void setUuid(UUID uuid) {

        this.uuid = uuid;
    }

    public void setBalance(double balance) {

        this.balance = balance;
    }
}
