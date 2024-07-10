package com.xboxbedrock.minecraft.games.events;

import com.xboxbedrock.minecraft.games.Games;
import com.xboxbedrock.minecraft.games.util.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static com.xboxbedrock.minecraft.games.util.PlayerUtil.isBannedItemStack;
import static com.xboxbedrock.minecraft.games.util.PlayerUtil.tellPlayerSmall;
import static org.bukkit.ChatColor.*;

public class BlockPlaceEvent implements Listener {

    public BlockPlaceEvent() {}

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event) {
        //Just in case another plugin cancels it
        if (!event.isCancelled()) {
            String worldName = event.getBlock().getWorld().getName();

            if (Games.config.getList("twelve_worlds").contains(worldName)) {
                String blockName = event.getBlockPlaced().getBlockData().getMaterial().name().toLowerCase();

                if (!Games.BLOCKS_112.contains(blockName)) {
                    event.getPlayer().sendTitle(BOLD + "" + RED + "Please only use 1.12.2 Blocks", RED + blockName + " is not a valid 1.12 block");

                    event.setCancelled(true);
                    return;
                }
                else {
                    event.setCancelled(false);
                }
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryCreativeEvent event) {
        String worldName = event.getWhoClicked().getWorld().getName();

        if (Games.config.getList("twelve_worlds").contains(worldName)) {

            final Player p = (Player) event.getWhoClicked();
            final int heldItemSlot = p.getInventory().getHeldItemSlot();

            if (event.getHotbarButton() == heldItemSlot) {
                final ItemStack item = p.getInventory().getItem(event.getSlot());
                if (item != null && isBannedItemStack(item)) {
                    event.setCancelled(true);
                    tellPlayerSmall(p);
                    p.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
                    return;
                }
            } else if (event.getHotbarButton() > -1) {
                if (event.getSlot() == heldItemSlot) {
                    final ItemStack item = p.getInventory().getItem(event.getHotbarButton());
                    if (item != null && isBannedItemStack(item)) {
                        event.setCancelled(true);
                        tellPlayerSmall(p);
                        p.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
                        p.getInventory().setItem(event.getHotbarButton(), new ItemStack(Material.AIR));
                        return;
                    }
                }
            }

            if (!event.getView().getTopInventory().equals(event.getView().getBottomInventory()) && event.getView().getTopInventory().equals(Util.getClickedInventory(event.getView(), event.getRawSlot())) && event.isShiftClick()) {
                final ItemStack item = event.getCurrentItem();
                if (item == null) return;
                final List<Integer> changedSlots = Util.getChangedSlots(p.getInventory(), item);
                if (changedSlots.contains(heldItemSlot) && isBannedItemStack(item)) {
                    event.setCancelled(true);
                    tellPlayerSmall(p);
                    p.getInventory().setItem(heldItemSlot, new ItemStack(Material.AIR));
                    for (Integer changedSlot : changedSlots) {
                        p.getInventory().setItem(changedSlot, new ItemStack(Material.AIR));
                    }
                    return;
                }
            }

            if (event.getSlot() == heldItemSlot) {
                final ItemStack cursor = event.getCursor();
                if (cursor != null && isBannedItemStack(cursor)) {
                    event.getInventory().remove(cursor);
                    event.setCancelled(true);
                    tellPlayerSmall(p);
                }
            }

        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    private void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        String worldName = event.getEntity().getWorld().getName();
        if (Games.config.getList("twelve_worlds").contains(worldName)) {
            if (!(event.getEntity() instanceof Player)) return;
            final Player p = (Player) event.getEntity();
            final int toSlot = p.getInventory().firstEmpty();
            if (toSlot == p.getInventory().getHeldItemSlot() && isBannedItemStack(event.getItem().getItemStack())) {
                event.setCancelled(true);
                tellPlayerSmall(p);
            }
        }

    }

    @EventHandler
    private void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        if (Games.config.getList("twelve_worlds").contains(worldName)) {
            final ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
            if (item != null && isBannedItemStack(item)) {
                event.setCancelled(true);
                tellPlayerSmall(event.getPlayer());
                event.getPlayer().getInventory().setItem(event.getNewSlot(), new ItemStack(Material.AIR));
            }
        }
    }
}
