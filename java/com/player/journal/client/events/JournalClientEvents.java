package com.player.journal.client.events;

import com.player.journal.api.PlayerJournalAPI;
import com.player.journal.client.JournalKeybindings;
import com.player.journal.client.JournalScreen;
import com.player.journal.network.ClientPayloadHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JournalClientEvents {

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            onItemTooltip(stack, lines);
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player != null && mc.screen == null) {
                while (JournalKeybindings.OPEN_JOURNAL_KEY.consumeClick()) {
                    mc.setScreen(new JournalScreen());
                }
            }
        });
    }

    public static void onItemTooltip(ItemStack stack, List<Component> tooltip) {
        if (stack.isEmpty()) return;

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        String potionId = "";
        if (stack.getItem() instanceof PotionItem) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.potion().isPresent()) {
                potionId = "potion:" + contents.potion().get().unwrapKey().get().location().toString();
            }
        }

        boolean isResearched = false;
        if (ClientPayloadHandler.researchedItems != null) {
            if (ClientPayloadHandler.researchedItems.contains(itemId) ||
                    (!potionId.isEmpty() && ClientPayloadHandler.researchedItems.contains(potionId))) {
                isResearched = true;
            }
        }


        List<String> craftingRestrictions = ClientPayloadHandler.serverCraftingRestrictions;
        for (String restriction : craftingRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedIds = parts[0].split(",");
                boolean match = false;
                for (String id : groupedIds) {
                    if (id.trim().equals(itemId)) { match = true; break; }
                }

                if (match) {
                    String[] skillReq = parts[1].split(":");
                    if (skillReq.length == 2 && skillReq[0].trim().equalsIgnoreCase("smithing")) {
                        int reqLevel = Integer.parseInt(skillReq[1].trim());
                        if (ClientPayloadHandler.smithingLevel < reqLevel) {
                            if (isResearched) {
                                tooltip.add(Component.literal("[!] Researched (Crafting Unlocked)").withStyle(ChatFormatting.GREEN));
                            } else {
                                tooltip.add(Component.literal("Requires Smithing " + reqLevel + " to craft!")
                                        .withStyle(ChatFormatting.RED));
                            }
                        }
                    }
                }
            }
        }

        // API Requirements
        Map<String, Integer> apiReqs = new LinkedHashMap<>();

        Map<String, Integer> usageApi = PlayerJournalAPI.getCustomUsage(itemId);
        if (usageApi != null) apiReqs.putAll(usageApi);

        Map<String, Integer> interactApi = PlayerJournalAPI.getCustomInteract(itemId);
        if (interactApi != null) {
            interactApi.forEach((k, v) -> apiReqs.merge(k, v, Math::max));
        }

        Map<String, Integer> blockApi = PlayerJournalAPI.getCustomBlockUsage(itemId);
        if (blockApi != null) {
            blockApi.forEach((k, v) -> apiReqs.merge(k, v, Math::max));
        }

        if (!potionId.isEmpty()) {
            Map<String, Integer> potionUsage = PlayerJournalAPI.getCustomUsage(potionId);
            if (potionUsage != null) {
                potionUsage.forEach((k, v) -> apiReqs.merge(k, v, Math::max));
            }
        }

        if (!apiReqs.isEmpty()) {
            List<String> failedSkills = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : apiReqs.entrySet()) {
                String skill = entry.getKey().trim().toLowerCase();
                int reqLevel = entry.getValue();
                int playerLevel = getClientLevel(skill);

                if (playerLevel < reqLevel) {
                    String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                    failedSkills.add("Level " + reqLevel + " " + displaySkill);
                }
            }

            if (!failedSkills.isEmpty()) {
                if (isResearched) {
                    tooltip.add(Component.literal("[!] Researched (Unlocked)").withStyle(ChatFormatting.GREEN));
                } else {
                    tooltip.add(Component.literal("[!] Restricted: " + String.join(" & ", failedSkills))
                            .withStyle(ChatFormatting.RED));
                }
            }
            return;
        }

        // Server Payload Restrictions
        List<String> usageRestrictions = new ArrayList<>();
        usageRestrictions.addAll(ClientPayloadHandler.serverArmorRestrictions);
        usageRestrictions.addAll(ClientPayloadHandler.serverPotionRestrictions);
        usageRestrictions.addAll(ClientPayloadHandler.serverItemRestrictions);
        usageRestrictions.addAll(ClientPayloadHandler.serverJewelryRestrictions);
        usageRestrictions.addAll(ClientPayloadHandler.serverPaladinsPriestsArmors);
        usageRestrictions.addAll(ClientPayloadHandler.serverPaladinsPriestsWeapons);
        usageRestrictions.addAll(ClientPayloadHandler.serverPaladinsPriestsShields);
        usageRestrictions.addAll(ClientPayloadHandler.serverRoguesWarriorsArmors);
        usageRestrictions.addAll(ClientPayloadHandler.serverRoguesWarriorsWeapons);
        usageRestrictions.addAll(ClientPayloadHandler.serverArchersArmors);
        usageRestrictions.addAll(ClientPayloadHandler.serverArchersWeapons);
        usageRestrictions.addAll(ClientPayloadHandler.serverWizardsArmors);
        usageRestrictions.addAll(ClientPayloadHandler.serverWizardsWeapons);
        usageRestrictions.addAll(ClientPayloadHandler.serverArsenalWeapons);
        usageRestrictions.addAll(ClientPayloadHandler.serverArtifactsItems);
        usageRestrictions.addAll(ClientPayloadHandler.serverTideItems);
        usageRestrictions.addAll(ClientPayloadHandler.serverGlidersItems);
        usageRestrictions.addAll(ClientPayloadHandler.serverLilisLuckyLuresItems);
        usageRestrictions.addAll(ClientPayloadHandler.serverImmersiveMachineryItems);
        usageRestrictions.addAll(ClientPayloadHandler.serverImmersiveAircraftItems);
        usageRestrictions.addAll(ClientPayloadHandler.serverSmallShipsItems);
        usageRestrictions.addAll(ClientPayloadHandler.serverAlchemyUtilities);
        usageRestrictions.addAll(ClientPayloadHandler.serverEnchantmentRestrictions);

        for (String restriction : usageRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length < 2) continue;

            String[] groupedIds = parts[0].split(",");
            boolean match = false;
            for (String id : groupedIds) {
                if (id.trim().equals(itemId) || (!potionId.isEmpty() && id.trim().equals(potionId))) {
                    match = true;
                    break;
                }
            }

            if (match) {
                List<String> failedSkills = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length != 2) continue;

                    String skill = skillReq[0].trim().toLowerCase();
                    int reqLevel = Integer.parseInt(skillReq[1].trim());
                    int playerLevel = getClientLevel(skill);

                    if (playerLevel < reqLevel) {
                        String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                        failedSkills.add("Level " + reqLevel + " " + displaySkill);
                    }
                }

                if (!failedSkills.isEmpty()) {
                    if (isResearched) {
                        tooltip.add(Component.literal("[!] Researched (Unlocked)").withStyle(ChatFormatting.GREEN));
                    } else {
                        tooltip.add(Component.literal("[!] Restricted: " + String.join(" & ", failedSkills))
                                .withStyle(ChatFormatting.RED));
                    }
                }
                break;
            }
        }
    }

    public static class BookButton extends Button {
        public BookButton(int x, int y, OnPress onPress) {
            super(x, y, 20, 20, Component.empty(), onPress, Button.DEFAULT_NARRATION);
            this.setTooltip(Tooltip.create(Component.literal("Player Journal")));
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.renderItem(new ItemStack(Items.BOOK), this.getX() + 2, this.getY() + 2);
        }
    }

    private static int getClientLevel(String skill) {
        return switch (skill.toLowerCase()) {
            case "vitality" -> ClientPayloadHandler.vitalityLevel;
            case "agility" -> ClientPayloadHandler.agilityLevel;
            case "combat" -> ClientPayloadHandler.combatLevel;
            case "defense" -> ClientPayloadHandler.defenseLevel;
            case "farming" -> ClientPayloadHandler.farmingLevel;
            case "mining" -> ClientPayloadHandler.miningLevel;
            case "smithing" -> ClientPayloadHandler.smithingLevel;
            case "archery" -> ClientPayloadHandler.archeryLevel;
            case "fishing" -> ClientPayloadHandler.fishingLevel;
            case "alchemy" -> ClientPayloadHandler.alchemyLevel;
            default -> 0;
        };
    }
}