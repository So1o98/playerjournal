package com.player.journal.mixin;

import com.player.journal.events.JournalServerEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void journal$preventRestrictedArmorEquip(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot) (Object) this;

        if (slot.container instanceof Inventory playerInventory) {
            int index = slot.getSlotIndex();


            if (index >= 36 && index <= 40) {
                Player player = playerInventory.player;

                if (player != null) {
                    String itemIdentifier = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

                    String failMessage = JournalServerEvents.getFailedRequirementFromMap(player, itemIdentifier);

                    if (failMessage != null) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}