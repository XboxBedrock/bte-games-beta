package com.xboxbedrock.minecraft.games.events;

import com.xboxbedrock.minecraft.games.Games;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.xboxbedrock.minecraft.games.util.PlayerUtil.forceResourcePack;
import static org.bukkit.ChatColor.*;

public class PlayerResourcePackStatusEvent implements Listener {


    @EventHandler
    public void onPlayerResourcePackStatus(org.bukkit.event.player.PlayerResourcePackStatusEvent event){
        if (event.getStatus() == org.bukkit.event.player.PlayerResourcePackStatusEvent.Status.DECLINED) {
            event.getPlayer().kickPlayer("" + RED + ITALIC + UNDERLINE + BOLD + "You must accept the resource pack!");
        }
    }

    @EventHandler
    public void onPlayerWorldChange(org.bukkit.event.player.PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        String worldNameGlobal = p.getWorld().getName();

        if (Games.config.getList("twelve_worlds").contains(worldNameGlobal)) {
            forceResourcePack(p);
        } else {
            p.removeResourcePacks();
        }
    }
}
