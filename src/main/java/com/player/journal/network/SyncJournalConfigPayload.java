package com.player.journal.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SyncJournalConfigPayload(
        List<String> armorRestrictions,
        List<String> potionRestrictions,
        List<String> itemRestrictions,
        List<String> jewelryRestrictions,
        List<String> farmersDelightRestrictions,
        List<String> paladinsPriestsArmors,
        List<String> paladinsPriestsWeapons,
        List<String> paladinsPriestsShields,
        List<String> roguesWarriorsArmors,
        List<String> roguesWarriorsWeapons,
        List<String> archersArmors,
        List<String> archersWeapons,
        List<String> wizardsArmors,
        List<String> wizardsWeapons,
        List<String> arsenalWeapons,
        List<String> artifactsItems,
        List<String> tideItems,
        List<String> glidersItems,
        List<String> lilisLuckyLuresItems,
        List<String> immersiveMachineryItems,
        List<String> immersiveAircraftItems,
        List<String> smallShipsItems,
        List<String> alchemyUtilities,
        List<String> enchantmentRestrictions,
        List<String> agilityMounts,
        List<String> craftingRestrictions, // <-- NEW LIST ADDED HERE

        int vitalityBase, double vitalityMult,
        int agilityBase, double agilityMult,
        int combatBase, double combatMult,
        int defenseBase, double defenseMult,
        int farmingBase, double farmingMult,
        int miningBase, double miningMult,
        int smithingBase, double smithingMult,
        int archeryBase, double archeryMult,
        int fishingBase, double fishingMult,
        int alchemyBase, double alchemyMult

) implements CustomPacketPayload {

    public static final Type<SyncJournalConfigPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("playerjournal", "sync_journal_config"));

    public static final StreamCodec<FriendlyByteBuf, SyncJournalConfigPayload> STREAM_CODEC = StreamCodec.ofMember(
            SyncJournalConfigPayload::write,
            SyncJournalConfigPayload::new
    );

    public SyncJournalConfigPayload(FriendlyByteBuf buffer) {
        this(
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf),
                buffer.readList(FriendlyByteBuf::readUtf), // <-- ADDED BUFFER READER

                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble(),
                buffer.readInt(), buffer.readDouble()
        );
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeCollection(this.armorRestrictions(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.potionRestrictions(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.itemRestrictions(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.jewelryRestrictions(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.farmersDelightRestrictions(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.paladinsPriestsArmors(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.paladinsPriestsWeapons(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.paladinsPriestsShields(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.roguesWarriorsArmors(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.roguesWarriorsWeapons(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.archersArmors(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.archersWeapons(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.wizardsArmors(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.wizardsWeapons(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.arsenalWeapons(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.artifactsItems(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.tideItems(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.glidersItems(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.lilisLuckyLuresItems(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.immersiveMachineryItems(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.immersiveAircraftItems(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.smallShipsItems(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.alchemyUtilities(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.enchantmentRestrictions(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.agilityMounts(), FriendlyByteBuf::writeUtf);
        buffer.writeCollection(this.craftingRestrictions(), FriendlyByteBuf::writeUtf); // <-- ADDED BUFFER WRITER

        buffer.writeInt(this.vitalityBase()); buffer.writeDouble(this.vitalityMult());
        buffer.writeInt(this.agilityBase()); buffer.writeDouble(this.agilityMult());
        buffer.writeInt(this.combatBase()); buffer.writeDouble(this.combatMult());
        buffer.writeInt(this.defenseBase()); buffer.writeDouble(this.defenseMult());
        buffer.writeInt(this.farmingBase()); buffer.writeDouble(this.farmingMult());
        buffer.writeInt(this.miningBase()); buffer.writeDouble(this.miningMult());
        buffer.writeInt(this.smithingBase()); buffer.writeDouble(this.smithingMult());
        buffer.writeInt(this.archeryBase()); buffer.writeDouble(this.archeryMult());
        buffer.writeInt(this.fishingBase()); buffer.writeDouble(this.fishingMult());
        buffer.writeInt(this.alchemyBase()); buffer.writeDouble(this.alchemyMult());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}