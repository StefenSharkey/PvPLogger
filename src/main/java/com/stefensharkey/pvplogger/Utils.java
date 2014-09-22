package com.stefensharkey.pvplogger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

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
    if (entity instanceof Player) {
      return ((Player) entity).getName();
    } else if (entity instanceof Projectile) {
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
