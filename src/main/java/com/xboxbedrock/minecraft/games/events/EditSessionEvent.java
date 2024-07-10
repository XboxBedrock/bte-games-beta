package com.xboxbedrock.minecraft.games.events;

import com.fastasyncworldedit.core.extent.DisallowedBlocksExtent;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.xboxbedrock.minecraft.games.Games;
import com.xboxbedrock.minecraft.games.util.BlockExclusionExtent;

public class EditSessionEvent {
    @Subscribe
    public void onEditSessionEvent(com.sk89q.worldedit.event.extent.EditSessionEvent event) {
        System.out.println("yes");

        String worldName = event.getWorld().getName();

        final Extent extent = event.getExtent();

        if (Games.config.getList("twelve_worlds").contains(worldName)) {
//new DisallowedBlocksExtent(extent, Games.BLOCKS_112, null)
            event.setExtent(new BlockExclusionExtent(extent));
        }
    }
}


