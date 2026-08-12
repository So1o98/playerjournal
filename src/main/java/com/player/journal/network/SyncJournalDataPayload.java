package com.player.journal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncJournalDataPayload(
        int vitalityLevel, float vitalityXP,
        int combatLevel, float combatXP,
        int defenseLevel, float defenseXP,
        int miningLevel, float miningXP,
        int farmingLevel, float farmingXP,
        int smithingLevel, float smithingXP,
        int archeryLevel, float archeryXP,
        int fishingLevel, float fishingXP,
        int agilityLevel, float agilityXP,
        int alchemyLevel, float alchemyXP,
        int tornPages
) implements CustomPacketPayload {

    public static final Type<SyncJournalDataPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "sync_journal_data"));

    public static final StreamCodec<FriendlyByteBuf, SyncJournalDataPayload> STREAM_CODEC = StreamCodec.ofMember(
            SyncJournalDataPayload::write,
            SyncJournalDataPayload::new
    );

    public SyncJournalDataPayload(FriendlyByteBuf buffer) {
        this(
                buffer.readInt(), buffer.readFloat(), // vitality
                buffer.readInt(), buffer.readFloat(), // combat
                buffer.readInt(), buffer.readFloat(), // defense
                buffer.readInt(), buffer.readFloat(), // mining
                buffer.readInt(), buffer.readFloat(), // farming
                buffer.readInt(), buffer.readFloat(), // smithing
                buffer.readInt(), buffer.readFloat(), // archery
                buffer.readInt(), buffer.readFloat(), // fishing
                buffer.readInt(), buffer.readFloat(), // agility
                buffer.readInt(), buffer.readFloat(), // alchemy
                buffer.readInt()                      // tornPages
        );
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(vitalityLevel);
        buffer.writeFloat(vitalityXP);

        buffer.writeInt(combatLevel);
        buffer.writeFloat(combatXP);

        buffer.writeInt(defenseLevel);
        buffer.writeFloat(defenseXP);

        buffer.writeInt(miningLevel);
        buffer.writeFloat(miningXP);

        buffer.writeInt(farmingLevel);
        buffer.writeFloat(farmingXP);

        buffer.writeInt(smithingLevel);
        buffer.writeFloat(smithingXP);

        buffer.writeInt(archeryLevel);
        buffer.writeFloat(archeryXP);

        buffer.writeInt(fishingLevel);
        buffer.writeFloat(fishingXP);

        buffer.writeInt(agilityLevel);
        buffer.writeFloat(agilityXP);

        buffer.writeInt(alchemyLevel);
        buffer.writeFloat(alchemyXP);

        buffer.writeInt(tornPages);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}