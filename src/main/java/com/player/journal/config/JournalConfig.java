package com.player.journal.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.ArrayList;
import java.util.List;

public class JournalConfig {
    public static final ModConfigSpec SPEC;

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
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_WIZARDS_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_WIZARDS_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_ARCHERS_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_ARCHERS_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_JEWELRY;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_PALADINS_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_PALADINS_SHIELDS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_PALADINS_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_ROGUES_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMITHING_ROGUES_WEAPONS;
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
    public static final ModConfigSpec.ConfigValue<List<? extends String>> FARMERS_DELIGHT_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> VILLAGER_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> JEWELRY_RESTRICTIONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> PALADINS_PRIESTS_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> PALADINS_PRIESTS_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> PALADINS_PRIESTS_SHIELDS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ROGUES_WARRIORS_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ROGUES_WARRIORS_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARCHERS_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARCHERS_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> WIZARDS_ARMORS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> WIZARDS_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARSENAL_WEAPONS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ARTIFACTS_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> TIDE_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> GLIDERS_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LILIS_LUCKY_LURES_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> IMMERSIVE_MACHINERY_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> IMMERSIVE_AIRCRAFT_ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SMALL_SHIPS_ITEMS;
    public static final ModConfigSpec.IntValue LEVELS_PER_PAGE_REWARD;
    public static final ModConfigSpec.IntValue STARTING_TORN_PAGES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // ==========================================
        //                GENERAL SETTINGS
        // ==========================================

        builder.push("General");
        LEVELS_PER_PAGE_REWARD = builder.comment("How many skill levels are required to be rewarded with 1 Torn Page? (e.g. 5 = rewarded at level 5, 10, 15...)").defineInRange("levelsPerPageReward", 5, 1, 100);
        STARTING_TORN_PAGES = builder.comment("How many Torn Pages should a player start with when they first join?").defineInRange("startingTornPages", 5, 0, 1000);
        builder.pop();

        // ==========================================
        //                VITALITY TAB
        // ==========================================
        builder.push("Vitality");
        XP_PER_HEART_HEALED = builder.comment("XP gained per 1 full heart healed").defineInRange("xpPerHeartHealed", 3.0, 0.0, 100.0);
        XP_BASE_REQUIREMENT = builder.comment("Base XP required for Level 2").defineInRange("xpBaseRequirement", 50, 1, 10000);
        XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("xpMultiplier", 1.2, 1.0, 5.0);
        LEVELS_PER_HEALTH_BOOST = builder.comment("Vitality levels required to gain 1 extra heart").defineInRange("levelsPerHealthBoost", 5, 1, 100);
        ENV_DAMAGE_COOLDOWN = builder.comment("Cooldown (in seconds) for gaining XP from environmental damage (fall, cactus, etc.)").defineInRange("envDamageCooldown", 15, 0, 300);
        builder.pop();

        // ==========================================
        //                AGILITY TAB
        // ==========================================
        builder.push("Agility");
        AGILITY_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Agility Level 2").defineInRange("agilityXpBaseRequirement", 50, 1, 10000);
        AGILITY_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("agilityXpMultiplier", 1.3, 1.0, 5.0);
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
        COMBAT_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Combat Level 2").defineInRange("combatXpBaseRequirement", 50, 1, 10000);
        COMBAT_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("combatXpMultiplier", 1.3, 1.0, 5.0);
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

        // --- NEW CUSTOM MOB XP DROPDOWN ---
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
                //leather armor
                "minecraft:leather_helmet;defense:1",
                "minecraft:leather_chestplate;defense:1",
                "minecraft:leather_leggings;defense:1",
                "minecraft:leather_boots;defense:1",
                //chainmail armor
                "minecraft:chainmail_helmet;defense:5",
                "minecraft:chainmail_chestplate;defense:5",
                "minecraft:chainmail_leggings;defense:5",
                "minecraft:chainmail_boots;defense:5",
                //iron armor
                "minecraft:iron_helmet;defense:15;combat:10",
                "minecraft:iron_chestplate;defense:15;combat:10",
                "minecraft:iron_leggings;defense:15;combat:10",
                "minecraft:iron_boots;defense:15;combat:10",
                //gold armor
                "minecraft:golden_helmet;defense:10;combat:5",
                "minecraft:golden_chestplate;defense:10;combat:5",
                "minecraft:golden_leggings;defense:10;combat:5",
                "minecraft:golden_boots;defense:10;combat:5",
                //diamond armor
                "minecraft:diamond_helmet;defense:20;combat:15",
                "minecraft:diamond_chestplate;defense:20;combat:15",
                "minecraft:diamond_leggings;defense:20;combat:15",
                "minecraft:diamond_boots;defense:20;combat:15",
                //netherite armor
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
        MINING_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("miningXpMultiplier", 1.3, 1.0, 5.0);
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
        FARMING_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Farming Level 2").defineInRange("farmingXpBaseRequirement", 50, 1, 10000);
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
                "minecraft:beetroot_seeds;farming:3",


                //famrers delight//

                "farmersdelight:wild_carrots;farming:2;4",
                "farmersdelight:wild_potatoes;farming:2;4",
                "farmersdelight:wild_beetroots;farming:3;5",
                "farmersdelight:wild_cabbages;farming:5;5",
                "farmersdelight:wild_tomatoes;farming:6;5",
                "farmersdelight:wild_onions;farming:7;5",
                "farmersdelight:wild_rice;farming:8;5",

                "farmersdelight:cabbage_seeds;farming:5",
                "farmersdelight:tomato_seeds;farming:6"

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

        // Vanilla Gear
        SMITHING_CRAFTING_IRON_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_iron_tools"), () -> List.of("minecraft:iron_sword,minecraft:iron_pickaxe,minecraft:iron_axe,minecraft:iron_shovel,minecraft:iron_hoe;smithing:7;15"), obj -> obj instanceof String);
        SMITHING_CRAFTING_IRON_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_iron_armor"), () -> List.of("minecraft:iron_helmet,minecraft:iron_chestplate,minecraft:iron_leggings,minecraft:iron_boots;smithing:11;20"), obj -> obj instanceof String);
        SMITHING_CRAFTING_GOLD_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_gold_tools"), () -> List.of("minecraft:golden_sword,minecraft:golden_pickaxe,minecraft:golden_axe,minecraft:golden_shovel,minecraft:golden_hoe;smithing:3;10"), obj -> obj instanceof String);
        SMITHING_CRAFTING_GOLD_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_gold_armor"), () -> List.of("minecraft:golden_helmet,minecraft:golden_chestplate,minecraft:golden_leggings,minecraft:golden_boots;smithing:6;15"), obj -> obj instanceof String);
        SMITHING_CRAFTING_DIAMOND_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_diamond_tools"), () -> List.of("minecraft:diamond_sword,minecraft:diamond_pickaxe,minecraft:diamond_axe,minecraft:diamond_shovel,minecraft:diamond_hoe;smithing:11;25"), obj -> obj instanceof String);
        SMITHING_CRAFTING_DIAMOND_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_diamond_armor"), () -> List.of("minecraft:diamond_helmet,minecraft:diamond_chestplate,minecraft:diamond_leggings,minecraft:diamond_boots;smithing:16;35"), obj -> obj instanceof String);
        SMITHING_CRAFTING_NETHERITE_TOOLS = builder.defineListAllowEmpty(List.of("smithing_crafting_netherite_tools"), () -> List.of("minecraft:netherite_sword,minecraft:netherite_pickaxe,minecraft:netherite_axe,minecraft:netherite_shovel,minecraft:netherite_hoe;smithing:16;50"), obj -> obj instanceof String);
        SMITHING_CRAFTING_NETHERITE_ARMOR = builder.defineListAllowEmpty(List.of("smithing_crafting_netherite_armor"), () -> List.of("minecraft:netherite_helmet,minecraft:netherite_chestplate,minecraft:netherite_leggings,minecraft:netherite_boots;smithing:21;60"), obj -> obj instanceof String);
        SMITHING_CRAFTING_CUSTOM = builder.defineListAllowEmpty(List.of("smithing_crafting_custom"), () -> List.of(), obj -> obj instanceof String);

