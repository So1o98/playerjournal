package com.player.journal.events;

import io.wispforest.accessories.api.events.CanEquipCallback;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class AccessoriesCompat {

    public static void register() {
        CanEquipCallback.EVENT.register((ItemStack stack, SlotReference reference) -> {
            if (reference.entity() instanceof ServerPlayer player) {
                String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();


                String failMessage = AccessoriesIntegration.getFailedRequirement(player, id);

                if (failMessage != null) {
                    player.displayClientMessage(Component.literal(failMessage), true);
                    return TriState.FALSE;
                }
            }
            return TriState.DEFAULT;
        });
    }
}