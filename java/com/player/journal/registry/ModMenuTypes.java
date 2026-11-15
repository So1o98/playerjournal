package com.player.journal.registry;

import com.player.journal.inventory.KnowledgeTableMenu;
import com.player.journal.inventory.ResearchBenchMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ModMenuTypes {

    private static final MenuType<KnowledgeTableMenu> KNOWLEDGE_TABLE_MENU_INSTANCE = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath("playerjournal", "knowledge_table_menu"),
            new MenuType<>(KnowledgeTableMenu::new, FeatureFlags.DEFAULT_FLAGS)
    );

    private static final MenuType<ResearchBenchMenu> RESEARCH_BENCH_INSTANCE = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath("playerjournal", "research_bench"),
            new ExtendedScreenHandlerType<>((syncId, inventory, pos) -> new ResearchBenchMenu(syncId, inventory, pos), BlockPos.STREAM_CODEC)
    );

    public static final Supplier<MenuType<KnowledgeTableMenu>> KNOWLEDGE_TABLE_MENU = () -> KNOWLEDGE_TABLE_MENU_INSTANCE;
    public static final Supplier<MenuType<ResearchBenchMenu>> RESEARCH_BENCH = () -> RESEARCH_BENCH_INSTANCE;

    public static void register() {
        // Triggers static initialization
    }
}