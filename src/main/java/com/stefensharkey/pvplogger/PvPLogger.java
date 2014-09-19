package com.stefensharkey.pvplogger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class PvPLogger extends JavaPlugin
{
    private Logger log;

    @Override
    public void onEnable()
    {
        log = getLogger();
    }

    @Override
    public void onDisable()
    {

    }

    public void logToFile(Entity attacker, Entity damaged, String message)
    {
        File dataFolder = getDataFolder();
        File attackerFile;
        File damagedFile;

        if(attacker instanceof Player)
        {
            attackerFile = new File(dataFolder, ((Player)attacker).getUniqueId().toString());
            if(!attackerFile.exists())
                try
                {
                    attackerFile.createNewFile();
                } catch(IOException e)
                {
                    e.printStackTrace();
                }
        }

        if(!dataFolder.exists())
            dataFolder.mkdir();


    }
}
