package com.stefensharkey.pvplogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

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
                    if(PvPLogger.debugMode)
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
                if(PvPLogger.debugMode)
                {
                    plugin.getLogger().info("");
                    plugin.getLogger().info("onEntityDamageEvent()");
                    plugin.getLogger().info("Event Type: " + event.getEventName());
                    plugin.getLogger().info("Entity: " + ((Player) event.getEntity()).getName());
                    plugin.getLogger().info("Cause: " + event.getCause());
                }

                logToFile(event, event.getEntity(), formatMessage(event));
            }

            if(PvPLogger.debugMode)
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
            if(PvPLogger.debugMode)
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
                    + Utils.getEntityName(entity) + " (UUID: " + entity.getUniqueId() + ") for " + damage + " damage."
                    + (PvPLogger.debugMode ? " (" + event.getEventName() + ")" : "")
                    + "\n" + (isLava ? getLavaInfo(entity) : getEntityInfo(damager))
                    + "\n" + getEntityInfo(event, entity)
                    + "\n";
        } else if(event instanceof EntityDamageByEntityEvent)
        {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            Entity entity = event.getEntity();
            double damage = event.getDamage();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getEntityName(damager) + " (UUID: " + damager.getUniqueId()
                    + ")" + (((LivingEntity) entity).getHealth() - damage <= 0 ? " killed " : " damaged ")
                    + Utils.getEntityName(entity) + " (UUID: " + entity.getUniqueId() + ") with " + Utils.getWeapon(damager)
                    + " for " + damage + " damage." + (PvPLogger.debugMode ? " (" + event.getEventName() + ")" : "")
                    + "\n" + getEntityInfo(event, damager)
                    + "\n" + getEntityInfo(event, entity)
                    + "\n";
        } else if(event != null)
        {
            Entity entity = event.getEntity();
            double damage = event.getDamage();

            return "[" + sdf.format(cal.getTime()) + "]: " + Utils.getEntityName(entity) + " (UUID: " + entity.getUniqueId()
                    + ") was" + (((LivingEntity) entity).getHealth() - damage <= 0 ? " killed " : " damaged ") + "by "
                    + event.getCause() + " for " + damage + " damage."
                    + (PvPLogger.debugMode ? " (" + event.getEventName() + ")" : "")
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
        if(PvPLogger.storageType == PvPLogger.JSON)
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject obj = new JsonObject();
            JsonObject blockObj = new JsonObject();
            JsonObject blockCoords = new JsonObject();

            obj.add(block.toString(), blockObj);

            blockObj.addProperty("name", block.toString());
            blockObj.addProperty("world", block.getWorld().getName());
            blockObj.add("coordinates", blockCoords);

            blockCoords.addProperty("x", block.getLocation().getX());
            blockCoords.addProperty("y", block.getLocation().getY());
            blockCoords.addProperty("z", block.getLocation().getZ());

            return gson.toJson(obj);
        }

        return block
                + " (" + block.getType()
                + ") {Coordinates:{X=" + block.getLocation().getBlockX()
                + ", Y=" + block.getLocation().getBlockY()
                + ", Z=" + block.getLocation().getBlockZ()
                + "}, World=" + block.getLocation().getWorld().getName() + "}";
    }

    public String getEntityInfo(EntityDamageEvent event, Entity entity)
    {
        if(PvPLogger.storageType == PvPLogger.JSON)
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject obj = new JsonObject();
            JsonObject entityObj = new JsonObject();
            JsonObject entityCoords = new JsonObject();
            JsonObject entityOrientation = new JsonObject();
            JsonObject entityEquipment = new JsonObject();
            JsonObject entityHand = new JsonObject();
            JsonArray entityHandEnchants = new JsonArray();
            JsonObject entityHelmet = new JsonObject();
            JsonArray entityHelmetEnchants = new JsonArray();
            JsonObject entityChestplate = new JsonObject();
            JsonArray entityChestplateEnchants = new JsonArray();
            JsonObject entityLeggings = new JsonObject();
            JsonArray entityLeggingsEnchants = new JsonArray();
            JsonObject entityBoots = new JsonObject();
            JsonArray entityBootsEnchants = new JsonArray();

            if(entity instanceof Projectile)
                if(((Projectile) entity).getShooter() instanceof LivingEntity)
                    entity = (Entity) ((Projectile) entity).getShooter();

            obj.add(Utils.getEntityName(entity), entityObj);

            entityObj.addProperty("name", Utils.getEntityName(entity));
            entityObj.addProperty("uuid", entity.getUniqueId().toString());
            entityObj.addProperty("type", entity.getType().toString());
            entityObj.addProperty("world", entity.getWorld().getName());

            if(entity instanceof LivingEntity)
            {
                entityObj.addProperty("health", (event.getEntity() == entity
                        ? ((LivingEntity) entity).getHealth() - event.getDamage()
                        : ((LivingEntity) entity).getHealth()));
                entityObj.addProperty("dead", event.getEntity() == entity && ((LivingEntity) entity).getHealth() - event.getDamage() <= 0);
            }

            if(entity instanceof Player)
            {
                entityObj.addProperty("hunger", ((Player) entity).getFoodLevel());
                entityObj.addProperty("gamemode", ((Player) entity).getGameMode().toString());
                entityObj.addProperty("flying", ((Player) entity).isFlying());
            }

            entityObj.add("coordinates", entityCoords);
            entityObj.add("orientation", entityOrientation);

            if(entity instanceof LivingEntity)
                entityObj.add("equipment", entityEquipment);

            entityCoords.addProperty("x", entity.getLocation().getX());
            entityCoords.addProperty("y", entity.getLocation().getY());
            entityCoords.addProperty("z", entity.getLocation().getZ());

            entityOrientation.addProperty("yaw", entity.getLocation().getYaw());
            entityOrientation.addProperty("pitch", entity.getLocation().getPitch());
            entityOrientation.addProperty("direction", Utils.getDirection(entity));

            if(entity instanceof LivingEntity)
            {
                if(((LivingEntity) entity).getEquipment().getItemInHand() != null)
                {
                    ItemStack hand = ((LivingEntity) entity).getEquipment().getItemInHand();

                    entityHand.addProperty("item", hand.getData().getItemType().toString());

                    if(hand.hasItemMeta())
                    {
                        if(hand.getItemMeta().hasDisplayName())
                            entityHand.addProperty("name", hand.getItemMeta().getDisplayName());

                        if(hand.getItemMeta().hasEnchants())
                        {
                            for(Map.Entry<Enchantment, Integer> enchantment: hand.getItemMeta().getEnchants().entrySet())
                            {
                                JsonObject entityHandEnchant = new JsonObject();

                                entityHandEnchants.add(entityHandEnchant);

                                entityHandEnchant.addProperty("name", enchantment.getKey().getName());
                                entityHandEnchant.addProperty("level", enchantment.getValue());
                            }

                            entityHand.add("enchantments", entityHandEnchants);
                        }
                    }

                    entityHand.addProperty("durability", hand.getDurability());
                    entityEquipment.add("hand", entityHand);
                }

                if(((LivingEntity) entity).getEquipment().getHelmet() != null
                        && ((LivingEntity) entity).getEquipment().getHelmet().getType() != Material.AIR)
                {
                    ItemStack helmet = ((LivingEntity) entity).getEquipment().getHelmet();

                    entityHelmet.addProperty("item", helmet.getData().getItemType().toString());

                    if(helmet.hasItemMeta())
                    {
                        if(helmet.getItemMeta().hasDisplayName())
                            entityHelmet.addProperty("name", helmet.getItemMeta().getDisplayName());

                        if(helmet.getItemMeta().hasEnchants())
                        {
                            for(Map.Entry<Enchantment, Integer> enchantment: helmet.getItemMeta().getEnchants().entrySet())
                            {
                                JsonObject entityHelmetEnchant = new JsonObject();

                                entityHelmetEnchants.add(entityHelmetEnchant);

                                entityHelmetEnchant.addProperty("name", enchantment.getKey().getName());
                                entityHelmetEnchant.addProperty("level", enchantment.getValue());
                            }

                            entityHelmet.add("enchantments", entityHelmetEnchants);
                        }
                    }

                    entityHelmet.addProperty("durability", helmet.getDurability());
                    entityEquipment.add("helmet", entityHelmet);
                }

                if(((LivingEntity) entity).getEquipment().getChestplate() != null
                        && ((LivingEntity) entity).getEquipment().getChestplate().getType() != Material.AIR)
                {
                    ItemStack chestplate = ((LivingEntity) entity).getEquipment().getChestplate();

                    entityChestplate.addProperty("item", chestplate.getData().getItemType().toString());

                    if(chestplate.hasItemMeta())
                    {
                        if(chestplate.getItemMeta().hasDisplayName())
                            entityChestplate.addProperty("name", chestplate.getItemMeta().getDisplayName());

                        if(chestplate.getItemMeta().hasEnchants())
                        {
                            for(Map.Entry<Enchantment, Integer> enchantment: chestplate.getItemMeta().getEnchants().entrySet())
                            {
                                JsonObject entityChestplateEnchant = new JsonObject();

                                entityChestplateEnchants.add(entityChestplateEnchant);

                                entityChestplateEnchant.addProperty("name", enchantment.getKey().getName());
                                entityChestplateEnchant.addProperty("level", enchantment.getValue());
                            }

                            entityChestplate.add("enchantments", entityBootsEnchants);
                        }
                    }

                    entityChestplate.addProperty("durability", chestplate.getDurability());
                    entityEquipment.add("chestplate", entityChestplate);
                }

                if(((LivingEntity) entity).getEquipment().getLeggings() != null
                        && ((LivingEntity) entity).getEquipment().getLeggings().getType() != Material.AIR)
                {
                    ItemStack leggings = ((LivingEntity) entity).getEquipment().getLeggings();

                    entityLeggings.addProperty("item", leggings.getData().getItemType().toString());

                    if(leggings.hasItemMeta())
                    {
                        if(leggings.getItemMeta().hasDisplayName())
                            entityLeggings.addProperty("name", leggings.getItemMeta().getDisplayName());

                        if(leggings.getItemMeta().hasEnchants())
                        {
                            for(Map.Entry<Enchantment, Integer> enchantment: leggings.getItemMeta().getEnchants().entrySet())
                            {
                                JsonObject entityLeggingsEnchant = new JsonObject();

                                entityLeggingsEnchants.add(entityLeggingsEnchant);

                                entityLeggingsEnchant.addProperty("name", enchantment.getKey().getName());
                                entityLeggingsEnchant.addProperty("level", enchantment.getValue());
                            }

                            entityLeggings.add("enchantments", entityLeggingsEnchants);
                        }
                    }

                    entityLeggings.addProperty("durability", leggings.getDurability());
                    entityEquipment.add("leggings", entityLeggings);
                }

                if(((LivingEntity) entity).getEquipment().getBoots() != null
                        && ((LivingEntity) entity).getEquipment().getBoots().getType() != Material.AIR)
                {
                    ItemStack boots = ((LivingEntity) entity).getEquipment().getBoots();

                    entityBoots.addProperty("item", boots.getData().getItemType().toString());

                    if(boots.hasItemMeta())
                    {
                        if(boots.getItemMeta().hasDisplayName())
                            entityBoots.addProperty("name", boots.getItemMeta().getDisplayName());

                        if(boots.getItemMeta().hasEnchants())
                        {
                            for(Map.Entry<Enchantment, Integer> enchantment: boots.getItemMeta().getEnchants().entrySet())
                            {
                                JsonObject entityBootsEnchant = new JsonObject();

                                entityBootsEnchants.add(entityBootsEnchant);

                                entityBootsEnchant.addProperty("name", enchantment.getKey().getName());
                                entityBootsEnchant.addProperty("level", enchantment.getValue());
                            }

                            entityBoots.add("enchantments", entityBootsEnchants);
                        }
                    }

                    entityBoots.addProperty("durability", boots.getDurability());
                    entityEquipment.add("boots", entityBoots);
                }
            }

            return gson.toJson(obj);
        }

        return Utils.getEntityName(entity)
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
                + ", Type=" + entity.getType()
                + "}";
    }

    public String getLavaInfo(Entity entity)
    {
        if(PvPLogger.storageType == PvPLogger.JSON)
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject obj = new JsonObject();
            JsonObject entityObj = new JsonObject();
            JsonObject entityCoords = new JsonObject();

            obj.add("Lava", entityObj);

            entityObj.addProperty("name", "Lava");
            entityObj.addProperty("world", entity.getWorld().getName());
            entityObj.add("coordinates", entityCoords);

            entityCoords.addProperty("x", entity.getLocation().getX());
            entityCoords.addProperty("y", entity.getLocation().getY());
            entityCoords.addProperty("z", entity.getLocation().getZ());

            return gson.toJson(obj);
        }

        return "Lava"
                + " {Coordinates:{X=" + entity.getLocation().getBlockX()
                + ", Y=" + entity.getLocation().getBlockY()
                + ", Z=" + entity.getLocation().getBlockZ()
                + "}, World=" + entity.getLocation().getWorld().getName() + "}";
    }
}
