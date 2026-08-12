package com.player.journal.registry;

import com.player.journal.inventory.KnowledgeTableMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, "playerjournal");

    public static final DeferredHolder<MenuType<?>, MenuType<KnowledgeTableMenu>> KNOWLEDGE_TABLE_MENU = MENUS.register("knowledge_table_menu",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new KnowledgeTableMenu(windowId, inv)));
}