        // --- WIZARDS CRAFTING RESTRICTIONS ---
        SMITHING_WIZARDS_ARMORS = builder.comment("Wizards Crafting Armor Restrictions")
                .defineListAllowEmpty(List.of("smithing_wizards_armors"), () -> List.of(
                        "wizards:wizard_robe_head;smithing:1;10",
                        "wizards:wizard_robe_chest;smithing:1;10",
                        "wizards:wizard_robe_legs;smithing:1;10",
                        "wizards:wizard_robe_feet;smithing:1;10",
                        "wizards:arcane_robe_head;smithing:3;15",
                        "wizards:arcane_robe_chest;smithing:3;15",
                        "wizards:arcane_robe_legs;smithing:3;15",
                        "wizards:arcane_robe_feet;smithing:3;15",
                        "wizards:fire_robe_head;smithing:5;20",
                        "wizards:fire_robe_chest;smithing:5;20",
                        "wizards:fire_robe_legs;smithing:5;20",
                        "wizards:fire_robe_feet;smithing:5;20",
                        "wizards:frost_robe_head;smithing:7;25",
                        "wizards:frost_robe_chest;smithing:7;25",
                        "wizards:frost_robe_legs;smithing:7;25",
                        "wizards:frost_robe_feet;smithing:7;25",
                        "wizards:netherite_arcane_robe_head;smithing:9;30",
                        "wizards:netherite_arcane_robe_chest;smithing:9;30",
                        "wizards:netherite_arcane_robe_legs;smithing:9;30",
                        "wizards:netherite_arcane_robe_feet;smithing:9;30",
                        "wizards:netherite_fire_robe_head;smithing:11;35",
                        "wizards:netherite_fire_robe_chest;smithing:11;35",
                        "wizards:netherite_fire_robe_legs;smithing:11;35",
                        "wizards:netherite_fire_robe_feet;smithing:11;35",
                        "wizards:netherite_frost_robe_head;smithing:14;40",
                        "wizards:netherite_frost_robe_chest;smithing:14;40",
                        "wizards:netherite_frost_robe_legs;smithing:14;40",
                        "wizards:netherite_frost_robe_feet;smithing:14;40"
                ), obj -> obj instanceof String);

        SMITHING_WIZARDS_WEAPONS = builder.comment("Wizards Crafting Weapon Restrictions")
                .defineListAllowEmpty(List.of("smithing_wizards_weapons"), () -> List.of(
                        "wizards:wand_novice;smithing:1;10",
                        "wizards:wand_arcane;smithing:2;15",
                        "wizards:wand_fire;smithing:3;15",
                        "wizards:wand_frost;smithing:3;15",
                        "wizards:wand_netherite_arcane;smithing:4;20",
                        "wizards:wand_netherite_fire;smithing:4;20",
                        "wizards:wand_netherite_frost;smithing:4;20",
                        "wizards:staff_wizard;smithing:6;25",
                        "wizards:staff_arcane;smithing:6;25",
                        "wizards:staff_fire;smithing:6;25",
                        "wizards:staff_frost;smithing:6;25",
                        "wizards:staff_netherite_arcane;smithing:8;30",
                        "wizards:staff_netherite_fire;smithing:8;30",
                        "wizards:staff_netherite_frost;smithing:8;30",
                        "wizards:staff_ruby_fire;smithing:9;35",
                        "wizards:staff_crystal_arcane;smithing:9;35",
                        "wizards:staff_smaragdant_frost;smithing:9;35",
                        "wizards:aether_wizard_staff;smithing:9;35"
                ), obj -> obj instanceof String);

        // --- ARCHERS CRAFTING RESTRICTIONS ---
        SMITHING_ARCHERS_ARMORS = builder.comment("Archers Crafting Armor Restrictions")
                .defineListAllowEmpty(List.of("smithing_archers_armors"), () -> List.of(
                        "archers:archer_armor_head;smithing:3;15",
                        "archers:archer_armor_chest;smithing:3;15",
                        "archers:archer_armor_legs;smithing:3;15",
                        "archers:archer_armor_feet;smithing:3;15",
                        "archers:ranger_armor_head;smithing:6;20",
                        "archers:ranger_armor_chest;smithing:6;20",
                        "archers:ranger_armor_legs;smithing:6;20",
                        "archers:ranger_armor_feet;smithing:6;20",
                        "archers:netherite_ranger_armor_head;smithing:11;35",
                        "archers:netherite_ranger_armor_chest;smithing:11;35",
                        "archers:netherite_ranger_armor_legs;smithing:11;35",
                        "archers:netherite_ranger_armor_feet;smithing:11;35",
                        "archers:small_quiver;smithing:2;10",
                        "archers:medium_quiver;smithing:5;15",
                        "archers:large_quiver;smithing:9;25"
                ), obj -> obj instanceof String);

        SMITHING_ARCHERS_WEAPONS = builder.comment("Archers Crafting Weapon Restrictions")
                .defineListAllowEmpty(List.of("smithing_archers_weapons"), () -> List.of(
                        "archers:rapid_crossbow;smithing:11;25",
                        "archers:netherite_rapid_crossbow;smithing:13;35",
                        "archers:ruby_rapid_crossbow;smithing:22;50",
                        "archers:aether_rapid_crossbow;smithing:22;50",
                        "archers:heavy_crossbow;smithing:23;50",
                        "archers:netherite_heavy_crossbow;smithing:25;55",
                        "archers:ruby_heavy_crossbow;smithing:26;60",
                        "archers:aether_heavy_crossbow;smithing:26;60",
                        "archers:flint_spear;smithing:2;10",
                        "archers:iron_spear;smithing:9;20",
                        "archers:golden_spear;smithing:7;15",
                        "archers:diamond_spear;smithing:11;25",
                        "archers:netherite_spear;smithing:13;35",
                        "archers:aeternium_spear;smithing:17;45",
                        "archers:ruby_spear;smithing:17;45",
                        "archers:aether_spear;smithing:17;45"
                ), obj -> obj instanceof String);

