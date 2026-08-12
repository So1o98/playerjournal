package com.player.journal.compat;

import com.player.journal.config.JournalConfig;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.List;

public enum AnimalBreedingProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    @SuppressWarnings("unchecked")
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        Entity entity = accessor.getEntity();
        ResourceLocation entityLoc = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (entityLoc == null) return;

        String entityId = entityLoc.toString();
        List<String> breedingConfig = (List<String>) JournalConfig.FARMING_BREEDING.get();

        for (String entry : breedingConfig) {
            String[] parts = entry.split(";");
            if (parts.length >= 2) {
                String[] groupedAnimals = parts[0].split(",");
                for (String a : groupedAnimals) {
                    if (a.trim().equals(entityId)) {
                        String[] skillReq = parts[1].split(":");
                        if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("farming")) {
                            int requiredLevel = Integer.parseInt(skillReq[1]);
                            int currentLevel = ClientPayloadHandler.farmingLevel;


                            if (currentLevel < requiredLevel) {
                                tooltip.add(Component.literal("Breeding requires Farming " + requiredLevel).withStyle(net.minecraft.ChatFormatting.RED));
                            } else {
                                tooltip.add(Component.literal("Farming " + requiredLevel + " Unlocked").withStyle(net.minecraft.ChatFormatting.GREEN));
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("playerjournal", "animal_breeding");
    }
}