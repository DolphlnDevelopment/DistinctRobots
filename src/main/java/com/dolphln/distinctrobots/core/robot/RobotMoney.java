package com.dolphln.distinctrobots.core.robot;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.Robot;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class RobotMoney {

    private int _task;
    private final Robot _robot;

    private int remaining;
    private final int interval;
    private final int intervalMoney;

    private int money;

    public RobotMoney(Robot _robot, int remaining, int interval, int intervalMoney, int money) {
        this._task = -1;
        this._robot = _robot;

        this.remaining = remaining <= 0 ? -1 : remaining;
        this.interval = interval;
        this.intervalMoney = intervalMoney;

        this.money = money;
    }


    public void start() {
        if (remaining == -1) {
            startNotRemaining();
            remaining = interval;
        } else {
            _task = new BukkitRunnable() {
                @Override
                public void run() {
                    remaining -= 1;
                    if (remaining == -1) {
                        money += intervalMoney;
                        remaining = interval;
                        startNotRemaining();
                        cancel();
                    }
                    _robot.updateHologram();
                }
            }.runTaskTimerAsynchronously(DistinctRobots.getInstance(), 20L, 20L).getTaskId();
        }
    }

    public void startNotRemaining() {
        _task = new BukkitRunnable() {
            @Override
            public void run() {
                remaining -= 1;
                if (remaining == -1) {
                    money += intervalMoney;
                    remaining = interval;
                }
                _robot.updateHologram();
            }
        }.runTaskTimerAsynchronously(DistinctRobots.getInstance(), 20L, 20L).getTaskId();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(_task);
        this._task = -1;
    }

    public int getRemaining() {
        return remaining;
    }

    public int getInterval() {
        return interval;
    }

    public int getIntervalMoney() {
        return intervalMoney;
    }

    public int getMoney() {
        return money;
    }

    public int resetMoney() {
        money = 0;
        return money;
    }

    public boolean isWorking() {
        return this._task != -1;
    }
}