        // --- JEWELRY CRAFTING RESTRICTIONS ---
        SMITHING_JEWELRY = builder.comment("Jewelry Crafting Restrictions")
                .defineListAllowEmpty(List.of("smithing_jewelry"), () -> List.of(
                        "jewelry:emerald_necklace;smithing:5;15",
                        "jewelry:diamond_necklace;smithing:4;15",
                        "jewelry:ruby_necklace;smithing:4;15",
                        "jewelry:topaz_necklace;smithing:4;15",
                        "jewelry:citrine_necklace;smithing:4;15",
                        "jewelry:jade_necklace;smithing:5;15",
                        "jewelry:sapphire_necklace;smithing:7;20",
                        "jewelry:tanzanite_necklace;smithing:6;20",
                        "jewelry:netherite_ruby_necklace;smithing:9;30",
                        "jewelry:netherite_topaz_necklace;smithing:9;30",
                        "jewelry:netherite_citrine_necklace;smithing:10;30",
                        "jewelry:netherite_jade_necklace;smithing:8;25",
                        "jewelry:netherite_sapphire_necklace;smithing:11;35",
                        "jewelry:netherite_tanzanite_necklace;smithing:12;35",
                        "jewelry:unique_attack_necklace;smithing:14;45",
                        "jewelry:unique_dex_necklace;smithing:16;50",
                        "jewelry:unique_tank_necklace;smithing:14;45",
                        "jewelry:unique_archer_necklace;smithing:12;40",
                        "jewelry:unique_arcane_necklace;smithing:13;45",
                        "jewelry:unique_fire_necklace;smithing:15;50",
                        "jewelry:unique_frost_necklace;smithing:16;50",
                        "jewelry:unique_healing_necklace;smithing:17;55",
                        "jewelry:unique_spell_necklace;smithing:18;55",
                        "jewelry:unique_crit_necklace;smithing:21;60",
                        "jewelry:copper_ring;smithing:1;10",
                        "jewelry:iron_ring;smithing:3;15",
                        "jewelry:gold_ring;smithing:1;10",
                        "jewelry:diamond_ring;smithing:2;15",
                        "jewelry:ruby_ring;smithing:3;15",
                        "jewelry:topaz_ring;smithing:5;20",
                        "jewelry:citrine_ring;smithing:4;20",
                        "jewelry:jade_ring;smithing:2;15",
                        "jewelry:sapphire_ring;smithing:7;25",
                        "jewelry:tanzanite_ring;smithing:6;25",
                        "jewelry:netherite_ruby_ring;smithing:11;35",
                        "jewelry:netherite_topaz_ring;smithing:9;30",
                        "jewelry:netherite_citrine_ring;smithing:11;35",
                        "jewelry:netherite_jade_ring;smithing:8;30",
                        "jewelry:netherite_sapphire_ring;smithing:8;30",
                        "jewelry:netherite_tanzanite_ring;smithing:14;45",
                        "jewelry:unique_attack_ring;smithing:21;60",
                        "jewelry:unique_dex_ring;smithing:19;55",
                        "jewelry:unique_tank_ring;smithing:21;60",
                        "jewelry:unique_archer_ring;smithing:16;50",
                        "jewelry:unique_arcane_ring;smithing:15;50",
                        "jewelry:unique_fire_ring;smithing:17;55",
                        "jewelry:unique_frost_ring;smithing:18;55",
                        "jewelry:unique_healing_ring;smithing:19;55",
                        "jewelry:unique_spell_ring;smithing:20;60",
                        "jewelry:unique_crit_ring;smithing:24;70"
                ), obj -> obj instanceof String);

        // --- PALADINS & PRIESTS CRAFTING RESTRICTIONS ---
        SMITHING_PALADINS_ARMORS = builder.comment("Paladins & Priests Crafting Armor Restrictions")
                .defineListAllowEmpty(List.of("smithing_paladins_armors"), () -> List.of(
                        "paladins:paladin_armor_head;smithing:12;30",
                        "paladins:paladin_armor_chest;smithing:12;30",
                        "paladins:paladin_armor_legs;smithing:12;30",
                        "paladins:paladin_armor_feet;smithing:12;30",
                        "paladins:crusader_armor_head;smithing:19;45",
                        "paladins:crusader_armor_chest;smithing:19;45",
                        "paladins:crusader_armor_legs;smithing:19;45",
                        "paladins:crusader_armor_feet;smithing:19;45",
                        "paladins:netherite_crusader_armor_head;smithing:22;55",
                        "paladins:netherite_crusader_armor_chest;smithing:22;55",
                        "paladins:netherite_crusader_armor_legs;smithing:22;55",
                        "paladins:netherite_crusader_armor_feet;smithing:22;55",
                        "paladins:priest_robe_head;smithing:1;10",
                        "paladins:priest_robe_chest;smithing:1;10",
                        "paladins:priest_robe_legs;smithing:1;10",
                        "paladins:priest_robe_feet;smithing:1;10",
                        "paladins:prior_robe_head;smithing:4;15",
                        "paladins:prior_robe_chest;smithing:4;15",
                        "paladins:prior_robe_legs;smithing:4;15",
                        "paladins:prior_robe_feet;smithing:4;15",
                        "paladins:netherite_prior_robe_head;smithing:17;40",
                        "paladins:netherite_prior_robe_chest;smithing:17;40",
                        "paladins:netherite_prior_robe_legs;smithing:17;40",
                        "paladins:netherite_prior_robe_feet;smithing:17;40"
                ), obj -> obj instanceof String);

        SMITHING_PALADINS_SHIELDS = builder.comment("Paladins & Priests Crafting Shield Restrictions")
                .defineListAllowEmpty(List.of("smithing_paladins_shields"), () -> List.of(
                        "paladins:iron_kite_shield;smithing:7;20",
                        "paladins:golden_kite_shield;smithing:5;15",
                        "paladins:diamond_kite_shield;smithing:16;35",
                        "paladins:netherite_kite_shield;smithing:19;45",
                        "paladins:aeternium_kite_shield;smithing:21;50",
                        "paladins:ruby_kite_shield;smithing:21;50",
                        "paladins:aether_kite_shield;smithing:21;50"
                ), obj -> obj instanceof String);

        SMITHING_PALADINS_WEAPONS = builder.comment("Paladins & Priests Crafting Weapon Restrictions")
                .defineListAllowEmpty(List.of("smithing_paladins_weapons"), () -> List.of(
                        "paladins:stone_claymore;smithing:7;15",
                        "paladins:iron_claymore;smithing:10;25",
                        "paladins:golden_claymore;smithing:6;15",
                        "paladins:diamond_claymore;smithing:14;35",
                        "paladins:netherite_claymore;smithing:20;45",
                        "paladins:aeternium_claymore;smithing:24;55",
                        "paladins:ruby_claymore;smithing:24;55",
                        "paladins:aether_claymore;smithing:24;55",
                        "paladins:wooden_great_hammer;smithing:1;10",
                        "paladins:stone_great_hammer;smithing:6;20",
                        "paladins:iron_great_hammer;smithing:12;30",
                        "paladins:golden_great_hammer;smithing:5;15",
                        "paladins:diamond_great_hammer;smithing:20;50",
                        "paladins:netherite_great_hammer;smithing:25;60",
                        "paladins:aeternium_great_hammer;smithing:28;70",
                        "paladins:ruby_great_hammer;smithing:28;70",
                        "paladins:aether_great_hammer;smithing:28;70",
                        "paladins:iron_mace;smithing:12;30",
                        "paladins:golden_mace;smithing:10;25",
                        "paladins:diamond_mace;smithing:15;40",
                        "paladins:netherite_mace;smithing:19;50",
                        "paladins:aeternium_mace;smithing:22;55",
                        "paladins:ruby_mace;smithing:22;55",
                        "paladins:aether_mace;smithing:22;55"
                ), obj -> obj instanceof String);

