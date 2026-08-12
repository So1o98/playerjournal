package com.player.journal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RipPagesPayload() implements CustomPacketPayload {
    public static final Type<RipPagesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "rip_pages"));

    public static final StreamCodec<FriendlyByteBuf, RipPagesPayload> CODEC = StreamCodec.unit(new RipPagesPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}