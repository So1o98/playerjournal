package com.player.journal;

import com.player.journal.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod("playerjournal")
public class PlayerJournal {

    public PlayerJournal(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        com.player.journal.registry.ModBlocks.BLOCKS.register(modEventBus);
        com.player.journal.registry.ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        com.player.journal.registry.ModMenuTypes.MENUS.register(modEventBus);

        // --- NEW: Capture the config instance so Cloth Config can force it to save to disk! ---
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, com.player.journal.config.JournalConfig.SPEC);

        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            com.player.journal.client.ClientSetup.setupConfigScreen(modContainer);
        }
    }
}