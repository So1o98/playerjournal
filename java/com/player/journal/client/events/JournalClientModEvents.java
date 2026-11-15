package com.player.journal.client.events;

import com.player.journal.client.JournalHotbarOverlay;
import com.player.journal.client.JournalKeybindings;
import com.player.journal.client.KnowledgeTableScreen;
import com.player.journal.client.PartyOverlay;
import com.player.journal.client.ResearchBenchScreen;
import com.player.journal.registry.ModMenuTypes;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screens.MenuScreens;

public class JournalClientModEvents {

    public static void register() {
        KeyBindingHelper.registerKeyBinding(JournalKeybindings.OPEN_JOURNAL_KEY);

        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            PartyOverlay.HUD_PARTY.render(guiGraphics, deltaTracker);
            JournalHotbarOverlay.HUD_JOURNAL_ICON.render(guiGraphics, deltaTracker);
        });

        MenuScreens.register(ModMenuTypes.KNOWLEDGE_TABLE_MENU.get(), KnowledgeTableScreen::new);
        MenuScreens.register(ModMenuTypes.RESEARCH_BENCH.get(), ResearchBenchScreen::new);
    }
}