        // --- ROGUES & WARRIORS CRAFTING RESTRICTIONS ---
        SMITHING_ROGUES_ARMORS = builder.comment("Rogues & Warriors Crafting Armor Restrictions")
                .defineListAllowEmpty(List.of("smithing_rogues_armors"), () -> List.of(
                        "rogues:rogue_armor_head;smithing:2;10",
                        "rogues:rogue_armor_chest;smithing:2;10",
                        "rogues:rogue_armor_legs;smithing:2;10",
                        "rogues:rogue_armor_feet;smithing:2;10",
                        "rogues:warrior_armor_head;smithing:4;15",
                        "rogues:warrior_armor_chest;smithing:4;15",
                        "rogues:warrior_armor_legs;smithing:4;15",
                        "rogues:warrior_armor_feet;smithing:4;15",
                        "rogues:assassin_armor_head;smithing:8;25",
                        "rogues:assassin_armor_chest;smithing:8;25",
                        "rogues:assassin_armor_legs;smithing:8;25",
                        "rogues:assassin_armor_feet;smithing:8;25",
                        "rogues:netherite_assassin_armor_head;smithing:13;35",
                        "rogues:netherite_assassin_armor_chest;smithing:13;35",
                        "rogues:netherite_assassin_armor_legs;smithing:13;35",
                        "rogues:netherite_assassin_armor_feet;smithing:13;35",
                        "rogues:berserker_armor_head;smithing:14;40",
                        "rogues:berserker_armor_chest;smithing:14;40",
                        "rogues:berserker_armor_legs;smithing:14;40",
                        "rogues:berserker_armor_feet;smithing:14;40",
                        "rogues:netherite_berserker_armor_head;smithing:17;45",
                        "rogues:netherite_berserker_armor_chest;smithing:17;45",
                        "rogues:netherite_berserker_armor_legs;smithing:17;45",
                        "rogues:netherite_berserker_armor_feet;smithing:17;45"
                ), obj -> obj instanceof String);

        SMITHING_ROGUES_WEAPONS = builder.comment("Rogues & Warriors Crafting Weapon Restrictions")
                .defineListAllowEmpty(List.of("smithing_rogues_weapons"), () -> List.of(
                        "rogues:flint_dagger;smithing:1;5",
                        "rogues:iron_dagger;smithing:2;10",
                        "rogues:golden_dagger;smithing:1;10",
                        "rogues:diamond_dagger;smithing:5;20",
                        "rogues:netherite_dagger;smithing:8;30",
                        "rogues:aeternium_dagger;smithing:13;40",
                        "rogues:ruby_dagger;smithing:13;40",
                        "rogues:aether_dagger;smithing:13;40",
                        "rogues:stone_double_axe;smithing:4;15",
                        "rogues:iron_double_axe;smithing:12;30",
                        "rogues:golden_double_axe;smithing:6;20",
                        "rogues:diamond_double_axe;smithing:16;40",
                        "rogues:netherite_double_axe;smithing:22;55",
                        "rogues:aeternium_double_axe;smithing:24;60",
                        "rogues:ruby_double_axe;smithing:24;60",
                        "rogues:aether_double_axe;smithing:24;60",
                        "rogues:iron_glaive;smithing:13;35",
                        "rogues:golden_glaive;smithing:5;20",
                        "rogues:diamond_glaive;smithing:17;45",
                        "rogues:netherite_glaive;smithing:20;50",
                        "rogues:aeternium_glaive;smithing:22;55",
                        "rogues:ruby_glaive;smithing:22;55",
                        "rogues:aether_glaive;smithing:22;55",
                        "rogues:iron_sickle;smithing:6;20",
                        "rogues:golden_sickle;smithing:2;15",
                        "rogues:diamond_sickle;smithing:9;30",
                        "rogues:netherite_sickle;smithing:14;40",
                        "rogues:aeternium_sickle;smithing:16;45",
                        "rogues:ruby_sickle;smithing:16;45",
                        "rogues:aether_sickle;smithing:16;45"
                ), obj -> obj instanceof String);

        // --- IMMERSIVE CRAFTING RESTRICTIONS ---
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
        ARCHERY_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Archery Level 2").defineInRange("archeryXpBaseRequirement", 50, 1, 10000);
        ARCHERY_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("archeryXpMultiplier", 1.3, 1.0, 5.0);
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
        FISHING_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Fishing Level 2").defineInRange("fishingXpBaseRequirement", 50, 1, 10000);
        FISHING_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("fishingXpMultiplier", 1.3, 1.0, 5.0);
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
        ALCHEMY_XP_BASE_REQUIREMENT = builder.comment("Base XP required for Alchemy Level 2").defineInRange("alchemyXpBaseRequirement", 50, 1, 10000);
        ALCHEMY_XP_MULTIPLIER = builder.comment("Multiplier for subsequent levels").defineInRange("alchemyXpMultiplier", 1.3, 1.0, 5.0);
        ALCHEMY_BREW_XP = builder.comment("The amount of Alchemy XP awarded when pulling a completed potion from a Brewing Stand.").defineInRange("alchemyBrewXp", 25.0, 0.0, 10000.0);
        ALCHEMY_DRINK_XP = builder.comment("The amount of Alchemy XP awarded when drinking a valid (non-water/base) potion.").defineInRange("alchemyDrinkXp", 15.0, 0.0, 10000.0);

        // --- NEW BOOK XP CONFIGS ---
        ALCHEMY_BOOK_XP = builder.comment("XP gained from shift right-clicking and consuming a standard Book").defineInRange("alchemyBookXp", 15.0, 0.0, 100000.0);
        ALCHEMY_ENCHANTED_BOOK_BASE_XP = builder.comment("Base XP gained from consuming an Enchanted Book").defineInRange("alchemyEnchantedBookBaseXp", 50.0, 0.0, 100000.0);
        ALCHEMY_ENCHANTED_BOOK_LEVEL_MULTIPLIER = builder.comment("Additional XP granted per stored enchantment level on a consumed Enchanted Book").defineInRange("alchemyEnchantedBookLevelMultiplier", 15.0, 0.0, 100000.0);
        // ---------------------------

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
                //Armors
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
                //Weapons
                "minecraft:sharpness;alchemy:6",
                "minecraft:smite;alchemy:6",
                "minecraft:bane_of_arthropods;alchemy:6",
                "minecraft:knockback;alchemy:6",
                "minecraft:fire_aspect;alchemy:6",
                "minecraft:looting;alchemy:6",
                //Tools
                "minecraft:efficiency;alchemy:8",
                "minecraft:silk_touch;alchemy:8",
                "minecraft:fortune;alchemy:8",
                //Bows
                "minecraft:power;alchemy:10",
                "minecraft:punch;alchemy:10",
                "minecraft:flame;alchemy:10",
                "minecraft:infinity;alchemy:10",
                //Rods
                "minecraft:luck_of_the_sea;alchemy:10",
                "minecraft:lure;alchemy:10",
                //Misc
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

