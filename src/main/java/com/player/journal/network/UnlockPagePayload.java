package com.player.journal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UnlockPagePayload(int pageIndex) implements CustomPacketPayload {

    public static final Type<UnlockPagePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "unlock_page"));

    public static final StreamCodec<FriendlyByteBuf, UnlockPagePayload> STREAM_CODEC = StreamCodec.ofMember(
            UnlockPagePayload::write,
            UnlockPagePayload::new
    );

    public UnlockPagePayload(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(pageIndex);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}