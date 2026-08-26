package com.player.journal.compat;

import com.player.journal.api.PlayerJournalAPI;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum EntityRestrictionProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(accessor.getEntity().getType()).toString();

        // Check Custom API Interact Restrictions (Mounts, Boats, Entities)
        Map<String, Integer> apiReqs = PlayerJournalAPI.getCustomInteract(entityId);

        if (!apiReqs.isEmpty()) {
            List<String> requirements = new ArrayList<>();
            boolean meetsAll = true;

            for (Map.Entry<String, Integer> req : apiReqs.entrySet()) {
                String skill = req.getKey().toLowerCase();
                int requiredLevel = req.getValue();
                int playerLevel = getClientLevel(skill);

                String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                requirements.add(displaySkill + " " + requiredLevel);
                if (playerLevel < requiredLevel) meetsAll = false;
            }

            net.minecraft.ChatFormatting color = meetsAll ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED;
            tooltip.add(Component.literal("Requires: " + String.join(" & ", requirements)).withStyle(color));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("playerjournal", "entity_restriction");
    }

    private int getClientLevel(String skill) {
        return switch (skill.toLowerCase()) {
            case "vitality" -> ClientPayloadHandler.vitalityLevel;
            case "agility" -> ClientPayloadHandler.agilityLevel;
            case "combat" -> ClientPayloadHandler.combatLevel;
            case "defense" -> ClientPayloadHandler.defenseLevel;
            case "mining" -> ClientPayloadHandler.miningLevel;
            case "farming" -> ClientPayloadHandler.farmingLevel;
            case "smithing" -> ClientPayloadHandler.smithingLevel;
            case "archery" -> ClientPayloadHandler.archeryLevel;
            case "fishing" -> ClientPayloadHandler.fishingLevel;
            case "alchemy" -> ClientPayloadHandler.alchemyLevel;
            default -> 0;
        };
    }
}