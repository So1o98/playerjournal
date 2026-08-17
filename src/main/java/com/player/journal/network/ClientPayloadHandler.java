package com.player.journal.network;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import java.util.ArrayList;
import java.util.List;

public class ClientPayloadHandler {

    public static boolean hasFarming = false;
    public static boolean hasMining = false;
    public static boolean hasSmithing = false;
    public static boolean hasArchery = false;
    public static boolean hasFishing = false;
    public static boolean hasAgility = true;
    public static boolean hasAlchemy = false;

    public static int vitalityLevel = 1;
    public static float vitalityXP = 0f;

    public static int agilityLevel = 1;
    public static float agilityXP = 0f;

    public static int combatLevel = 1;
    public static float combatXP = 0f;

    public static int defenseLevel = 1;
    public static float defenseXP = 0f;

    public static int miningLevel = 0;
    public static float miningXP = 0f;

    public static int farmingLevel = 0;
    public static float farmingXP = 0f;

    public static int smithingLevel = 0;
    public static float smithingXP = 0f;

    public static int archeryLevel = 0;
    public static float archeryXP = 0f;

    public static int fishingLevel = 0;
    public static float fishingXP = 0f;

    public static int alchemyLevel = 0;
    public static float alchemyXP = 0f;

    public static int tornPages = 0;

    public static List<String> serverArmorRestrictions = new ArrayList<>();
    public static List<String> serverPotionRestrictions = new ArrayList<>();
    public static List<String> serverItemRestrictions = new ArrayList<>();
    public static List<String> serverJewelryRestrictions = new ArrayList<>();
    public static List<String> serverFarmersDelightRestrictions = new ArrayList<>();
    public static List<String> serverPaladinsPriestsArmors = new ArrayList<>();
    public static List<String> serverPaladinsPriestsWeapons = new ArrayList<>();
    public static List<String> serverPaladinsPriestsShields = new ArrayList<>();
    public static List<String> serverRoguesWarriorsArmors = new ArrayList<>();
    public static List<String> serverRoguesWarriorsWeapons = new ArrayList<>();
    public static List<String> serverArchersArmors = new ArrayList<>();
    public static List<String> serverArchersWeapons = new ArrayList<>();
    public static List<String> serverWizardsArmors = new ArrayList<>();
    public static List<String> serverWizardsWeapons = new ArrayList<>();
    public static List<String> serverArsenalWeapons = new ArrayList<>();
    public static List<String> serverArtifactsItems = new ArrayList<>();
    public static List<String> serverTideItems = new ArrayList<>();
    public static List<String> serverGlidersItems = new ArrayList<>();
    public static List<String> serverLilisLuckyLuresItems = new ArrayList<>();
    public static List<String> serverImmersiveMachineryItems = new ArrayList<>();
    public static List<String> serverImmersiveAircraftItems = new ArrayList<>();
    public static List<String> serverSmallShipsItems = new ArrayList<>();
    public static List<String> serverAlchemyUtilities = new ArrayList<>();
    public static List<String> serverEnchantmentRestrictions = new ArrayList<>();
    public static List<String> serverAgilityMounts = new ArrayList<>();
    public static List<PartyMember> pendingInvites = new ArrayList<>();
    public static java.util.List<String> serverCraftingRestrictions = new java.util.ArrayList<>();

    public static int serverVitalityBase = -1; public static double serverVitalityMult = -1;
    public static int serverAgilityBase = -1; public static double serverAgilityMult = -1;
    public static int serverCombatBase = -1; public static double serverCombatMult = -1;
    public static int serverDefenseBase = -1; public static double serverDefenseMult = -1;
    public static int serverFarmingBase = -1; public static double serverFarmingMult = -1;
    public static int serverMiningBase = -1; public static double serverMiningMult = -1;
    public static int serverSmithingBase = -1; public static double serverSmithingMult = -1;
    public static int serverArcheryBase = -1; public static double serverArcheryMult = -1;
    public static int serverFishingBase = -1; public static double serverFishingMult = -1;
    public static int serverAlchemyBase = -1; public static double serverAlchemyMult = -1;

