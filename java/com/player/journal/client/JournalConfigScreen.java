package com.player.journal.client;

import com.player.journal.config.JournalConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;

public class JournalConfigScreen extends Screen {
    private final Screen parent;

    public JournalConfigScreen(Screen parent) {
        super(Component.literal("Player Journal Config"));
        this.parent = parent;
    }

    public static Screen createScreen(Screen parentScreen) {
        return new JournalConfigScreen(parentScreen);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Open playerjournal.json"), btn -> {
            File file = FabricLoader.getInstance().getConfigDir().resolve("playerjournal.json").toFile();
            Util.getPlatform().openFile(file);
        }).bounds(centerX - 100, centerY - 30, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Reload Config from Disk"), btn -> {
            JournalConfig.load();
            JournalScreen.listsBuilt = false;
        }).bounds(centerX - 100, centerY - 5, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
            JournalConfig.save();
            if (this.minecraft != null) {
                this.minecraft.setScreen(this.parent);
            }
        }).bounds(centerX - 100, centerY + 25, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 55, 0xFFFFFF);
    }
}