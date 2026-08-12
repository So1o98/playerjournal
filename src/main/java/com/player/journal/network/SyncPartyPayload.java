package com.player.journal.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public record SyncPartyPayload(List<PartyMemberData> roster) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncPartyPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "sync_party"));


    public static final StreamCodec<RegistryFriendlyByteBuf, PartyMemberData> MEMBER_CODEC = StreamCodec.composite(
            net.minecraft.core.UUIDUtil.STREAM_CODEC, PartyMemberData::uuid,
            ByteBufCodecs.STRING_UTF8, PartyMemberData::name,
            ByteBufCodecs.BOOL, PartyMemberData::inRange,
            PartyMemberData::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPartyPayload> CODEC = StreamCodec.composite(
            MEMBER_CODEC.apply(ByteBufCodecs.list()), SyncPartyPayload::roster,
            SyncPartyPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public record PartyMemberData(UUID uuid, String name, boolean inRange) {}
}