package com.player.journal.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class JournalKeybindings {
    public static final KeyMapping OPEN_JOURNAL_KEY = new KeyMapping(
            "key.playerjournal.open",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.playerjournal.main"
    );
}