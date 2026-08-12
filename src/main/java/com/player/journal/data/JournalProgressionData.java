package com.player.journal.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class JournalProgressionData implements INBTSerializable<CompoundTag> {

    private static final ResourceLocation HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("playerjournal", "vitality_health_boost");
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("playerjournal", "agility_speed_boost");
    private static final ResourceLocation JUMP_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("playerjournal", "agility_jump_boost");

    public static final int MAX_LEVEL = 50;

    private int vitalityLevel = 1;
    private float vitalityXP = 0f;

    private int agilityLevel = 1;
    private float agilityXP = 0f;

    private int combatLevel = 1;
    private float combatXP = 0f;

    private int defenseLevel = 1;
    private float defenseXP = 0f;

    private int miningLevel = 0;
    private float miningXP = 0f;

    private int farmingLevel = 0;
    private float farmingXP = 0f;

    private int smithingLevel = 0;
    private float smithingXP = 0f;

    private int archeryLevel = 0;
    private float archeryXP = 0f;

    private int fishingLevel = 0;
    private float fishingXP = 0f;

    private int alchemyLevel = 0;
    private float alchemyXP = 0f;

    private int tornPages = -1;


    private int getMaxLevel() {
        return MAX_LEVEL;
    }

    public boolean hasMining() { return this.miningLevel >= 1; }
    public void unlockMining() { if (this.miningLevel == 0) this.miningLevel = 1; }

    public boolean hasFarming() { return this.farmingLevel >= 1; }
    public void unlockFarming() { if (this.farmingLevel == 0) this.farmingLevel = 1; }

    public boolean hasSmithing() { return this.smithingLevel >= 1; }
    public void unlockSmithing() { if (this.smithingLevel == 0) this.smithingLevel = 1; }

    public boolean hasArchery() { return this.archeryLevel >= 1; }
    public void unlockArchery() { if (this.archeryLevel == 0) this.archeryLevel = 1; }

    public boolean hasFishing() { return this.fishingLevel >= 1; }
    public void unlockFishing() { if (this.fishingLevel == 0) this.fishingLevel = 1; }

    public boolean hasAgility() { return this.agilityLevel >= 1; }
    public void unlockAgility() { if (this.agilityLevel == 0) this.agilityLevel = 1; }

    public boolean hasAlchemy() { return this.alchemyLevel >= 1; }
    public void unlockAlchemy() { if (this.alchemyLevel == 0) this.alchemyLevel = 1; }

    public int getVitalityLevel() { return vitalityLevel; }
    public float getVitalityXP() { return vitalityXP; }
    public int getAgilityLevel() { return agilityLevel; }
    public float getAgilityXP() { return agilityXP; }
    public int getCombatLevel() { return combatLevel; }
    public float getCombatXP() { return combatXP; }
    public int getDefenseLevel() { return defenseLevel; }
    public float getDefenseXP() { return defenseXP; }
    public int getMiningLevel() { return miningLevel; }
    public float getMiningXP() { return miningXP; }
    public int getFarmingLevel() { return farmingLevel; }
    public float getFarmingXP() { return farmingXP; }
    public int getSmithingLevel() { return smithingLevel; }
    public float getSmithingXP() { return smithingXP; }
    public int getArcheryLevel() { return archeryLevel; }
    public float getArcheryXP() { return archeryXP; }
    public int getFishingLevel() { return fishingLevel; }
    public float getFishingXP() { return fishingXP; }
    public int getAlchemyLevel() { return alchemyLevel; }
    public float getAlchemyXP() { return alchemyXP; }


    public void setVitalityLevel(int level, ServerPlayer player) {
        this.vitalityLevel = Math.min(getMaxLevel(), Math.max(1, level));
        if (player != null) syncPlayerHealth(player);
    }
    public void setAgilityLevel(int level, ServerPlayer player) {
        this.agilityLevel = Math.min(getMaxLevel(), Math.max(1, level));
        if (player != null) syncPlayerAgility(player);
    }
    public void setCombatLevel(int level) { this.combatLevel = Math.min(getMaxLevel(), Math.max(1, level)); }
    public void setDefenseLevel(int level) { this.defenseLevel = Math.min(getMaxLevel(), Math.max(1, level)); }
    public void setMiningLevel(int level) { this.miningLevel = Math.min(getMaxLevel(), Math.max(0, level)); }
    public void setFarmingLevel(int level) { this.farmingLevel = Math.min(getMaxLevel(), Math.max(0, level)); }
    public void setSmithingLevel(int level) { this.smithingLevel = Math.min(getMaxLevel(), Math.max(0, level)); }
    public void setArcheryLevel(int level) { this.archeryLevel = Math.min(getMaxLevel(), Math.max(0, level)); }
    public void setFishingLevel(int level) { this.fishingLevel = Math.min(getMaxLevel(), Math.max(0, level)); }
    public void setAlchemyLevel(int level) { this.alchemyLevel = Math.min(getMaxLevel(), Math.max(0, level)); }

    public void setTornPages(int pages) { this.tornPages = Math.max(0, pages); }

    private void initPagesIfNeeded() {
        if (this.tornPages == -1) {
            try {
                this.tornPages = com.player.journal.config.JournalConfig.STARTING_TORN_PAGES.get();
            } catch (Exception e) {
                this.tornPages = 0;
            }
        }
    }

    public int getTornPages() {
        initPagesIfNeeded();
        return tornPages;
    }

    public void addTornPages(int amount) {
        initPagesIfNeeded();
        this.tornPages += amount;
    }

    public boolean consumeTornPages(int amount) {
        initPagesIfNeeded();
        if (this.tornPages >= amount) {
            this.tornPages -= amount;
            return true;
        }
        return false;
    }

    private void checkPageReward(ServerPlayer player, int currentLevel) {
        try {
            int interval = com.player.journal.config.JournalConfig.LEVELS_PER_PAGE_REWARD.get();
            if (interval > 0 && currentLevel % interval == 0) {
                this.addTornPages(1);
                if (player != null) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§dYou found a Torn Page hidden in your Journal!").withStyle(net.minecraft.ChatFormatting.ITALIC), false);
                    player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        } catch (Exception e) {}
    }

    public boolean addVitalityXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.vitalityLevel >= max) {
            this.vitalityXP = 0;
            return false;
        }

        this.vitalityXP += amount;
        int baseReq = com.player.journal.config.JournalConfig.XP_BASE_REQUIREMENT.get();
        double multiplier = com.player.journal.config.JournalConfig.XP_MULTIPLIER.get();
        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.vitalityLevel - 1));
        boolean leveledUp = false;

        while (this.vitalityXP >= requiredXP) {
            this.vitalityXP -= requiredXP;
            this.vitalityLevel++;
            leveledUp = true;
            checkPageReward(player, this.vitalityLevel);

            if (this.vitalityLevel >= max) {
                this.vitalityLevel = max;
                this.vitalityXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.vitalityLevel - 1));
        }

        if (leveledUp) syncPlayerHealth(player);
        return leveledUp;
    }

    public boolean addAgilityXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.agilityLevel >= max) {
            this.agilityXP = 0;
            return false;
        }

        this.agilityXP += amount;
        int baseReq = 50;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.AGILITY_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.AGILITY_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.agilityLevel - 1));
        boolean leveledUp = false;

        while (this.agilityXP >= requiredXP) {
            this.agilityXP -= requiredXP;
            this.agilityLevel++;
            leveledUp = true;
            checkPageReward(player, this.agilityLevel);

            if (this.agilityLevel >= max) {
                this.agilityLevel = max;
                this.agilityXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.agilityLevel - 1));
        }

        if (leveledUp) syncPlayerAgility(player);
        return leveledUp;
    }

    public boolean addCombatXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.combatLevel >= max) {
            this.combatXP = 0;
            return false;
        }

        this.combatXP += amount;
        int baseReq = com.player.journal.config.JournalConfig.COMBAT_XP_BASE_REQUIREMENT.get();
        double multiplier = com.player.journal.config.JournalConfig.COMBAT_XP_MULTIPLIER.get();
        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.combatLevel - 1));
        boolean leveledUp = false;

        while (this.combatXP >= requiredXP) {
            this.combatXP -= requiredXP;
            this.combatLevel++;
            leveledUp = true;
            checkPageReward(player, this.combatLevel);

            if (this.combatLevel >= max) {
                this.combatLevel = max;
                this.combatXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.combatLevel - 1));
        }
        return leveledUp;
    }

    public boolean addDefenseXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.defenseLevel >= max) {
            this.defenseXP = 0;
            return false;
        }

        this.defenseXP += amount;
        int baseReq = 100;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.DEFENSE_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.DEFENSE_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.defenseLevel - 1));
        boolean leveledUp = false;

        while (this.defenseXP >= requiredXP) {
            this.defenseXP -= requiredXP;
            this.defenseLevel++;
            leveledUp = true;
            checkPageReward(player, this.defenseLevel);

            if (this.defenseLevel >= max) {
                this.defenseLevel = max;
                this.defenseXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.defenseLevel - 1));
        }
        return leveledUp;
    }

    public boolean addMiningXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.miningLevel >= max) {
            this.miningXP = 0;
            return false;
        }

        this.miningXP += amount;
        int baseReq = 100;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.MINING_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.MINING_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.miningLevel - 1));
        boolean leveledUp = false;

        while (this.miningXP >= requiredXP) {
            this.miningXP -= requiredXP;
            this.miningLevel++;
            leveledUp = true;
            checkPageReward(player, this.miningLevel);

            if (this.miningLevel >= max) {
                this.miningLevel = max;
                this.miningXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.miningLevel - 1));
        }
        return leveledUp;
    }

    public boolean addFarmingXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.farmingLevel >= max) {
            this.farmingXP = 0;
            return false;
        }

        this.farmingXP += amount;
        int baseReq = 100;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.FARMING_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.FARMING_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.farmingLevel - 1));
        boolean leveledUp = false;

        while (this.farmingXP >= requiredXP) {
            this.farmingXP -= requiredXP;
            this.farmingLevel++;
            leveledUp = true;
            checkPageReward(player, this.farmingLevel);

            if (this.farmingLevel >= max) {
                this.farmingLevel = max;
                this.farmingXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.farmingLevel - 1));
        }
        return leveledUp;
    }

    public boolean addSmithingXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.smithingLevel >= max) {
            this.smithingXP = 0;
            return false;
        }

        this.smithingXP += amount;
        int baseReq = 100;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.SMITHING_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.SMITHING_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.smithingLevel - 1));
        boolean leveledUp = false;

        while (this.smithingXP >= requiredXP) {
            this.smithingXP -= requiredXP;
            this.smithingLevel++;
            leveledUp = true;
            checkPageReward(player, this.smithingLevel);

            if (this.smithingLevel >= max) {
                this.smithingLevel = max;
                this.smithingXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.smithingLevel - 1));
        }
        return leveledUp;
    }

    public boolean addArcheryXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.archeryLevel >= max) {
            this.archeryXP = 0;
            return false;
        }

        this.archeryXP += amount;
        int baseReq = 50;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.ARCHERY_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.ARCHERY_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.archeryLevel - 1));
        boolean leveledUp = false;

        while (this.archeryXP >= requiredXP) {
            this.archeryXP -= requiredXP;
            this.archeryLevel++;
            leveledUp = true;
            checkPageReward(player, this.archeryLevel);

            if (this.archeryLevel >= max) {
                this.archeryLevel = max;
                this.archeryXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.archeryLevel - 1));
        }
        return leveledUp;
    }

    public boolean addFishingXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.fishingLevel >= max) {
            this.fishingXP = 0;
            return false;
        }

        this.fishingXP += amount;
        int baseReq = 50;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.FISHING_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.FISHING_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.fishingLevel - 1));
        boolean leveledUp = false;

        while (this.fishingXP >= requiredXP) {
            this.fishingXP -= requiredXP;
            this.fishingLevel++;
            leveledUp = true;
            checkPageReward(player, this.fishingLevel);

            if (this.fishingLevel >= max) {
                this.fishingLevel = max;
                this.fishingXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.fishingLevel - 1));
        }
        return leveledUp;
    }

    public boolean addAlchemyXP(float amount, ServerPlayer player) {
        int max = getMaxLevel();
        if (this.alchemyLevel >= max) {
            this.alchemyXP = 0;
            return false;
        }

        this.alchemyXP += amount;
        int baseReq = 50;
        double multiplier = 1.2;

        try {
            baseReq = com.player.journal.config.JournalConfig.ALCHEMY_XP_BASE_REQUIREMENT.get();
            multiplier = com.player.journal.config.JournalConfig.ALCHEMY_XP_MULTIPLIER.get();
        } catch (Exception e) {}

        int requiredXP = (int) (baseReq * Math.pow(multiplier, this.alchemyLevel - 1));
        boolean leveledUp = false;

        while (this.alchemyXP >= requiredXP) {
            this.alchemyXP -= requiredXP;
            this.alchemyLevel++;
            leveledUp = true;
            checkPageReward(player, this.alchemyLevel);

            if (this.alchemyLevel >= max) {
                this.alchemyLevel = max;
                this.alchemyXP = 0;
                break;
            }

            requiredXP = (int) (baseReq * Math.pow(multiplier, this.alchemyLevel - 1));
        }
        return leveledUp;
    }

    public void syncPlayerHealth(ServerPlayer player) {
        AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.removeModifier(HEALTH_MODIFIER_ID);
            int levelsPerBoost = com.player.journal.config.JournalConfig.LEVELS_PER_HEALTH_BOOST.get();
            int extraHearts = (this.vitalityLevel - 1) / levelsPerBoost;
            float healthBonus = extraHearts * 2.0f;
            float totalModifier = -12.0f + healthBonus;

            maxHealthAttr.addPermanentModifier(new AttributeModifier(
                    HEALTH_MODIFIER_ID,
                    totalModifier,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    public void syncPlayerAgility(ServerPlayer player) {
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MODIFIER_ID);
            double baseSpeed = com.player.journal.config.JournalConfig.AGILITY_SPEED_BASE_MODIFIER.get();
            double speedPerLevel = com.player.journal.config.JournalConfig.AGILITY_SPEED_PER_LEVEL.get();
            double speedBonus = baseSpeed + ((this.agilityLevel - 1) * speedPerLevel);

            speedAttr.addPermanentModifier(new AttributeModifier(
                    SPEED_MODIFIER_ID,
                    speedBonus,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }

        AttributeInstance jumpAttr = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (jumpAttr != null) {
            jumpAttr.removeModifier(JUMP_MODIFIER_ID);
            double baseJump = com.player.journal.config.JournalConfig.AGILITY_JUMP_BASE_MODIFIER.get();
            double jumpPerLevel = com.player.journal.config.JournalConfig.AGILITY_JUMP_PER_LEVEL.get();
            double jumpBonus = baseJump + ((this.agilityLevel - 1) * jumpPerLevel);

            jumpAttr.addPermanentModifier(new AttributeModifier(
                    JUMP_MODIFIER_ID,
                    jumpBonus,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        initPagesIfNeeded();

        CompoundTag tag = new CompoundTag();
        tag.putInt("vitalityLevel", this.vitalityLevel);
        tag.putFloat("vitalityXP", this.vitalityXP);
        tag.putInt("agilityLevel", this.agilityLevel);
        tag.putFloat("agilityXP", this.agilityXP);
        tag.putInt("combatLevel", this.combatLevel);
        tag.putFloat("combatXP", this.combatXP);
        tag.putInt("defenseLevel", this.defenseLevel);
        tag.putFloat("defenseXP", this.defenseXP);
        tag.putInt("miningLevel", this.miningLevel);
        tag.putFloat("miningXP", this.miningXP);
        tag.putInt("farmingLevel", this.farmingLevel);
        tag.putFloat("farmingXP", this.farmingXP);
        tag.putInt("smithingLevel", this.smithingLevel);
        tag.putFloat("smithingXP", this.smithingXP);
        tag.putInt("archeryLevel", this.archeryLevel);
        tag.putFloat("archeryXP", this.archeryXP);
        tag.putInt("fishingLevel", this.fishingLevel);
        tag.putFloat("fishingXP", this.fishingXP);
        tag.putInt("alchemyLevel", this.alchemyLevel);
        tag.putFloat("alchemyXP", this.alchemyXP);

        tag.putInt("tornPages", this.tornPages);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.vitalityLevel = tag.contains("vitalityLevel") ? Math.min(MAX_LEVEL, tag.getInt("vitalityLevel")) : 1;
        this.vitalityXP = tag.getFloat("vitalityXP");
        this.agilityLevel = tag.contains("agilityLevel") ? Math.min(MAX_LEVEL, tag.getInt("agilityLevel")) : 1;
        this.agilityXP = tag.getFloat("agilityXP");
        this.combatLevel = tag.contains("combatLevel") ? Math.min(MAX_LEVEL, tag.getInt("combatLevel")) : 1;
        this.combatXP = tag.getFloat("combatXP");
        this.defenseLevel = tag.contains("defenseLevel") ? Math.min(MAX_LEVEL, tag.getInt("defenseLevel")) : 1;
        this.defenseXP = tag.getFloat("defenseXP");
        this.miningLevel = tag.contains("miningLevel") ? Math.min(MAX_LEVEL, tag.getInt("miningLevel")) : 0;
        this.miningXP = tag.getFloat("miningXP");

        if (tag.contains("farmingLevel")) {
            this.farmingLevel = Math.min(MAX_LEVEL, tag.getInt("farmingLevel"));
        } else {
            this.farmingLevel = tag.getBoolean("hasFarming") ? 1 : 0;
        }
        this.farmingXP = tag.getFloat("farmingXP");

        if (tag.contains("smithingLevel")) {
            this.smithingLevel = Math.min(MAX_LEVEL, tag.getInt("smithingLevel"));
        } else {
            this.smithingLevel = tag.getBoolean("hasSmithing") ? 1 : 0;
        }
        this.smithingXP = tag.getFloat("smithingXP");

        if (tag.contains("archeryLevel")) {
            this.archeryLevel = Math.min(MAX_LEVEL, tag.getInt("archeryLevel"));
        } else {
            this.archeryLevel = tag.getBoolean("hasArchery") ? 1 : 0;
        }
        this.archeryXP = tag.getFloat("archeryXP");

        if (tag.contains("fishingLevel")) {
            this.fishingLevel = Math.min(MAX_LEVEL, tag.getInt("fishingLevel"));
        } else {
            this.fishingLevel = tag.getBoolean("hasFishing") ? 1 : 0;
        }
        this.fishingXP = tag.getFloat("fishingXP");

        if (tag.contains("alchemyLevel")) {
            this.alchemyLevel = Math.min(MAX_LEVEL, tag.getInt("alchemyLevel"));
        } else {
            this.alchemyLevel = tag.getBoolean("hasAlchemy") ? 1 : 0;
        }
        this.alchemyXP = tag.getFloat("alchemyXP");

        this.tornPages = tag.contains("tornPages") ? tag.getInt("tornPages") : -1;
    }
}