        // --- NEW FARMER's DELIGHT DROPDOWN ---
        FARMERS_DELIGHT_RESTRICTIONS = builder.comment("Farmer's Delight specific item restrictions")
                .defineListAllowEmpty(List.of("farmers_delight_restrictions"), () -> List.of(
                        "farmersdelight:skillet;farming:5",
                        "farmersdelight:cooking_pot;farming:5" ,
                        "farmersdelight:cutting_board;farming:5" ,
                        "farmersdelight:stove;farming:5" ,
                        "farmersdelight:flint_knife;farming:2;combat:2",
                        "farmersdelight:iron_knife;farming:5;combat:5" ,
                        "farmersdelight:golden_knife;farming:5;combat:5" ,
                        "farmersdelight:diamond_knife;farming:5;combat:5" ,
                        "farmersdelight:netherite_knife;farming:5;combat:5"
                ), obj -> obj instanceof String);

// --- NEW JEWELRY DROPDOWN ---
        JEWELRY_RESTRICTIONS = builder.comment("Jewelry (RPG Series) specific item restrictions")
                .defineListAllowEmpty(List.of("jewelry_restrictions"), () -> List.of(

                        //necklaces
                        "jewelry:diamond_necklace;agility:5",
                        "jewelry:ruby_necklace;combat:5",
                        "jewelry:topaz_necklace;vitality:5",
                        "jewelry:citrine_necklace;vitality:6",
                        "jewelry:jade_necklace;archery:6",
                        "jewelry:sapphire_necklace;vitality:10",
                        "jewelry:tanzanite_necklace;vitality:8",
                        "jewelry:netherite_ruby_necklace;combat:12",
                        "jewelry:netherite_topaz_necklace;vitality:12",
                        "jewelry:netherite_citrine_necklace;vitality:13",
                        "jewelry:netherite_jade_necklace;archery:10",
                        "jewelry:netherite_sapphire_necklace;vitality:15",
                        "jewelry:netherite_tanzanite_necklace;vitality:16",
                        "jewelry:unique_attack_necklace;combat:18",
                        "jewelry:unique_dex_necklace;agility:15;combat:20",
                        "jewelry:unique_tank_necklace;vitality:18",
                        "jewelry:unique_archer_necklace;agility:15;archery:15",
                        "jewelry:unique_arcane_necklace;vitality:17",
                        "jewelry:unique_fire_necklace;vitality:19",
                        "jewelry:unique_frost_necklace;vitality:20",
                        "jewelry:unique_healing_necklace;vitality:21",
                        "jewelry:unique_spell_necklace;vitality:22",
                        "jewelry:unique_crit_necklace;combat:25",
                        //rings
                        "jewelry:copper_ring;vitality:3",
                        "jewelry:iron_ring;vitality:5",
                        "jewelry:gold_ring;vitality:1",
                        "jewelry:diamond_ring;agility:3;combat:3",
                        "jewelry:ruby_ring;combat:5",
                        "jewelry:topaz_ring;vitality:8",
                        "jewelry:citrine_ring;vitality:6",
                        "jewelry:jade_ring;archery:3",
                        "jewelry:sapphire_ring;vitality:10",
                        "jewelry:tanzanite_ring;vitality:8",
                        "jewelry:netherite_ruby_ring;combat:15",
                        "jewelry:netherite_topaz_ring;vitality:12",
                        "jewelry:netherite_citrine_ring;vitality:15",
                        "jewelry:netherite_jade_ring;archery:10",
                        "jewelry:netherite_sapphire_ring;vitality:10",
                        "jewelry:netherite_tanzanite_ring;vitality:18",
                        "jewelry:unique_attack_ring;combat:25",
                        "jewelry:unique_dex_ring;agility:15;combat:23",
                        "jewelry:unique_tank_ring;vitality:25",
                        "jewelry:unique_archer_ring;archery:20",
                        "jewelry:unique_arcane_ring;vitality:19",
                        "jewelry:unique_fire_ring;vitality:21",
                        "jewelry:unique_frost_ring;vitality:22",
                        "jewelry:unique_healing_ring;vitality:23",
                        "jewelry:unique_spell_ring;vitality:24",
                        "jewelry:unique_crit_ring;combat:28"
                ), obj -> obj instanceof String);

        // --- NEW PALADINS & PRIESTS DROPDOWN ---
        PALADINS_PRIESTS_ARMORS = builder.comment("Paladins & Priests Armor Restrictions")
                .defineListAllowEmpty(List.of("paladins_priests_armors"), () -> List.of(

                        //paladin armor
                        "paladins:paladin_armor_head;defense:16",
                        "paladins:paladin_armor_chest;defense:16",
                        "paladins:paladin_armor_legs;defense:16",
                        "paladins:paladin_armor_feet;defense:16",
                        //crusader armor
                        "paladins:crusader_armor_head;defense:23",
                        "paladins:crusader_armor_chest;defense:23",
                        "paladins:crusader_armor_legs;defense:23",
                        "paladins:crusader_armor_feet;defense:23",
                        //netherite crusader armor
                        "paladins:netherite_crusader_armor_head;defense:26",
                        "paladins:netherite_crusader_armor_chest;defense:26",
                        "paladins:netherite_crusader_armor_legs;defense:26",
                        "paladins:netherite_crusader_armor_feet;defense:26",
                        //priest armor
                        "paladins:priest_robe_head;defense:1;vitality:3",
                        "paladins:priest_robe_chest;defense:1;vitality:3",
                        "paladins:priest_robe_legs;defense:1;vitality:3",
                        "paladins:priest_robe_feet;defense:1;vitality:3",
                        //prior armor
                        "paladins:prior_robe_head;defense:6;vitality:5",
                        "paladins:prior_robe_chest;defense:6;vitality:5",
                        "paladins:prior_robe_legs;defense:6;vitality:5",
                        "paladins:prior_robe_feet;defense:6;vitality:5",
                        //netherite prior armor
                        "paladins:netherite_prior_robe_head;defense:21;vitality:8",
                        "paladins:netherite_prior_robe_chest;defense:21;vitality:8",
                        "paladins:netherite_prior_robe_legs;defense:21;vitality:8",
                        "paladins:netherite_prior_robe_feet;defense:21;vitality:8"
                ), obj -> obj instanceof String);

        PALADINS_PRIESTS_WEAPONS = builder.comment("Paladins & Priests Weapon Restrictions")
                .defineListAllowEmpty(List.of("paladins_priests_weapons"), () -> List.of(

                        //claymores
                        "paladins:stone_claymore;combat:10",
                        "paladins:iron_claymore;combat:14",
                        "paladins:golden_claymore;combat:8",
                        "paladins:diamond_claymore;combat:18",
                        "paladins:netherite_claymore;combat:24",
                        "paladins:aeternium_claymore;combat:28",
                        "paladins:ruby_claymore;combat:28",
                        "paladins:aether_claymore;combat:28",
                        //hammers
                        "paladins:wooden_great_hammer;combat:4",
                        "paladins:stone_great_hammer;combat:9",
                        "paladins:iron_great_hammer;combat:16",
                        "paladins:golden_great_hammer;combat:7",
                        "paladins:diamond_great_hammer;combat:24",
                        "paladins:netherite_great_hammer;combat:29",
                        "paladins:aeternium_great_hammer;combat:32",
                        "paladins:ruby_great_hammer;combat:32",
                        "paladins:aether_great_hammer;combat:32",
                        //maces
                        "paladins:iron_mace;combat:16",
                        "paladins:golden_mace;combat:13",
                        "paladins:diamond_mace;combat:19",
                        "paladins:netherite_mace;combat:23",
                        "paladins:aeternium_mace;combat:26",
                        "paladins:ruby_mace;combat:26",
                        "paladins:aether_mace;combat:26"
                ), obj -> obj instanceof String);

