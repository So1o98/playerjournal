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

    public static final net.neoforged.neoforge.registries.DeferredBlock<net.minecraft.world.level.block.Block> RESEARCH_BENCH =
            BLOCKS.register("research_bench", () -> new com.player.journal.block.ResearchBenchBlock(
                    net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS).noOcclusion()
            ));
}