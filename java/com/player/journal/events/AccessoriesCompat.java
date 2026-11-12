package com.player.journal.events;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
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
                    player.displayClientMessage(Component.literal(failMessage).withStyle(net.minecraft.ChatFormatting.RED), true);
                    return TriState.FALSE;
                }
            }
            return TriState.DEFAULT;
        });
    }

    public static void enforceTick(ServerPlayer player) {
        AccessoriesCapability.getOptionally(player).ifPresent(capability -> {
            for (var entry : capability.getContainers().entrySet()) {
                var container = entry.getValue();

                for (int i = 0; i < container.getSize(); i++) {

                    ItemStack stack = container.getAccessories().getItem(i);

                    if (!stack.isEmpty()) {
                        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                        String failMessage = AccessoriesIntegration.getFailedRequirement(player, id);

                        if (failMessage != null) {

                            container.getAccessories().setItem(i, ItemStack.EMPTY);

                            if (!player.getInventory().add(stack.copy())) {
                                player.drop(stack.copy(), false);
                            }
                        }
                    }
                }
            }
        });
    }
}