package com.player.journal.compat;

import com.player.journal.config.JournalConfig;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.List;

public class VillagerRestrictionProvider implements IEntityComponentProvider {

    public static final VillagerRestrictionProvider INSTANCE = new VillagerRestrictionProvider();

    @Override
    @SuppressWarnings("unchecked")
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        String professionId = null;


        if (accessor.getEntity() instanceof Villager villager) {
            professionId = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()).toString();
        } else if (accessor.getEntity() instanceof WanderingTrader) {
            professionId = "Wandering Trader";
        }

        if (professionId == null) return;


        List<String> villagerRestrictions = (List<String>) JournalConfig.VILLAGER_RESTRICTIONS.get();


        if (professionId.equals("Wandering Trader")) {
            checkAndDrawTooltip(tooltip, "all_classes", 1);
            return;
        }

        for (String restriction : villagerRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2 && parts[0].equals(professionId)) {
                String[] skillReq = parts[1].split(":");
                if (skillReq.length == 2) {
                    checkAndDrawTooltip(tooltip, skillReq[0].toLowerCase(), Integer.parseInt(skillReq[1]));
                }
            }
        }
    }

    private void checkAndDrawTooltip(ITooltip tooltip, String skill, int requiredLevel) {
        boolean isUnlocked = false;

        switch (skill) {
            case "vitality" -> isUnlocked = ClientPayloadHandler.vitalityLevel >= requiredLevel;
            case "combat" -> isUnlocked = ClientPayloadHandler.combatLevel >= requiredLevel;
            case "farming" -> isUnlocked = ClientPayloadHandler.hasFarming;
            case "mining" -> isUnlocked = ClientPayloadHandler.hasMining;
            case "fishing" -> isUnlocked = ClientPayloadHandler.hasFishing;
            case "smithing" -> isUnlocked = ClientPayloadHandler.hasSmithing;
            case "archery" -> isUnlocked = ClientPayloadHandler.hasArchery;
            case "all_classes" -> isUnlocked = ClientPayloadHandler.hasFarming &&
                    ClientPayloadHandler.hasMining &&
                    ClientPayloadHandler.hasSmithing &&
                    ClientPayloadHandler.hasArchery &&
                    ClientPayloadHandler.hasFishing;
        }

        if (isUnlocked) {
            tooltip.add(Component.literal("Unlocked").withStyle(net.minecraft.ChatFormatting.GREEN));
        } else {
            String skillDisplay = skill.equals("all_classes") ? "All Classes" : skill.substring(0, 1).toUpperCase() + skill.substring(1);
            String reqText = (requiredLevel == 1 && !skill.equals("vitality") && !skill.equals("combat"))
                    ? skillDisplay + " Class"
                    : skillDisplay + " " + requiredLevel;

            tooltip.add(Component.literal("Locked: Requires " + reqText).withStyle(net.minecraft.ChatFormatting.RED));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("playerjournal", "villager_restriction");
    }
}