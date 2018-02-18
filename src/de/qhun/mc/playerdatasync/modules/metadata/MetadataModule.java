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
package de.qhun.mc.playerdatasync.modules.metadata;

import de.qhun.mc.playerdatasync.modules.AbstractModule;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

/**
 * a module that handles the sync of the player's cursor, air, potion effects,
 * gamemode ...
 *
 * @author Wrath
 */
public class MetadataModule extends AbstractModule<MetadataConfiguration> {

    // the repository
    private PlayerMetadataRepository repository;
    
    private UUID eventReferenceJoin;
    private UUID eventReferenceQuit;
    
    @Override
    public void construct() {

        // setup the repository
        this.repository = new PlayerMetadataRepository();

        // setup domain models
        domainModelSetup.setupDomainModel(new Class<?>[]{
            PlayerMetadata.class
        });
    }
    
    @Override
    public boolean enable() {

        // add player join event
        this.eventReferenceJoin = this.eventRegister.addEvent(PlayerJoinEvent.class, this::onPlayerJoin);
        this.eventReferenceQuit = this.eventRegister.addEvent(PlayerQuitEvent.class, this::onPlayerQuit);
        
        return true;
    }
    
    @Override
    public boolean disable() {

        // log disable 
        this.logInfoPrefixed("Module is disableing. Saving all metadata for the online players.");

        // removes the events from the event register
        this.eventRegister.removeEvent(PlayerJoinEvent.class, this.eventReferenceJoin);
        this.eventRegister.removeEvent(PlayerQuitEvent.class, this.eventReferenceQuit);

        // save the metadata for all online players
        this.plugin.getServer().getOnlinePlayers().forEach(this::savePlayerMetadata);
        
        return true;
    }

    /**
     * fires if a player is joining
     *
     * @param event
     */
    private void onPlayerJoin(PlayerJoinEvent event) {
        
        Player player = event.getPlayer();

        // get player domain model
        UUID playerUuid = event.getPlayer().getUniqueId();
        PlayerMetadata playerMetadata = this.repository.findByPrimary(
                Arrays.asList(playerUuid, this.configuration.getBranchName())
        );

        // if a player model is available, load these data
        if (playerMetadata != null) {

            // set all data from the database to the bukkit api
            if (this.configuration.isHealthEnabled()) {
                
                player.setHealthScale(playerMetadata.getHealthScale());
                player.setHealth(playerMetadata.getHealth());
            }
            
            if (this.configuration.isXpEnabled()) {
                
                player.setLevel(playerMetadata.getLevel());
                player.setExp(playerMetadata.getXp());
            }
            
            if (this.configuration.isFoodEnabled()) {
                
                player.setFoodLevel(playerMetadata.getFood());
                player.setSaturation(playerMetadata.getSaturation());
                player.setExhaustion(playerMetadata.getExhaustion());
            }
            
            if (this.configuration.isGamemodeEnabled()) {
                
                player.setGameMode(playerMetadata.getGamemode());
            }
            
            if (this.configuration.isFlightEnabled()) {
                
                player.setAllowFlight(playerMetadata.isFlight());
            }
            
            if (this.configuration.isPotionEffectEnabled()) {

                // remove all effects first
                player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
                player.addPotionEffects((Collection<PotionEffect>) playerMetadata.getPotionEffects());
            }
            
            if (this.configuration.isFireEnabled()) {
                
                player.setFireTicks(playerMetadata.getFireTicks());
            }
            
            if (this.configuration.isAirEnabled()) {
                
                player.setRemainingAir(playerMetadata.getAir());
            }
            
            if (this.configuration.isCurrentHoldItemEnabled()) {
                
                player.getInventory().setHeldItemSlot(playerMetadata.getHoldItemSlot());
            }
            
        }
    }

    /**
     * fires if a player is quitting
     *
     * @param event
     */
    private void onPlayerQuit(PlayerQuitEvent event) {
        
        this.savePlayerMetadata(event.getPlayer());
    }

    /**
     * saves the metadata
     *
     * @param player
     */
    private void savePlayerMetadata(Player player) {

        // get player from db
        UUID playerUuid = player.getUniqueId();
        PlayerMetadata playerMetadata = this.repository.findByPrimary(
                Arrays.asList(playerUuid, this.configuration.getBranchName())
        );

        // check if a player instance exists
        if (playerMetadata == null) {
            
            playerMetadata = new PlayerMetadata(
                    player.getUniqueId(), this.configuration.getBranchName()
            );
        }

        // now set all data
        if (this.configuration.isHealthEnabled()) {
            
            playerMetadata.setHealth(player.getHealth());
            playerMetadata.setHealthScale(player.getHealthScale());
        }
        
        if (this.configuration.isXpEnabled()) {
            
            playerMetadata.setLevel(player.getLevel());
            playerMetadata.setXp(player.getExp());
        }
        
        if (this.configuration.isFoodEnabled()) {
            
            playerMetadata.setFood(player.getFoodLevel());
            playerMetadata.setSaturation(player.getSaturation());
            playerMetadata.setExhaustion(player.getExhaustion());
        }
        
        if (this.configuration.isGamemodeEnabled()) {
            
            playerMetadata.setGamemode(player.getGameMode());
        }
        
        if (this.configuration.isFlightEnabled()) {
            
            playerMetadata.setFlight(player.getAllowFlight());
        }
        
        if (this.configuration.isPotionEffectEnabled()) {
            
            playerMetadata.setPotionEffects((List<PotionEffect>) player.getActivePotionEffects());
        }
        
        if (this.configuration.isFireEnabled()) {
            
            playerMetadata.setFireTicks(player.getFireTicks());
        }
        
        if (this.configuration.isAirEnabled()) {
            
            playerMetadata.setAir(player.getRemainingAir());
        }
        
        if (this.configuration.isCurrentHoldItemEnabled()) {
            
            playerMetadata.setHoldItemSlot(player.getInventory().getHeldItemSlot());
        }

        // save to database
        this.repository.store(playerMetadata);
    }
    
}