        PALADINS_PRIESTS_SHIELDS = builder.comment("Paladins & Priests Shield Restrictions")
                .defineListAllowEmpty(List.of("paladins_priests_shields"), () -> List.of(
                        "paladins:iron_kite_shield;defense:10;combat:8",
                        "paladins:diamond_kite_shield;defense:20;combat:12",
                        "paladins:netherite_kite_shield;defense:23;combat:16",
                        "paladins:aeternium_kite_shield;defense:25;combat:20",
                        "paladins:ruby_kite_shield;defense:25;combat:20",
                        "paladins:aether_kite_shield;defense:25;combat:20"
                ), obj -> obj instanceof String);

        // --- NEW ROGUES & WARRIORS DROPDOWN ---
        ROGUES_WARRIORS_ARMORS = builder.comment("Rogues and Warriors Armor Restrictions")
                .defineListAllowEmpty(List.of("rogues_warriors_armors"), () -> List.of(
                        "rogues:rogue_armor_head;defense:2;agility:5",
                        "rogues:rogue_armor_chest;defense:4;agility:5",
                        "rogues:rogue_armor_legs;defense:4;agility:5",
                        "rogues:rogue_armor_feet;defense:4;agility:5",

                        "rogues:warrior_armor_head;defense:6;combat:5",
                        "rogues:warrior_armor_chest;defense:6;combat:5",
                        "rogues:warrior_armor_legs;defense:6;combat:5",
                        "rogues:warrior_armor_feet;defense:6;combat:5",

                        "rogues:assassin_armor_head;;defense:11;agility:8",
                        "rogues:assassin_armor_chest;defense:11;agility:8",
                        "rogues:assassin_armor_legs;defense:11;agility:8",
                        "rogues:assassin_armor_feet;defense:11;agility:8",

                        "rogues:netherite_assassin_armor_head;defense:16;agility:12;combat:10",
                        "rogues:netherite_assassin_armor_chest;defense:16;agility:12;combat:10",
                        "rogues:netherite_assassin_armor_legs;defense:16;agility:12;combat:10",
                        "rogues:netherite_assassin_armor_feet;defense:16;agility:12;combat:10",

                        "rogues:berserker_armor_head;defense:18;combat:14",
                        "rogues:berserker_armor_chest;defense:18;combat:14",
                        "rogues:berserker_armor_legs;;defense:18;combat:14",
                        "rogues:berserker_armor_feet;defense:18;combat:14",

                        "rogues:netherite_berserker_armor_head;defense:21;agility:15;combat:16",
                        "rogues:netherite_berserker_armor_chest;defense:21;agility:15;combat:16",
                        "rogues:netherite_berserker_armor_legs;defense:21;agility:15;combat:16",
                        "rogues:netherite_berserker_armor_feet;defense:21;agility:15;combat:16"
                ), obj -> obj instanceof String);

        ROGUES_WARRIORS_WEAPONS = builder.comment("Rogues and Warriors Weapon Restrictions")
                .defineListAllowEmpty(List.of("rogues_warriors_weapons"), () -> List.of(
                        "rogues:flint_dagger;combat:2",
                        "rogues:iron_dagger;combat:4",
                        "rogues:golden_dagger;combat:3",
                        "rogues:diamond_dagger;combat:7",
                        "rogues:netherite_dagger;combat:11",
                        "rogues:aeternium_dagger;combat:16",
                        "rogues:ruby_dagger;combat:16",
                        "rogues:aether_dagger;combat:16",

                        "rogues:stone_double_axe;combat:6",
                        "rogues:iron_double_axe;combat:15",
                        "rogues:golden_double_axe;combat:8",
                        "rogues:diamond_double_axe;combat:20",
                        "rogues:netherite_double_axe;combat:26",
                        "rogues:aeternium_double_axe;combat:28",
                        "rogues:ruby_double_axe;combat:28",
                        "rogues:aether_double_axe;combat:28",

                        "rogues:iron_glaive;combat:17",
                        "rogues:golden_glaive;combat:7",
                        "rogues:diamond_glaive;combat:21",
                        "rogues:netherite_glaive;combat:24",
                        "rogues:aeternium_glaive;combat:26",
                        "rogues:ruby_glaive;combat:26",
                        "rogues:aether_glaive;combat:26",

                        "rogues:iron_sickle;combat:8",
                        "rogues:golden_sickle;combat:4",
                        "rogues:diamond_sickle;combat:11",
                        "rogues:netherite_sickle;combat:17",
                        "rogues:aeternium_sickle;combat:20",
                        "rogues:ruby_sickle;combat:20",
                        "rogues:aether_sickle;combat:20"
                ), obj -> obj instanceof String);

        // --- NEW ARCHERS DROPDOWN ---
        ARCHERS_ARMORS = builder.comment("Archers Armor Restrictions")
                .defineListAllowEmpty(List.of("archers_armors"), () -> List.of(
                        "archers:archer_armor_head;defense:5;archery:5",
                        "archers:archer_armor_chest;defense:5;archery:5",
                        "archers:archer_armor_legs;defense:5;archery:5",
                        "archers:archer_armor_feet;defense:5;archery:5",

                        "archers:ranger_armor_head;defense:8;archery:8",
                        "archers:ranger_armor_chest;defense:8;archery:8",
                        "archers:ranger_armor_legs;defense:8;archery:8",
                        "archers:ranger_armor_feet;defense:8;archery:8",

                        "archers:netherite_ranger_armor_head;defense:12;archery:15",
                        "archers:netherite_ranger_armor_chest;defense:12;archery:15",
                        "archers:netherite_ranger_armor_legs;defense:12;archery:15",
                        "archers:netherite_ranger_armor_feet;defense:12;archery:15",

                        "archers:small_quiver;archery:4",
                        "archers:medium_quiver;archery:8",
                        "archers:large_quiver;archery:12"
                ), obj -> obj instanceof String);

        ARCHERS_WEAPONS = builder.comment("Archers Weapon Restrictions")
                .defineListAllowEmpty(List.of("archers_weapons"), () -> List.of(
                        "archers:rapid_crossbow;archery:14",
                        "archers:netherite_rapid_crossbow;archery:16",
                        "archers:ruby_rapid_crossbow;archery:26",
                        "archers:aether_rapid_crossbow;archery:26",
                        "archers:heavy_crossbow;archery:27",
                        "archers:netherite_heavy_crossbow;archery:29",
                        "archers:ruby_heavy_crossbow;archery:31",
                        "archers:aether_heavy_crossbow;archery:31",

                        "archers:flint_spear;combat:5;archery:3",
                        "archers:iron_spear;combat:13;archery:8",
                        "archers:golden_spear;combat:10;archery:6",
                        "archers:diamond_spear;combat:15;archery:10",
                        "archers:netherite_spear;combat:17;archery:12",
                        "archers:aeternium_spear;combat:21;archery:15",
                        "archers:ruby_spear;combat:21;archery:15",
                        "archers:aether_spear;combat:21;archery:15"

                ), obj -> obj instanceof String);

