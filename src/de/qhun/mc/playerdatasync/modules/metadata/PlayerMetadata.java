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

import de.qhun.mc.playerdatasync.database.decorators.Column;
import de.qhun.mc.playerdatasync.database.decorators.ColumnType;
import de.qhun.mc.playerdatasync.database.decorators.Entity;
import de.qhun.mc.playerdatasync.database.decorators.NotNull;
import de.qhun.mc.playerdatasync.database.decorators.Primary;
import de.qhun.mc.playerdatasync.database.decorators.Table;
import java.util.List;
import java.util.UUID;
import org.bukkit.potion.PotionEffect;
import org.bukkit.GameMode;

/**
 * the metadata domain model
 *
 * @author Wrath
 */
@Entity
@Table
public class PlayerMetadata {

    @Primary(index = 0)
    @Column(size = 36)
    @NotNull
    private UUID uuid;

    @Primary(index = 1)
    @Column(size = 24)
    @NotNull
    private String branch;

    @Column(type = ColumnType.Double)
    private double health;

    @Column(type = ColumnType.Double)
    private double healthScale;

    @Column(type = ColumnType.Integer)
    private int level;

    @Column(type = ColumnType.Float)
    private float xp;

    @Column(type = ColumnType.Integer)
    private int food;

    @Column(type = ColumnType.Float)
    private float saturation;

    @Column(type = ColumnType.Float)
    private float exhaustion;

    @Column(size = 12)
    private String gamemode;

    @Column(type = ColumnType.Boolean)
    private boolean flight;

    @Column(type = ColumnType.Text)
    private String potionEffects;

    @Column(type = ColumnType.Integer)
    private int fireTicks;

    @Column(type = ColumnType.Integer)
    private int air;

    @Column(type = ColumnType.Integer)
    private int holdItemSlot;

    protected PlayerMetadata() {
    }

    public PlayerMetadata(UUID uuid, String branch) {

        this.uuid = uuid;
        this.branch = branch;
    }

    /**
     * get the uuid
     *
     * @return
     */
    public UUID getUuid() {

        return this.uuid;
    }

    /**
     * get the current branch name
     *
     * @return
     */
    public String getBranch() {

        return branch;
    }

    /**
     * get the current health
     *
     * @return
     */
    public double getHealth() {

        return health;
    }

    /**
     * get the clients health scale
     *
     * @return
     */
    public double getHealthScale() {

        return healthScale;
    }

    /**
     * get the absolute level
     *
     * @return
     */
    public int getLevel() {

        return level;
    }

    /**
     * get the current xp
     *
     * @return
     */
    public float getXp() {

        return xp;
    }

    /**
     * get the food level
     *
     * @return
     */
    public int getFood() {

        return food;
    }

    /**
     * get the saturation
     *
     * @return
     */
    public float getSaturation() {

        return saturation;
    }

    /**
     * get the exhaustion
     *
     * @return
     */
    public float getExhaustion() {

        return exhaustion;
    }

    /**
     * get the current gamemode
     *
     * @return
     */
    public GameMode getGamemode() {

        return GameMode.valueOf(this.gamemode);
    }

    /**
     * get the flag if the player is allowed to flight
     *
     * @return
     */
    public boolean isFlight() {

        return flight;
    }

    /**
     * get all current active potion effects
     *
     * @return
     */
    public List<PotionEffect> getPotionEffects() {

        return PotionEffectSerializer.deserialize(this.potionEffects);
    }

    /**
     * get the current fire ticks
     *
     * @return
     */
    public int getFireTicks() {

        return fireTicks;
    }

    /**
     * get the remaining air
     *
     * @return
     */
    public int getAir() {

        return air;
    }

    /**
     * get the current hold item slot
     *
     * @return
     */
    public int getHoldItemSlot() {

        return holdItemSlot;
    }

    /**
     * set the current health
     *
     * @param health
     */
    public void setHealth(double health) {

        this.health = health;
    }

    /**
     * set the clients health scale
     *
     * @param healthScale
     */
    public void setHealthScale(double healthScale) {

        this.healthScale = healthScale;
    }

    /**
     * set the absolute level
     *
     * @param level
     */
    public void setLevel(int level) {

        this.level = level;
    }

    /**
     * set the xp
     *
     * @param xp
     */
    public void setXp(float xp) {

        this.xp = xp;
    }

    /**
     * set the food level
     *
     * @param food
     */
    public void setFood(int food) {

        this.food = food;
    }

    /**
     * set the saturation
     *
     * @param saturation
     */
    public void setSaturation(float saturation) {

        this.saturation = saturation;
    }

    /**
     * set the exhaustion
     *
     * @param exhaustion
     */
    public void setExhaustion(float exhaustion) {

        this.exhaustion = exhaustion;
    }

    /**
     * set the current gamemode
     *
     * @param gamemode
     */
    public void setGamemode(GameMode gamemode) {

        this.gamemode = gamemode.name();
    }

    /**
     * set a flag is the player is allowed to flight
     *
     * @param flight
     */
    public void setFlight(boolean flight) {

        this.flight = flight;
    }

    /**
     * set the current active potion effects
     *
     * @param potionEffects
     */
    public void setPotionEffects(List<PotionEffect> potionEffects) {

        this.potionEffects = PotionEffectSerializer.serialize(potionEffects);
    }

    /**
     * set the fire ticks
     *
     * @param fireTicks
     */
    public void setFireTicks(int fireTicks) {

        this.fireTicks = fireTicks;
    }

    /**
     * set the remaining air
     *
     * @param air
     */
    public void setAir(int air) {

        this.air = air;
    }

    /**
     * set the currently hold item slot
     *
     * @param holdItemSlot
     */
    public void setHoldItemSlot(int holdItemSlot) {

        this.holdItemSlot = holdItemSlot;
    }
}
