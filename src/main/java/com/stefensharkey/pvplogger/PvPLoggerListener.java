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
    private final Plugin plugin;

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

    public String entityInfo(Block block)
    {
        return block
                + " {Coordinates:{X=" + block.getLocation().getBlockX()
                + ", Y=" + block.getLocation().getBlockY()
                + ", Z=" + block.getLocation().getBlockZ()
                + "}, World=" + block.getLocation().getWorld().getName() + "}";
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

    public String lavaInfo(Entity entity)
    {
        return "Lava"
                + " {Coordinates:{X=" + entity.getLocation().getBlockX()
                + ", Y=" + entity.getLocation().getBlockY()
                + ", Z=" + entity.getLocation().getBlockZ()
                + "}, World=" + entity.getLocation().getWorld().getName() + "}";
    }

    public String formatMessage(Event event)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        if(event instanceof EntityDamageByBlockEvent)
        {
            Block damager = ((EntityDamageByBlockEvent) event).getDamager();
            Entity entity = ((EntityDamageByBlockEvent) event).getEntity();
            boolean isLava = ((EntityDamageByBlockEvent) event).getCause().equals(EntityDamageEvent.DamageCause.LAVA);

            return "[" + sdf.format(cal.getTime()) + "]: " + (isLava ? "Lava" : damager) + " damaged " + Utils.getName(entity)
                    + " (UUID: " + entity.getUniqueId() + ") for " + ((EntityDamageByBlockEvent) event).getDamage()
                    + " damage."
                    + "\n" + (isLava ? lavaInfo(entity) : entityInfo(damager))
                    + "\n" + entityInfo(entity)
                    + "\n";
        } else if(event instanceof EntityDamageByEntityEvent)
        {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            Entity entity = ((EntityDamageByEntityEvent) event).getEntity();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getName(damager) + " (UUID: "
                    + damager.getUniqueId() + ") damaged " + Utils.getName(entity) + " (UUID: "
                    + entity.getUniqueId() + ") with " + Utils.getWeapon(damager) + " for "
                    + ((EntityDamageByEntityEvent) event).getDamage() + " damage."
                    + "\n" + entityInfo(damager)
                    + "\n" + entityInfo(entity)
                    + "\n";
        } else if(event instanceof EntityDamageEvent)
        {
            Entity entity = ((EntityDamageEvent) event).getEntity();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getName(entity) + " (UUID: "
                    + entity.getUniqueId() + ") was damaged by " + ((EntityDamageEvent) event).getCause() + " for "
                    + ((EntityDamageEvent) event).getDamage() + " damage."
                    + "\n" + entityInfo(entity)
                    + "\n";
        }

        return "";
    }

    public void logToFile(EntityDamageEvent event, Entity entity, String message)
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

            if(entity instanceof Player)
            {
                saveTo = new File(plugin.getDataFolder(), (entity).getUniqueId().toString() + ".log");

                fileWriter = new FileWriter(saveTo, true);
                printWriter = new PrintWriter(fileWriter);
                printWriter.println(message);
                printWriter.flush();
                printWriter.close();
            }
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
