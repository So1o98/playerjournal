package com.player.journal.compat;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JournalJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {

        registration.registerEntityComponent(VillagerRestrictionProvider.INSTANCE, Villager.class);
        registration.registerEntityComponent(VillagerRestrictionProvider.INSTANCE, WanderingTrader.class);


        registration.registerBlockComponent(BlockRestrictionProvider.INSTANCE, Block.class);


        registration.registerEntityComponent(AnimalBreedingProvider.INSTANCE, Animal.class);
    }
}