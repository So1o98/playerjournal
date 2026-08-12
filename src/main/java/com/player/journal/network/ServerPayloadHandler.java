package com.player.journal.network;

import com.player.journal.data.JournalProgressionData;
import com.player.journal.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {

    public static void handleUnlockPage(UnlockPagePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA);
                int cost = 5;

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

                        PacketDistributor.sendToPlayer(player, new SyncJournalDataPayload(
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
                                data.getTornPages()
                        ));
                    }
                }
            }
        });
    }

    public static void handleRipPages(RipPagesPayload payload, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                com.player.journal.data.JournalProgressionData data = player.getData(com.player.journal.registry.ModAttachments.JOURNAL_DATA);

                if (data.getTornPages() >= 5) {
                    data.setTornPages(data.getTornPages() - 5);

                    net.minecraft.world.item.ItemStack bookStack = new net.minecraft.world.item.ItemStack(com.player.journal.registry.ModItems.BOOK_OF_KNOWLEDGE.get());


                    bookStack.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                            net.minecraft.network.chat.Component.literal(player.getName().getString() + "'s Book of Knowledge")
                                    .withStyle(net.minecraft.ChatFormatting.GOLD));

                    if (!player.getInventory().add(bookStack)) {
                        player.drop(bookStack, false);
                    }

                    player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);


                    SyncJournalDataPayload syncPayload = new SyncJournalDataPayload(
                            data.getVitalityLevel(), data.getVitalityXP(), data.getCombatLevel(), data.getCombatXP(),
                            data.getDefenseLevel(), data.getDefenseXP(), data.getMiningLevel(), data.getMiningXP(),
                            data.getFarmingLevel(), data.getFarmingXP(), data.getSmithingLevel(), data.getSmithingXP(),
                            data.getArcheryLevel(), data.getArcheryXP(), data.getFishingLevel(), data.getFishingXP(),
                            data.getAgilityLevel(), data.getAgilityXP(), data.getAlchemyLevel(), data.getAlchemyXP(),
                            data.getTornPages()
                    );
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, syncPayload);
                }
            }
        });
    }
}