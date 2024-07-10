package com.xboxbedrock.minecraft.games.events;

import com.xboxbedrock.minecraft.games.Games;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xboxbedrock.minecraft.games.util.PlayerUtil.*;
import static java.lang.String.valueOf;
import static org.bukkit.ChatColor.*;

public class PlayerJoinEvent implements Listener {

    public static HashMap<UUID, BukkitTask> taskStore = new HashMap<>();
    public static HashMap<UUID, BukkitTask> taskStoreInv = new HashMap<>();

    public PlayerJoinEvent() {
    }

    @EventHandler
    private void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        Player p = event.getPlayer();

        p.sendTitle(BOLD + "" + GOLD + "Welcome to BTE Games", RED + "May the best team win!");

        AtomicInteger count = new AtomicInteger();

        PlayerJoinEvent.taskStore.put(p.getUniqueId(), Games.instance.getServer().getScheduler().runTaskTimer(Games.instance, () -> {
            count.getAndIncrement();
            if (count.get() >= 6) {
                p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.0F, 1.0F);
                BukkitTask task = taskStore.get(p.getUniqueId());
                task.cancel();
            } else {
                p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.0F);
            }
        }


                , 5, 5));

        String worldNameGlobal = p.getWorld().getName();

        if (Games.config.getList("twelve_worlds").contains(worldNameGlobal)) {
            forceResourcePack(p);
        } else {
            p.removeResourcePacks();
        }

        PlayerJoinEvent.taskStoreInv.put(p.getUniqueId(), Games.instance.getServer().getScheduler().runTaskTimer(Games.instance, () -> {
            if (!p.isOnline()){
                BukkitTask task = taskStoreInv.get(p.getUniqueId());
                task.cancel();
            }

            String worldName = p.getWorld().getName();
            if( Games.config.getList("twelve_worlds").contains(worldName)) for (ItemStack itemStack : p.getInventory()) {
                boolean hasBanned = false;
                if (isBannedItemStack(itemStack)) {
                    if (!hasBanned) hasBanned = true;
                    p.getInventory().remove(itemStack);
                }

                if (hasBanned) tellPlayerSmall(p);

            }




             }, 5, 5));




    }

}