package com.player.journal.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class XpBuffEffect extends MobEffect {
    private final float multiplier;

    public XpBuffEffect(MobEffectCategory category, int color, float multiplier) {
        super(category, color);
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }
}