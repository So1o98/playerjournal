package com.player.journal.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ModCreativeTabs {

    private static final CreativeModeTab JOURNAL_TAB_INSTANCE = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath("playerjournal", "journal_tab"),
            FabricItemGroup.builder()
                    .title(Component.literal("Player Journal"))
                    .icon(() -> new ItemStack(ModItems.MINING_PAGE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.MINING_PAGE.get());
                        output.accept(ModItems.FARMING_PAGE.get());
                        output.accept(ModItems.SMITHING_PAGE.get());
                        output.accept(ModItems.ARCHERY_PAGE.get());
                    }).build()
    );

    public static final Supplier<CreativeModeTab> JOURNAL_TAB = () -> JOURNAL_TAB_INSTANCE;

    public static void register() {
        // Triggers static initialization
    }
}