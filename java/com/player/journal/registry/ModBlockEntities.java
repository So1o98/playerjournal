package com.player.journal.registry;

import com.player.journal.block.entity.KnowledgeTableBlockEntity;
import com.player.journal.block.entity.ResearchBenchBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final Supplier<BlockEntityType<KnowledgeTableBlockEntity>> KNOWLEDGE_TABLE_BE =
            register("knowledge_table_be", () -> BlockEntityType.Builder.of(
                    KnowledgeTableBlockEntity::new, ModBlocks.KNOWLEDGE_TABLE.get()).build(null));

    public static final Supplier<BlockEntityType<ResearchBenchBlockEntity>> RESEARCH_BENCH =
            register("research_bench", () -> BlockEntityType.Builder.of(
                    ResearchBenchBlockEntity::new, ModBlocks.RESEARCH_BENCH.get()).build(null));

    private static <T extends BlockEntityType<?>> Supplier<T> register(String name, Supplier<T> supplier) {
        T type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("playerjournal", name), supplier.get());
        return () -> type;
    }

    public static void register() {
        // Triggers static initialization
    }
}