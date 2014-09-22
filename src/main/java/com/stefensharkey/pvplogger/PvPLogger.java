/*
 * This file is part of PvPLogger.
 *
 * PvPLogger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PvPLogger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PvPLogger.  If not, see <http://www.gnu.org/licenses/>.
 */

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
