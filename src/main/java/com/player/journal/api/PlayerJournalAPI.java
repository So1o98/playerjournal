package com.player.journal.api;

import com.player.journal.data.JournalProgressionData;
import com.player.journal.data.RestrictionDataLoader;
import com.player.journal.events.JournalServerEvents;
import com.player.journal.registry.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import com.player.journal.network.SyncJournalDataPayload;

import java.util.Map;

@SuppressWarnings("unused")
public class PlayerJournalAPI {

    // --- CUSTOM CODE-BASED RESTRICTION MAPS ---
    private static final java.util.Map<String, java.util.Map<String, Integer>> CUSTOM_USAGE_RESTRICTIONS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<String, java.util.Map<String, Integer>> CUSTOM_INTERACT_RESTRICTIONS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<String, java.util.Map<String, Integer>> CUSTOM_CRAFTING_RESTRICTIONS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<String, Float> CUSTOM_CRAFTING_XP = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<String, java.util.Map<String, Integer>> CUSTOM_BLOCK_RESTRICTIONS = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<String, Float> CUSTOM_BLOCK_XP = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Allows other mods to restrict an item's usage via Java code.
     */
    public static void registerUsageRestriction(String itemId, String skill, int requiredLevel) {
        CUSTOM_USAGE_RESTRICTIONS.computeIfAbsent(itemId, k -> new java.util.HashMap<>())
                .put(skill.toLowerCase(), requiredLevel);
    }

    /**
     * Allows other mods to restrict interacting with an item (e.g., right-clicking) via Java code.
     */
    public static void registerInteractRestriction(String itemId, String skill, int requiredLevel) {
        CUSTOM_INTERACT_RESTRICTIONS.computeIfAbsent(itemId, k -> new java.util.HashMap<>())
                .put(skill.toLowerCase(), requiredLevel);
    }

    /**
     * Allows other mods to restrict an item's crafting via Java code, and set an XP reward.
     */
    public static void registerCraftingRestriction(String itemId, String skill, int requiredLevel, float xpReward) {
        CUSTOM_CRAFTING_RESTRICTIONS.computeIfAbsent(itemId, k -> new java.util.HashMap<>())
                .put(skill.toLowerCase(), requiredLevel);
        if (xpReward > 0) {
            CUSTOM_CRAFTING_XP.put(itemId, xpReward);
        }
    }

    /**
     * Allows other mods to restrict block breaking/harvesting via Java code, and set an XP reward.
     */
    public static void registerBlockRestriction(String blockId, String skill, int requiredLevel, float xpReward) {
        CUSTOM_BLOCK_RESTRICTIONS.computeIfAbsent(blockId, k -> new java.util.HashMap<>())
                .put(skill.toLowerCase(), requiredLevel);
        if (xpReward > 0) {
            CUSTOM_BLOCK_XP.put(blockId, xpReward);
        }
    }

    // Helper methods for the server to read this custom data
    public static java.util.Map<String, Integer> getCustomUsage(String itemId) {
        return CUSTOM_USAGE_RESTRICTIONS.getOrDefault(itemId, java.util.Collections.emptyMap());
    }

    public static java.util.Map<String, Integer> getCustomInteract(String itemId) {
        return CUSTOM_INTERACT_RESTRICTIONS.getOrDefault(itemId, java.util.Collections.emptyMap());
    }

    public static java.util.Map<String, Integer> getCustomCrafting(String itemId) {
        return CUSTOM_CRAFTING_RESTRICTIONS.getOrDefault(itemId, java.util.Collections.emptyMap());
    }

    public static float getCustomCraftingXp(String itemId) {
        return CUSTOM_CRAFTING_XP.getOrDefault(itemId, 0f);
    }

    public static java.util.Map<String, Integer> getCustomBlockUsage(String blockId) {
        return CUSTOM_BLOCK_RESTRICTIONS.getOrDefault(blockId, java.util.Collections.emptyMap());
    }

    public static float getCustomBlockXp(String blockId) {
        return CUSTOM_BLOCK_XP.getOrDefault(blockId, 0f);
    }

    // --- NEW EXPORTER METHODS FOR THE GUI ---

    /**
     * Returns all custom code-registered item usage restrictions.
     */
    public static java.util.Map<String, java.util.Map<String, Integer>> getAllCustomUsageRestrictions() {
        return java.util.Collections.unmodifiableMap(CUSTOM_USAGE_RESTRICTIONS);
    }

    /**
     * Returns all custom code-registered item interact restrictions.
     */
    public static java.util.Map<String, java.util.Map<String, Integer>> getAllCustomInteractRestrictions() {
        return java.util.Collections.unmodifiableMap(CUSTOM_INTERACT_RESTRICTIONS);
    }

