package com.stefensharkey.pvplogger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PvPLogger extends JavaPlugin
{
    public static boolean DEBUG_MODE = false;

    @Override
    public void onEnable()
    {
        if(!new File(getDataFolder() + File.separator + "config.yml").exists())
            saveDefaultConfig();

        getDebugMode();

        getServer().getPluginManager().registerEvents(new PvPLoggerListener(this), this);
    }

    @Override
    public void onDisable()
    {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length != 0)
            if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("pvplogger.reload"))
            {
                reloadConfig();
                getDebugMode();
                sender.sendMessage(ChatColor.DARK_RED + "PvPLogger has been reloaded!");
            }

        return false;
    }

    public boolean getDebugMode()
    {
        return DEBUG_MODE = getConfig().getBoolean("debug");
    }
}
