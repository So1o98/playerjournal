package com.player.journal.client;

import com.player.journal.client.events.ClientEvents;
import com.player.journal.client.events.JournalClientEvents;
import com.player.journal.client.events.JournalClientModEvents;
import com.player.journal.network.ClientPayloadHandler;
import com.player.journal.network.InspectJournalPayload;
import com.player.journal.network.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;

public class ClientSetup {

    public static void init() {

        ModNetworking.registerClient();


        JournalClientModEvents.register();


        ClientEvents.register();


        JournalClientEvents.register();


        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> onClientDisconnect());
    }

    public static void openInspectScreen(InspectJournalPayload payload) {
        Minecraft.getInstance().setScreen(new InspectJournalScreen(payload));
    }

    public static void onClientDisconnect() {
        ClientPayloadHandler.serverArmorRestrictions.clear();
        ClientPayloadHandler.serverPotionRestrictions.clear();
        ClientPayloadHandler.serverItemRestrictions.clear();
        ClientPayloadHandler.serverJewelryRestrictions.clear();
        ClientPayloadHandler.serverPaladinsPriestsArmors.clear();
        ClientPayloadHandler.serverPaladinsPriestsWeapons.clear();
        ClientPayloadHandler.serverPaladinsPriestsShields.clear();
        ClientPayloadHandler.serverRoguesWarriorsArmors.clear();
        ClientPayloadHandler.serverRoguesWarriorsWeapons.clear();
        ClientPayloadHandler.serverArchersArmors.clear();
        ClientPayloadHandler.serverArchersWeapons.clear();
        ClientPayloadHandler.serverWizardsArmors.clear();
        ClientPayloadHandler.serverWizardsWeapons.clear();
        ClientPayloadHandler.serverArsenalWeapons.clear();
        ClientPayloadHandler.serverArtifactsItems.clear();
        ClientPayloadHandler.serverTideItems.clear();
        ClientPayloadHandler.serverGlidersItems.clear();
        ClientPayloadHandler.serverLilisLuckyLuresItems.clear();
        ClientPayloadHandler.serverImmersiveMachineryItems.clear();
        ClientPayloadHandler.serverImmersiveAircraftItems.clear();
        ClientPayloadHandler.serverSmallShipsItems.clear();
        ClientPayloadHandler.serverAlchemyUtilities.clear();
        ClientPayloadHandler.serverEnchantmentRestrictions.clear();
        ClientPayloadHandler.serverAgilityMounts.clear();
        ClientPayloadHandler.serverCraftingRestrictions.clear();

        JournalScreen.listsBuilt = false;
    }
}