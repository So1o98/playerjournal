package com.player.journal.client;

import net.fabricmc.api.ClientModInitializer;

public class PlayerJournalClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientSetup.init();
    }
}