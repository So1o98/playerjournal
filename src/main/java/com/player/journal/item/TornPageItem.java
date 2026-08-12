package com.player.journal.item;

import com.player.journal.data.JournalProgressionData;
import com.player.journal.network.SyncJournalDataPayload;
import com.player.journal.registry.ModAttachments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class TornPageItem extends Item {

    public TornPageItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            JournalProgressionData data = serverPlayer.getData(ModAttachments.JOURNAL_DATA);

            data.addTornPages(1);
            serverPlayer.setData(ModAttachments.JOURNAL_DATA, data);


            PacketDistributor.sendToPlayer(serverPlayer, new SyncJournalDataPayload(
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

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}