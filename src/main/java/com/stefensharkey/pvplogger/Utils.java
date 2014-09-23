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

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.BlockProjectileSource;

public class Utils {

  public static String getDirection(Entity entity) {
    int degrees = (Math.round(entity.getLocation().getYaw()) + 270) % 360;
    if (degrees <= 22) {
      return "NORTH";
    }
    if (degrees <= 67) {
      return "NORTH_EAST";
    }
    if (degrees <= 112) {
      return "EAST";
    }
    if (degrees <= 157) {
      return "SOUTH_EAST";
    }
    if (degrees <= 202) {
      return "SOUTH";
    }
    if (degrees <= 247) {
      return "SOUTH_WEST";
    }
    if (degrees <= 292) {
      return "WEST";
    }
    if (degrees <= 337) {
      return "NORTH_WEST";
    }
    if (degrees <= 359) {
      return "NORTH";
    }

    return "UNKNOWN";
  }

  public static String getEntityName(Entity entity) {
    if (entity instanceof LivingEntity) {
      if (entity instanceof Player) {
        return ((Player) entity).getName();
      }

      return ((LivingEntity) entity).getCustomName();
    } else if (entity instanceof Projectile) {
      if (((Projectile) entity).getShooter() instanceof BlockProjectileSource) {
        return ((BlockProjectileSource) ((Projectile) entity).getShooter()).getBlock().getType().name();
      }

      return ((Projectile) entity).getShooter().toString();
    }

    return entity.toString();
  }

  public static String getWeapon(Entity entity) {
    if (entity instanceof LivingEntity) {
      return ((LivingEntity) entity).getEquipment().getItemInHand().toString();
    } else if (entity instanceof Projectile) {
      String text = entity.toString();

      if (((Projectile) entity).getShooter() instanceof LivingEntity) {
        text +=
            " from " + ((LivingEntity) ((Projectile) entity).getShooter()).getEquipment()
                .getItemInHand();
      }

      return text;
    }

    return entity.toString();
  }
}
