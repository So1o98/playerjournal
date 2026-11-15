package com.player.journal.network;

import com.player.journal.data.JournalProgressionData;
import com.player.journal.registry.ModAttachments;
import com.player.journal.registry.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class ServerPayloadHandler {

    public static void handleUnlockPage(UnlockPagePayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        context.server().execute(() -> {
            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);
            int cost = 8;

            if (data.getTornPages() >= cost) {
                boolean unlocked = false;

                switch (payload.pageIndex()) {
                    case 6: if (!data.hasFarming()) { data.unlockFarming(); unlocked = true; } break;
                    case 7: if (!data.hasMining()) { data.unlockMining(); unlocked = true; } break;
                    case 8: if (!data.hasSmithing()) { data.unlockSmithing(); unlocked = true; } break;
                    case 9: if (!data.hasArchery()) { data.unlockArchery(); unlocked = true; } break;
                    case 10: if (!data.hasFishing()) { data.unlockFishing(); unlocked = true; } break;
                    case 11: if (!data.hasAlchemy()) { data.unlockAlchemy(); unlocked = true; } break;
                }

                if (unlocked) {
                    data.consumeTornPages(cost);
                    player.setAttached(ModAttachments.JOURNAL_DATA_TYPE, data);

                    ServerPlayNetworking.send(player, new SyncJournalDataPayload(
                            data.getVitalityLevel(), data.getVitalityXP(),
                            data.getCombatLevel(), data.getCombatXP(),
                            data.getDefenseLevel(), data.getDefenseXP(),
                            data.getMiningLevel(), data.getMiningXP(),
                            data.getFarmingLevel(), data.getFarmingXP(),
                            data.getSmithingLevel(), data.getSmithingXP(),
                            data.getArcheryLevel(), data.getArcheryXP(),
                            data.getFishingLevel(), data.getFishingXP(),
                            data.getAgilityLevel(), data.getAgilityXP(),
                            data.getAlchemyLevel(), data.getAlchemyXP(),
                            data.getTornPages(),
                            data.getResearchedItems()
                    ));
                }
            }
        });
    }

    public static void handleRipPages(RipPagesPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        context.server().execute(() -> {
            JournalProgressionData data = player.getAttachedOrCreate(ModAttachments.JOURNAL_DATA_TYPE);

            if (data.getTornPages() >= 5) {
                data.setTornPages(data.getTornPages() - 5);
                player.setAttached(ModAttachments.JOURNAL_DATA_TYPE, data);

                ItemStack bookStack = new ItemStack(ModItems.BOOK_OF_KNOWLEDGE.get());

                bookStack.set(DataComponents.CUSTOM_NAME,
                        Component.literal(player.getName().getString() + "'s Book of Knowledge")
                                .withStyle(ChatFormatting.GOLD));

                if (!player.getInventory().add(bookStack)) {
                    player.drop(bookStack, false);
                }

                player.level().playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);

                SyncJournalDataPayload syncPayload = new SyncJournalDataPayload(
                        data.getVitalityLevel(), data.getVitalityXP(), data.getCombatLevel(), data.getCombatXP(),
                        data.getDefenseLevel(), data.getDefenseXP(), data.getMiningLevel(), data.getMiningXP(),
                        data.getFarmingLevel(), data.getFarmingXP(), data.getSmithingLevel(), data.getSmithingXP(),
                        data.getArcheryLevel(), data.getArcheryXP(), data.getFishingLevel(), data.getFishingXP(),
                        data.getAgilityLevel(), data.getAgilityXP(), data.getAlchemyLevel(), data.getAlchemyXP(),
                        data.getTornPages(),
                        data.getResearchedItems()
                );
                ServerPlayNetworking.send(player, syncPayload);
            }
        });
    }
}