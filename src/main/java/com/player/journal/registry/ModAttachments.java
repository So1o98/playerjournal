package com.player.journal.registry;

import com.player.journal.data.JournalProgressionData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {


    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "playerjournal");


    public static final Supplier<AttachmentType<JournalProgressionData>> JOURNAL_DATA =
            ATTACHMENT_TYPES.register(
                    "journal_data",

                    () -> AttachmentType.serializable(JournalProgressionData::new).copyOnDeath().build()
            );
}