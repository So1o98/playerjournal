package com.player.journal.client;

import com.player.journal.network.InspectJournalPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class InspectJournalScreen extends Screen {

    private static final ResourceLocation CONTENTS_TEXTURE = ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/contents.png");
    private final InspectJournalPayload data;

    private final int imageWidth = 400;
    private final int imageHeight = 200;

    public InspectJournalScreen(InspectJournalPayload data) {
        super(Component.literal("Inspecting " + data.targetName() + "'s Journal"));
        this.data = data;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x90000000);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTENTS_TEXTURE, x, y, imageWidth, imageHeight, 0.0f, 0.0f, 1000, 500, 1000, 500);
    }

    private void drawSkillRow(GuiGraphics guiGraphics, int x, int y, String name, int level) {
        if (level <= 0) {
            guiGraphics.drawString(this.font, "• " + name + " (Locked)", x, y, 0x8B0000, false);
        } else {
            guiGraphics.drawString(this.font, "• " + name + " (Lvl " + level + ")", x, y, 0x006400, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;


        int leftPageCenter = x + 114;
        int topY = y + 45;


        net.minecraft.client.resources.PlayerSkin skin = net.minecraft.client.resources.DefaultPlayerSkin.get(java.util.UUID.nameUUIDFromBytes(data.targetName().getBytes()));


        if (this.minecraft != null && this.minecraft.getConnection() != null) {
            net.minecraft.client.multiplayer.PlayerInfo playerInfo = this.minecraft.getConnection().getPlayerInfo(data.targetName());
            if (playerInfo != null) {
                skin = playerInfo.getSkin();
            }
        }


        int headSize = 32;
        int headX = leftPageCenter - (headSize / 2);
        net.minecraft.client.gui.components.PlayerFaceRenderer.draw(guiGraphics, skin, headX, topY, headSize);


        String name = data.targetName();
        guiGraphics.drawString(this.font, name, leftPageCenter - (this.font.width(name) / 2), topY + headSize + 6, 0x006400, false);



        int rightPageX = x + 235;
        int rightPageY = y + 25;

        guiGraphics.drawString(this.font, data.targetName() + "'s Levels:", rightPageX, rightPageY, 0x333333, false);

        int rightListY = rightPageY + 16;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Vitality", data.vitality()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Agility", data.agility()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Combat", data.combat()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Defense", data.defense()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Farming", data.farming()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Mining", data.mining()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Smithing", data.smithing()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Archery", data.archery()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Fishing", data.fishing()); rightListY += 12;
        drawSkillRow(guiGraphics, rightPageX, rightListY, "Alchemy", data.alchemy());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}