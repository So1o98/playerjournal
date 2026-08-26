package com.player.journal.client.events;

import com.player.journal.client.JournalScreen;
import com.player.journal.config.JournalConfig;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = "playerjournal", value = Dist.CLIENT)
public class ClientEvents {


    private static final Map<Integer, Float> lastKnownXP = new HashMap<>();
    private static final Map<Integer, Long> lastUpdateTimeMap = new HashMap<>();
    private static final List<Integer> displayOrder = new ArrayList<>();

    private static final long DISPLAY_DURATION_MS = 5000;
    private static final long FADE_DURATION_MS = 1500;

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
    public static void onRenderHUD(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui) return;

        JournalScreen.rebuildUnlocksIfNeeded();
        long currentTime = System.currentTimeMillis();


        displayOrder.removeIf(page -> !JournalScreen.PINNED_PAGES.contains(page));
        lastKnownXP.keySet().removeIf(page -> !JournalScreen.PINNED_PAGES.contains(page));
        lastUpdateTimeMap.keySet().removeIf(page -> !JournalScreen.PINNED_PAGES.contains(page));


        for (Integer page : JournalScreen.PINNED_PAGES) {
            float currentXP = getCurrentXP(page);


            if (!lastKnownXP.containsKey(page) || lastKnownXP.get(page) != currentXP) {
                lastKnownXP.put(page, currentXP);


                displayOrder.remove(Integer.valueOf(page));
                displayOrder.add(page);


                lastUpdateTimeMap.put(page, currentTime);
            }
        }

        // 3. Cull pages that have fully faded out
        displayOrder.removeIf(page -> currentTime - lastUpdateTimeMap.getOrDefault(page, 0L) > DISPLAY_DURATION_MS + FADE_DURATION_MS);

        // If nothing is actively updating, stop here and save frames
        if (displayOrder.isEmpty()) return;

        // 4. Render the active stack
        GuiGraphics graphics = event.getGuiGraphics();
        Font font = mc.font;

