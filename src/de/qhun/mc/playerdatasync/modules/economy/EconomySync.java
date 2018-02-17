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

import java.util.Timer;
import java.util.TimerTask;
import net.milkbowl.vault.economy.Economy;

/**
 *
 * @author Wrath
 */
public class EconomySync {

    // the interval in seconds
    private final int intervalSeconds;

    // the economy instance holder
    private final Economy economy;

    // the repository holder
    private final PlayerAccountRepository repository;

    // the timer instance
    private final Timer timer;

    public EconomySync(Economy economy, PlayerAccountRepository repository, int intervalSeconds) {

        this.intervalSeconds = intervalSeconds;
        this.economy = economy;
        this.repository = repository;
        this.timer = new Timer();
    }

    /**
     * start the sync process
     */
    public void startSync() {

        // set the timer schedule
        this.timer.scheduleAtFixedRate(
                this.createTask(() -> {

                    // call the timer tick function
                    this.timerTick();
                }),
                0, this.intervalSeconds
        );
    }

    /**
     * stop the sync process
     */
    public void stopSync() {

        // stop the current timer
        this.timer.cancel();
    }

    /**
     * creates a timertank and allow usage if lamdba functions
     *
     * @return
     */
    private TimerTask createTask(Runnable r) {

        return new TimerTask() {

            @Override
            public void run() {
                r.run();
            }
        };
    }

    /**
     * the timer tick. lets sync!
     */
    private void timerTick() {

        // get all players from the database
        this.repository.findAll().forEach(player -> {

        });
    }
}
