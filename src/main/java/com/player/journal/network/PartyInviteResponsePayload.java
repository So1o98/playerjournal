package com.player.journal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PartyInviteResponsePayload(boolean accepted, String inviterName) implements CustomPacketPayload {

    public static final Type<PartyInviteResponsePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "party_invite_response"));

    public static final StreamCodec<FriendlyByteBuf, PartyInviteResponsePayload> STREAM_CODEC = StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.BOOL, PartyInviteResponsePayload::accepted,
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, PartyInviteResponsePayload::inviterName,
            PartyInviteResponsePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handlePartyInviteResponse(PartyInviteResponsePayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {

                if (payload.accepted()) {
                    com.player.journal.party.PartyManager.acceptInvite(player);
                } else {
                    com.player.journal.party.PartyManager.declineInvite(player);
                }

            }
        });
    }
}