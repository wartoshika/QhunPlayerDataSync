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

import de.qhun.mc.playerdatasync.database.GenericRepository;
import java.util.UUID;

/**
 * the repository to access the player's account data
 *
 * @author Wrath
 */
public class PlayerAccountRepository extends GenericRepository<PlayerAccount, UUID> {

    /**
     * get the player account class
     *
     * @return
     */
    @Override
    protected Class<PlayerAccount> getEntityClass() {

        return PlayerAccount.class;
    }
}
