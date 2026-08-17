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

public class PlayerJournalAPI {

    /**
     * Safely retrieves a player's level for a specific skill.
     * Returns 0 if the skill doesn't exist or the player has no data.
     */
    public static int getPlayerLevel(Player player, String skillName) {
        if (player.level().isClientSide()) return 0; // Data is authoritative on the server

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

    /**
     * Checks if a player has unlocked a specific chapter page (Skill Level >= 1).
     */
    public static boolean hasUnlockedPage(Player player, String skillName) {
        String skill = skillName.toLowerCase();

        // Base stats are unlocked by default
        if (skill.equals("vitality") || skill.equals("agility") ||
                skill.equals("combat") || skill.equals("defense")) {
            return true;
        }

        return getPlayerLevel(player, skillName) >= 1;
    }

    /**
     * Returns the player's current balance of Torn Pages (currency).
     */
    public static int getTornPagesBalance(Player player) {
        if (player.level().isClientSide()) return 0;

        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
        return data != null ? data.getTornPages() : 0;
    }

    /**
     * Checks if a specific item is restricted in the Datapack system.
     * Returns an empty map if there are no usage restrictions.
     */
    public static Map<String, Integer> getItemUsageRestrictions(String itemId) {
        return RestrictionDataLoader.getItemRestrictions(itemId);
    }

    /**
     * Checks if a player meets all the requirements to use/equip an item.
     * (Returns true if they can use it, false if their level is too low).
     */
    /**
     * Checks if a player meets all the requirements to use/equip an item.
     * This checks the Datapack FIRST, and falls back to the TOML Config!
     */
    public static boolean canPlayerUseItem(ServerPlayer player, String itemId) {
        if (player.level().isClientSide()) return true;
        if (player.isCreative() || player.isSpectator()) return true;

        // Call the hybrid method we just made public!
        String failMessage = JournalServerEvents.getFailedRequirementFromMap(player, itemId);

        // If failMessage returns null, they have the required levels (or the item has no restrictions).
        // If it returns a string, they are restricted, so we return false!
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
}