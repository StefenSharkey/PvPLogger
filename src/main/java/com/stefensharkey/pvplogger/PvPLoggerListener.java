package com.stefensharkey.pvplogger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

public class PvPLoggerListener implements Listener
{
    private Plugin instance;

    public PvPLoggerListener(Plugin instance)
    {
        this.instance = instance;
        Bukkit.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntityEvent(final EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
        {

        }
    }
}
