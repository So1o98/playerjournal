package com.player.journal.events;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;

public class CuriosCompat {

    public static void register() {
        NeoForge.EVENT_BUS.addListener(CuriosCompat::onCurioEquip);
    }

    private static void onCurioEquip(CurioCanEquipEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            String id = BuiltInRegistries.ITEM.getKey(event.getStack().getItem()).toString();

            String failMessage = AccessoriesIntegration.getFailedRequirement(player, id);

            if (failMessage != null) {
                player.displayClientMessage(Component.literal(failMessage), true);
                event.setEquipResult(TriState.FALSE);
            }
        }
    }
}