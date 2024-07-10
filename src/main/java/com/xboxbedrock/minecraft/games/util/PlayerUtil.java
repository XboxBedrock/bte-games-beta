package com.xboxbedrock.minecraft.games.util;

import com.xboxbedrock.minecraft.games.Games;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.RED;

public class PlayerUtil {

    public static boolean isBannedItemStack(ItemStack item) {
        if (item == null) return false;
        return !Games.BLOCKS_112.contains(item.getType().name().toLowerCase());
    }

    public static void tellPlayerSmall(Player player) {
        if (player == null) return;
        Games.instance.getServer().getScheduler().runTaskLater(Games.instance, () -> {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("" + BOLD + RED + "Please only use 1.12.2 blocks"));
        }, 7);
    }

    public static void forceResourcePack(Player player) {
        byte[] bytes = hexStringToByteArray(Games.PACK_SHA1);
        if (player == null) return;
        player.setResourcePack(Games.PACK_URL, bytes, "Accept pack please", true);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
