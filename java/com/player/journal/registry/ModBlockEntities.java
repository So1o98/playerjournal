package com.player.journal.registry;

import com.player.journal.block.entity.KnowledgeTableBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "playerjournal");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KnowledgeTableBlockEntity>> KNOWLEDGE_TABLE_BE =
            BLOCK_ENTITIES.register("knowledge_table_be", () -> BlockEntityType.Builder.of(
                    KnowledgeTableBlockEntity::new, ModBlocks.KNOWLEDGE_TABLE.get()).build(null));

    public static final java.util.function.Supplier<net.minecraft.world.level.block.entity.BlockEntityType<com.player.journal.block.entity.ResearchBenchBlockEntity>> RESEARCH_BENCH =
            BLOCK_ENTITIES.register("research_bench", () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(
                    com.player.journal.block.entity.ResearchBenchBlockEntity::new,
                    com.player.journal.registry.ModBlocks.RESEARCH_BENCH.get()
            ).build(null));
}