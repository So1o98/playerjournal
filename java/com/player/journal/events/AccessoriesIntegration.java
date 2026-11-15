package com.player.journal.events;

import com.player.journal.config.JournalConfig;
import com.player.journal.data.JournalProgressionData;
import com.player.journal.registry.ModAttachments;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AccessoriesIntegration {

    public static void register() {
        if (FabricLoader.getInstance().isModLoaded("accessories")) {
            AccessoriesCompat.register();
        }
    }

    public static String getFailedRequirement(ServerPlayer player, String itemIdentifier) {
        List<String> allAccessoryRestrictions = new ArrayList<>(JournalConfig.getAllItemRestrictions());

        JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

        // Research bypass check
        if (data.hasResearched(itemIdentifier)) {
            return null;
        }

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
                    int playerLevel = switch (skill) {
                        case "vitality" -> data.getVitalityLevel();
                        case "agility" -> data.getAgilityLevel();
                        case "combat" -> data.getCombatLevel();
                        case "defense" -> data.getDefenseLevel();
                        case "mining" -> data.getMiningLevel();
                        case "farming" -> data.getFarmingLevel();
                        case "smithing" -> data.getSmithingLevel();
                        case "fishing" -> data.getFishingLevel();
                        case "archery" -> data.getArcheryLevel();
                        case "alchemy" -> data.getAlchemyLevel();
                        default -> 0;
                    };

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