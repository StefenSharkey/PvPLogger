package com.stefensharkey.pvplogger;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PvPLogger extends JavaPlugin
{
    private Logger log;

    @Override
    public void onEnable()
    {
        log = getLogger();
    }

    @Override
    public void onDisable()
    {

    }
}
