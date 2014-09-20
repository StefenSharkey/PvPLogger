package com.stefensharkey.pvplogger;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class PvPLoggerListener implements Listener
{
    private Plugin plugin;

    public PvPLoggerListener(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onEntityDamageEvent(final EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player)
            logToFile(event, event.getEntity(), formatMessage(event));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onEntityDamageByEntityEvent(final EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
            logToFile(event, event.getEntity(), formatMessage(event));
    }

    public String entityInfo(Block entity)
    {
        return entity
                + " {Coordinates:{X=" + entity.getLocation().getBlockX()
                + ", Y=" + entity.getLocation().getBlockY()
                + ", Z=" + entity.getLocation().getBlockZ()
                + "}, Orientation:{Yaw=" + entity.getLocation().getYaw()
                + ", Pitch=" + entity.getLocation().getPitch()
                + "}, World=" + entity.getLocation().getWorld().getName() + "}";
    }

    public String entityInfo(Entity entity)
    {
        return Utils.getName(entity)
                + " {Coordinates:{X=" + entity.getLocation().getX()
                + ", Y=" + entity.getLocation().getY()
                + ", Z=" + entity.getLocation().getZ()
                + "}, Orientation:{Yaw=" + entity.getLocation().getYaw()
                + ", Pitch=" + entity.getLocation().getPitch()
                + ", Direction=" + Utils.getDirection(entity)
                + "}, World=" + entity.getLocation().getWorld().getName() + "}";
    }

    public String formatMessage(Event event)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        if(event instanceof EntityDamageByBlockEvent)
        {
            Block attacker = ((EntityDamageByBlockEvent) event).getDamager();
            Entity damaged = ((EntityDamageByBlockEvent) event).getEntity();

            return "[" + sdf.format(cal.getTime()) + "]: " + attacker + " attacked " + Utils.getName(damaged)
                    + " (UUID: " + damaged.getUniqueId() + ") for "+ ((EntityDamageByBlockEvent) event).getDamage()
                    + " damage."
                    + "\n" + entityInfo(attacker)
                    + "\n" + entityInfo(damaged)
                    + "\n";
        }
        else if(event instanceof EntityDamageByEntityEvent)
        {
            Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();
            Entity damaged = ((EntityDamageByEntityEvent) event).getEntity();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getName(attacker) + " (UUID: "
                    + attacker.getUniqueId() + ") attacked " + Utils.getName(damaged) + " (UUID: "
                    + damaged.getUniqueId() + ") with " + Utils.getWeapon(attacker) + " for "
                    + ((EntityDamageByEntityEvent) event).getDamage() + " damage."
                    + "\n" + entityInfo(attacker)
                    + "\n" + entityInfo(damaged)
                    + "\n";
        }
        else if(event instanceof EntityDamageEvent)
        {
            Entity damaged = ((EntityDamageEvent) event).getEntity();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getName(damaged) + " (UUID: "
                    + damaged.getUniqueId() + ") was damaged by " + ((EntityDamageEvent) event).getCause() + " for "
                    + ((EntityDamageEvent) event).getDamage() + " damage."
                    + "\n" + entityInfo(damaged)
                    + "\n";
        }

        return "";
    }

    public void logToFile(EntityDamageEvent event, Entity damaged, String message)
    {
        File saveTo;
        FileWriter fileWriter;
        PrintWriter printWriter;

        if(!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        try
        {
            if(event instanceof EntityDamageByEntityEvent)
            {
                Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();

                if(attacker instanceof Player)
                {
                    saveTo = new File(plugin.getDataFolder(), (attacker).getUniqueId().toString() + ".log");

                    fileWriter = new FileWriter(saveTo, true);
                    printWriter = new PrintWriter(fileWriter);
                    printWriter.println(message);
                    printWriter.flush();
                    printWriter.close();
                }
            }

            if(damaged instanceof Player)
            {
                saveTo = new File(plugin.getDataFolder(), (damaged).getUniqueId().toString() + ".log");

                fileWriter = new FileWriter(saveTo, true);
                printWriter = new PrintWriter(fileWriter);
                printWriter.println(message);
                printWriter.flush();
                printWriter.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
