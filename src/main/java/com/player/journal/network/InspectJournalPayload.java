package com.player.journal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record InspectJournalPayload(
        String targetName,
        int vitality,
        int agility,
        int combat,
        int defense,
        int farming,
        int mining,
        int smithing,
        int archery,
        int fishing,
        int alchemy
) implements CustomPacketPayload {

    public static final Type<InspectJournalPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "inspect_journal"));

    public static final StreamCodec<FriendlyByteBuf, InspectJournalPayload> STREAM_CODEC = StreamCodec.ofMember(
            InspectJournalPayload::write, InspectJournalPayload::new
    );

    public InspectJournalPayload(FriendlyByteBuf buf) {
        this(
                buf.readUtf(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(targetName);
        buf.writeInt(vitality);
        buf.writeInt(agility);
        buf.writeInt(combat);
        buf.writeInt(defense);
        buf.writeInt(farming);
        buf.writeInt(mining);
        buf.writeInt(smithing);
        buf.writeInt(archery);
        buf.writeInt(fishing);
        buf.writeInt(alchemy);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}