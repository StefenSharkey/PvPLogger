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

package com.stefensharkey.pvplogger.configuration;

import com.stefensharkey.pvplogger.storage.MySQLDatabase;

import org.bukkit.plugin.Plugin;

import java.io.File;

public class Configuration {

  private boolean debugMode;

  private MySQLDatabase mySQLDatabase;
  private Plugin plugin;
  private StorageType storageType;

  public Configuration(Plugin plugin) {
    this.plugin = plugin;
  }

  public void loadConfig() {
    if (!new File(plugin.getDataFolder() + File.separator + "config.yml").exists()) {
      plugin.saveDefaultConfig();
    }

    setDebugMode(plugin.getConfig().getBoolean("debug"));

    switch (plugin.getConfig().getString("output-format").toLowerCase()) {
      case "json":
        setStorageType(StorageType.JSON);
        break;
      case "mysql":
        setStorageType(StorageType.MYSQL);
        break;
      default:
        plugin.getConfig().set("output-format", "json");
        setStorageType(StorageType.JSON);
        break;
    }
  }

  public MySQLDatabase getMySQLDatabase() {
    return mySQLDatabase;
  }

  public StorageType getStorageType() {
    return storageType;
  }

  public void setStorageType(StorageType storageType) {
    this.storageType = storageType;
  }

  public boolean getDebugMode() {
    return debugMode;
  }

  public void setDebugMode(boolean debugMode) {
    this.debugMode = debugMode;
  }

  public boolean toggleDebugMode() {
    return debugMode = !debugMode;
  }
}
