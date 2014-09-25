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

import com.stefensharkey.pvplogger.configuration.Configuration;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPLogger extends JavaPlugin {

  private static Configuration config;

  public static Plugin plugin;

  @Override
  public void onEnable() {

    plugin = this;

    config = new Configuration(plugin);

    config.loadConfig();
    getServer().getPluginManager().registerEvents(new PvPLoggerListener(), this);
  }

  @Override
  public void onDisable() {}

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length != 0) {
      if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("pvplogger.reload")) {
        reloadConfig();
        config.loadConfig();
        sender.sendMessage(ChatColor.DARK_RED + "PvPLogger has been reloaded!");
      }
    }

    return false;
  }

  public static Configuration getConfiguration() {
    return config;
  }
}
