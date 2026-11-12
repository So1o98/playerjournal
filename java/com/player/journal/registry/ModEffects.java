package com.player.journal.registry;

import com.player.journal.effect.XpBuffEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "playerjournal");

    // 10% Personal Buff (Gold color particles)
    public static final DeferredHolder<MobEffect, XpBuffEffect> PERSONAL_XP_BUFF = EFFECTS.register("personal_xp_buff",
            () -> new XpBuffEffect(MobEffectCategory.BENEFICIAL, 0xFFD700, 0.50f));

    // 10% Party Buff (Green color particles)
    public static final DeferredHolder<MobEffect, XpBuffEffect> PARTY_XP_BUFF = EFFECTS.register("party_xp_buff",
            () -> new XpBuffEffect(MobEffectCategory.BENEFICIAL, 0x00FF00, 0.50f));

    // 10% Global Buff (Cyan color particles)
    public static final DeferredHolder<MobEffect, XpBuffEffect> GLOBAL_XP_BUFF = EFFECTS.register("global_xp_buff",
            () -> new XpBuffEffect(MobEffectCategory.BENEFICIAL, 0x00FFFF, 0.50f));
}