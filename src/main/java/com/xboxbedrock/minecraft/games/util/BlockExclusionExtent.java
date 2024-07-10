package com.xboxbedrock.minecraft.games.util;

import com.fastasyncworldedit.core.extent.processor.ProcessorScope;
import com.fastasyncworldedit.core.queue.IBatchProcessor;
import com.fastasyncworldedit.core.queue.IChunk;
import com.fastasyncworldedit.core.queue.IChunkGet;
import com.fastasyncworldedit.core.queue.IChunkSet;
import com.fastasyncworldedit.core.util.ExtentTraverser;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypesCache;
import com.xboxbedrock.minecraft.games.Games;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockExclusionExtent extends AbstractDelegateExtent implements IBatchProcessor {

    public BlockExclusionExtent(Extent extent) {
        super(extent);
        System.out.println("hi");

        super.addProcessor(this);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(final BlockVector3 location, final T block) throws WorldEditException {


        BlockData bukkitBlock = Games.bukkitAdapter.adapt(block);

        String blockName = bukkitBlock.getMaterial().name().toLowerCase();

        boolean valid = Games.BLOCKS_112.contains(blockName);

        System.out.println("invalid block: " + blockName);

        if (valid) return super.setBlock(location, block);

        return true;

    }

    @Override
    public <B extends BlockStateHolder<B>> boolean setBlock(int x, int y, int z, final B block) throws WorldEditException {
        BlockData bukkitBlock = Games.bukkitAdapter.adapt(block);

        String blockName = bukkitBlock.getMaterial().name().toLowerCase();

        boolean valid = Games.BLOCKS_112.contains(blockName);

        System.out.println("invalid block: " + blockName);

        if (valid) return super.setBlock(x, y, z, block);

        return true;
    }

    @Override
    public IChunkSet processSet(final IChunk chunk, final IChunkGet get, final IChunkSet set) {
        for (int layer = set.getMinSectionPosition(); layer <= set.getMaxSectionPosition(); layer++) {
            if (!set.hasSection(layer)) {
                continue;
            }
            char[] blocks = Objects.requireNonNull(set.loadIfPresent(layer));
            it:
            for (int i = 0; i < blocks.length; i++) {
                char block = blocks[i];
                if (block == BlockTypesCache.ReservedIDs.__RESERVED__) {
                    continue;
                }
                BlockState state = BlockTypesCache.states[block];
                if (!Games.BLOCKS_112.contains((state.getBlockType().id().replace("minecraft:", "")).toLowerCase())) {
                    blocks[i] = BlockTypesCache.ReservedIDs.__RESERVED__;
                    continue;
                }
                blocks[i] = state.getOrdinalChar();
            }
        }
        return set;
    }


    @Nullable
    @Override
    public Entity createEntity(final Location location, final BaseEntity entity) {
        return super.createEntity(location, entity);
    }

    @Override
    public boolean setBiome(final BlockVector3 position, final BiomeType biome) {
        return super.setBiome(position, biome);
    }

    @Override
    protected Operation commitBefore() {
        return super.commitBefore();
    }

    @Nullable
    @Override
    public Extent construct(final Extent child) {
        if (getExtent() != child) {
            new ExtentTraverser<Extent>(this).setNext(child);
        }
        return this;
    }

    @Override
    public ProcessorScope getScope() {
        return ProcessorScope.REMOVING_BLOCKS;
    }

}
