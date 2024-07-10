package com.xboxbedrock.minecraft.games.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.bukkit.ChatColor.BOLD;

public class PlayerMoveEvent implements Listener {

    private Plugin plugin;

    private Map<String, Integer> levelYOffset;

    public PlayerMoveEvent(Plugin plugin) {
        this.plugin = plugin;

        this.levelYOffset = new HashMap<>();

        for (Object level: plugin.getConfig().getList("work_in")) {

            String levelName = (String) level;

            int levelY = plugin.getConfig().getInt("offsets."+levelName+".y");

            this.levelYOffset.put(levelName, levelY);

            this.startKeepActionBarAlive();
        }
    }

    @EventHandler
    void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        setHeightInActionBar(player);
    }


    private void startKeepActionBarAlive() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                setHeightInActionBar(p);
            }
        }, 0, 20);
    }

    private void setHeightInActionBar(Player p) {
        boolean inValidWorld = levelYOffset.keySet().contains(p.getWorld().getName());

        if (inValidWorld && (p.getInventory().getItemInMainHand().getType() != Material.DEBUG_STICK)) {
            int height = p.getLocation().getBlockY() - levelYOffset.get(p.getWorld().getName());
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(BOLD + valueOf(height) + "m"));
        } else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        }

    }

}
