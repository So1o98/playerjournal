package com.player.journal.client;

import com.player.journal.network.ClientPayloadHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.resources.ResourceLocation;

public class PartyOverlay {

    private static final ResourceLocation PARTY_TEXTURE = ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/party.png");

    public static final LayeredDraw.Layer HUD_PARTY = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();

        if (mc.options.hideGui || mc.player == null || ClientPayloadHandler.partyMembers == null || ClientPayloadHandler.partyMembers.isEmpty()) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();
        int imageWidth = 177;
        int imageHeight = 90;

        guiGraphics.pose().pushPose();

        float scale = 0.80f;
        guiGraphics.pose().scale(scale, scale, 1.0f);

        int x = (int) ((screenWidth / scale) - imageWidth - 10);
        int y = (int) (10 / scale);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float opacity = 0.70F;


        guiGraphics.setColor(1.0F, 1.0F, 1.0F, opacity);
        guiGraphics.blit(PARTY_TEXTURE, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        int headSize = 16;


        int normalTextColor = 0xFF000000;
        int outOfRangeTextColor = 0xFFAAAAAA;


        float textScale = 1.0f / scale;

        for (int i = 0; i < Math.min(3, ClientPayloadHandler.partyMembers.size()); i++) {
            ClientPayloadHandler.PartyMember member = ClientPayloadHandler.partyMembers.get(i);

            int targetY = y + 14 + (i * 20);

            int headX = x + 12;
            int nameTextX = x + 36;
            int hpTextX = x + 108;

            float currentHp = member.hp;
            int distance = 50;

            boolean isClientPlayer = member.name.equals(mc.player.getName().getString());
            boolean isOutOfRange = !isClientPlayer && (distance > 100 || !member.inRange);

            // 2. Draw Heads
            if (isOutOfRange) {
                guiGraphics.setColor(0.3F, 0.3F, 0.3F, opacity);
            } else {
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            PlayerFaceRenderer.draw(guiGraphics, member.skin, headX, targetY, headSize);


            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(nameTextX, targetY + 3, 0);
            guiGraphics.pose().scale(textScale, textScale, 1.0f);
            guiGraphics.drawString(mc.font, member.name, 0, 0, isOutOfRange ? outOfRangeTextColor : normalTextColor, false);
            guiGraphics.pose().popPose();


            String hpText = (int)currentHp + " HP";
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(hpTextX, targetY + 3, 0);
            guiGraphics.pose().scale(textScale, textScale, 1.0f);
            guiGraphics.drawString(mc.font, hpText, 0, 0, isOutOfRange ? outOfRangeTextColor : normalTextColor, false);
            guiGraphics.pose().popPose();
        }

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        guiGraphics.pose().popPose();
    };
}