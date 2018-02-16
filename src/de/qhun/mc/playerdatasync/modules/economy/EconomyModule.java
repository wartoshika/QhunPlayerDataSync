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

import de.qhun.mc.playerdatasync.DependencyManager;
import de.qhun.mc.playerdatasync.config.EconomyConfiguration;
import de.qhun.mc.playerdatasync.database.domainmodel.DomainModelSetup;
import de.qhun.mc.playerdatasync.events.EventRegister;
import de.qhun.mc.playerdatasync.modules.AbstractModule;
import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import net.milkbowl.vault.economy.Economy;

/**
 *
 * @author Wrath
 */
public class EconomyModule extends AbstractModule<EconomyConfiguration> {

    // holds the repository to access the player data
    private final PlayerAccountRepository playerAccountRepository;
    private final Economy economy;
    private final JavaPlugin plugin;

    public EconomyModule(EventRegister eventRegister, DomainModelSetup domainModelSetup, JavaPlugin plugin) {
        super(eventRegister);

        // setup the repository
        this.playerAccountRepository = new PlayerAccountRepository();
        this.plugin = plugin;

        // setup domain models
        domainModelSetup.setupDomainModel(new Class<?>[]{
            PlayerAccount.class
        });

        // get the economy service provider
        this.economy = plugin.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public boolean enable() {

        // add player join event
        this.eventRegister.addEvent(PlayerJoinEvent.class, (event) -> {

            this.logInfoPrefixed("PLAYER JOIN EVENT! Player:" + event.getPlayer().getName());

            // get player domain model
            UUID playerUuid = event.getPlayer().getUniqueId();
            PlayerAccount player = this.playerAccountRepository.findByPrimaryOrCreate(PlayerAccount.class, playerUuid);

            // if the player is not in the database, add the player
            if (player.getUuid() == null) {

                player.setUuid(playerUuid);
                //player.setBalance(this.economy.getBalance(playerUuid));
            }

            this.logInfoPrefixed("Player's uuid is " + player.getUuid());

        });

        return true;
    }

    @Override
    public boolean disable() {

        return true;
    }

    @Override
    public void checkDependencies(DependencyManager dependencyManager) {

        // checking vault and eco system
        if (this.configuration.isEnabled() && !dependencyManager.isEconomyAvailable()) {

            throw new Error(
                    "I could not find any Vault and/or Eco System. Please be sure to add Vault and a compatible ecosystem to your Minecraft server."
            );
        }
    }

}
