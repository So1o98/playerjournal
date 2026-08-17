package com.player.journal.client.events;

import com.player.journal.config.JournalConfig;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import com.player.journal.client.PartyOverlay;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "playerjournal", value = Dist.CLIENT)
public class JournalClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        String potionId = "";
        if (stack.getItem() instanceof PotionItem) {
            PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
            if (contents != null && contents.potion().isPresent()) {
                potionId = "potion:" + contents.potion().get().unwrapKey().get().location().toString();
            }
        }

        // --- Crafting Restrictions ---
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
                            event.getToolTip().add(Component.literal("Requires Smithing " + reqLevel + " to craft!")
                                    .withStyle(ChatFormatting.RED));
                        }
                    }
                }
            }
        }

        // --- Usage Restrictions (Now silently contains Datapacks via Payload Sync!) ---
        List<String> usageRestrictions = new ArrayList<>();
        usageRestrictions.addAll(ClientPayloadHandler.serverArmorRestrictions);
        usageRestrictions.addAll(ClientPayloadHandler.serverPotionRestrictions);
        usageRestrictions.addAll(ClientPayloadHandler.serverItemRestrictions); // <-- Datapack data is hiding in here!
        usageRestrictions.addAll(ClientPayloadHandler.serverJewelryRestrictions);
        usageRestrictions.addAll(ClientPayloadHandler.serverFarmersDelightRestrictions);
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
                    int playerLevel = 0;

                    switch (skill) {
                        case "vitality" -> playerLevel = ClientPayloadHandler.vitalityLevel;
                        case "agility" -> playerLevel = ClientPayloadHandler.agilityLevel;
                        case "combat" -> playerLevel = ClientPayloadHandler.combatLevel;
                        case "defense" -> playerLevel = ClientPayloadHandler.defenseLevel;
                        case "farming" -> playerLevel = ClientPayloadHandler.farmingLevel;
                        case "mining" -> playerLevel = ClientPayloadHandler.miningLevel;
                        case "smithing" -> playerLevel = ClientPayloadHandler.smithingLevel;
                        case "archery" -> playerLevel = ClientPayloadHandler.archeryLevel;
                        case "fishing" -> playerLevel = ClientPayloadHandler.fishingLevel;
                        case "alchemy" -> playerLevel = ClientPayloadHandler.alchemyLevel;
                    }

                    if (playerLevel < reqLevel) {
                        String displaySkill = skill.substring(0, 1).toUpperCase() + skill.substring(1);
                        failedSkills.add("Level " + reqLevel + " " + displaySkill);
                    }
                }

                if (!failedSkills.isEmpty()) {
                    event.getToolTip().add(Component.literal("[!] Restricted: " + String.join(" & ", failedSkills))
                            .withStyle(ChatFormatting.RED));
                }
                break;
            }
        }
    }

    public static class BookButton extends net.minecraft.client.gui.components.Button {
        public BookButton(int x, int y, OnPress onPress) {
            super(x, y, 20, 20, Component.empty(), onPress, net.minecraft.client.gui.components.Button.DEFAULT_NARRATION);
            this.setTooltip(net.minecraft.client.gui.components.Tooltip.create(Component.literal("Player Journal")));
        }

        @Override
        public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.renderItem(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BOOK), this.getX() + 2, this.getY() + 2);
        }
    }

    @SubscribeEvent
    public static void registerGuiOverlays(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event) {
        event.registerAboveAll(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("playerjournal", "party_hud"), PartyOverlay.HUD_PARTY);
    }
}