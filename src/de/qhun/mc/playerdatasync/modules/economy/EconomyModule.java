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
import de.qhun.mc.playerdatasync.modules.AbstractModule;
import de.qhun.mc.playerdatasync.util.Autoload;
import de.qhun.mc.playerdatasync.util.ServiceProvider;
import java.util.UUID;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.milkbowl.vault.economy.Economy;

/**
 * the economy module. handles sync the player's money
 *
 * @author Wrath
 */
public class EconomyModule extends AbstractModule<EconomyConfiguration> {

    // holds the repository to access the player data
    private PlayerAccountRepository playerAccountRepository;
    private Economy economy;
    private EconomySync sync;
    private PlayerAccount player;

    @Autoload
    private ServiceProvider serviceProvider;

    private int eventReferenceJoin;
    private int eventReferenceQuit;

    @Override
    public void construct() {

        // setup the repository
        this.playerAccountRepository = new PlayerAccountRepository();

        // setup domain models
        domainModelSetup.setupDomainModel(new Class<?>[]{
            PlayerAccount.class
        });

        // get the economy service provider
        this.economy = this.serviceProvider.get(Economy.class);

        // create the sync handler
        this.sync = new EconomySync(
                economy, this.playerAccountRepository,
                this.configuration.getSyncInterval()
        );
    }

    @Override
    public boolean enable() {

        // add player join event
        this.eventReferenceJoin = this.eventRegister.addEvent(PlayerJoinEvent.class, (event) -> this.onPlayerJoin(event));
        this.eventReferenceQuit = this.eventRegister.addEvent(PlayerQuitEvent.class, (event) -> this.onPlayerQuit(event));

        // start sync process if enabled
        if (this.configuration.isSyncEnabled()) {

            this.sync.startSync();
        }

        // add player leave event
        return true;
    }

    @Override
    public boolean disable() {

        // removes the events from the event register
        this.eventRegister.removeEvent(PlayerJoinEvent.class, this.eventReferenceJoin);
        this.eventRegister.removeEvent(PlayerQuitEvent.class, this.eventReferenceQuit);

        // stop sync in enabled
        if (this.configuration.isSyncEnabled()) {

            this.sync.stopSync();
        }

        return true;
    }

    /**
     * handles the player join event
     *
     * @param event
     */
    private void onPlayerJoin(PlayerJoinEvent event) {

        // create an async task to not disturb the user while joining the game
        // on the main thread of the server
        this.createAsyncTask(() -> {

            // get player domain model
            UUID playerUuid = event.getPlayer().getUniqueId();
            this.player = this.playerAccountRepository.findByPrimary(playerUuid);

            // if there is a change in the players balance, sync it
            if (this.player != null && this.player.getBalance() != this.economy.getBalance(event.getPlayer())) {

                // give the player that amount of money he/she owns
                this.economy.withdrawPlayer(event.getPlayer(), this.economy.getBalance(event.getPlayer()));
                this.economy.depositPlayer(event.getPlayer(), this.player.getBalance());
            }

            // wait 10 ticks, this should equal to 500 milliseconds
        }, 10L);
    }

    /**
     * handles player quit event
     *
     * @param event
     */
    private void onPlayerQuit(PlayerQuitEvent event) {

        // check if a player instance exists
        if (this.player == null) {

            this.player = new PlayerAccount(event.getPlayer().getUniqueId());
        }

        // get current balance
        this.player.setBalance(this.economy.getBalance(event.getPlayer()));

        // save the balance
        this.playerAccountRepository.store(player);
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
