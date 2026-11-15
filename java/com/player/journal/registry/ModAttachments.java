package com.player.journal.registry;

import com.player.journal.data.JournalProgressionData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class ModAttachments {

    public static final AttachmentType<JournalProgressionData> JOURNAL_DATA_TYPE = AttachmentRegistry.<JournalProgressionData>builder()
            .initializer(JournalProgressionData::new)
            .persistent(JournalProgressionData.CODEC)
            .copyOnDeath()
            .buildAndRegister(ResourceLocation.fromNamespaceAndPath("playerjournal", "journal_data"));

    // Kept as a Supplier so ModAttachments.JOURNAL_DATA.get() still compiles
    public static final Supplier<AttachmentType<JournalProgressionData>> JOURNAL_DATA = () -> JOURNAL_DATA_TYPE;

    // Convenience helper to replace player.getData(ModAttachments.JOURNAL_DATA)
    public static JournalProgressionData get(Player player) {
        return player.getAttachedOrCreate(JOURNAL_DATA_TYPE);
    }

    public static void register() {
        // Triggers static initialization
    }
}