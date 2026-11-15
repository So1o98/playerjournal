package com.player.journal.mixin;

import com.player.journal.events.JournalServerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEventMixin {

    @Shadow public abstract ItemStack getUseItem();
    @Shadow public abstract boolean isDamageSourceBlocked(DamageSource damageSource);

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void journal$onHurtHead(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide) {

            float modified = JournalServerEvents.onPlayerDamagePre(self, source, amount);
            if (modified <= 0f && amount > 0f) {
                cir.setReturnValue(false);
                return;
            }
            if (self instanceof ServerPlayer player) {
                if (amount > 0.0f && this.isDamageSourceBlocked(source)) {
                    JournalServerEvents.onShieldBlock(player, source, amount);
                } else {
                    JournalServerEvents.onIncomingDamage(player, source, amount);
                }
            }
        }
    }

    @Inject(method = "actuallyHurt", at = @At("TAIL"))
    private void journal$onActuallyHurtTail(DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide) {
            JournalServerEvents.onDamagePost(self, source, amount);
        }
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void journal$onHeal(float healAmount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide && self instanceof ServerPlayer player && healAmount > 0) {
            if (player.getHealth() > 0.0f && player.getHealth() < player.getMaxHealth()) {
                JournalServerEvents.onPlayerHeal(player, healAmount);
            }
        }
    }

    @Inject(method = "startUsingItem", at = @At("HEAD"), cancellable = true)
    private void journal$onStartUsingItem(InteractionHand hand, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide && self instanceof ServerPlayer player) {
            if (JournalServerEvents.onItemUseStart(player, player.getItemInHand(hand))) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void journal$onCompleteUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide && self instanceof ServerPlayer player) {
            JournalServerEvents.onItemUsed(player, this.getUseItem().copy());
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void journal$onCheckTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide && self instanceof ServerPlayer player) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (!JournalServerEvents.canUseTotem(player, stack)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}