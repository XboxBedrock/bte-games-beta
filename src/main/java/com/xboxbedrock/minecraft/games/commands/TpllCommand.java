package com.xboxbedrock.minecraft.games.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.xboxbedrock.minecraft.games.Games;
import io.papermc.lib.PaperLib;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.projection.GeographicProjection;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.ChatColor.RED;


public class TpllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player player = (Player) commandSender;
        if (!command.getName().equalsIgnoreCase("tpll")) {
            player.sendMessage(Games.config.getString("prefix") + "§7Usage: /tpll <longitudes> <latitudes>");
            return true;
        }
        if (!player.hasPermission("t+-.tpll")) {
            player.sendMessage(Games.config.getString("prefix") + "§7No permission for /tpll");
            return true;
        }


        // Entity selector

        // detect if command starts with @ or with a player name

        if ((args[0].startsWith("@") || !isDouble(args[0].replace(",", "").replace("°", ""))) && player.hasPermission("t+-.forcetpll")) {
            if (args[0].equals("@a")) {
                StringBuilder playerList = new StringBuilder();
                Games.instance.getServer().getOnlinePlayers().forEach(p -> {
                    p.chat("/tpll " + String.join(" ", args).substring(2));
                    if (Games.instance.getServer().getOnlinePlayers().size() > 1) {
                        playerList.append(p.getName()).append(", ");
                    } else {
                        playerList.append(p.getName()).append(" ");
                    }
                });
                // delete last comma if no player follows
                if (playerList.length() > 0 && playerList.charAt(playerList.length() - 2) == ',') {
                    playerList.deleteCharAt(playerList.length() - 2);
                }
                player.sendMessage(Games.config.getString("prefix") + "§7Teleported §9" + playerList + "§7to" + String.join(" ", args).substring(2));
                return true;
            } else if (args[0].equals("@p")) {
                // find nearest player but not the player itself
                Player nearestPlayer = null;
                double nearestDistance = Double.MAX_VALUE;
                for (Player p : Games.instance.getServer().getOnlinePlayers()) {
                    if (p.getLocation().distanceSquared(player.getLocation()) < nearestDistance && (!p.equals(player) || Games.instance.getServer().getOnlinePlayers().size() == 1)) {
                        nearestPlayer = p;
                        nearestDistance = p.getLocation().distanceSquared(player.getLocation());
                    }
                }
                if (nearestPlayer != null) {
                    player.sendMessage(Games.config.getString("prefix") + "§7Teleported §9" + nearestPlayer.getName() + " §7to" + String.join(" ", args).substring(2));
                    nearestPlayer.chat("/tpll " + String.join(" ", args).substring(2));
                }
                return true;
            } else {
                Player target = null;
                //check if target player is online
                for (Player p : Games.instance.getServer().getOnlinePlayers()) {
                    if (p.getName().equals(args[0])) {
                        target = p;
                    }
                }

                if (target == null) {
                    player.sendMessage(Games.config.getString("prefix") + "§cNo player found with name §9" + args[0]);
                    return true;
                }

                player.sendMessage(Games.config.getString("prefix") + "§7Teleported §9" + target.getName() + " §7to " + args[1] + " " + args[2]);
                target.chat("/tpll " + String.join(" ", args).replace(target.getName(), ""));
                return true;
            }
        }

        //If sender is not a player cancel the command.
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command can only be used by players!");
            return true;
        }

        // -
        if (args.length >= 2) {

            World tpWorld = player.getWorld();

            String worldName = tpWorld.getName();

            if (!Games.config.getList("work_in").contains(worldName)) {
                commandSender.sendMessage(Games.config.getString("prefix") + RED + "TPLL is disabled in this world!");
                return true;
            }

            int xOffset = Games.config.getInt("offsets." + worldName + ".x");
            int zOffset = Games.config.getInt("offsets." + worldName + ".z");
            int yOffset = Games.config.getInt("offsets." + worldName + ".y");

            double[] coordinates = new double[2];
            coordinates[1] = Double.parseDouble(args[0].replace(",", "").replace("°", ""));
            coordinates[0] = Double.parseDouble(args[1].replace("°", ""));


            EarthGeneratorSettings bteSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
            GeographicProjection projection = bteSettings.projection();

            double[] mcCoordinates;
            try {
                mcCoordinates = projection.fromGeo(coordinates[0], coordinates[1]);
            } catch (OutOfProjectionBoundsException e) {
                commandSender.sendMessage(RED + "Location is not within projection bounds");
                return true;
            }

            double height;
            if (args.length >= 3) {
                height = Double.parseDouble(args[2]) + yOffset;
            } else {
                height = 0;
            }
            Location location = new Location(tpWorld, mcCoordinates[0], height, mcCoordinates[1], player.getLocation().getYaw(), player.getLocation().getPitch());

            if (PaperLib.isChunkGenerated(location)) {

                if (args.length >= 3) {
                    location = new Location(tpWorld, mcCoordinates[0], height, mcCoordinates[1], player.getLocation().getYaw(), player.getLocation().getPitch());
                } else {
                    location = new Location(tpWorld, mcCoordinates[0], tpWorld.getHighestBlockYAt((int) mcCoordinates[0], (int) mcCoordinates[1]) + 1, mcCoordinates[1], player.getLocation().getYaw(), player.getLocation().getPitch());
                }
            } else {
                player.sendMessage(Games.config.getString("prefix") + "§7Location is not generated, no going here for you!");
            }
            PaperLib.teleportAsync(player, location);


            if (args.length >= 3) {
                player.sendMessage(Games.config.getString("prefix") + "§7Teleported to " + coordinates[1] + ", " + coordinates[0] + ", " + height + ".");
            } else {
                player.sendMessage(Games.config.getString("prefix") + "§7Teleported to " + coordinates[1] + ", " + coordinates[0] + ".");
            }

            return true;

        }
        return true;
    }

    public boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
