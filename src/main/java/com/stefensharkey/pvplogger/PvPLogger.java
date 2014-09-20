package com.stefensharkey.pvplogger;

import org.bukkit.plugin.java.JavaPlugin;

public class PvPLogger extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new PvPLoggerListener(this), this);
    }

    @Override
    public void onDisable()
    {

    }
}
