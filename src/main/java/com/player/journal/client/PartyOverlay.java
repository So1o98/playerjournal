package com.player.journal.client;

import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PartyOverlay {

    public static final LayeredDraw.Layer HUD_PARTY = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();

        if (mc.options.hideGui || mc.player == null || ClientPayloadHandler.partyMembers == null || ClientPayloadHandler.partyMembers.size() <= 1) {
            return;
        }

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();


        int headSize = 16;
        int spacing = 6;

        int startX = (width / 2) + 105;
        int startY = height - 20;

        for (int i = 0; i < ClientPayloadHandler.partyMembers.size(); i++) {
            ClientPayloadHandler.PartyMember member = ClientPayloadHandler.partyMembers.get(i);
            int currentX = startX + (i * (headSize + spacing));

            if (!member.inRange) {
                guiGraphics.setColor(0.3F, 0.3F, 0.3F, 1.0F);
            } else {
                guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }


            PlayerFaceRenderer.draw(guiGraphics, member.skin, currentX, startY, headSize);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

            guiGraphics.pose().pushPose();

            guiGraphics.pose().translate(currentX + headSize - 6, startY + headSize - 6, 100);
            guiGraphics.pose().scale(0.55f, 0.55f, 1.0f);
            guiGraphics.renderItem(new ItemStack(Items.BOOK), 0, 0);
            guiGraphics.pose().popPose();
        }
    };
}