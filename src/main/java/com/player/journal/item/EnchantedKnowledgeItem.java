package com.player.journal.item;

import com.player.journal.registry.ModEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.server.level.ServerPlayer;

public class EnchantedKnowledgeItem extends Item {

    public EnchantedKnowledgeItem(Properties properties) {
        super(properties);
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

            int durationTicks = 20 * 60 * 15;
            int buffType = 0;


            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (customData.contains("BuffDuration")) {
                durationTicks = customData.copyTag().getInt("BuffDuration");
            }
            if (customData.contains("BuffType")) {
                buffType = customData.copyTag().getInt("BuffType");
            }


            var effect = switch (buffType) {
                case 1 -> ModEffects.PARTY_XP_BUFF;
                case 2 -> ModEffects.GLOBAL_XP_BUFF;
                default -> ModEffects.PERSONAL_XP_BUFF;
            };


            if (buffType == 2) {
                for (ServerPlayer onlinePlayer : player.getServer().getPlayerList().getPlayers()) {
                    onlinePlayer.addEffect(new MobEffectInstance(effect, durationTicks, 0, false, false, true));
                }
                player.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal("§6[Server] §e" + player.getName().getString() + " activated a Global XP Buff for " + (durationTicks / 1200) + " minutes!"), false
                );
            }

            else {
                player.addEffect(new MobEffectInstance(effect, durationTicks, 0, false, false, true));
            }

            level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }
        return stack;
    }
}