    /**
     * Returns all custom code-registered block restrictions.
     */
    public static java.util.Map<String, java.util.Map<String, Integer>> getAllCustomBlockRestrictions() {
        return java.util.Collections.unmodifiableMap(CUSTOM_BLOCK_RESTRICTIONS);
    }

    // --- EXISTING API METHODS ---

    /**
     * Safely retrieves a player's level for a specific skill.
     * Returns 0 if the skill doesn't exist or the player has no data.
     */
    public static int getPlayerLevel(Player player, String skillName) {
        if (player.level().isClientSide()) return 0;

        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
        if (data == null) return 0;

        return switch (skillName.toLowerCase()) {
            case "vitality" -> data.getVitalityLevel();
            case "agility" -> data.getAgilityLevel();
            case "combat" -> data.getCombatLevel();
            case "defense" -> data.getDefenseLevel();
            case "mining" -> data.getMiningLevel();
            case "farming" -> data.getFarmingLevel();
            case "smithing" -> data.getSmithingLevel();
            case "archery" -> data.getArcheryLevel();
            case "fishing" -> data.getFishingLevel();
            case "alchemy" -> data.getAlchemyLevel();
            default -> 0;
        };
    }

    public static boolean hasUnlockedPage(Player player, String skillName) {
        String skill = skillName.toLowerCase();

        if (skill.equals("vitality") || skill.equals("agility") ||
                skill.equals("combat") || skill.equals("defense")) {
            return true;
        }

        return getPlayerLevel(player, skillName) >= 1;
    }

    public static int getTornPagesBalance(Player player) {
        if (player.level().isClientSide()) return 0;

        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
        return data != null ? data.getTornPages() : 0;
    }

    public static Map<String, Integer> getItemUsageRestrictions(String itemId) {
        return RestrictionDataLoader.getItemRestrictions(itemId);
    }

    public static boolean canPlayerUseItem(ServerPlayer player, String itemId) {
        if (player.level().isClientSide()) return true;
        if (player.isCreative() || player.isSpectator()) return true;

        String failMessage = JournalServerEvents.getFailedRequirementFromMap(player, itemId);

        return failMessage == null;
    }

    public static void grantCustomXp(ServerPlayer player, String skillName, float amount) {
        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
        if (data == null) return;

        if (!hasUnlockedPage(player, skillName)) return;

        float finalXp = amount * JournalServerEvents.getXpMultiplier(player);
        boolean leveledUp = false;

        switch (skillName.toLowerCase()) {
            case "vitality" -> leveledUp = data.addVitalityXP(finalXp, player);
            case "agility" -> leveledUp = data.addAgilityXP(finalXp, player);
            case "combat" -> leveledUp = data.addCombatXP(finalXp, player);
            case "defense" -> leveledUp = data.addDefenseXP(finalXp, player);
            case "mining" -> leveledUp = data.addMiningXP(finalXp, player);
            case "farming" -> leveledUp = data.addFarmingXP(finalXp, player);
            case "smithing" -> leveledUp = data.addSmithingXP(finalXp, player);
            case "archery" -> leveledUp = data.addArcheryXP(finalXp, player);
            case "fishing" -> leveledUp = data.addFishingXP(finalXp, player);
            case "alchemy" -> leveledUp = data.addAlchemyXP(finalXp, player);
        }

        if (finalXp > 0) {
            SyncJournalDataPayload payload = new SyncJournalDataPayload(
                    data.getVitalityLevel(), data.getVitalityXP(),
                    data.getCombatLevel(), data.getCombatXP(),
                    data.getDefenseLevel(), data.getDefenseXP(),
                    data.getMiningLevel(), data.getMiningXP(),
                    data.getFarmingLevel(), data.getFarmingXP(),
                    data.getSmithingLevel(), data.getSmithingXP(),
                    data.getArcheryLevel(), data.getArcheryXP(),
                    data.getFishingLevel(), data.getFishingXP(),
                    data.getAgilityLevel(), data.getAgilityXP(),
                    data.getAlchemyLevel(), data.getAlchemyXP(),
                    data.getTornPages()
            );
            PacketDistributor.sendToPlayer(player, payload);

            JournalServerEvents.sharePartyXP(player, skillName.toLowerCase(), finalXp);
        }
    }
    public static void clearAllCustomRestrictionsForClient() {
        CUSTOM_USAGE_RESTRICTIONS.clear();
        CUSTOM_INTERACT_RESTRICTIONS.clear();
        CUSTOM_BLOCK_RESTRICTIONS.clear();
    }
}