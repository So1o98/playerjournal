package com.player.journal.client;

import com.player.journal.inventory.KnowledgeTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeTableScreen extends AbstractContainerScreen<KnowledgeTableMenu> {
    private static final ResourceLocation ENCHANTMENT_TABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/enchanting_table.png");


    private static final ResourceLocation ENCHANTMENT_SLOT_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final ResourceLocation ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot_highlighted");
    private static final ResourceLocation ENCHANTMENT_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/enchanting_table/enchantment_slot");

    private static final ResourceLocation[] ENCHANTMENT_LEVEL_SPRITES = new ResourceLocation[]{
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3")
    };
    private static final ResourceLocation[] ENCHANTMENT_LEVEL_DISABLED_SPRITES = new ResourceLocation[]{
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_1_disabled"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_2_disabled"),
            ResourceLocation.withDefaultNamespace("container/enchanting_table/level_3_disabled")
    };


    private static final net.minecraft.network.chat.Style SGA_STYLE = net.minecraft.network.chat.Style.EMPTY.withFont(ResourceLocation.withDefaultNamespace("alt"));

    public KnowledgeTableScreen(KnowledgeTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(ENCHANTMENT_TABLE_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int buttonX = x + 60;
        int buttonY = y + 14;

        int bookCount = this.menu.getSlot(0).hasItem() ? 1 : 0;
        int amethystCount = this.menu.getSlot(1).hasItem() ? this.menu.getSlot(1).getItem().getCount() : 0;
        int playerLevels = this.minecraft.player.experienceLevel;

        for (int i = 0; i < 3; i++) {
            int currentY = buttonY + (i * 19);


            int costAmethyst = 1;
            int costLevels = (i + 1) * 3;

            boolean canAfford = bookCount > 0 && amethystCount >= costAmethyst && (playerLevels >= costLevels || this.minecraft.player.isCreative());
            boolean isHovered = mouseX >= buttonX && mouseX <= buttonX + 108 && mouseY >= currentY && mouseY <= currentY + 19;

            ResourceLocation buttonSprite = canAfford ? (isHovered ? ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE : ENCHANTMENT_SLOT_SPRITE) : ENCHANTMENT_SLOT_DISABLED_SPRITE;
            guiGraphics.blitSprite(buttonSprite, buttonX, currentY, 108, 19);

            ResourceLocation orbSprite = canAfford ? ENCHANTMENT_LEVEL_SPRITES[i] : ENCHANTMENT_LEVEL_DISABLED_SPRITES[i];
            guiGraphics.blitSprite(orbSprite, buttonX + 1, currentY + 1, 16, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int buttonX = x + 60;
        int buttonY = y + 14;

        int bookCount = this.menu.getSlot(0).hasItem() ? 1 : 0;
        int amethystCount = this.menu.getSlot(1).hasItem() ? this.menu.getSlot(1).getItem().getCount() : 0;
        int playerLevels = this.minecraft.player.experienceLevel;

        for (int i = 0; i < 3; i++) {
            int currentY = buttonY + (i * 19);


            int costAmethyst = 1;
            int costLevels = (i + 1) * 3;

            boolean canAfford = bookCount > 0 && amethystCount >= costAmethyst && (playerLevels >= costLevels || this.minecraft.player.isCreative());
            boolean isHovered = mouseX >= buttonX && mouseX <= buttonX + 108 && mouseY >= currentY && mouseY <= currentY + 19;

            int textColorSGA = canAfford ? (isHovered ? 0xFFFF80 : 0x685e4a) : 0x404040;
            int textColorNumber = canAfford ? 0x80FF20 : 0x404040;

            String crypticText = (i == 0) ? "klaatu barada" : (i == 1) ? "nikto fhtagn" : "xyzzy plugh";

            guiGraphics.drawString(this.font, Component.literal(crypticText).withStyle(SGA_STYLE), buttonX + 20, currentY + 5, textColorSGA, false);

            String lvlString = String.valueOf(costLevels);
            int lvlWidth = this.font.width(lvlString);
            guiGraphics.drawString(this.font, lvlString, buttonX + 108 - lvlWidth - 2, currentY + 5, textColorNumber, true);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int buttonX = x + 60;
        int buttonY = y + 14;

        int bookCount = this.menu.getSlot(0).hasItem() ? 1 : 0;
        int amethystCount = this.menu.getSlot(1).hasItem() ? this.menu.getSlot(1).getItem().getCount() : 0;
        int playerLevels = this.minecraft.player.experienceLevel;

        for (int i = 0; i < 3; i++) {
            int currentY = buttonY + (i * 19);
            if (mouseX >= buttonX && mouseX <= buttonX + 108 && mouseY >= currentY && mouseY <= currentY + 19) {

                int costAmethyst = 1;
                int costLevels = (i + 1) * 3;

                List<Component> tooltip = new ArrayList<>();
                String buffName = (i == 0) ? "Personal XP Buff" : (i == 1) ? "Party XP Buff" : "Global XP Buff";

                tooltip.add(Component.literal("Tier " + (i + 1) + " - " + buffName).withStyle(net.minecraft.ChatFormatting.GOLD));
                tooltip.add(Component.literal("Duration: 15 to 60 Mins (Random)").withStyle(net.minecraft.ChatFormatting.YELLOW));

                boolean hasXP = playerLevels >= costLevels || this.minecraft.player.isCreative();
                boolean hasAmethyst = amethystCount >= costAmethyst;

                tooltip.add(Component.literal("Cost: " + costLevels + " Levels").withStyle(hasXP ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED));
                tooltip.add(Component.literal("Cost: " + costAmethyst + " Amethyst Shard").withStyle(hasAmethyst ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED));

                if (bookCount == 0) {
                    tooltip.add(Component.literal("Requires: Book of Knowledge").withStyle(net.minecraft.ChatFormatting.RED));
                }

                guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int buttonX = x + 60;
        int buttonY = y + 14;

        for (int i = 0; i < 3; i++) {
            int currentY = buttonY + (i * 19);
            if (mouseX >= buttonX && mouseX <= buttonX + 108 && mouseY >= currentY && mouseY <= currentY + 19) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}