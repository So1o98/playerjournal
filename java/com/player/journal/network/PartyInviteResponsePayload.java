package com.player.journal.network;

import com.player.journal.party.PartyManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record PartyInviteResponsePayload(boolean accepted, String inviterName) implements CustomPacketPayload {

    public static final Type<PartyInviteResponsePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "party_invite_response"));

    public static final StreamCodec<FriendlyByteBuf, PartyInviteResponsePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PartyInviteResponsePayload::accepted,
            ByteBufCodecs.STRING_UTF8, PartyInviteResponsePayload::inviterName,
            PartyInviteResponsePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handlePartyInviteResponse(PartyInviteResponsePayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        context.server().execute(() -> {
            if (payload.accepted()) {
                PartyManager.acceptInvite(player);
            } else {
                PartyManager.declineInvite(player);
            }
        });
    }
}