package com.player.journal;

import com.player.journal.command.JournalCommands;
import com.player.journal.config.JournalConfig;
import com.player.journal.events.AccessoriesIntegration;
import com.player.journal.events.JournalServerEvents;
import com.player.journal.network.ModNetworking;
import com.player.journal.registry.*;
import net.fabricmc.api.ModInitializer;

public class PlayerJournal implements ModInitializer {
    public static final String MOD_ID = "playerjournal";

    @Override
    public void onInitialize() {

        JournalConfig.load();


        ModAttachments.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModCreativeTabs.register();
        ModEffects.register();
        ModMenuTypes.register();


        ModNetworking.registerCommon();
        JournalCommands.register();
        JournalServerEvents.register();
        AccessoriesIntegration.register();
    }
}