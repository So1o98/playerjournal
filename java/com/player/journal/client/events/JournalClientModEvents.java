package com.player.journal.client.events;

import com.player.journal.client.JournalHotbarOverlay;
import com.player.journal.client.JournalKeybindings;
import com.player.journal.client.PartyOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "playerjournal", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JournalClientModEvents {

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(JournalKeybindings.OPEN_JOURNAL_KEY);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("playerjournal", "party_hud"), PartyOverlay.HUD_PARTY);
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("playerjournal", "journal_hotbar_icon"), JournalHotbarOverlay.HUD_JOURNAL_ICON);
    }

    // --- NEW: Register the Research Bench Screen ---
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(com.player.journal.registry.ModMenuTypes.RESEARCH_BENCH.get(), com.player.journal.client.ResearchBenchScreen::new);
    }
}