package com.player.journal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ReceivePartyInvitePayload(String inviterName) implements CustomPacketPayload {

    public static final Type<ReceivePartyInvitePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "receive_party_invite"));

    public static final StreamCodec<FriendlyByteBuf, ReceivePartyInvitePayload> STREAM_CODEC = StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, ReceivePartyInvitePayload::inviterName,
            ReceivePartyInvitePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}