package com.xboxbedrock.minecraft.games;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.fastasyncworldedit.bukkit.adapter.SimpleBukkitAdapter;
import com.sk89q.worldedit.WorldEdit;
import com.xboxbedrock.minecraft.games.commands.TpllCommand;
import com.xboxbedrock.minecraft.games.events.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public final class Games extends JavaPlugin {

    public static FileConfiguration config;
    public static Games instance;
    public static ProtocolManager protocolManager;
    public static SimpleBukkitAdapter bukkitAdapter = new SimpleBukkitAdapter();

    public static String PACK_URL = "https://download.mc-packs.net/pack/b5bed1453335c3aee94cc388f67172e093986c48.zip";
    public static String PACK_SHA1 = "b5bed1453335c3aee94cc388f67172e093986c48";

    public static Set<String> BLOCKS_112 = new HashSet<String>();

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        this.saveDefaultConfig();
        this.saveResource("112.txt", true);

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(getDataFolder() + File.separator + "112.txt");
        } catch (FileNotFoundException e) {
            getLogger().severe("Failed to load 112.txt");
            setEnabled(false);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        reader.lines().forEach((String line) -> {
            Games.BLOCKS_112.add(line);
        });

        config = getConfig();

        protocolManager = ProtocolLibrary.getProtocolManager();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerResourcePackStatusEvent(), this);

        WorldEdit.getInstance().getEventBus().register(new EditSessionEvent());

        getCommand("tpll").setExecutor(new TpllCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
