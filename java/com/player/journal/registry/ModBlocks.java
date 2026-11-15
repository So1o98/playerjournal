package com.player.journal.registry;

import com.player.journal.block.KnowledgeTableBlock;
import com.player.journal.block.ResearchBenchBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {

    public static final Supplier<Block> KNOWLEDGE_TABLE = registerBlock("knowledge_table",
            () -> new KnowledgeTableBlock(BlockBehaviour.Properties.of()
                    .strength(2.5f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final Supplier<Block> RESEARCH_BENCH = registerBlock("research_bench",
            () -> new ResearchBenchBlock(
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()
            ));

    private static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> blockSupplier) {
        T block = Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath("playerjournal", name), blockSupplier.get());
        return () -> block;
    }

    public static void register() {
        // Triggers static initialization
    }
}