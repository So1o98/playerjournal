package com.player.journal.registry;

import com.player.journal.block.KnowledgeTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("playerjournal");

    public static final DeferredBlock<Block> KNOWLEDGE_TABLE = BLOCKS.register("knowledge_table",
            () -> new KnowledgeTableBlock(BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));
}