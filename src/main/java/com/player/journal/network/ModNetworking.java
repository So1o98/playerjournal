package com.player.journal.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = "playerjournal", bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("playerjournal");

        registrar.playToClient(
                SyncJournalDataPayload.TYPE,
                SyncJournalDataPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleData(payload, context)
        );

        registrar.playToClient(
                SyncJournalConfigPayload.TYPE,
                SyncJournalConfigPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleConfigData(payload, context)
        );

        registrar.playToClient(
                InspectJournalPayload.TYPE,
                InspectJournalPayload.STREAM_CODEC,
                (payload, context) -> ClientPayloadHandler.handleInspectJournal(payload, context)
        );


        registrar.playToClient(
                SyncPartyPayload.TYPE,
                SyncPartyPayload.CODEC,
                (payload, context) -> ClientPayloadHandler.handlePartySync(payload, context)
        );

        registrar.playToServer(
                UnlockPagePayload.TYPE,
                UnlockPagePayload.STREAM_CODEC,
                (payload, context) -> ServerPayloadHandler.handleUnlockPage(payload, context)
        );

        registrar.playToServer(
                com.player.journal.network.RipPagesPayload.TYPE,
                com.player.journal.network.RipPagesPayload.CODEC,
                com.player.journal.network.ServerPayloadHandler::handleRipPages
        );

        registrar.playToServer(
                PartyInviteResponsePayload.TYPE,
                PartyInviteResponsePayload.STREAM_CODEC,
                PartyInviteResponsePayload::handlePartyInviteResponse
        );
        registrar.playToClient(
                ReceivePartyInvitePayload.TYPE,
                ReceivePartyInvitePayload.STREAM_CODEC,
                ClientPayloadHandler::handleReceiveInvite
        );
    }

}