        // --- NEW WIZARDS DROPDOWN ---
        WIZARDS_ARMORS = builder.comment("Wizards Armor Restrictions")
                .defineListAllowEmpty(List.of("wizards_armors"), () -> List.of(
                        "wizards:wizard_robe_head;defense:3;alchemy:4",
                        "wizards:wizard_robe_chest;defense:3;alchemy:4",
                        "wizards:wizard_robe_legs;defense:3;alchemy:4",
                        "wizards:wizard_robe_feet;defense:3;alchemy:4",

                        "wizards:arcane_robe_head;defense:4;alchemy:6",
                        "wizards:arcane_robe_chest;defense:4;alchemy:6",
                        "wizards:arcane_robe_legs;defense:4;alchemy:6",
                        "wizards:arcane_robe_feet;defense:4;alchemy:6",

                        "wizards:fire_robe_head;defense:5;alchemy:7",
                        "wizards:fire_robe_chest;defense:5;alchemy:7",
                        "wizards:fire_robe_legs;defense:5;alchemy:7",
                        "wizards:fire_robe_feet;defense:5;alchemy:7",

                        "wizards:frost_robe_head;defense:5;alchemy:8",
                        "wizards:frost_robe_chest;defense:5;alchemy:8",
                        "wizards:frost_robe_legs;defense:5;alchemy:8",
                        "wizards:frost_robe_feet;defense:5;alchemy:8",

                        "wizards:netherite_arcane_robe_head;defense:8;alchemy:12",
                        "wizards:netherite_arcane_robe_chest;defense:8;alchemy:12",
                        "wizards:netherite_arcane_robe_legs;defense:8;alchemy:12",
                        "wizards:netherite_arcane_robe_feet;defense:8;alchemy:12",

                        "wizards:netherite_fire_robe_head;defense:9;alchemy:13",
                        "wizards:netherite_fire_robe_chest;defense:9alchemy:13",
                        "wizards:netherite_fire_robe_legs;defense:9;alchemy:13",
                        "wizards:netherite_fire_robe_feet;defense:9;alchemy:13",

                        "wizards:netherite_frost_robe_head;defense:9;alchemy:14",
                        "wizards:netherite_frost_robe_chest;defense:9;alchemy:14",
                        "wizards:netherite_frost_robe_legs;defense:9;alchemy:14",
                        "wizards:netherite_frost_robe_feet;defense:9;alchemy:14"
                ), obj -> obj instanceof String);

