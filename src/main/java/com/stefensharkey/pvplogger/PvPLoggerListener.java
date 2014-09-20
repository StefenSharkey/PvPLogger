package com.stefensharkey.pvplogger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    public void onEntityDamageByEntityEvent(final EntityDamageByEntityEvent event)
    {
        logToFile(event.getDamager(), event.getEntity(), formatMessage(event));
    }

    public String entityInfo(Entity entity)
    {
        return formatName(entity)
                + " {Coordinates={X=" + entity.getLocation().getBlockX()
                + " Y=" + entity.getLocation().getBlockY()
                + " Z=" + entity.getLocation().getBlockZ()
                + "}, Orientation={Yaw=" + entity.getLocation().getYaw()
                + " Pitch=" + entity.getLocation().getPitch()
                + "}, World=" + entity.getLocation().getWorld().getName() + "}";
    }

    public String formatMessage(EntityDamageByEntityEvent event)
    {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(cal.getTime()) + "]: " + formatName(event.getDamager()) + " (UUID: "
                + event.getDamager().getUniqueId() + ") attacked " + formatName(event.getEntity()) + " (UUID: "
                + event.getEntity().getUniqueId() + ") with " + formatWeapon(event.getDamager()) + " for "
                + event.getDamage() + " damage."
                + "\n" + entityInfo(event.getDamager()) + "\n" + entityInfo(event.getEntity())
                + "\n";
    }

    public String formatName(Entity entity)
    {
        if(entity instanceof Projectile)
            return ((Projectile) entity).getShooter().toString();
        if(entity instanceof Player)
            return ((Player) entity).getName();
        return entity.toString();
    }

    public String formatWeapon(Entity entity)
    {
        if(entity instanceof LivingEntity)
            return ((LivingEntity) entity).getEquipment().getItemInHand().toString();
        return entity.toString();
    }

    public void logToFile(Entity attacker, Entity damaged, String message)
    {
        File saveTo;
        FileWriter fileWriter;
        PrintWriter printWriter;

        if(!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        try
        {
            if(attacker instanceof Player)
            {
                saveTo = new File(plugin.getDataFolder(), (attacker).getUniqueId().toString() + ".log");

                fileWriter = new FileWriter(saveTo, true);
                printWriter = new PrintWriter(fileWriter);
                printWriter.println(message);
                printWriter.flush();
                printWriter.close();
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
