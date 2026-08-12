package com.player.journal.client.events;

import com.player.journal.client.JournalScreen;
import com.player.journal.config.JournalConfig;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.List;

@EventBusSubscriber(modid = "playerjournal", value = Dist.CLIENT)
public class ClientEvents {


    private static float lastVitalityXP = -1, lastAgilityXP = -1, lastCombatXP = -1, lastDefenseXP = -1, lastFarmingXP = -1;

    private static float lastMiningXP = -1, lastSmithingXP = -1, lastArcheryXP = -1, lastFishingXP = -1, lastAlchemyXP = -1;

    @SubscribeEvent
    public static void onInventoryOpen(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {

            String className = screen.getClass().getName().toLowerCase();


            if (screen instanceof InventoryScreen || className.contains("curios") || className.contains("accessor")) {

                JournalButton journalButton = new JournalButton(screen, button -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().setScreen(new com.player.journal.client.JournalScreen());
                    }
                });

                event.addListener(journalButton);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            lastVitalityXP = -1; lastAgilityXP = -1; lastCombatXP = -1; lastDefenseXP = -1; lastFarmingXP = -1;
            lastMiningXP = -1; lastSmithingXP = -1; lastArcheryXP = -1; lastFishingXP = -1; lastAlchemyXP = -1;
            return;
        }


        lastVitalityXP = checkAndAnnounceXP(mc, 2, "Vitality", ClientPayloadHandler.vitalityLevel, ClientPayloadHandler.vitalityXP, lastVitalityXP, JournalConfig.XP_BASE_REQUIREMENT.get(), JournalConfig.XP_MULTIPLIER.get());
        lastAgilityXP = checkAndAnnounceXP(mc, 3, "Agility", ClientPayloadHandler.agilityLevel, ClientPayloadHandler.agilityXP, lastAgilityXP, JournalConfig.AGILITY_XP_BASE_REQUIREMENT.get(), JournalConfig.AGILITY_XP_MULTIPLIER.get());
        lastCombatXP = checkAndAnnounceXP(mc, 4, "Combat", ClientPayloadHandler.combatLevel, ClientPayloadHandler.combatXP, lastCombatXP, JournalConfig.COMBAT_XP_BASE_REQUIREMENT.get(), JournalConfig.COMBAT_XP_MULTIPLIER.get());
        lastDefenseXP = checkAndAnnounceXP(mc, 5, "Defense", ClientPayloadHandler.defenseLevel, ClientPayloadHandler.defenseXP, lastDefenseXP, JournalConfig.DEFENSE_XP_BASE_REQUIREMENT.get(), JournalConfig.DEFENSE_XP_MULTIPLIER.get());


        lastFarmingXP = checkAndAnnounceXP(mc, 6, "Farming", ClientPayloadHandler.farmingLevel, ClientPayloadHandler.farmingXP, lastFarmingXP, JournalConfig.FARMING_XP_BASE_REQUIREMENT.get(), JournalConfig.FARMING_XP_MULTIPLIER.get());
        lastMiningXP = checkAndAnnounceXP(mc, 7, "Mining", ClientPayloadHandler.miningLevel, ClientPayloadHandler.miningXP, lastMiningXP, JournalConfig.MINING_XP_BASE_REQUIREMENT.get(), JournalConfig.MINING_XP_MULTIPLIER.get());
        lastSmithingXP = checkAndAnnounceXP(mc, 8, "Smithing", ClientPayloadHandler.smithingLevel, ClientPayloadHandler.smithingXP, lastSmithingXP, JournalConfig.SMITHING_XP_BASE_REQUIREMENT.get(), JournalConfig.SMITHING_XP_MULTIPLIER.get());
        lastArcheryXP = checkAndAnnounceXP(mc, 9, "Archery", ClientPayloadHandler.archeryLevel, ClientPayloadHandler.archeryXP, lastArcheryXP, JournalConfig.ARCHERY_XP_BASE_REQUIREMENT.get(), JournalConfig.ARCHERY_XP_MULTIPLIER.get());
        lastFishingXP = checkAndAnnounceXP(mc, 10, "Fishing", ClientPayloadHandler.fishingLevel, ClientPayloadHandler.fishingXP, lastFishingXP, JournalConfig.FISHING_XP_BASE_REQUIREMENT.get(), JournalConfig.FISHING_XP_MULTIPLIER.get());
        lastAlchemyXP = checkAndAnnounceXP(mc, 11, "Alchemy", ClientPayloadHandler.alchemyLevel, ClientPayloadHandler.alchemyXP, lastAlchemyXP, JournalConfig.ALCHEMY_XP_BASE_REQUIREMENT.get(), JournalConfig.ALCHEMY_XP_MULTIPLIER.get());
    }

    private static float checkAndAnnounceXP(Minecraft mc, int page, String skillName, int currentLevel, float currentXP, float lastXP, int baseReq, double mult) {

        if (lastXP == -1) return currentXP;


        if (currentXP > lastXP && JournalScreen.PINNED_PAGES.contains(page)) {


            JournalScreen.rebuildUnlocksIfNeeded();

            int nextLevelXP = (int) (baseReq * Math.pow(mult, currentLevel - 1));
            String nextUnlock = "None";


            List<JournalScreen.UnlockEntry> unlocksList = JournalScreen.getListForPage(page);
            for (JournalScreen.UnlockEntry entry : unlocksList) {
                if (entry.requiredLevel > currentLevel) {
                    nextUnlock = entry.stack.getHoverName().getString();


                    if (entry.requiredLevel > currentLevel + 1) {
                        nextUnlock += " (Lvl " + entry.requiredLevel + ")";
                    }

                    break;
                }
            }


            mc.player.displayClientMessage(Component.literal("§e" + skillName + " §6[Lvl " + currentLevel + "] §7: " + (int)currentXP + "/" + nextLevelXP + "xp | §6Next Unlock: §f" + nextUnlock), false);
        }

        return currentXP;
    }


    public static class JournalButton extends Button {
        private final AbstractContainerScreen<?> screen;

        public JournalButton(AbstractContainerScreen<?> screen, OnPress onPress) {

            super(0, 0, 12, 12, Component.empty(), onPress, Button.DEFAULT_NARRATION);
            this.screen = screen;
            this.setTooltip(Tooltip.create(Component.literal("Player Journal")));
        }

        @Override
        public void renderWidget(net.minecraft.client.gui.GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

            this.setX(this.screen.getGuiLeft() + 61);
            this.setY(this.screen.getGuiTop() + 64);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(this.getX(), this.getY(), 0);
            guiGraphics.pose().scale(0.75f, 0.75f, 1.0f);

            guiGraphics.renderItem(new ItemStack(Items.BOOK), 0, 0);

            guiGraphics.pose().popPose();
        }
    }
    @SubscribeEvent
    public static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(com.player.journal.registry.ModMenuTypes.KNOWLEDGE_TABLE_MENU.get(), com.player.journal.client.KnowledgeTableScreen::new);
    }
}