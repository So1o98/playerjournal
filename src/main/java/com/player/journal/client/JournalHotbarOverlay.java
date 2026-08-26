package com.player.journal.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class JournalHotbarOverlay {

    public static final LayeredDraw.Layer HUD_JOURNAL_ICON = (guiGraphics, deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;


        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();


        int x = (screenWidth / 2) + 98;
        int y = screenHeight - 20;


        guiGraphics.renderItem(new ItemStack(Items.BOOK), x, y);


        String keyName = JournalKeybindings.OPEN_JOURNAL_KEY.getTranslatedKeyMessage().getString().toUpperCase();


        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 10, y + 10, 200);
        guiGraphics.pose().scale(0.6f, 0.6f, 1.0f);
        guiGraphics.drawString(mc.font, keyName, 0, 0, 0xFFDD00, true);
        guiGraphics.pose().popPose();
    };
}