package com.stefensharkey.pvplogger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
        {
            if(event.getEntity().getType() != EntityType.SPLASH_POTION
                    && event.getCause() != EntityDamageEvent.DamageCause.MAGIC)
            {
                if(!event.getEventName().equals("EntityDamageByEntityEvent"))
                {
                    if(PvPLogger.DEBUG_MODE)
                    {
                        plugin.getLogger().info("");
                        plugin.getLogger().info("onEntityDamageEvent()");
                        plugin.getLogger().info("Event Type: " + event.getEventName());
                        plugin.getLogger().info("Entity: " + ((Player) event.getEntity()).getName());
                        plugin.getLogger().info("Cause: " + event.getCause());
                    }

                    logToFile(event, event.getEntity(), formatMessage(event));
                    return;
                }

                logToFile(event, event.getEntity(), formatMessage(event));
            }
            else if(event.getCause() == EntityDamageEvent.DamageCause.MAGIC)
            {
                if(PvPLogger.DEBUG_MODE)
                {
                    plugin.getLogger().info("");
                    plugin.getLogger().info("onEntityDamageEvent()");
                    plugin.getLogger().info("Event Type: " + event.getEventName());
                    plugin.getLogger().info("Entity: " + ((Player) event.getEntity()).getName());
                    plugin.getLogger().info("Cause: " + event.getCause());
                }

                logToFile(event, event.getEntity(), formatMessage(event));
            }

            if(PvPLogger.DEBUG_MODE)
            {
                plugin.getLogger().info("");
                plugin.getLogger().info("Unlogged event! Contact the mod author! Posting details:");
                plugin.getLogger().info("onEntityDamageEvent()");
                plugin.getLogger().info("Event Type: " + event.getEventName());
                plugin.getLogger().info("Entity: " + ((Player) event.getEntity()).getName());
                plugin.getLogger().info("Cause: " + event.getCause());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onEntityDamageByEntityEvent(final EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
        {
            if(PvPLogger.DEBUG_MODE)
            {
                plugin.getLogger().info("");
                plugin.getLogger().info("onEntityDamageEvent()");
                plugin.getLogger().info("Event Type: " + event.getEventName());
                plugin.getLogger().info("Damager: " + ((Player) event.getDamager()).getName());
                plugin.getLogger().info("Entity: " + event.getEntity());
                plugin.getLogger().info("Cause: " + event.getCause());
            }

            logToFile(event, event.getEntity(), formatMessage(event));
        }
    }

    public String formatMessage(EntityDamageEvent event)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        if(event instanceof EntityDamageByBlockEvent)
        {
            Block damager = ((EntityDamageByBlockEvent) event).getDamager();
            Entity entity = event.getEntity();
            double damage = event.getDamage();
            boolean isLava = event.getCause().equals(EntityDamageEvent.DamageCause.LAVA);

            return "[" + sdf.format(cal.getTime()) + "]: " + (isLava ? "Lava" : damager)
                    + (((LivingEntity) entity).getHealth() - damage <= 0 ? " killed " : " damaged ")
                    + Utils.getName(entity) + " (UUID: " + entity.getUniqueId() + ") for " + damage + " damage."
                    + (PvPLogger.DEBUG_MODE ? " (" + event.getEventName() + ")" : "")
                    + "\n" + (isLava ? getLavaInfo(entity) : getEntityInfo(damager))
                    + "\n" + getEntityInfo(event, entity)
                    + "\n";
        } else if(event instanceof EntityDamageByEntityEvent)
        {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            Entity entity = event.getEntity();
            double damage = event.getDamage();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getName(damager) + " (UUID: " + damager.getUniqueId()
                    + ")" + (((LivingEntity) entity).getHealth() - damage <= 0 ? " killed " : " damaged ")
                    + Utils.getName(entity) + " (UUID: " + entity.getUniqueId() + ") with " + Utils.getWeapon(damager)
                    + " for " + damage + " damage." + (PvPLogger.DEBUG_MODE ? " (" + event.getEventName() + ")" : "")
                    + "\n" + getEntityInfo(event, damager)
                    + "\n" + getEntityInfo(event, entity)
                    + "\n";
        } else if(event != null)
        {
            Entity entity = event.getEntity();
            double damage = event.getDamage();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getName(entity) + " (UUID: " + entity.getUniqueId()
                    + ") was" + (((LivingEntity) entity).getHealth() - damage <= 0 ? " killed " : " damaged ") + "by "
                    + event.getCause() + " for " + damage + " damage."
                    + (PvPLogger.DEBUG_MODE ? " (" + event.getEventName() + ")" : "")
                    + "\n" + getEntityInfo(event, entity)
                    + "\n";
        }

        return "";
    }

    public void logToFile(EntityDamageEvent event, Entity entity, String message)
    {
        File saveTo = new File(plugin.getDataFolder(), "userdata" + File.separator);

        FileWriter fileWriter;
        PrintWriter printWriter;

        if(!saveTo.exists())
            saveTo.mkdirs();

        try
        {
            if(event instanceof EntityDamageByEntityEvent)
            {
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();

                if(damager instanceof Player || damager.getType().equals(EntityType.PLAYER))
                {

                    fileWriter = new FileWriter(new File(saveTo, damager.getUniqueId() + ".log"), true);
                    printWriter = new PrintWriter(fileWriter);
                    printWriter.println(message);
                    printWriter.flush();
                    printWriter.close();
                }
            }

            if(entity instanceof Player || entity.getType().equals(EntityType.PLAYER))
            {
                fileWriter = new FileWriter(new File(saveTo, entity.getUniqueId() + ".log"), true);
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

    public String getEntityInfo(Block block)
    {
        return block
                + " (" + block.getType()
                + ") {Coordinates:{X=" + block.getLocation().getBlockX()
                + ", Y=" + block.getLocation().getBlockY()
                + ", Z=" + block.getLocation().getBlockZ()
                + "}, World=" + block.getLocation().getWorld().getName() + "}";
    }

    public String getEntityInfo(EntityDamageEvent event, Entity entity)
    {
        return Utils.getName(entity)
                + " {Coordinates:{X=" + entity.getLocation().getX()
                + ", Y=" + entity.getLocation().getY()
                + ", Z=" + entity.getLocation().getZ()
                + "}, Orientation:{Yaw=" + entity.getLocation().getYaw()
                + ", Pitch=" + entity.getLocation().getPitch()
                + ", Direction=" + Utils.getDirection(entity)
                + (entity instanceof LivingEntity ? "}, Equipment:{Hand=" + ((LivingEntity) entity).getEquipment().getItemInHand()
                +  (((LivingEntity) entity).getEquipment().getHelmet() != null
                    && ((LivingEntity) entity).getEquipment().getHelmet().getType() != Material.AIR ? ", Helmet=" + ((LivingEntity) entity).getEquipment().getHelmet() : "")
                +  (((LivingEntity) entity).getEquipment().getChestplate() != null
                    && ((LivingEntity) entity).getEquipment().getChestplate().getType() != Material.AIR ? ", Chestplate=" + ((LivingEntity) entity).getEquipment().getChestplate() : "")
                +  (((LivingEntity) entity).getEquipment().getLeggings() != null
                    && ((LivingEntity) entity).getEquipment().getLeggings().getType() != Material.AIR ?", Leggings=" + ((LivingEntity) entity).getEquipment().getLeggings() : "")
                +  (((LivingEntity) entity).getEquipment().getBoots() != null
                    && ((LivingEntity) entity).getEquipment().getBoots().getType() != Material.AIR ? ", Boots=" + ((LivingEntity) entity).getEquipment().getBoots() : "")
                +  (((LivingEntity) entity).getActivePotionEffects().size() > 0 ? "}, Effects:{" +  ((LivingEntity) entity).getActivePotionEffects() : "")
                +  "}, Health=" + (((LivingEntity) entity).getHealth() - event.getDamage()) : "}")
                + ", World=" + entity.getLocation().getWorld().getName()
                + (entity instanceof Player ? ", Flying=" + ((Player) entity).isFlying()
                +  ", GameMode=" + ((Player) entity).getGameMode() : "")
                + ", EntityType=" + entity.getType()
                + "}";
    }

    public String getLavaInfo(Entity entity)
    {
        return "Lava"
                + " {Coordinates:{X=" + entity.getLocation().getBlockX()
                + ", Y=" + entity.getLocation().getBlockY()
                + ", Z=" + entity.getLocation().getBlockZ()
                + "}, World=" + entity.getLocation().getWorld().getName() + "}";
    }
}
