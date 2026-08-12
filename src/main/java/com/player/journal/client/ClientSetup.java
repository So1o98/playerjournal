package com.player.journal.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.common.EventBusSubscriber;


@EventBusSubscriber(modid = "playerjournal", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class ClientSetup {

    public static void setupConfigScreen(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (container, parentScreen) -> com.player.journal.client.JournalConfigScreen.createScreen(parentScreen));
    }

    public static void openInspectScreen(com.player.journal.network.InspectJournalPayload payload) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.player.journal.client.InspectJournalScreen(payload));
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void onClientDisconnect(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {

        com.player.journal.network.ClientPayloadHandler.serverArmorRestrictions.clear();
        com.player.journal.network.ClientPayloadHandler.serverPotionRestrictions.clear();
        com.player.journal.network.ClientPayloadHandler.serverItemRestrictions.clear();
        com.player.journal.network.ClientPayloadHandler.serverJewelryRestrictions.clear();
        com.player.journal.network.ClientPayloadHandler.serverFarmersDelightRestrictions.clear();
        com.player.journal.network.ClientPayloadHandler.serverPaladinsPriestsArmors.clear();
        com.player.journal.network.ClientPayloadHandler.serverPaladinsPriestsWeapons.clear();
        com.player.journal.network.ClientPayloadHandler.serverPaladinsPriestsShields.clear();
        com.player.journal.network.ClientPayloadHandler.serverRoguesWarriorsArmors.clear();
        com.player.journal.network.ClientPayloadHandler.serverRoguesWarriorsWeapons.clear();
        com.player.journal.network.ClientPayloadHandler.serverArchersArmors.clear();
        com.player.journal.network.ClientPayloadHandler.serverArchersWeapons.clear();
        com.player.journal.network.ClientPayloadHandler.serverWizardsArmors.clear();
        com.player.journal.network.ClientPayloadHandler.serverWizardsWeapons.clear();
        com.player.journal.network.ClientPayloadHandler.serverArsenalWeapons.clear();
        com.player.journal.network.ClientPayloadHandler.serverArtifactsItems.clear();
        com.player.journal.network.ClientPayloadHandler.serverTideItems.clear();
        com.player.journal.network.ClientPayloadHandler.serverGlidersItems.clear();
        com.player.journal.network.ClientPayloadHandler.serverLilisLuckyLuresItems.clear();
        com.player.journal.network.ClientPayloadHandler.serverImmersiveMachineryItems.clear();
        com.player.journal.network.ClientPayloadHandler.serverImmersiveAircraftItems.clear();
        com.player.journal.network.ClientPayloadHandler.serverSmallShipsItems.clear();
        com.player.journal.network.ClientPayloadHandler.serverAlchemyUtilities.clear();
        com.player.journal.network.ClientPayloadHandler.serverEnchantmentRestrictions.clear();
        com.player.journal.network.ClientPayloadHandler.serverAgilityMounts.clear();


        com.player.journal.client.JournalScreen.listsBuilt = false;
    }
}