        float scale = JournalConfig.HUD_SCALE.get().floatValue();
        String anchor = JournalConfig.HUD_ANCHOR.get().toUpperCase();
        int offsetX = JournalConfig.HUD_OFFSET_X.get();
        int offsetY = JournalConfig.HUD_OFFSET_Y.get();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0f);

        int scaledScreenWidth = (int) (screenWidth / scale);
        int scaledScreenHeight = (int) (screenHeight / scale);
        int scaledOffsetX = (int) (offsetX / scale);
        int scaledOffsetY = (int) (offsetY / scale);

        int lineSpacing = 12;
        int totalHeight = displayOrder.size() * lineSpacing;

        int currentY;
        if (anchor.contains("BOTTOM")) {
            currentY = scaledScreenHeight - scaledOffsetY - totalHeight;
        } else {
            currentY = scaledOffsetY;
        }

        // Draw each actively updating skill
        for (Integer page : displayOrder) {
            String text = getPinnedSkillText(page);

            if (text != null) {
                // Calculate Alpha for this specific line
                long timeSinceUpdate = currentTime - lastUpdateTimeMap.getOrDefault(page, 0L);
                float alpha = 1.0f;

                if (timeSinceUpdate > DISPLAY_DURATION_MS) {
                    alpha = 1.0f - ((timeSinceUpdate - DISPLAY_DURATION_MS) / (float) FADE_DURATION_MS);
                }

                alpha = Math.max(0.0f, Math.min(1.0f, alpha));
                int alphaBits = (int) (alpha * 255);

                // If it's barely visible, skip drawing to prevent ghosting
                if (alphaBits > 5) {
                    int textColor = (alphaBits << 24) | 0xFFFFFF; // Merge alpha channel into white text

                    int textWidth = font.width(text);
                    int x;

                    if (anchor.contains("RIGHT")) {
                        x = scaledScreenWidth - scaledOffsetX - textWidth;
                    } else {
                        x = scaledOffsetX;
                    }

                    // Draw string with custom alpha injected
                    graphics.drawString(font, text, x, currentY, textColor, true);
                }

                // Always move down, even if skipping a mostly-invisible frame, to keep structure smooth
                currentY += lineSpacing;
            }
        }

        graphics.pose().popPose();
    }

    private static float getCurrentXP(int page) {
        return switch (page) {
            case 2 -> ClientPayloadHandler.vitalityXP;
            case 3 -> ClientPayloadHandler.agilityXP;
            case 4 -> ClientPayloadHandler.combatXP;
            case 5 -> ClientPayloadHandler.defenseXP;
            case 6 -> ClientPayloadHandler.farmingXP;
            case 7 -> ClientPayloadHandler.miningXP;
            case 8 -> ClientPayloadHandler.smithingXP;
            case 9 -> ClientPayloadHandler.archeryXP;
            case 10 -> ClientPayloadHandler.fishingXP;
            case 11 -> ClientPayloadHandler.alchemyXP;
            default -> 0f;
        };
    }

    private static String getPinnedSkillText(int page) {
        String skillName = "";
        int currentLevel = 0;
        float currentXP = 0;
        int baseReq = 0;
        double mult = 1.0;

        switch (page) {
            case 2 -> { skillName = "Vitality"; currentLevel = ClientPayloadHandler.vitalityLevel; currentXP = ClientPayloadHandler.vitalityXP; baseReq = JournalConfig.XP_BASE_REQUIREMENT.get(); mult = JournalConfig.XP_MULTIPLIER.get(); }
            case 3 -> { skillName = "Agility"; currentLevel = ClientPayloadHandler.agilityLevel; currentXP = ClientPayloadHandler.agilityXP; baseReq = JournalConfig.AGILITY_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.AGILITY_XP_MULTIPLIER.get(); }
            case 4 -> { skillName = "Combat"; currentLevel = ClientPayloadHandler.combatLevel; currentXP = ClientPayloadHandler.combatXP; baseReq = JournalConfig.COMBAT_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.COMBAT_XP_MULTIPLIER.get(); }
            case 5 -> { skillName = "Defense"; currentLevel = ClientPayloadHandler.defenseLevel; currentXP = ClientPayloadHandler.defenseXP; baseReq = JournalConfig.DEFENSE_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.DEFENSE_XP_MULTIPLIER.get(); }
            case 6 -> { skillName = "Farming"; currentLevel = ClientPayloadHandler.farmingLevel; currentXP = ClientPayloadHandler.farmingXP; baseReq = JournalConfig.FARMING_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.FARMING_XP_MULTIPLIER.get(); }
            case 7 -> { skillName = "Mining"; currentLevel = ClientPayloadHandler.miningLevel; currentXP = ClientPayloadHandler.miningXP; baseReq = JournalConfig.MINING_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.MINING_XP_MULTIPLIER.get(); }
            case 8 -> { skillName = "Smithing"; currentLevel = ClientPayloadHandler.smithingLevel; currentXP = ClientPayloadHandler.smithingXP; baseReq = JournalConfig.SMITHING_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.SMITHING_XP_MULTIPLIER.get(); }
            case 9 -> { skillName = "Archery"; currentLevel = ClientPayloadHandler.archeryLevel; currentXP = ClientPayloadHandler.archeryXP; baseReq = JournalConfig.ARCHERY_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.ARCHERY_XP_MULTIPLIER.get(); }
            case 10 -> { skillName = "Fishing"; currentLevel = ClientPayloadHandler.fishingLevel; currentXP = ClientPayloadHandler.fishingXP; baseReq = JournalConfig.FISHING_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.FISHING_XP_MULTIPLIER.get(); }
            case 11 -> { skillName = "Alchemy"; currentLevel = ClientPayloadHandler.alchemyLevel; currentXP = ClientPayloadHandler.alchemyXP; baseReq = JournalConfig.ALCHEMY_XP_BASE_REQUIREMENT.get(); mult = JournalConfig.ALCHEMY_XP_MULTIPLIER.get(); }
            default -> { return null; }
        }

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

        return "§e" + skillName + " §6[Lvl " + currentLevel + "] §7: " + (int)currentXP + "/" + nextLevelXP + "xp | §6Next Unlock: §f" + nextUnlock;
    }

    public static class JournalButton extends Button {
        private final AbstractContainerScreen<?> screen;

        public JournalButton(AbstractContainerScreen<?> screen, OnPress onPress) {
            super(0, 0, 12, 12, Component.empty(), onPress, Button.DEFAULT_NARRATION);
            this.screen = screen;
            this.setTooltip(Tooltip.create(Component.literal("Player Journal")));
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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