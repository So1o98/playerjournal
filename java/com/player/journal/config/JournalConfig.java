package com.player.journal.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class JournalConfig {


    public static class ModConfigSpec {
        private final List<ConfigValue<?>> entries = new ArrayList<>();

        public void save() {
            JournalConfig.save();
        }

        public static class ConfigValue<T> {
            protected final List<String> path;
            protected final T defaultValue;
            protected T value;

            public ConfigValue(List<String> path, T defaultValue) {
                this.path = new ArrayList<>(path);
                this.defaultValue = defaultValue;
                this.value = defaultValue;
            }

            public T get() {
                return value != null ? value : defaultValue;
            }

            public T getDefault() {
                return defaultValue;
            }

            public void set(T value) {
                this.value = value;
            }

            public void save() {
                JournalConfig.save();
            }

            public List<String> getPath() {
                return path;
            }
        }

        public static class IntValue extends ConfigValue<Integer> {
            private final int min;
            private final int max;

            public IntValue(List<String> path, int defaultValue, int min, int max) {
                super(path, defaultValue);
                this.min = min;
                this.max = max;
            }

            @Override
            public void set(Integer val) {
                this.value = Math.max(min, Math.min(max, val));
            }
        }

        public static class DoubleValue extends ConfigValue<Double> {
            private final double min;
            private final double max;

            public DoubleValue(List<String> path, double defaultValue, double min, double max) {
                super(path, defaultValue);
                this.min = min;
                this.max = max;
            }

            @Override
            public void set(Double val) {
                this.value = Math.max(min, Math.min(max, val));
            }
        }

        public static class BooleanValue extends ConfigValue<Boolean> {
            public BooleanValue(List<String> path, boolean defaultValue) {
                super(path, defaultValue);
            }
        }

        public static class Builder {
            private final List<String> currentPath = new ArrayList<>();
            private final List<ConfigValue<?>> builtEntries = new ArrayList<>();

            public Builder push(String name) {
                currentPath.add(name);
                return this;
            }

            public Builder pop() {
                if (!currentPath.isEmpty()) {
                    currentPath.remove(currentPath.size() - 1);
                }
                return this;
            }

            public Builder comment(String... comments) {
                return this;
            }

            private List<String> fullPath(String key) {
                List<String> p = new ArrayList<>(currentPath);
                p.add(key);
                return p;
            }

            public IntValue defineInRange(String key, int defaultValue, int min, int max) {
                IntValue v = new IntValue(fullPath(key), defaultValue, min, max);
                builtEntries.add(v);
                return v;
            }

            public DoubleValue defineInRange(String key, double defaultValue, double min, double max) {
                DoubleValue v = new DoubleValue(fullPath(key), defaultValue, min, max);
                builtEntries.add(v);
                return v;
            }

            public BooleanValue define(String key, boolean defaultValue) {
                BooleanValue v = new BooleanValue(fullPath(key), defaultValue);
                builtEntries.add(v);
                return v;
            }

            public <T> ConfigValue<T> define(String key, T defaultValue) {
                ConfigValue<T> v = new ConfigValue<>(fullPath(key), defaultValue);
                builtEntries.add(v);
                return v;
            }

            public <T> ConfigValue<List<? extends T>> defineListAllowEmpty(List<String> keyPath, Supplier<List<? extends T>> defaultSupplier, Predicate<Object> validator) {
                List<String> p = new ArrayList<>(currentPath);
                p.addAll(keyPath);
                List<? extends T> def = new ArrayList<>(defaultSupplier.get());
                ConfigValue<List<? extends T>> v = new ConfigValue<>(p, def);
                builtEntries.add(v);
                return v;
            }

            public ModConfigSpec build() {
                ModConfigSpec spec = new ModConfigSpec();
                spec.entries.addAll(builtEntries);
                return spec;
            }
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("playerjournal.json");

    public static final ModConfigSpec SPEC;

    // --- GENERAL SETTINGS ---
    public static final ModConfigSpec.IntValue LEVELS_PER_PAGE_REWARD;
    public static final ModConfigSpec.IntValue STARTING_TORN_PAGES;

    // --- CLIENT HUD OVERLAY SETTINGS ---
    public static final ModConfigSpec.DoubleValue HUD_SCALE;
    public static final ModConfigSpec.ConfigValue<String> HUD_ANCHOR;
    public static final ModConfigSpec.IntValue HUD_OFFSET_X;
    public static final ModConfigSpec.IntValue HUD_OFFSET_Y;

    // --- VITALITY SETTINGS ---
    public static final ModConfigSpec.DoubleValue XP_PER_HEART_HEALED;
    public static final ModConfigSpec.IntValue XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue XP_MULTIPLIER;
    public static final ModConfigSpec.IntValue LEVELS_PER_HEALTH_BOOST;
    public static final ModConfigSpec.IntValue ENV_DAMAGE_COOLDOWN;

    // --- AGILITY SETTINGS ---
    public static final ModConfigSpec.IntValue AGILITY_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue AGILITY_XP_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue AGILITY_XP_PER_BLOCK;
    public static final ModConfigSpec.DoubleValue AGILITY_SPEED_BASE_MODIFIER;
    public static final ModConfigSpec.DoubleValue AGILITY_SPEED_PER_LEVEL;
    public static final ModConfigSpec.DoubleValue AGILITY_JUMP_BASE_MODIFIER;
    public static final ModConfigSpec.DoubleValue AGILITY_JUMP_PER_LEVEL;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> AGILITY_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> AGILITY_MOUNTS;

    // --- COMBAT SETTINGS ---
    public static final ModConfigSpec.BooleanValue ENABLE_CUSTOM_MOB_XP;
    public static final ModConfigSpec.IntValue COMBAT_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue COMBAT_XP_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOB_XP_VALUES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_MOB_XP_VALUES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> WEAPON_RESTRICTIONS;

    // --- DEFENSE SETTINGS ---
    public static final ModConfigSpec.IntValue DEFENSE_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue DEFENSE_XP_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue DEFENSE_SHIELD_XP_PER_DAMAGE;
    public static final ModConfigSpec.DoubleValue DEFENSE_ARMOR_XP_PER_DAMAGE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARMOR_RESTRICTIONS;

    // --- MINING SETTINGS ---
    public static final ModConfigSpec.IntValue MINING_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue MINING_XP_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_TOOL_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_MINING_TOOLS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_UTILITIES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_COAL;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_COPPER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_IRON;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_GOLD;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_LAPIS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_EMERALD;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_DIAMOND;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_QUARTZ;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_NETHERITE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_STONE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_DEEPSLATE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_PRISMARINE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_NETHER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MINING_REDSTONE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_MINING_BLOCKS;

    // --- FARMING SETTINGS ---
    public static final ModConfigSpec.IntValue FARMING_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue FARMING_XP_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FARMING_HOES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FARMING_AXES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FARMING_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FARMING_UTILITIES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FARMING_CROPS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FARMING_BREEDING;

    // --- SMITHING SETTINGS ---
    public static final ModConfigSpec.IntValue SMITHING_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue SMITHING_XP_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_SMELTING;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_UTILITIES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_IRON_TOOLS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_IRON_ARMOR;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_GOLD_TOOLS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_GOLD_ARMOR;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_DIAMOND_TOOLS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_DIAMOND_ARMOR;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_NETHERITE_TOOLS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_NETHERITE_ARMOR;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_CRAFTING_CUSTOM;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_IMMERSIVE_AIRCRAFT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_IMMERSIVE_MACHINERY;

    // --- ARCHERY SETTINGS ---
    public static final ModConfigSpec.IntValue ARCHERY_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue ARCHERY_XP_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ARCHERY_XP_PER_HIT;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARCHERY_BOWS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARCHERY_CROSSBOWS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARCHERY_TRIDENTS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARCHERY_ARROWS;

    // --- FISHING SETTINGS ---
    public static final ModConfigSpec.IntValue FISHING_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue FISHING_XP_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue FISHING_XP_PER_CATCH;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FISHING_RODS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FISHING_OCEAN_BLOCKS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FISHING_DIVING_GEAR;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FISHING_CUSTOM;

    // --- ALCHEMY SETTINGS ---
    public static final ModConfigSpec.IntValue ALCHEMY_XP_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue ALCHEMY_XP_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ALCHEMY_BREW_XP;
    public static final ModConfigSpec.DoubleValue ALCHEMY_DRINK_XP;
    public static final ModConfigSpec.DoubleValue ALCHEMY_BOOK_XP;
    public static final ModConfigSpec.DoubleValue ALCHEMY_ENCHANTED_BOOK_BASE_XP;
    public static final ModConfigSpec.DoubleValue ALCHEMY_ENCHANTED_BOOK_LEVEL_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALCHEMY_UTILITIES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> POTION_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENCHANTMENT_RESTRICTIONS;

    // --- GLOBAL RESTRICTIONS ---
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_ITEM_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> VILLAGER_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> TIDE_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> GLIDERS_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LILIS_LUCKY_LURES_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> IMMERSIVE_MACHINERY_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> IMMERSIVE_AIRCRAFT_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMALL_SHIPS_ITEMS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // ==========================================
        //                GENERAL SETTINGS
        // ==========================================
        builder.push("General");
        LEVELS_PER_PAGE_REWARD = builder.comment("How many skill levels are required to be rewarded with 1 Torn Page? (e.g. 5 = rewarded at level 5, 10, 15...)").defineInRange("levelsPerPageReward", 5, 1, 100);
        STARTING_TORN_PAGES = builder.comment("How many Torn Pages should a player start with when they first join?").defineInRange("startingTornPages", 5, 0, 1000);

        builder.push("HUD_Overlay");
        HUD_SCALE = builder.comment("Scale of the pinned pages overlay (1.0 is normal, 0.75 is small, 1.5 is large)")
                .defineInRange("hud_scale", 1.0, 0.1, 3.0);
        HUD_ANCHOR = builder.comment("Which corner to anchor the HUD to. Valid options: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT")
                .define("hud_anchor", "BOTTOM_RIGHT");
        HUD_OFFSET_X = builder.comment("Horizontal offset from the edge of the screen")
                .defineInRange("hud_offset_x", 5, 0, 2000);
        HUD_OFFSET_Y = builder.comment("Vertical offset from the edge of the screen")
                .defineInRange("hud_offset_y", 5, 0, 2000);
        builder.pop();
        builder.pop();

        // ==========================================
        //                VITALITY TAB
        // ==========================================
        builder.push("Vitality");
        XP_PER_HEART_HEALED = builder.comment("XP gained per 1 full heart healed").defineInRange("xpPerHeartHealed", 3.0, 0.0, 100.0);
        XP_BASE_REQUIREMENT = builder.comment("Base XP required for Level 2").defineInRange("xpBaseRequirement", 50, 1, 10000);
        XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("xpMultiplier", 1.2, 1.0, 5.0);
        LEVELS_PER_HEALTH_BOOST = builder.comment("Vitality levels required to gain 1 extra heart").defineInRange("levelsPerHealthBoost", 3, 1, 100);
        ENV_DAMAGE_COOLDOWN = builder.comment("Cooldown (in seconds) for gaining XP from environmental damage (fall, cactus, etc.)").defineInRange("envDamageCooldown", 15, 0, 300);
        builder.pop();

        // ==========================================
        //                AGILITY TAB
        // ==========================================
        builder.push("Agility");
        AGILITY_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Agility Level 2").defineInRange("agilityXpBaseRequirement", 150, 1, 10000);
        AGILITY_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("agilityXpMultiplier", 1.2, 1.0, 5.0);
        AGILITY_XP_PER_BLOCK = builder.comment("Agility XP gained per block moved").defineInRange("agilityXpPerBlock", 0.1, 0.0, 100.0);
        AGILITY_SPEED_BASE_MODIFIER = builder.comment("Starting speed modifier at Level 1 (Negative means slower than vanilla)").defineInRange("agilitySpeedBaseModifier", -0.025, -0.1, 0.1);
        AGILITY_SPEED_PER_LEVEL = builder.comment("Speed modifier gained per Agility level").defineInRange("agilitySpeedPerLevel", 0.001, 0.0, 0.1);
        AGILITY_JUMP_BASE_MODIFIER = builder.comment("Starting jump modifier at Level 1 (Negative means lower jump than vanilla)").defineInRange("agilityJumpBaseModifier", -0.0, -0.4, 0.5);
        AGILITY_JUMP_PER_LEVEL = builder.comment("Jump modifier gained per Agility level").defineInRange("agilityJumpPerLevel", 0.001, 0.0, 0.1);
        AGILITY_ITEMS = builder.defineListAllowEmpty(List.of("agility_items"), () -> List.of(
                "minecraft:elytra;agility:25",
                "minecraft:ender_pearl;agility:18",
                "minecraft:oak_boat;agility:4",
                "minecraft:spruce_boat;agility:4",
                "minecraft:birch_boat;agility:4",
                "minecraft:jungle_boat;agility:4",
                "minecraft:acacia_boat;agility:4",
                "minecraft:dark_oak_boat;agility:4",
                "minecraft:mangrove_boat;agility:4",
                "minecraft:cherry_boat;agility:4",
                "minecraft:bamboo_raft;agility:4",
                "minecraft:acacia_chest_boat:6",
                "minecraft:birch_chest_boat:6",
                "minecraft:cherry_chest_boat:6",
                "minecraft:dark_oak_chest_boat:6",
                "minecraft:jungle_chest_boat:6",
                "minecraft:mangrove_chest_boat:6",
                "minecraft:oak_chest_boat:6",
                "minecraft:spruce_chest_boat:6"
        ), obj -> obj instanceof String);
        AGILITY_MOUNTS = builder.comment("Format: entity_id;skill:level").defineListAllowEmpty(List.of("agility_mounts"), () -> List.of(
                "minecraft:horse;agility:12",
                "minecraft:donkey;agility:8",
                "minecraft:mule;agility:10",
                "minecraft:camel;agility:15"
        ), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             COMBAT TAB
        // ==========================================
        builder.push("Combat");
        ENABLE_CUSTOM_MOB_XP = builder
                .comment("Set to false to disable custom mob XP values and use vanilla/other mod XP handling.")
                .define("enableCustomMobXP", false);
        COMBAT_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Combat Level 2").defineInRange("combatXpBaseRequirement", 200, 1, 10000);
        COMBAT_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("combatXpMultiplier", 1.2, 1.0, 5.0);
        MOB_XP_VALUES = builder.defineListAllowEmpty(List.of("mob_xp_values"), () -> List.of(
                "minecraft:ender_dragon;500",
                "minecraft:wither;300",
                "minecraft:warden;250",
                "minecraft:elder_guardian;50",
                "minecraft:ravager;30",
                "minecraft:evoker;20",
                "minecraft:vindicator;15",
                "minecraft:piglin_brute;15",
                "minecraft:breeze;15",
                "minecraft:zoglin;15",
                "minecraft:enderman;15",
                "minecraft:ghast;15",
                "minecraft:shulker;15",
                "minecraft:creeper;10",
                "minecraft:witch;10",
                "minecraft:blaze;10",
                "minecraft:wither_skeleton;10",
                "minecraft:guardian;10",
                "minecraft:pillager;10",
                "minecraft:cave_spider;10",
                "minecraft:hoglin;10",
                "minecraft:zombie;5",
                "minecraft:skeleton;5",
                "minecraft:spider;8",
                "minecraft:husk;5",
                "minecraft:stray;5",
                "minecraft:drowned;5",
                "minecraft:bogged;5",
                "minecraft:slime;5",
                "minecraft:magma_cube;5",
                "minecraft:phantom;8",
                "minecraft:cow;2",
                "minecraft:pig;2",
                "minecraft:sheep;2",
                "minecraft:chicken;2",
                "minecraft:mooshroom;2",
                "minecraft:horse;2",
                "minecraft:donkey;2",
                "minecraft:mule;2",
                "minecraft:llama;2",
                "minecraft:trader_llama;2",
                "minecraft:goat;2",
                "minecraft:camel;2",
                "minecraft:sniffer;3",
                "minecraft:armadillo;2",
                "minecraft:wolf;2",
                "minecraft:cat;1",
                "minecraft:ocelot;2",
                "minecraft:fox;2",
                "minecraft:panda;2",
                "minecraft:polar_bear;5",
                "minecraft:turtle;2",
                "minecraft:rabbit;1",
                "minecraft:squid;1",
                "minecraft:glow_squid;1",
                "minecraft:dolphin;2",
                "minecraft:axolotl;2",
                "minecraft:frog;2",
                "minecraft:bat;1",
                "minecraft:bee;2",
                "minecraft:cod;1",
                "minecraft:salmon;1",
                "minecraft:pufferfish;1",
                "minecraft:tropical_fish;1",
                "minecraft:tadpole;1",
                "minecraft:villager;1",
                "minecraft:wandering_trader;5",
                "minecraft:iron_golem;15",
                "minecraft:snow_golem;2"
        ), obj -> obj instanceof String);

        CUSTOM_MOB_XP_VALUES = builder.comment("Add custom or modded mob XP values here (Format: entity_id;xp_reward)")
                .defineListAllowEmpty(List.of("custom_mob_xp_values"), () -> List.of(), obj -> obj instanceof String);

        WEAPON_RESTRICTIONS = builder.defineListAllowEmpty(List.of("weapon_restrictions"), () -> List.of(
                "minecraft:stone_sword;combat:4",
                "minecraft:iron_sword;combat:10",
                "minecraft:golden_sword;combat:7",
                "minecraft:diamond_sword;combat:15",
                "minecraft:netherite_sword;combat:20",
                "minecraft:mace;combat:10"
        ), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             DEFENSE TAB
        // ==========================================
        builder.push("Defense");
        DEFENSE_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Defense Level 2").defineInRange("defenseXpBaseRequirement", 50, 1, 10000);
        DEFENSE_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("defenseXpMultiplier", 1.3, 1.0, 5.0);
        DEFENSE_SHIELD_XP_PER_DAMAGE = builder.comment("XP gained per 1 point of damage successfully blocked by a shield").defineInRange("defenseShieldXpPerDamage", 8, 0.0, 100.0);
        DEFENSE_ARMOR_XP_PER_DAMAGE = builder.comment("XP gained per 1 point of damage mitigated by physical armor").defineInRange("defenseArmorXpPerDamage", 5, 0.0, 100.0);

        ARMOR_RESTRICTIONS = builder.defineListAllowEmpty(List.of("armor_restrictions"), () -> List.of(
                "minecraft:leather_helmet;defense:1",
                "minecraft:leather_chestplate;defense:1",
                "minecraft:leather_leggings;defense:1",
                "minecraft:leather_boots;defense:1",
                "minecraft:chainmail_helmet;defense:5",
                "minecraft:chainmail_chestplate;defense:5",
                "minecraft:chainmail_leggings;defense:5",
                "minecraft:chainmail_boots;defense:5",
                "minecraft:iron_helmet;defense:15;combat:10",
                "minecraft:iron_chestplate;defense:15;combat:10",
                "minecraft:iron_leggings;defense:15;combat:10",
                "minecraft:iron_boots;defense:15;combat:10",
                "minecraft:golden_helmet;defense:10;combat:5",
                "minecraft:golden_chestplate;defense:10;combat:5",
                "minecraft:golden_leggings;defense:10;combat:5",
                "minecraft:golden_boots;defense:10;combat:5",
                "minecraft:diamond_helmet;defense:20;combat:15",
                "minecraft:diamond_chestplate;defense:20;combat:15",
                "minecraft:diamond_leggings;defense:20;combat:15",
                "minecraft:diamond_boots;defense:20;combat:15",
                "minecraft:netherite_helmet;defense:25;combat:20",
                "minecraft:netherite_chestplate;defense:25;combat:20",
                "minecraft:netherite_leggings;defense:25;combat:20",
                "minecraft:netherite_boots;defense:25;combat:20"
        ), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             MINING TAB
        // ==========================================
        builder.push("Mining");
        MINING_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Mining Level 2").defineInRange("miningXpBaseRequirement", 50, 1, 10000);
        MINING_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("miningXpMultiplier", 1.4, 1.0, 5.0);
        MINING_TOOL_RESTRICTIONS = builder.defineListAllowEmpty(List.of("mining_tool_restrictions"), () -> List.of(
                "minecraft:stone_pickaxe,minecraft:stone_shovel;mining:4",
                "minecraft:iron_pickaxe,minecraft:iron_shovel;mining:10",
                "minecraft:golden_pickaxe,minecraft:golden_shovel;mining:8",
                "minecraft:diamond_pickaxe,minecraft:diamond_shovel;mining:15",
                "minecraft:netherite_pickaxe,minecraft:netherite_shovel;mining:30"
        ), obj -> obj instanceof String);
        CUSTOM_MINING_TOOLS = builder.defineListAllowEmpty(List.of("custom_mining_tools"), () -> List.of(), obj -> obj instanceof String);
        MINING_UTILITIES = builder.comment("Blocks that require interaction (Modded Machines, etc)").defineListAllowEmpty(List.of("mining_utilities"), () -> List.of(), obj -> obj instanceof String);
        MINING_COAL = builder.defineListAllowEmpty(List.of("mining_coal"), () -> List.of("minecraft:coal_ore;mining:2;5", "minecraft:deepslate_coal_ore;mining:2;5", "minecraft:coal_block;mining:2"), obj -> obj instanceof String);
        MINING_COPPER = builder.defineListAllowEmpty(List.of("mining_copper"), () -> List.of("minecraft:copper_ore;mining:4;10", "minecraft:deepslate_copper_ore;mining:4;10", "minecraft:raw_copper_block;mining:4", "minecraft:copper_block;mining:4"), obj -> obj instanceof String);
        MINING_IRON = builder.defineListAllowEmpty(List.of("mining_iron"), () -> List.of("minecraft:iron_ore;mining:8;15", "minecraft:deepslate_iron_ore;mining:8;15", "minecraft:raw_iron_block;mining:8", "minecraft:iron_block;mining:8"), obj -> obj instanceof String);
        MINING_GOLD = builder.defineListAllowEmpty(List.of("mining_gold"), () -> List.of("minecraft:gold_ore;mining:8;25", "minecraft:deepslate_gold_ore;mining:8;25", "minecraft:nether_gold_ore;mining:8;25", "minecraft:raw_gold_block;mining:8", "minecraft:gold_block;mining:8"), obj -> obj instanceof String);
        MINING_LAPIS = builder.defineListAllowEmpty(List.of("mining_lapis"), () -> List.of("minecraft:lapis_ore;mining:12;20", "minecraft:deepslate_lapis_ore;mining:12;20", "minecraft:lapis_block;mining:12"), obj -> obj instanceof String);
        MINING_EMERALD = builder.defineListAllowEmpty(List.of("mining_emerald"), () -> List.of("minecraft:emerald_ore;mining:16;40", "minecraft:deepslate_emerald_ore;mining:16;40", "minecraft:emerald_block;mining:16"), obj -> obj instanceof String);
        MINING_DIAMOND = builder.defineListAllowEmpty(List.of("mining_diamond"), () -> List.of("minecraft:diamond_ore;mining:15;50", "minecraft:deepslate_diamond_ore;mining:15;50", "minecraft:diamond_block;mining:15"), obj -> obj instanceof String);
        MINING_QUARTZ = builder.defineListAllowEmpty(List.of("mining_quartz"), () -> List.of("minecraft:nether_quartz_ore;mining:12;15"), obj -> obj instanceof String);
        MINING_NETHERITE = builder.defineListAllowEmpty(List.of("mining_netherite"), () -> List.of("minecraft:ancient_debris;mining:25;250", "minecraft:netherite_block;mining:25"), obj -> obj instanceof String);
        MINING_STONE = builder.defineListAllowEmpty(List.of("mining_stone"), () -> List.of("minecraft:stone,minecraft:stone_stairs,minecraft:stone_slab,minecraft:smooth_stone,minecraft:smooth_stone_slab;mining:1;1", "minecraft:cobblestone,minecraft:cobblestone_stairs,minecraft:cobblestone_slab,minecraft:cobblestone_wall;mining:1;1", "minecraft:mossy_cobblestone,minecraft:mossy_cobblestone_stairs,minecraft:mossy_cobblestone_slab,minecraft:mossy_cobblestone_wall;mining:1;1", "minecraft:stone_bricks,minecraft:stone_brick_stairs,minecraft:stone_brick_slab,minecraft:stone_brick_wall;mining:1;1", "minecraft:mossy_stone_bricks,minecraft:mossy_stone_brick_stairs,minecraft:mossy_stone_brick_slab,minecraft:mossy_stone_brick_wall;mining:1;1", "minecraft:cracked_stone_bricks,minecraft:chiseled_stone_bricks;mining:1;1", "minecraft:andesite,minecraft:andesite_stairs,minecraft:andesite_slab,minecraft:andesite_wall;mining:1;1", "minecraft:polished_andesite,minecraft:polished_andesite_stairs,minecraft:polished_andesite_slab;mining:1;1", "minecraft:diorite,minecraft:diorite_stairs,minecraft:diorite_slab,minecraft:diorite_wall;mining:1;1", "minecraft:polished_diorite,minecraft:polished_diorite_stairs,minecraft:polished_diorite_slab;mining:1;1", "minecraft:granite,minecraft:granite_stairs,minecraft:granite_slab,minecraft:granite_wall;mining:1;1", "minecraft:polished_granite,minecraft:polished_granite_stairs,minecraft:polished_granite_slab;mining:1;1", "minecraft:sandstone,minecraft:sandstone_stairs,minecraft:sandstone_slab,minecraft:sandstone_wall,minecraft:chiseled_sandstone,minecraft:cut_sandstone,minecraft:cut_sandstone_slab,minecraft:smooth_sandstone,minecraft:smooth_sandstone_stairs,minecraft:smooth_sandstone_slab;mining:1;1", "minecraft:red_sandstone,minecraft:red_sandstone_stairs,minecraft:red_sandstone_slab,minecraft:red_sandstone_wall,minecraft:chiseled_red_sandstone,minecraft:cut_red_sandstone,minecraft:cut_red_sandstone_slab,minecraft:smooth_red_sandstone,minecraft:smooth_red_sandstone_stairs,minecraft:smooth_red_sandstone_slab;mining:1;1", "minecraft:tuff,minecraft:tuff_stairs,minecraft:tuff_slab,minecraft:tuff_wall;mining:1;1", "minecraft:tuff_bricks,minecraft:tuff_brick_stairs,minecraft:tuff_brick_slab,minecraft:tuff_brick_wall,minecraft:chiseled_tuff_bricks;mining:1;1", "minecraft:polished_tuff,minecraft:polished_tuff_stairs,minecraft:polished_tuff_slab,minecraft:polished_tuff_wall,minecraft:chiseled_tuff;mining:1;1", "minecraft:calcite,minecraft:dripstone_block,minecraft:mud_bricks,minecraft:mud_brick_stairs,minecraft:mud_brick_slab,minecraft:mud_brick_wall;mining:1;1"), obj -> obj instanceof String);
        MINING_DEEPSLATE = builder.defineListAllowEmpty(List.of("mining_deepslate"), () -> List.of("minecraft:deepslate,minecraft:chiseled_deepslate;mining:3;2", "minecraft:cobbled_deepslate,minecraft:cobbled_deepslate_stairs,minecraft:cobbled_deepslate_slab,minecraft:cobbled_deepslate_wall;mining:3;2", "minecraft:polished_deepslate,minecraft:polished_deepslate_stairs,minecraft:polished_deepslate_slab,minecraft:polished_deepslate_wall;mining:3;2", "minecraft:deepslate_bricks,minecraft:deepslate_brick_stairs,minecraft:deepslate_brick_slab,minecraft:deepslate_brick_wall,minecraft:cracked_deepslate_bricks;mining:3;2", "minecraft:deepslate_tiles,minecraft:deepslate_tile_stairs,minecraft:deepslate_tile_slab,minecraft:deepslate_tile_wall,minecraft:cracked_deepslate_tiles;mining:3;2"), obj -> obj instanceof String);
        MINING_PRISMARINE = builder.defineListAllowEmpty(List.of("mining_prismarine"), () -> List.of("minecraft:prismarine,minecraft:prismarine_stairs,minecraft:prismarine_slab,minecraft:prismarine_wall;mining:10;5", "minecraft:prismarine_bricks,minecraft:prismarine_brick_stairs,minecraft:prismarine_brick_slab;mining:10;5", "minecraft:dark_prismarine,minecraft:dark_prismarine_stairs,minecraft:dark_prismarine_slab;mining:10;5"), obj -> obj instanceof String);
        MINING_NETHER = builder.defineListAllowEmpty(List.of("mining_nether"), () -> List.of("minecraft:netherrack,minecraft:crimson_nylium,minecraft:warped_nylium,minecraft:magma_block,minecraft:bone_block;mining:3;1", "minecraft:basalt,minecraft:polished_basalt,minecraft:smooth_basalt;mining:3;1", "minecraft:blackstone,minecraft:blackstone_stairs,minecraft:blackstone_slab,minecraft:blackstone_wall,minecraft:gilded_blackstone;mining:3;2", "minecraft:polished_blackstone,minecraft:polished_blackstone_stairs,minecraft:polished_blackstone_slab,minecraft:polished_blackstone_wall,minecraft:chiseled_polished_blackstone;mining:3;2", "minecraft:polished_blackstone_bricks,minecraft:polished_blackstone_brick_stairs,minecraft:polished_blackstone_brick_slab,minecraft:polished_blackstone_brick_wall,minecraft:cracked_polished_blackstone_bricks;mining:3;2", "minecraft:nether_bricks,minecraft:nether_brick_stairs,minecraft:nether_brick_slab,minecraft:nether_brick_wall;mining:3;2", "minecraft:cracked_nether_bricks,minecraft:chiseled_nether_bricks,minecraft:red_nether_bricks,minecraft:red_nether_brick_stairs,minecraft:red_nether_brick_slab,minecraft:red_nether_brick_wall;mining:3;2", "minecraft:quartz_block,minecraft:quartz_stairs,minecraft:quartz_slab,minecraft:smooth_quartz,minecraft:smooth_quartz_stairs,minecraft:smooth_quartz_slab,minecraft:chiseled_quartz_block,minecraft:quartz_pillar,minecraft:quartz_bricks;mining:5;2", "minecraft:obsidian,minecraft:crying_obsidian,minecraft:respawn_anchor,minecraft:lodestone;mining:15;100"), obj -> obj instanceof String);
        MINING_REDSTONE = builder.defineListAllowEmpty(List.of("mining_redstone"), () -> List.of("minecraft:redstone_ore,minecraft:deepslate_redstone_ore,minecraft:redstone_block;mining:10;20", "minecraft:dispenser,minecraft:dropper,minecraft:observer,minecraft:piston,minecraft:sticky_piston;mining:6;2", "minecraft:rail,minecraft:powered_rail,minecraft:detector_rail,minecraft:activator_rail;mining:4;2", "minecraft:hopper,minecraft:cauldron,minecraft:heavy_weighted_pressure_plate,minecraft:light_weighted_pressure_plate;mining:4;2"), obj -> obj instanceof String);
        CUSTOM_MINING_BLOCKS = builder.defineListAllowEmpty(List.of("custom_mining_blocks"), () -> List.of(
                "jewelry:gem_vein;mining:18;50",
                "jewelry:deepslate_gem_vein;mining:18;50"), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             FARMING TAB
        // ==========================================
        builder.push("Farming");
        FARMING_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Farming Level 2").defineInRange("farmingXpBaseRequirement", 150, 1, 10000);
        FARMING_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("farmingXpMultiplier", 1.3, 1.0, 5.0);
        FARMING_HOES = builder.comment("Format: hoe_id;skill:level").defineListAllowEmpty(List.of("farming_hoes"), () -> List.of(
                "minecraft:wooden_hoe;farming:1",
                "minecraft:stone_hoe;farming:5",
                "minecraft:iron_hoe;farming:10",
                "minecraft:golden_hoe;farming:10",
                "minecraft:diamond_hoe;farming:15",
                "minecraft:netherite_hoe;farming:25"
        ), obj -> obj instanceof String);
        FARMING_AXES = builder.comment("Format: axe_id;skill:level").defineListAllowEmpty(List.of("farming_axes"), () -> List.of(
                "minecraft:stone_axe;farming:3",
                "minecraft:iron_axe;farming:8",
                "minecraft:golden_axe;farming:10",
                "minecraft:diamond_axe;farming:15",
                "minecraft:netherite_axe;farming:25"
        ), obj -> obj instanceof String);
        FARMING_ITEMS = builder.comment("Items like buckets, shears, and bone meal").defineListAllowEmpty(List.of("farming_items"), () -> List.of(
                "minecraft:shears;farming:9",
                "minecraft:bucket,minecraft:water_bucket,minecraft:lava_bucket;farming:3",
                "minecraft:milk_bucket;farming:4",
                "minecraft:bone_meal;farming:6"
        ), obj -> obj instanceof String);
        FARMING_UTILITIES = builder.comment("Blocks that require interaction (Composters, Beehives, etc)").defineListAllowEmpty(List.of("farming_utilities"), () -> List.of(
                "minecraft:composter;farming:2",
                "minecraft:smoker;farming:12",
                "minecraft:beehive,minecraft:bee_nest;farming:14"
        ), obj -> obj instanceof String);
        FARMING_CROPS = builder.comment("Format: block_id;skill:level;xp_reward (XP is awarded ONLY when fully grown!)").defineListAllowEmpty(List.of("farming_crops"), () -> List.of(
                "minecraft:wheat;farming:1;3",
                "minecraft:carrots;farming:2;4",
                "minecraft:carrot;farming:2;4",
                "minecraft:potato;farming:2;4",
                "minecraft:potatoes;farming:2;4",
                "minecraft:beetroots;farming:3;5",
                "minecraft:melon;farming:4;8",
                "minecraft:pumpkin;farming:4;8",
                "minecraft:sugar_cane;farming:5;2",
                "minecraft:cocoa;farming:6;5",
                "minecraft:sweet_berry_bush;farming:3;2",
                "minecraft:cave_vines;farming:5;3",
                "minecraft:nether_wart;farming:10;10",
                "minecraft:wheat_seeds;farming:1",
                "minecraft:pumpkin_seeds;farming:4",
                "minecraft:melon_seeds;farming:4",
                "minecraft:torchflower_seeds;farming:8",
                "minecraft:beetroot_seeds;farming:3"
        ), obj -> obj instanceof String);
        FARMING_BREEDING = builder.comment("Format: entity_id;skill:level;xp_reward").defineListAllowEmpty(List.of("farming_breeding"), () -> List.of(
                "minecraft:chicken;farming:5;5",
                "minecraft:pig;farming:6;8",
                "minecraft:sheep;farming:7;10",
                "minecraft:cow;farming:8;12",
                "minecraft:mooshroom;farming:12;20",
                "minecraft:horse,minecraft:donkey;farming:10;25",
                "minecraft:llama;farming:10;25",
                "minecraft:bee;farming:8;15",
                "minecraft:turtle;farming:12;30",
                "minecraft:sniffer;farming:15;50"
        ), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             SMITHING TAB
        // ==========================================
        builder.push("Smithing");
        SMITHING_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Smithing Level 2").defineInRange("smithingXpBaseRequirement", 100, 1, 10000);
        SMITHING_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("smithingXpMultiplier", 1.3, 1.0, 5.0);
        SMITHING_SMELTING = builder.comment("Format: item_id;xp_reward").defineListAllowEmpty(List.of("smithing_smelting"), () -> List.of(
                "minecraft:iron_ingot;2", "minecraft:gold_ingot;3", "minecraft:copper_ingot;1", "minecraft:netherite_scrap;15"
        ), obj -> obj instanceof String);
        SMITHING_UTILITIES = builder.comment("Blocks that require interaction (Anvils, Blast Furnaces, etc.)").defineListAllowEmpty(List.of("smithing_utilities"), () -> List.of(
                "minecraft:blast_furnace;smithing:5",
                "minecraft:anvil;smithing:1",
                "minecraft:chipped_anvil;smithing:1",
                "minecraft:damaged_anvil;smithing:1",
                "minecraft:smithing_table;smithing:13",
                "minecraft:stonecutter;smithing:3"
        ), obj -> obj instanceof String);

        SMITHING_CRAFTING_IRON_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_iron_tools"), () -> List.of("minecraft:iron_sword,minecraft:iron_pickaxe,minecraft:iron_axe,minecraft:iron_shovel,minecraft:iron_hoe;smithing:7;15"), obj -> obj instanceof String);
        SMITHING_CRAFTING_IRON_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_iron_armor"), () -> List.of("minecraft:iron_helmet,minecraft:iron_chestplate,minecraft:iron_leggings,minecraft:iron_boots;smithing:11;20"), obj -> obj instanceof String);
        SMITHING_CRAFTING_GOLD_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_gold_tools"), () -> List.of("minecraft:golden_sword,minecraft:golden_pickaxe,minecraft:golden_axe,minecraft:golden_shovel,minecraft:golden_hoe;smithing:3;10"), obj -> obj instanceof String);
        SMITHING_CRAFTING_GOLD_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_gold_armor"), () -> List.of("minecraft:golden_helmet,minecraft:golden_chestplate,minecraft:golden_leggings,minecraft:golden_boots;smithing:6;15"), obj -> obj instanceof String);
        SMITHING_CRAFTING_DIAMOND_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_diamond_tools"), () -> List.of("minecraft:diamond_sword,minecraft:diamond_pickaxe,minecraft:diamond_axe,minecraft:diamond_shovel,minecraft:diamond_hoe;smithing:11;25"), obj -> obj instanceof String);
        SMITHING_CRAFTING_DIAMOND_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_diamond_armor"), () -> List.of("minecraft:diamond_helmet,minecraft:diamond_chestplate,minecraft:diamond_leggings,minecraft:diamond_boots;smithing:16;35"), obj -> obj instanceof String);
        SMITHING_CRAFTING_NETHERITE_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_netherite_tools"), () -> List.of("minecraft:netherite_sword,minecraft:netherite_pickaxe,minecraft:netherite_axe,minecraft:netherite_shovel,minecraft:netherite_hoe;smithing:16;50"), obj -> obj instanceof String);
        SMITHING_CRAFTING_NETHERITE_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_netherite_armor"), () -> List.of("minecraft:netherite_helmet,minecraft:netherite_chestplate,minecraft:netherite_leggings,minecraft:netherite_boots;smithing:21;60"), obj -> obj instanceof String);
        SMITHING_CRAFTING_CUSTOM = builder.defineListAllowEmpty(List.of("smithing_crafting_custom"), () -> List.of(), obj -> obj instanceof String);

        SMITHING_IMMERSIVE_AIRCRAFT = builder.comment("Immersive Aircraft Crafting Restrictions")
                .defineListAllowEmpty(List.of("smithing_immersive_aircraft"), () -> List.of(
                        "immersive_aircraft:enhanced_propeller;smithing:10;25",
                        "immersive_aircraft:eco_engine;smithing:12;30",
                        "immersive_aircraft:nether_engine;smithing:15;40",
                        "immersive_aircraft:steel_boiler;smithing:11;25",
                        "immersive_aircraft:industrial_gears;smithing:13;30",
                        "immersive_aircraft:sturdy_pipes;smithing:10;20",
                        "immersive_aircraft:gyroscope;smithing:14;35",
                        "immersive_aircraft:gyroscope_hud;smithing:14;35",
                        "immersive_aircraft:gyroscope_dials;smithing:14;35",
                        "immersive_aircraft:hull_reinforcement;smithing:12;30",
                        "immersive_aircraft:improved_landing_gear;smithing:10;25"
                ), obj -> obj instanceof String);

        SMITHING_IMMERSIVE_MACHINERY = builder.comment("Immersive Machinery Crafting Restrictions")
                .defineListAllowEmpty(List.of("smithing_immersive_machinery"), () -> List.of(
                        "immersive_machinery:iron_drill;smithing:7;25",
                        "immersive_machinery:diamond_drill;smithing:12;35",
                        "immersive_machinery:netherite_drill;smithing:17;50"
                ), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             ARCHERY TAB
        // ==========================================
        builder.push("Archery");
        ARCHERY_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Archery Level 2").defineInRange("archeryXpBaseRequirement", 100, 1, 10000);
        ARCHERY_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("archeryXpMultiplier", 1.2, 1.0, 5.0);
        ARCHERY_XP_PER_HIT = builder.comment("Base XP gained per successful projectile hit").defineInRange("archeryXpPerHit", 5.0, 0.0, 100.0);

        ARCHERY_BOWS = builder.defineListAllowEmpty(List.of("archery_bows"), () -> List.of("minecraft:bow;archery:1"), obj -> obj instanceof String);
        ARCHERY_CROSSBOWS = builder.defineListAllowEmpty(List.of("archery_crossbows"), () -> List.of("minecraft:crossbow;archery:5"), obj -> obj instanceof String);
        ARCHERY_TRIDENTS = builder.defineListAllowEmpty(List.of("archery_tridents"), () -> List.of("minecraft:trident;archery:10"), obj -> obj instanceof String);
        ARCHERY_ARROWS = builder.defineListAllowEmpty(List.of("archery_arrows"), () -> List.of("minecraft:spectral_arrow;archery:5", "minecraft:tipped_arrow;archery:10"), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             FISHING TAB
        // ==========================================
        builder.push("Fishing");
        FISHING_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Fishing Level 2").defineInRange("fishingXpBaseRequirement", 100, 1, 10000);
        FISHING_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("fishingXpMultiplier", 1.2, 1.0, 5.0);
        FISHING_XP_PER_CATCH = builder.comment("XP gained per item successfully fished").defineInRange("fishingXpPerCatch", 15.0, 0.0, 500.0);

        FISHING_RODS = builder.defineListAllowEmpty(List.of("fishing_rods"), () -> List.of("minecraft:fishing_rod;fishing:1"), obj -> obj instanceof String);
        FISHING_OCEAN_BLOCKS = builder.defineListAllowEmpty(List.of("fishing_ocean_blocks"), () -> List.of("minecraft:sponge,minecraft:wet_sponge;fishing:5", "minecraft:sea_lantern;fishing:10", "minecraft:kelp,minecraft:dried_kelp_block;fishing:2", "minecraft:sea_pickle;fishing:3", "minecraft:conduit;fishing:15"), obj -> obj instanceof String);
        FISHING_DIVING_GEAR = builder.defineListAllowEmpty(List.of("fishing_diving_gear"), () -> List.of("minecraft:turtle_helmet;fishing:10", "minecraft:heart_of_the_sea;fishing:15", "minecraft:nautilus_shell;fishing:5"), obj -> obj instanceof String);
        FISHING_CUSTOM = builder.defineListAllowEmpty(List.of("fishing_custom_items"), () -> List.of(), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             Alchemy Tab
        // ==========================================
        builder.push("Alchemy");
        ALCHEMY_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Alchemy Level 2").defineInRange("alchemyXpBaseRequirement", 200, 1, 10000);
        ALCHEMY_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("alchemyXpMultiplier", 1.2, 1.0, 5.0);
        ALCHEMY_BREW_XP = builder.comment("The amount of Alchemy XP awarded when pulling a completed potion from a Brewing Stand.").defineInRange("alchemyBrewXp", 25.0, 0.0, 10000.0);
        ALCHEMY_DRINK_XP = builder.comment("The amount of Alchemy XP awarded when drinking a valid (non-water/base) potion.").defineInRange("alchemyDrinkXp", 15.0, 0.0, 10000.0);

        ALCHEMY_BOOK_XP = builder.comment("XP gained from shift right-clicking and consuming a standard Book").defineInRange("alchemyBookXp", 15.0, 0.0, 100000.0);
        ALCHEMY_ENCHANTED_BOOK_BASE_XP = builder.comment("Base XP gained from consuming an Enchanted Book").defineInRange("alchemyEnchantedBookBaseXp", 50.0, 0.0, 100000.0);
        ALCHEMY_ENCHANTED_BOOK_LEVEL_MULTIPLIER = builder.comment("Additional XP granted per stored enchantment level on a consumed Enchanted Book").defineInRange("alchemyEnchantedBookLevelMultiplier", 15.0, 0.0, 100000.0);

        ALCHEMY_UTILITIES = builder.comment("Blocks that require interaction (Brewing Stands, Enchanting Tables, etc)").defineListAllowEmpty(List.of("alchemy_utilities"), () -> List.of(
                "minecraft:brewing_stand;alchemy:5",
                "minecraft:grindstone;alchemy:10",
                "minecraft:enchanting_table;alchemy:15",
                "spell_engine:spell_binding;alchemy:20"
        ), obj -> obj instanceof String);

        POTION_RESTRICTIONS = builder.defineListAllowEmpty(List.of("potion_restrictions"), () -> List.of(
                "potion:minecraft:healing;vitality:1;alchemy:1",
                "potion:minecraft:strong_healing;vitality:5;alchemy:1",
                "potion:minecraft:regeneration;vitality:1;alchemy:1",
                "potion:minecraft:strong_regeneration;vitality:5;alchemy:1",
                "potion:minecraft:long_regeneration;vitality:8;alchemy:1",
                "potion:minecraft:swiftness;agility:5;alchemy:1",
                "potion:minecraft:strong_swiftness;agility:15;alchemy:1",
                "potion:minecraft:long_swiftness;agility:10;alchemy:1"
        ), obj -> obj instanceof String);

        ENCHANTMENT_RESTRICTIONS = builder.comment("Format: enchantment_id;skill:level").defineListAllowEmpty(List.of("enchantment_restrictions"), () -> List.of(
                "minecraft:protection;alchemy:5",
                "minecraft:fire_protection;alchemy:5",
                "minecraft:feather_falling;alchemy:5",
                "minecraft:blast_protection;alchemy:5",
                "minecraft:projectile_protection;alchemy:5",
                "minecraft:respiration;alchemy:5",
                "minecraft:aqua_affinity;alchemy:5",
                "minecraft:thorns;alchemy:5",
                "minecraft:depth_strider;alchemy:5",
                "minecraft:frost_walker;alchemy:5",
                "minecraft:sharpness;alchemy:6",
                "minecraft:smite;alchemy:6",
                "minecraft:bane_of_arthropods;alchemy:6",
                "minecraft:knockback;alchemy:6",
                "minecraft:fire_aspect;alchemy:6",
                "minecraft:looting;alchemy:6",
                "minecraft:efficiency;alchemy:8",
                "minecraft:silk_touch;alchemy:8",
                "minecraft:fortune;alchemy:8",
                "minecraft:power;alchemy:10",
                "minecraft:punch;alchemy:10",
                "minecraft:flame;alchemy:10",
                "minecraft:infinity;alchemy:10",
                "minecraft:luck_of_the_sea;alchemy:10",
                "minecraft:lure;alchemy:10",
                "minecraft:unbreaking;alchemy:12",
                "minecraft:mending;alchemy:12"
        ), obj -> obj instanceof String);
        builder.pop();

        // ==========================================
        //             RESTRICTIONS TAB
        // ==========================================
        builder.push("Restrictions");

        ITEM_RESTRICTIONS = builder.defineListAllowEmpty(List.of("item_restrictions"), () -> List.of(
                "minecraft:shield;defense:5;combat:5",
                "minecraft:totem_of_undying;vitality:20"
        ), obj -> obj instanceof String);

        CUSTOM_ITEM_RESTRICTIONS = builder.defineListAllowEmpty(List.of("custom_item_restrictions"), () -> List.of(), obj -> obj instanceof String);

        TIDE_ITEMS = builder.comment("Tide Item Restrictions")
                .defineListAllowEmpty(List.of("tide_items"), () -> List.of(
                        "minecraft:fishing_rod;fishing:1",
                        "tide:stone_fishing_rod;fishing:3",
                        "tide:iron_fishing_rod;fishing:6",
                        "tide:golden_fishing_rod;fishing:9",
                        "tide:crystal_fishing_rod;fishing:12",
                        "tide:diamond_fishing_rod;fishing:15",
                        "tide:midas_fishing_rod;fishing:18",
                        "tide:netherite_fishing_rod;fishing:21",
                        "tide:dragonfin_boots;defense:24",
                        "tide:starlight_bow;archery:18"
                ), obj -> obj instanceof String);

        GLIDERS_ITEMS = builder.comment("Gliders Item Restrictions")
                .defineListAllowEmpty(List.of("gliders_items"), () -> List.of(
                        "vc_gliders:paraglider_wood;agility:5",
                        "vc_gliders:paraglider_iron;agility:8",
                        "vc_gliders:paraglider_gold;agility:12",
                        "vc_gliders:paraglider_diamond;agility:13",
                        "vc_gliders:paraglider_netherite;agility:16"
                ), obj -> obj instanceof String);

        LILIS_LUCKY_LURES_ITEMS = builder.comment("Lili's Lucky Lures Item Restrictions")
                .defineListAllowEmpty(List.of("lilis_lucky_lures_items"), () -> List.of(
                        "lilis_lucky_lures:bamboo_fishing_rod;fishing:2",
                        "lilis_lucky_lures:fishing_net;fishing:5",
                        "lilis_lucky_lures:spear;combat:8",
                        "lilis_lucky_lures:hanging_frame;fishing:7",
                        "lilis_lucky_lures:fish_net;fishing:8",
                        "lilis_lucky_lures:fish_trap;fishing:10",
                        "lilis_lucky_lures:fish_trophy_frame;fishing:11",
                        "lilis_lucky_lures:anglers_hat;fishing:13"
                ), obj -> obj instanceof String);

        IMMERSIVE_MACHINERY_ITEMS = builder.comment("Immersive Machinery Item Restrictions")
                .defineListAllowEmpty(List.of("immersive_machinery_items"), () -> List.of(
                        "immersive_machinery:tunnel_digger;mining:18",
                        "immersive_machinery:bamboo_bee;agility:8",
                        "immersive_machinery:redstone_sheep;farming:10",
                        "immersive_machinery:copperfin;fishing:13"
                ), obj -> obj instanceof String);

        IMMERSIVE_AIRCRAFT_ITEMS = builder.comment("Immersive Aircraft Item Restrictions")
                .defineListAllowEmpty(List.of("immersive_aircraft_items"), () -> List.of(
                        "immersive_aircraft:biplane;agility:25",
                        "immersive_aircraft:gyrodyne;agility:17",
                        "immersive_aircraft:quadrocopter;agility:19",
                        "immersive_aircraft:airship;agility:22",
                        "immersive_aircraft:cargo_airship;agility:24",
                        "immersive_aircraft:warship;agility:28;combat:30",
                        "immersive_aircraft:bamboo_hopper;agility:16"
                ), obj -> obj instanceof String);

        SMALL_SHIPS_ITEMS = builder.comment("Small Ships Item Restrictions")
                .defineListAllowEmpty(List.of("small_ships_items"), () -> List.of(
                        "smallships:oak_cog;agility:1",
                        "smallships:spruce_cog;agility:8",
                        "smallships:birch_cog;agility:8",
                        "smallships:jungle_cog;agility:8",
                        "smallships:acacia_cog;agility:8",
                        "smallships:dark_oak_cog;agility:8",
                        "smallships:mangrove_cog;agility:8",
                        "smallships:cherry_cog;agility:8",
                        "smallships:bamboo_cog;agility:8",
                        "smallships:oak_brigg;agility:12",
                        "smallships:spruce_brigg;agility:12",
                        "smallships:birch_brigg;agility:12",
                        "smallships:jungle_brigg;agility:12",
                        "smallships:acacia_brigg;agility:12",
                        "smallships:dark_oak_brigg;agility:12",
                        "smallships:mangrove_brigg;agility:12",
                        "smallships:cherry_brigg;agility:12",
                        "smallships:bamboo_brigg;agility:12",
                        "smallships:oak_galley;agility:10",
                        "smallships:spruce_galley;agility:10",
                        "smallships:birch_galley;agility:10",
                        "smallships:jungle_galley;agility:10",
                        "smallships:acacia_galley;agility:10",
                        "smallships:dark_oak_galley;agility:10",
                        "smallships:mangrove_galley;agility:10",
                        "smallships:cherry_galley;agility:10",
                        "smallships:bamboo_galley;agility:10",
                        "smallships:oak_drakkar;agility:16",
                        "smallships:spruce_drakkar;agility:16",
                        "smallships:birch_drakkar;agility:16",
                        "smallships:jungle_drakkar;agility:16",
                        "smallships:acacia_drakkar;agility:16",
                        "smallships:dark_oak_drakkar;agility:16",
                        "smallships:mangrove_drakkar;agility:16",
                        "smallships:cherry_drakkar;agility:16",
                        "smallships:bamboo_drakkar;agility:16"
                ), obj -> obj instanceof String);

        VILLAGER_RESTRICTIONS = builder.defineListAllowEmpty(List.of("villager_restrictions"), () -> List.of(
                "minecraft:librarian;alchemy:8",
                "minecraft:cleric;alchemy:5",
                "minecraft:cartographer;vitality:5",
                "minecraft:farmer;farming:5",
                "minecraft:butcher;farming:5",
                "minecraft:shepherd;farming:5",
                "minecraft:toolsmith;mining:5",
                "minecraft:fisherman;fishing:5",
                "minecraft:armorer;smithing:5",
                "minecraft:leatherworker;smithing:3",
                "minecraft:weaponsmith;smithing:8",
                "minecraft:mason;smithing:7",
                "minecraft:fletcher;archery:5"
        ), obj -> obj instanceof String);

        builder.pop();
        SPEC = builder.build();
    }

    @SuppressWarnings("unchecked")
    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;

            for (ModConfigSpec.ConfigValue<?> entry : SPEC.entries) {
                JsonElement el = getElementAtPath(root, entry.getPath());
                if (el == null || el.isJsonNull()) continue;

                if (entry instanceof ModConfigSpec.IntValue intVal && el.isJsonPrimitive()) {
                    intVal.set(el.getAsInt());
                } else if (entry instanceof ModConfigSpec.DoubleValue doubleVal && el.isJsonPrimitive()) {
                    doubleVal.set(el.getAsDouble());
                } else if (entry instanceof ModConfigSpec.BooleanValue boolVal && el.isJsonPrimitive()) {
                    boolVal.set(el.getAsBoolean());
                } else if (entry.getDefault() instanceof String && el.isJsonPrimitive()) {
                    ((ModConfigSpec.ConfigValue<String>) entry).set(el.getAsString());
                } else if (entry.getDefault() instanceof List<?> && el.isJsonArray()) {
                    List<String> list = new ArrayList<>();
                    for (JsonElement item : el.getAsJsonArray()) {
                        list.add(item.getAsString());
                    }
                    ((ModConfigSpec.ConfigValue<List<? extends String>>) entry).set(list);
                }
            }
        } catch (Exception e) {
            System.err.println("[PlayerJournal] Failed to load playerjournal.json, using defaults:");
            e.printStackTrace();
        }
        save();
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (ModConfigSpec.ConfigValue<?> entry : SPEC.entries) {
                setElementAtPath(root, entry.getPath(), entry.get());
            }
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(root, writer);
            }
        } catch (Exception e) {
            System.err.println("[PlayerJournal] Failed to save playerjournal.json:");
            e.printStackTrace();
        }
    }

    private static JsonElement getElementAtPath(JsonObject root, List<String> path) {
        JsonObject current = root;
        for (int i = 0; i < path.size() - 1; i++) {
            String p = path.get(i);
            if (!current.has(p) || !current.get(p).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(p);
        }
        return current.get(path.get(path.size() - 1));
    }

    private static void setElementAtPath(JsonObject root, List<String> path, Object value) {
        JsonObject current = root;
        for (int i = 0; i < path.size() - 1; i++) {
            String p = path.get(i);
            if (!current.has(p) || !current.get(p).isJsonObject()) {
                current.add(p, new JsonObject());
            }
            current = current.getAsJsonObject(p);
        }
        String key = path.get(path.size() - 1);
        if (value instanceof Integer i) {
            current.addProperty(key, i);
        } else if (value instanceof Double d) {
            current.addProperty(key, d);
        } else if (value instanceof Boolean b) {
            current.addProperty(key, b);
        } else if (value instanceof String s) {
            current.addProperty(key, s);
        } else if (value instanceof List<?> list) {
            JsonArray arr = new JsonArray();
            for (Object o : list) {
                arr.add(String.valueOf(o));
            }
            current.add(key, arr);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAllItemRestrictions() {
        List<String> combined = new ArrayList<>();
        combined.addAll((List<String>) ITEM_RESTRICTIONS.get());
        combined.addAll((List<String>) WEAPON_RESTRICTIONS.get());
        combined.addAll((List<String>) CUSTOM_ITEM_RESTRICTIONS.get());
        combined.addAll((List<String>) FARMING_ITEMS.get());
        combined.addAll((List<String>) TIDE_ITEMS.get());
        combined.addAll((List<String>) GLIDERS_ITEMS.get());
        combined.addAll((List<String>) LILIS_LUCKY_LURES_ITEMS.get());
        combined.addAll((List<String>) IMMERSIVE_MACHINERY_ITEMS.get());
        combined.addAll((List<String>) IMMERSIVE_AIRCRAFT_ITEMS.get());
        combined.addAll((List<String>) SMALL_SHIPS_ITEMS.get());

        combined.addAll((List<String>) ARCHERY_BOWS.get());
        combined.addAll((List<String>) ARCHERY_CROSSBOWS.get());
        combined.addAll((List<String>) ARCHERY_TRIDENTS.get());
        combined.addAll((List<String>) ARCHERY_ARROWS.get());

        combined.addAll((List<String>) FISHING_RODS.get());
        combined.addAll((List<String>) FISHING_DIVING_GEAR.get());
        combined.addAll((List<String>) FISHING_CUSTOM.get());

        combined.addAll((List<String>) AGILITY_ITEMS.get());

        for (String restriction : (List<String>) MINING_TOOL_RESTRICTIONS.get()) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) for (String tool : parts[0].split(",")) combined.add(tool.trim() + ";" + parts[1]);
        }
        for (String restriction : (List<String>) CUSTOM_MINING_TOOLS.get()) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) for (String tool : parts[0].split(",")) combined.add(tool.trim() + ";" + parts[1]);
        }
        for (String restriction : (List<String>) FARMING_HOES.get()) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) for (String tool : parts[0].split(",")) combined.add(tool.trim() + ";" + parts[1]);
        }
        for (String restriction : (List<String>) FARMING_AXES.get()) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) for (String tool : parts[0].split(",")) combined.add(tool.trim() + ";" + parts[1]);
        }
        return combined;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getSmithingUtilities() {
        return new ArrayList<>((List<String>) SMITHING_UTILITIES.get());
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAllBlockRestrictions() {
        List<String> combined = new ArrayList<>();
        combined.addAll((List<String>) MINING_COAL.get());
        combined.addAll((List<String>) MINING_COPPER.get());
        combined.addAll((List<String>) MINING_IRON.get());
        combined.addAll((List<String>) MINING_GOLD.get());
        combined.addAll((List<String>) MINING_LAPIS.get());
        combined.addAll((List<String>) MINING_EMERALD.get());
        combined.addAll((List<String>) MINING_DIAMOND.get());
        combined.addAll((List<String>) MINING_QUARTZ.get());
        combined.addAll((List<String>) MINING_NETHERITE.get());
        combined.addAll((List<String>) MINING_STONE.get());
        combined.addAll((List<String>) MINING_DEEPSLATE.get());
        combined.addAll((List<String>) MINING_PRISMARINE.get());
        combined.addAll((List<String>) MINING_NETHER.get());
        combined.addAll((List<String>) MINING_REDSTONE.get());
        combined.addAll((List<String>) CUSTOM_MINING_BLOCKS.get());
        combined.addAll((List<String>) MINING_UTILITIES.get());
        combined.addAll((List<String>) FARMING_UTILITIES.get());
        combined.addAll((List<String>) ALCHEMY_UTILITIES.get());
        combined.addAll((List<String>) FARMING_CROPS.get());
        combined.addAll((List<String>) FISHING_OCEAN_BLOCKS.get());
        combined.addAll(getSmithingUtilities());
        return combined;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getUtilityBlockRestrictions() {
        List<String> combined = new ArrayList<>();
        combined.addAll((List<String>) MINING_UTILITIES.get());
        combined.addAll((List<String>) FARMING_UTILITIES.get());
        combined.addAll((List<String>) ALCHEMY_UTILITIES.get());
        combined.addAll(getSmithingUtilities());
        return combined;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAllCraftingRestrictions() {
        List<String> combined = new ArrayList<>();
        combined.addAll((List<String>) SMITHING_CRAFTING_IRON_TOOLS.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_IRON_ARMOR.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_GOLD_TOOLS.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_GOLD_ARMOR.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_DIAMOND_TOOLS.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_DIAMOND_ARMOR.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_NETHERITE_TOOLS.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_NETHERITE_ARMOR.get());
        combined.addAll((List<String>) SMITHING_CRAFTING_CUSTOM.get());
        combined.addAll((List<String>) SMITHING_IMMERSIVE_AIRCRAFT.get());
        combined.addAll((List<String>) SMITHING_IMMERSIVE_MACHINERY.get());
        return combined;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAllMobXpValues() {
        List<String> combined = new ArrayList<>();
        combined.addAll((List<String>) MOB_XP_VALUES.get());
        combined.addAll((List<String>) CUSTOM_MOB_XP_VALUES.get());
        return combined;
    }
}