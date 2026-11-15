package com.player.journal.mixin;

import com.player.journal.events.JournalServerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class PlayerEventMixin {

    @Mixin(Entity.class)
    public static abstract class MountMixin {
        @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("HEAD"), cancellable = true)
        private void journal$onStartRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
            Entity self = (Entity) (Object) this;
            if (!self.level().isClientSide && self instanceof ServerPlayer player) {
                if (JournalServerEvents.onEntityMount(player, vehicle)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Mixin(Player.class)
    public static abstract class XpLevelMixin {
        @Inject(method = "giveExperienceLevels", at = @At("HEAD"))
        private void journal$onGiveXpLevels(int levels, CallbackInfo ci) {
            Player self = (Player) (Object) this;
            if (!self.level().isClientSide && self instanceof ServerPlayer serverPlayer && levels > 0) {
                JournalServerEvents.onPlayerXpLevelChange(serverPlayer, levels);
            }
        }
    }

    @Mixin(ServerPlayer.class)
    public static abstract class GameModeMixin {
        @Inject(method = "setGameMode", at = @At("HEAD"))
        private void journal$onSetGameMode(GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
            ServerPlayer self = (ServerPlayer) (Object) this;
            JournalServerEvents.onGameModeChange(self, gameMode);
        }
    }

    @Mixin(ItemStack.class)
    public static abstract class CraftTriggerMixin {
        @Inject(method = "onCraftedBy", at = @At("HEAD"))
        private void journal$onItemCrafted(Level level, Player player, int amount, CallbackInfo ci) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                ItemStack self = (ItemStack) (Object) this;

                JournalServerEvents.onItemCrafted(serverPlayer, self);
            }
        }
    }

    @Mixin(FurnaceResultSlot.class)
    public static abstract class SmeltTriggerMixin {
        @Shadow @Final private Player player;

        @Inject(method = "checkTakeAchievements", at = @At("HEAD"))
        private void journal$onItemSmelted(ItemStack stack, CallbackInfo ci) {
            if (!this.player.level().isClientSide && this.player instanceof ServerPlayer serverPlayer) {
                JournalServerEvents.onItemSmelted(serverPlayer, stack);
            }
        }
    }

    @Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
    public static abstract class BrewTriggerMixin {
        @Inject(method = "onTake", at = @At("HEAD"))
        private void journal$onPotionBrewed(Player player, ItemStack stack, CallbackInfo ci) {
            if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                JournalServerEvents.onPotionBrewed(serverPlayer);
            }
        }
    }

    @Mixin(FishingHook.class)
    public static abstract class FishTriggerMixin {
        @Shadow public abstract Player getPlayerOwner();

        @Inject(method = "retrieve", at = @At("RETURN"))
        private void journal$onFishRetrieve(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
            if (cir.getReturnValue() == 1) {
                Player owner = this.getPlayerOwner();
                if (owner instanceof ServerPlayer serverPlayer && !serverPlayer.level().isClientSide) {
                    JournalServerEvents.onPlayerFish(serverPlayer);
                }
            }
        }
    }

    @Mixin(Animal.class)
    public static abstract class BreedTriggerMixin {
        @Inject(method = "spawnChildFromBreeding", at = @At("HEAD"))
        private void journal$onBabySpawn(ServerLevel level, Animal mate, CallbackInfo ci) {
            Animal self = (Animal) (Object) this;
            ServerPlayer breeder = self.getLoveCause();
            if (breeder == null) breeder = mate.getLoveCause();
            if (breeder != null) {
                JournalServerEvents.onBabySpawn(breeder, self);
            }
        }
    }

    @Mixin(AnvilMenu.class)
    public static abstract class AnvilTriggerMixin {
        @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
        private void journal$onAnvilUpdate(CallbackInfo ci) {
            AnvilMenu self = (AnvilMenu) (Object) this;
            Player player = ((com.player.journal.mixin.ItemCombinerMenuAccessor) self).journal$getPlayer();
            ItemStack right = self.getSlot(1).getItem();
            if (JournalServerEvents.onAnvilUpdate(player, right)) {
                self.getSlot(2).set(ItemStack.EMPTY);
                ci.cancel();
            }
        }
    }
}