package com.player.journal.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModNetworking {

    public static void registerCommon() {

        PayloadTypeRegistry.playS2C().register(SyncJournalDataPayload.TYPE, SyncJournalDataPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncJournalConfigPayload.TYPE, SyncJournalConfigPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(InspectJournalPayload.TYPE, InspectJournalPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPartyPayload.TYPE, SyncPartyPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ReceivePartyInvitePayload.TYPE, ReceivePartyInvitePayload.STREAM_CODEC);


        PayloadTypeRegistry.playC2S().register(UnlockPagePayload.TYPE, UnlockPagePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RipPagesPayload.TYPE, RipPagesPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PartyInviteResponsePayload.TYPE, PartyInviteResponsePayload.STREAM_CODEC);


        ServerPlayNetworking.registerGlobalReceiver(UnlockPagePayload.TYPE, ServerPayloadHandler::handleUnlockPage);
        ServerPlayNetworking.registerGlobalReceiver(RipPagesPayload.TYPE, ServerPayloadHandler::handleRipPages);
        ServerPlayNetworking.registerGlobalReceiver(PartyInviteResponsePayload.TYPE, PartyInviteResponsePayload::handlePartyInviteResponse);
    }

    public static void registerClient() {

        ClientPlayNetworking.registerGlobalReceiver(SyncJournalDataPayload.TYPE, ClientPayloadHandler::handleData);
        ClientPlayNetworking.registerGlobalReceiver(SyncJournalConfigPayload.TYPE, ClientPayloadHandler::handleConfigData);
        ClientPlayNetworking.registerGlobalReceiver(InspectJournalPayload.TYPE, ClientPayloadHandler::handleInspectJournal);
        ClientPlayNetworking.registerGlobalReceiver(SyncPartyPayload.TYPE, ClientPayloadHandler::handlePartySync);
        ClientPlayNetworking.registerGlobalReceiver(ReceivePartyInvitePayload.TYPE, ClientPayloadHandler::handleReceiveInvite);
    }
}