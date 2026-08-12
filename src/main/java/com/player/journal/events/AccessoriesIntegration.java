package com.player.journal.events;

import com.player.journal.config.JournalConfig;
import com.player.journal.data.JournalProgressionData;
import com.player.journal.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;


@EventBusSubscriber(modid = "playerjournal")
public class AccessoriesIntegration {

    @SubscribeEvent
    public static void onModSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            if (ModList.get().isLoaded("accessories")) {
                AccessoriesCompat.register();
            }
            if (ModList.get().isLoaded("curios")) {
                CuriosCompat.register();
            }
        });
    }


    public static String getFailedRequirement(ServerPlayer player, String itemIdentifier) {
        List<String> allAccessoryRestrictions = new ArrayList<>();
        allAccessoryRestrictions.addAll((List<String>) JournalConfig.JEWELRY_RESTRICTIONS.get());
        allAccessoryRestrictions.addAll(JournalConfig.getAllItemRestrictions());

        JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);

        for (String restriction : allAccessoryRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length < 2) continue;

            String[] groupedIds = parts[0].split(",");
            boolean matchFound = false;

            for (String id : groupedIds) {
                if (id.trim().equals(itemIdentifier)) {
                    matchFound = true;
                    break;
                }
            }

            if (matchFound) {
                List<String> failedSkills = new ArrayList<>();

                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length != 2) continue;

                    String skill = skillReq[0].toLowerCase().trim();
                    int requiredLevel = Integer.parseInt(skillReq[1].trim());
                    int playerLevel = 0;

                    switch (skill) {
                        case "vitality" -> playerLevel = data.getVitalityLevel();
                        case "agility" -> playerLevel = data.getAgilityLevel();
                        case "combat" -> playerLevel = data.getCombatLevel();
                        case "defense" -> playerLevel = data.getDefenseLevel();
                        case "mining" -> playerLevel = data.getMiningLevel();
                        case "farming" -> playerLevel = data.getFarmingLevel();
                        case "smithing" -> playerLevel = data.getSmithingLevel();
                        case "fishing" -> playerLevel = data.getFishingLevel();
                        case "archery" -> playerLevel = data.getArcheryLevel();
                        case "alchemy" -> playerLevel = data.getAlchemyLevel();
                    }

                    if (playerLevel < requiredLevel) {
                        String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                        if (requiredLevel == 1 && !skill.equals("vitality") && !skill.equals("agility") && !skill.equals("combat") && !skill.equals("defense") && !skill.equals("mining") && !skill.equals("farming") && !skill.equals("smithing")) {
                            failedSkills.add(displaySkill + " Class");
                        } else {
                            failedSkills.add(displaySkill + " " + requiredLevel);
                        }
                    }
                }

                if (!failedSkills.isEmpty()) {
                    return "Requires " + String.join(" & ", failedSkills) + "!";
                }
                return null;
            }
        }
        return null;
    }
}