    public static void handleData(SyncJournalDataPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            vitalityLevel = payload.vitalityLevel();
            vitalityXP = payload.vitalityXP();

            combatLevel = payload.combatLevel();
            combatXP = payload.combatXP();

            defenseLevel = payload.defenseLevel();
            defenseXP = payload.defenseXP();

            miningLevel = payload.miningLevel();
            miningXP = payload.miningXP();
            hasMining = miningLevel >= 1;

            farmingLevel = payload.farmingLevel();
            farmingXP = payload.farmingXP();
            hasFarming = farmingLevel >= 1;

            smithingLevel = payload.smithingLevel();
            smithingXP = payload.smithingXP();
            hasSmithing = smithingLevel >= 1;

            archeryLevel = payload.archeryLevel();
            archeryXP = payload.archeryXP();
            hasArchery = archeryLevel >= 1;

            fishingLevel = payload.fishingLevel();
            fishingXP = payload.fishingXP();
            hasFishing = fishingLevel >= 1;

            agilityLevel = payload.agilityLevel();
            agilityXP = payload.agilityXP();
            hasAgility = agilityLevel >= 1;

            alchemyLevel = payload.alchemyLevel();
            alchemyXP = payload.alchemyXP();
            hasAlchemy = alchemyLevel >= 1;

            tornPages = payload.tornPages();
        });
    }

    public static void handleConfigData(SyncJournalConfigPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            serverArmorRestrictions = payload.armorRestrictions();
            serverPotionRestrictions = payload.potionRestrictions();
            serverItemRestrictions = payload.itemRestrictions();
            serverJewelryRestrictions = payload.jewelryRestrictions();
            serverFarmersDelightRestrictions = payload.farmersDelightRestrictions();
            serverPaladinsPriestsArmors = payload.paladinsPriestsArmors();
            serverPaladinsPriestsWeapons = payload.paladinsPriestsWeapons();
            serverPaladinsPriestsShields = payload.paladinsPriestsShields();
            serverRoguesWarriorsArmors = payload.roguesWarriorsArmors();
            serverRoguesWarriorsWeapons = payload.roguesWarriorsWeapons();
            serverArchersArmors = payload.archersArmors();
            serverArchersWeapons = payload.archersWeapons();
            serverWizardsArmors = payload.wizardsArmors();
            serverWizardsWeapons = payload.wizardsWeapons();
            serverArsenalWeapons = payload.arsenalWeapons();
            serverArtifactsItems = payload.artifactsItems();
            serverTideItems = payload.tideItems();
            serverGlidersItems = payload.glidersItems();
            serverLilisLuckyLuresItems = payload.lilisLuckyLuresItems();
            serverImmersiveMachineryItems = payload.immersiveMachineryItems();
            serverImmersiveAircraftItems = payload.immersiveAircraftItems();
            serverSmallShipsItems = payload.smallShipsItems();
            serverAlchemyUtilities = payload.alchemyUtilities();
            serverEnchantmentRestrictions = payload.enchantmentRestrictions();
            serverAgilityMounts = payload.agilityMounts();
            serverCraftingRestrictions = payload.craftingRestrictions();

            serverVitalityBase = payload.vitalityBase(); serverVitalityMult = payload.vitalityMult();
            serverAgilityBase = payload.agilityBase(); serverAgilityMult = payload.agilityMult();
            serverCombatBase = payload.combatBase(); serverCombatMult = payload.combatMult();
            serverDefenseBase = payload.defenseBase(); serverDefenseMult = payload.defenseMult();
            serverFarmingBase = payload.farmingBase(); serverFarmingMult = payload.farmingMult();
            serverMiningBase = payload.miningBase(); serverMiningMult = payload.miningMult();
            serverSmithingBase = payload.smithingBase(); serverSmithingMult = payload.smithingMult();
            serverArcheryBase = payload.archeryBase(); serverArcheryMult = payload.archeryMult();
            serverFishingBase = payload.fishingBase(); serverFishingMult = payload.fishingMult();
            serverAlchemyBase = payload.alchemyBase(); serverAlchemyMult = payload.alchemyMult();
        });
    }

    public static void handleInspectJournal(com.player.journal.network.InspectJournalPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
                com.player.journal.client.ClientSetup.openInspectScreen(payload);
            }
        });
    }

    // ==========================================
    //               PARTY SYSTEM
    // ==========================================

    public static List<PartyMember> partyMembers = new ArrayList<>();

    public static class PartyMember {
        public final String name;
        public final ResourceLocation skin;
        public final boolean inRange;

        public PartyMember(String name, ResourceLocation skin, boolean inRange) {
            this.name = name;
            this.skin = skin;
            this.inRange = inRange;
        }
    }

    public static void handlePartySync(final SyncPartyPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            partyMembers.clear();
            Minecraft mc = Minecraft.getInstance();

            for (SyncPartyPayload.PartyMemberData data : payload.roster()) {
                ResourceLocation skinLocation;


                if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(data.uuid()) != null) {
                    skinLocation = mc.getConnection().getPlayerInfo(data.uuid()).getSkin().texture();
                } else {

                    skinLocation = mc.getSkinManager().getInsecureSkin(new com.mojang.authlib.GameProfile(data.uuid(), data.name())).texture();
                }

                partyMembers.add(new PartyMember(data.name(), skinLocation, data.inRange()));
            }
        });
    }
    public static void handleReceiveInvite(com.player.journal.network.ReceivePartyInvitePayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            // Attempt to grab the player's skin from the connection, or fallback to default
            net.minecraft.client.multiplayer.PlayerInfo info = net.minecraft.client.Minecraft.getInstance().getConnection().getPlayerInfo(payload.inviterName());
            net.minecraft.resources.ResourceLocation skin = info != null ? info.getSkin().texture() : net.minecraft.client.resources.DefaultPlayerSkin.get(java.util.UUID.randomUUID()).texture();


            pendingInvites.add(new PartyMember(payload.inviterName(), skin, true));
        });
    }
}