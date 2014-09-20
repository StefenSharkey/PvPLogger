package com.stefensharkey.pvplogger;

import org.bukkit.plugin.java.JavaPlugin;

public class PvPLogger extends JavaPlugin
{
    public static boolean DEBUG_MODE = false;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        DEBUG_MODE = getConfig().getBoolean("debug");

        getServer().getPluginManager().registerEvents(new PvPLoggerListener(this), this);
    }

    @Override
    public void onDisable()
    {

    }
}
