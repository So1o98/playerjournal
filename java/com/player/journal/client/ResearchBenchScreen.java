package com.player.journal.client;

import com.player.journal.inventory.ResearchBenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ResearchBenchScreen extends AbstractContainerScreen<ResearchBenchMenu> {


    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/container/research_bench.png");

    public ResearchBenchScreen(ResearchBenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int frame = this.menu.getProgressFrame();
        if (frame > 0) {

            ResourceLocation sprite = ResourceLocation.fromNamespaceAndPath("playerjournal", "container/research_bench/progress_" + frame);
            int spriteWidth = 30;
            int spriteHeight = 16;

            guiGraphics.blitSprite(sprite, x + 69, y + 35, spriteWidth, spriteHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}