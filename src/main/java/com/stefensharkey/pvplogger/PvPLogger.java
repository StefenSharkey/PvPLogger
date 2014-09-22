package com.stefensharkey.pvplogger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PvPLogger extends JavaPlugin {

  public static final int FLATFILE = 0;
  public static final int JSON = 1;
  public static boolean debugMode = false;
  public static int storageType = 0;

  @Override
  public void onEnable() {
    if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
      saveDefaultConfig();
    }

    loadCustomConfig();
    getServer().getPluginManager().registerEvents(new PvPLoggerListener(this), this);
  }

  @Override
  public void onDisable() {
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length != 0) {
      if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("pvplogger.reload")) {
        reloadConfig();
        loadCustomConfig();

        sender.sendMessage(ChatColor.DARK_RED + "PvPLogger has been reloaded!");
      }
    }

    return false;
  }

  public void loadCustomConfig() {
    debugMode = getConfig().getBoolean("debug");

    switch (getConfig().getString("output-format").toLowerCase()) {
      case "flatfile":
        storageType = FLATFILE;
        break;
      case "json":
        storageType = JSON;
        break;
      default:
        storageType = FLATFILE;
        break;
    }
  }
}
