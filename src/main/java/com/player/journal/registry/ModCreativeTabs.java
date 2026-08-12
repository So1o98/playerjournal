package com.player.journal.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "playerjournal");

    public static final Supplier<CreativeModeTab> JOURNAL_TAB = CREATIVE_MODE_TABS.register("journal_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Player Journal"))
                    .icon(() -> new ItemStack(ModItems.MINING_PAGE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.MINING_PAGE.get());
                        output.accept(ModItems.FARMING_PAGE.get());
                        output.accept(ModItems.SMITHING_PAGE.get());
                        output.accept(ModItems.ARCHERY_PAGE.get());
                    }).build());
}