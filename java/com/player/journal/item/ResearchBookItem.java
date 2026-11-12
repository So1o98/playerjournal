package com.player.journal.item;

import com.player.journal.data.JournalProgressionData;
import com.player.journal.network.SyncJournalDataPayload;
import com.player.journal.registry.ModAttachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ResearchBookItem extends Item {

    public ResearchBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.contains("ResearchedItemID")) {
            String targetId = customData.copyTag().getString("ResearchedItemID");
            tooltipComponents.add(Component.literal("Unlocks: " + targetId).withStyle(net.minecraft.ChatFormatting.GRAY));
            tooltipComponents.add(Component.literal("Hold Right-Click to read and unlock.").withStyle(net.minecraft.ChatFormatting.YELLOW));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide && entityLiving instanceof ServerPlayer player) {
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

            if (customData.contains("ResearchedItemID")) {
                String targetId = customData.copyTag().getString("ResearchedItemID");
                JournalProgressionData data = player.getData(ModAttachments.JOURNAL_DATA.get());

                if (!data.hasResearched(targetId)) {
                    data.addResearchedItem(targetId);

                    // --- NEW: Dynamic Messaging Logic ---
                    Item targetItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(targetId));
                    String itemName = targetItem.getDescription().getString();
                    boolean isBlock = targetItem instanceof BlockItem;

                    String messageText = isBlock ?
                            "§a" + itemName + " Researched. You can now mine this block!" :
                            "§a" + itemName + " Researched. You can now use this item!";

                    player.displayClientMessage(Component.literal(messageText).withStyle(net.minecraft.ChatFormatting.BOLD), true);
                    // ------------------------------------

                    level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }

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
                            data.getTornPages(),
                            data.getResearchedItems()
                    ));
                } else {
                    player.displayClientMessage(Component.literal("§cYou have already researched this item."), true);
                }
            }
        }
        return stack;
    }
}