        WIZARDS_WEAPONS = builder.comment("Wizards Weapon Restrictions")
                .defineListAllowEmpty(List.of("wizards_weapons"), () -> List.of(
                        "wizards:wand_novice;combat:2;alchemy:2",
                        "wizards:wand_arcane;combat:4;alchemy;5",
                        "wizards:wand_fire;combat:5;alchemy:6",
                        "wizards:wand_frost;combat:5;alchemy:7",
                        "wizards:wand_netherite_arcane;combat:6;alchemy:10",
                        "wizards:wand_netherite_fire;combat:6;alchemy:10",
                        "wizards:wand_netherite_frost;combat:6;alchemy:10",

                        "wizards:staff_wizard;combat:8;alchemy:12",
                        "wizards:staff_arcane;combat:8;alchemy:12",
                        "wizards:staff_fire;combat:8;alchemy:12",
                        "wizards:staff_frost;combat:8;alchemy:12",
                        "wizards:staff_netherite_arcane;combat:10;alchemy:14",
                        "wizards:staff_netherite_fire;combat:10;alchemy:14",
                        "wizards:staff_netherite_frost;combat:10;alchemy:14",
                        "wizards:staff_ruby_fire;combat:13;alchemy:16",
                        "wizards:staff_crystal_arcane;combat:13;alchemy:16",
                        "wizards:staff_smaragdant_frost;combat:13;alchemy:16",
                        "wizards:aether_wizard_staff;combat:13;alchemy:16"
                ), obj -> obj instanceof String);
        // --- NEW ARSENAL DROPDOWN ---
        ARSENAL_WEAPONS = builder.comment("Arsenal Weapon Restrictions")
                .defineListAllowEmpty(List.of("arsenal_weapons"), () -> List.of(
                        "arsenal:unique_claymore_1;combat:28",
                        "arsenal:unique_claymore_2;combat:28",
                        "arsenal:unique_claymore_sw;combat:28",

                        "arsenal:unique_staff_damage_1;combat:9;alchemy:17",
                        "arsenal:unique_staff_damage_2;combat:9;alchemy:17",
                        "arsenal:unique_staff_damage_3;combat:9;alchemy:17",
                        "arsenal:unique_staff_damage_4;combat:9;alchemy:17",
                        "arsenal:unique_staff_damage_5;combat:9;alchemy:17",
                        "arsenal:unique_staff_damage_6;combat:9;alchemy:17",
                        "arsenal:unique_staff_damage_sw;combat:9;alchemy:17",
                        "arsenal:unique_staff_heal_1;vitality:10;alchemy:20",
                        "arsenal:unique_staff_heal_2;vitality:10;alchemy:20",
                        "arsenal:unique_staff_heal_sw;vitality:10;alchemy:20",

                        "arsenal:unique_spear_1;combat:24;archery:20",
                        "arsenal:unique_spear_2;combat:24;archery:20",
                        "arsenal:unique_spear_sw;combat:24;archery:20",

                        "arsenal:unique_dagger_1;combat:7",
                        "arsenal:unique_dagger_2;combat:7",
                        "arsenal:unique_dagger_sw;combat:7",

                        "arsenal:unique_sickle_1;combat:9",
                        "arsenal:unique_sickle_2;combat:9",
                        "arsenal:unique_sickle_sw;combat:9",

                        "arsenal:unique_longsword_sw;combat:18",

                        "arsenal:unique_double_axe_1;combat:22",
                        "arsenal:unique_double_axe_2;combat:22",
                        "arsenal:unique_double_axe_sw;combat:22",

                        "arsenal:unique_glaive_1;combat:16",
                        "arsenal:unique_glaive_2;combat:16",
                        "arsenal:unique_glaive_sw;combat:16",

                        "arsenal:unique_hammer_1;combat:32",
                        "arsenal:unique_hammer_2;combat:32",
                        "arsenal:unique_hammer_sw;combat:32",

                        "arsenal:unique_mace_1;combat:18",
                        "arsenal:unique_mace_2;combat:18",
                        "arsenal:unique_mace_sw;combat:18",

                        "arsenal:unique_longbow_1;archery:26",
                        "arsenal:unique_longbow_2;archery:26",
                        "arsenal:unique_longbow_sw;archery:26",

                        "arsenal:unique_heavy_crossbow_1;archery:35",
                        "arsenal:unique_heavy_crossbow_2;archery:35",
                        "arsenal:unique_heavy_crossbow_sw;archery:35",

                        "arsenal:unique_shield_1;defense:22",
                        "arsenal:unique_shield_2;defense:22",
                        "arsenal:unique_shield_sw;defense:22"
                ), obj -> obj instanceof String);
        // --- NEW ARTIFACTS DROPDOWN ---
        ARTIFACTS_ITEMS = builder.comment("Artifacts Item Restrictions")
                .defineListAllowEmpty(List.of("artifacts_items"), () -> List.of(
                        "artifacts:umbrella;agility:18",
                        "artifacts:everlasting_beef;vitality:10",
                        "artifacts:eternal_steak;vitality:15",
                        "artifacts:plastic_drinking_hat;vitality:8;agility:8",
                        "artifacts:novelty_drinking_hat;vitality:10;agility:10",
                        "artifacts:snorkel;agility:10;fishing:10",
                        "artifacts:night_vision_goggles;mining:15",
                        "artifacts:villager_hat;farming:15",
                        "artifacts:superstitious_hat;combat:16",
                        "artifacts:cowboy_hat;agility:16",
                        "artifacts:anglers_hat;fishing:20",
                        "artifacts:lucky_scarf;mining:20",
                        "artifacts:scarf_of_invisibility;vitality:20;agility:20;defense:20",
                        "artifacts:cross_necklace;combat:34",
                        "artifacts:panic_necklace;agility:30",
                        "artifacts:shock_pendant;combat:33",
                        "artifacts:flame_pendant;combat:30",
                        "artifacts:thorn_pendant;combat:28",
                        "artifacts:charm_of_sinking;agility:25",
                        "artifacts:charm_of_shrinking;agility:15",
                        "artifacts:cloud_in_a_bottle;agility:35",
                        "artifacts:obsidian_skull;vitality:25;combat:25",
                        "artifacts:antidote_vessel;vitality:32",
                        "artifacts:universal_attractor;agility:18",
                        "artifacts:crystal_heart;vitality:40",
                        "artifacts:helium_flamingo;agility:31",
                        "artifacts:chorus_totem;vitality:36",
                        "artifacts:warp_drive;vitality:28;agility:25",
                        "artifacts:digging_claws;mining:27",
                        "artifacts:feral_claws;agility:32;combat:34",
                        "artifacts:power_glove;combat:30",
                        "artifacts:fire_gauntlet;combat:36",
                        "artifacts:pocket_piston;combat:37",
                        "artifacts:vampiric_glove;combat:40",
                        "artifacts:golden_hook;vitality:30;combat:35",
                        "artifacts:onion_ring;mining:26",
                        "artifacts:pickaxe_heater;mining:32",
                        "artifacts:withered_bracelet;combat:40",
                        "artifacts:aqua_dashers;agility:36",
                        "artifacts:bunny_hoppers;agility:45",
                        "artifacts:kitty_slippers;agility:36",
                        "artifacts:running_shoes;agility:31",
                        "artifacts:snowshoes;agility:25",
                        "artifacts:steadfast_spikes;defense:29",
                        "artifacts:flippers;agility:37",
                        "artifacts:rooted_boots;vitality:28;agility:28",
                        "artifacts:strider_shoes;vitality:39;agility:36"
                ), obj -> obj instanceof String);
        // --- NEW TIDE DROPDOWN ---
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
        // --- NEW GLIDERS DROPDOWN ---
        GLIDERS_ITEMS = builder.comment("Gliders Item Restrictions")
                .defineListAllowEmpty(List.of("gliders_items"), () -> List.of(
                        "vc_gliders:paraglider_wood;agility:5",
                        "vc_gliders:paraglider_iron;agility:8",
                        "vc_gliders:paraglider_gold;agility:12",
                        "vc_gliders:paraglider_diamond;agility:13",
                        "vc_gliders:paraglider_netherite;agility:16"
                ), obj -> obj instanceof String);
        // --- NEW LILI'S LUCKY LURES DROPDOWN ---
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
        // --- NEW IMMERSIVE MODS DROPDOWNS ---
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
        // --- NEW SMALL SHIPS DROPDOWN ---
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
    public static List<String> getAllItemRestrictions() {
        List<String> combined = new ArrayList<>();
        combined.addAll((List<String>) ITEM_RESTRICTIONS.get());
        combined.addAll((List<String>) WEAPON_RESTRICTIONS.get());
        combined.addAll((List<String>) CUSTOM_ITEM_RESTRICTIONS.get());
        combined.addAll((List<String>) FARMERS_DELIGHT_RESTRICTIONS.get());
        combined.addAll((List<String>) PALADINS_PRIESTS_ARMORS.get());
        combined.addAll((List<String>) PALADINS_PRIESTS_WEAPONS.get());
        combined.addAll((List<String>) PALADINS_PRIESTS_SHIELDS.get());
        combined.addAll((List<String>) ROGUES_WARRIORS_ARMORS.get());
        combined.addAll((List<String>) ROGUES_WARRIORS_WEAPONS.get());
        combined.addAll((List<String>) ARCHERS_ARMORS.get());
        combined.addAll((List<String>) ARCHERS_WEAPONS.get());
        combined.addAll((List<String>) WIZARDS_ARMORS.get());
        combined.addAll((List<String>) WIZARDS_WEAPONS.get());
        combined.addAll((List<String>) ARSENAL_WEAPONS.get());
        combined.addAll((List<String>) ARTIFACTS_ITEMS.get());
        combined.addAll((List<String>) FARMING_ITEMS.get());
        combined.addAll((List<String>) TIDE_ITEMS.get());
        combined.addAll((List<String>) JEWELRY_RESTRICTIONS.get());
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
        combined.addAll((List<String>) ALCHEMY_UTILITIES.get()); // NEW
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
        combined.addAll((List<String>) ALCHEMY_UTILITIES.get()); // NEW
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
        combined.addAll((List<String>) SMITHING_WIZARDS_ARMORS.get());
        combined.addAll((List<String>) SMITHING_WIZARDS_WEAPONS.get());
        combined.addAll((List<String>) SMITHING_ARCHERS_ARMORS.get());
        combined.addAll((List<String>) SMITHING_ARCHERS_WEAPONS.get());
        combined.addAll((List<String>) SMITHING_JEWELRY.get());
        combined.addAll((List<String>) SMITHING_PALADINS_ARMORS.get());
        combined.addAll((List<String>) SMITHING_PALADINS_SHIELDS.get());
        combined.addAll((List<String>) SMITHING_PALADINS_WEAPONS.get());
        combined.addAll((List<String>) SMITHING_IMMERSIVE_AIRCRAFT.get());
        combined.addAll((List<String>) SMITHING_IMMERSIVE_MACHINERY.get());
        return combined;
    }

    // --- NEW HELPER METHOD FOR YOUR BACKEND / EVENTS ---
    @SuppressWarnings("unchecked")
    public static List<String> getAllMobXpValues() {
        List<String> combined = new ArrayList<>();
        combined.addAll((List<String>) MOB_XP_VALUES.get());
        combined.addAll((List<String>) CUSTOM_MOB_XP_VALUES.get());
        return combined;
    }
}