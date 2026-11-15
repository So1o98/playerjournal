package com.player.journal.registry;

import com.player.journal.effect.XpBuffEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModEffects {

    // 10% Personal Buff (Gold color particles)
    public static final Holder<MobEffect> PERSONAL_XP_BUFF = Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            ResourceLocation.fromNamespaceAndPath("playerjournal", "personal_xp_buff"),
            new XpBuffEffect(MobEffectCategory.BENEFICIAL, 0xFFD700, 0.50f)
    );

    // 10% Party Buff (Green color particles)
    public static final Holder<MobEffect> PARTY_XP_BUFF = Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            ResourceLocation.fromNamespaceAndPath("playerjournal", "party_xp_buff"),
            new XpBuffEffect(MobEffectCategory.BENEFICIAL, 0x00FF00, 0.50f)
    );

    // 10% Global Buff (Cyan color particles)
    public static final Holder<MobEffect> GLOBAL_XP_BUFF = Registry.registerForHolder(
            BuiltInRegistries.MOB_EFFECT,
            ResourceLocation.fromNamespaceAndPath("playerjournal", "global_xp_buff"),
            new XpBuffEffect(MobEffectCategory.BENEFICIAL, 0x00FFFF, 0.50f)
    );

    public static void register() {
        // Triggers static initialization
    }
}