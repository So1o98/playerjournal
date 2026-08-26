package com.player.journal.compat;

import com.player.journal.api.PlayerJournalAPI;
import com.player.journal.config.JournalConfig;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockRestrictionProvider implements IBlockComponentProvider {

    public static final BlockRestrictionProvider INSTANCE = new BlockRestrictionProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        String blockId = BuiltInRegistries.BLOCK.getKey(accessor.getBlock()).toString();

        // 1. Check Custom API Block Restrictions FIRST (Breaking/Harvesting)
        Map<String, Integer> apiReqs = PlayerJournalAPI.getCustomBlockUsage(blockId);

        // 2. Fallback to API Interact Restrictions if block usage is empty (Markets/Utilities)
        if (apiReqs.isEmpty()) {
            apiReqs = PlayerJournalAPI.getCustomInteract(blockId);
        }

        if (!apiReqs.isEmpty()) {
            for (Map.Entry<String, Integer> req : apiReqs.entrySet()) {
                String skill = req.getKey().trim().toLowerCase();
                int reqLevel = req.getValue();
                int playerLevel = getClientLevel(skill);
                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);

                if (playerLevel >= reqLevel) {
                    tooltip.add(Component.literal("✔ " + displaySkill + " " + reqLevel + " Unlocked").withStyle(net.minecraft.ChatFormatting.GREEN));
                } else {
                    tooltip.add(Component.literal("Locked: Requires " + displaySkill + " " + reqLevel).withStyle(net.minecraft.ChatFormatting.RED));
                }
            }
            return;
        }

        // 3. Fallback to Config Logic if API has no entry for this block
        List<String> allRestrictions = new ArrayList<>();
        allRestrictions.addAll(JournalConfig.getAllBlockRestrictions());
        allRestrictions.addAll(JournalConfig.getUtilityBlockRestrictions());
        allRestrictions.addAll(JournalConfig.getSmithingUtilities());

        for (String restriction : allRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedBlocks = parts[0].split(",");
                for (String b : groupedBlocks) {
                    if (b.trim().equals(blockId)) {
                        String[] skillReq = parts[1].split(":");
                        if (skillReq.length == 2) {
                            String skill = skillReq[0].trim().toLowerCase();
                            int reqLevel = Integer.parseInt(skillReq[1].trim());
                            int playerLevel = getClientLevel(skill);
                            String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);

                            if (playerLevel >= reqLevel) {
                                tooltip.add(Component.literal("✔ " + displaySkill + " " + reqLevel + " Unlocked").withStyle(net.minecraft.ChatFormatting.GREEN));
                            } else {
                                tooltip.add(Component.literal("Locked: Requires " + displaySkill + " " + reqLevel).withStyle(net.minecraft.ChatFormatting.RED));
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    private int getClientLevel(String skill) {
        return switch (skill.toLowerCase()) {
            case "mining" -> ClientPayloadHandler.miningLevel;
            case "farming" -> ClientPayloadHandler.farmingLevel;
            case "vitality" -> ClientPayloadHandler.vitalityLevel;
            case "combat" -> ClientPayloadHandler.combatLevel;
            case "smithing" -> ClientPayloadHandler.smithingLevel;
            case "agility" -> ClientPayloadHandler.agilityLevel;
            case "defense" -> ClientPayloadHandler.defenseLevel;
            case "archery" -> ClientPayloadHandler.archeryLevel;
            case "fishing" -> ClientPayloadHandler.fishingLevel;
            case "alchemy" -> ClientPayloadHandler.alchemyLevel;
            default -> 0;
        };
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("playerjournal", "block_restriction");
    }
}