package com.player.journal.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class JournalKeybindings {
    public static final KeyMapping OPEN_JOURNAL_KEY = new KeyMapping(
            "key.playerjournal.open",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.playerjournal.main"
    );
}