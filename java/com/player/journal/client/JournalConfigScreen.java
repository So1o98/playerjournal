package com.player.journal.client;

import com.player.journal.config.JournalConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class JournalConfigScreen {

    @SuppressWarnings("unchecked")
    public static Screen createScreen(Screen parentScreen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parentScreen)
                .setTitle(Component.literal("Player Journal Config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

// ==========================================
//                GENERAL TAB
// ==========================================
        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.literal("General"));

        generalCategory.addEntry(entryBuilder.startIntField(Component.literal("Levels Per Page Reward"), JournalConfig.LEVELS_PER_PAGE_REWARD.get())
                .setDefaultValue(5)
                .setTooltip(Component.literal("How many skill levels are required to be rewarded with 1 Torn Page?\n(e.g. 5 = rewarded at level 5, 10, 15...)"))
                .setSaveConsumer(JournalConfig.LEVELS_PER_PAGE_REWARD::set)
                .build());

        generalCategory.addEntry(entryBuilder.startIntField(Component.literal("Starting Torn Pages"), JournalConfig.STARTING_TORN_PAGES.get())
                .setDefaultValue(5)
                .setTooltip(Component.literal("How many Torn Pages should a player start with when they first join?"))
                .setSaveConsumer(JournalConfig.STARTING_TORN_PAGES::set)
                .build());


        generalCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Pinned Pages Scale"), JournalConfig.HUD_SCALE.get())
                .setDefaultValue(1.0)
                .setMin(0.1)
                .setMax(3.0)
                .setTooltip(Component.literal("Scale of the pinned pages overlay.\n(1.0 is normal, 0.75 is small, 1.5 is large)"))
                .setSaveConsumer(JournalConfig.HUD_SCALE::set)
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.literal("Pinned Pages Anchor"), JournalConfig.HUD_ANCHOR.get())
                .setDefaultValue("BOTTOM_RIGHT")
                .setSelections(java.util.List.of("TOP_LEFT", "TOP_RIGHT", "BOTTOM_LEFT", "BOTTOM_RIGHT"))
                .setTooltip(Component.literal("Which corner to anchor the pinned pages HUD to."))
                .setSaveConsumer(JournalConfig.HUD_ANCHOR::set)
                .build());

        generalCategory.addEntry(entryBuilder.startIntField(Component.literal("Pinned Pages Offset X"), JournalConfig.HUD_OFFSET_X.get())
                .setDefaultValue(5)
                .setMin(0)
                .setMax(2000)
                .setTooltip(Component.literal("Horizontal distance from the edge of the screen."))
                .setSaveConsumer(JournalConfig.HUD_OFFSET_X::set)
                .build());

        generalCategory.addEntry(entryBuilder.startIntField(Component.literal("Pinned Pages Offset Y"), JournalConfig.HUD_OFFSET_Y.get())
                .setDefaultValue(5)
                .setMin(0)
                .setMax(2000)
                .setTooltip(Component.literal("Vertical distance from the edge of the screen."))
                .setSaveConsumer(JournalConfig.HUD_OFFSET_Y::set)
                .build());

        // ==========================================
        //                VITALITY TAB
        // ==========================================
        ConfigCategory vitalityCategory = builder.getOrCreateCategory(Component.literal("Vitality"));
        vitalityCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Per Heart Healed"), JournalConfig.XP_PER_HEART_HEALED.get()).setDefaultValue(2.0).setSaveConsumer(JournalConfig.XP_PER_HEART_HEALED::set).build());
        vitalityCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.XP_BASE_REQUIREMENT.get()).setDefaultValue(50).setSaveConsumer(JournalConfig.XP_BASE_REQUIREMENT::set).build());
        vitalityCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.XP_MULTIPLIER.get()).setDefaultValue(1.2).setSaveConsumer(JournalConfig.XP_MULTIPLIER::set).build());
        vitalityCategory.addEntry(entryBuilder.startIntField(Component.literal("Levels Per Health Boost"), JournalConfig.LEVELS_PER_HEALTH_BOOST.get()).setDefaultValue(3).setSaveConsumer(JournalConfig.LEVELS_PER_HEALTH_BOOST::set).build());
        vitalityCategory.addEntry(entryBuilder.startIntField(Component.literal("PVP & ENV Damage Cooldown (s)"), JournalConfig.ENV_DAMAGE_COOLDOWN.get()).setDefaultValue(30).setTooltip(Component.literal("Cooldown in seconds for gaining XP from environmental damage.")).setSaveConsumer(JournalConfig.ENV_DAMAGE_COOLDOWN::set).build());

        // ==========================================
        //                AGILITY TAB
        // ==========================================
        ConfigCategory agilityCategory = builder.getOrCreateCategory(Component.literal("Agility"));
        agilityCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.AGILITY_XP_BASE_REQUIREMENT.get()).setDefaultValue(150).setSaveConsumer(JournalConfig.AGILITY_XP_BASE_REQUIREMENT::set).build());
        agilityCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Speed Base Modifier"), JournalConfig.AGILITY_SPEED_BASE_MODIFIER.get()).setTooltip(Component.literal("Starting speed penalty/bonus at Level 1.")).setDefaultValue(-0.025).setSaveConsumer(JournalConfig.AGILITY_SPEED_BASE_MODIFIER::set).build());
        agilityCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Speed Per Level"), JournalConfig.AGILITY_SPEED_PER_LEVEL.get()).setTooltip(Component.literal("Speed gained per level.")).setDefaultValue(0.001).setSaveConsumer(JournalConfig.AGILITY_SPEED_PER_LEVEL::set).build());
        agilityCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Jump Base Modifier"), JournalConfig.AGILITY_JUMP_BASE_MODIFIER.get()).setTooltip(Component.literal("Starting jump penalty/bonus at Level 1.")).setDefaultValue(-0.05).setSaveConsumer(JournalConfig.AGILITY_JUMP_BASE_MODIFIER::set).build());
        agilityCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Jump Per Level"), JournalConfig.AGILITY_JUMP_PER_LEVEL.get()).setTooltip(Component.literal("Jump height gained per level.")).setDefaultValue(0.001).setSaveConsumer(JournalConfig.AGILITY_JUMP_PER_LEVEL::set).build());
        agilityCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.AGILITY_XP_MULTIPLIER.get()).setDefaultValue(1.2).setSaveConsumer(JournalConfig.AGILITY_XP_MULTIPLIER::set).build());
        agilityCategory.addEntry(entryBuilder.startStrList(Component.literal("Agility Items"), (List<String>) JournalConfig.AGILITY_ITEMS.get()).setTooltip(Component.literal("Format: item_id;skill:level\nExample: minecraft:elytra;agility:10")).setDefaultValue(List.of("minecraft:elytra;agility:25", "minecraft:ender_pearl;agility:18", "minecraft:oak_boat;agility:4", "minecraft:spruce_boat;agility:4", "minecraft:birch_boat;agility:4", "minecraft:jungle_boat;agility:4", "minecraft:acacia_boat;agility:4", "minecraft:dark_oak_boat;agility:4", "minecraft:mangrove_boat;agility:4", "minecraft:cherry_boat;agility:4", "minecraft:bamboo_raft;agility:4","minecraft:acacia_chest_boat:6",
                "minecraft:birch_chest_boat:6", "minecraft:cherry_chest_boat:6", "minecraft:dark_oak_chest_boat:6", "minecraft:jungle_chest_boat:6", "minecraft:mangrove_chest_boat:6", "minecraft:oak_chest_boat:6", "minecraft:spruce_chest_boat:6")).setSaveConsumer(JournalConfig.AGILITY_ITEMS::set).build());
        agilityCategory.addEntry(entryBuilder.startStrList(Component.literal("Mount Restrictions"), (List<String>) JournalConfig.AGILITY_MOUNTS.get())
                .setTooltip(Component.literal("Format: entity_id;skill:level"))
                .setDefaultValue(List.of("minecraft:horse;agility:10", "minecraft:donkey;agility:10", "minecraft:mule;agility:10", "minecraft:camel;agility:15"))
                .setSaveConsumer(JournalConfig.AGILITY_MOUNTS::set).build());

        // ==========================================
        //                COMBAT TAB
        // ==========================================
        ConfigCategory combatCategory = builder.getOrCreateCategory(Component.literal("Combat"));

        combatCategory.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Custom Mob XP"), JournalConfig.ENABLE_CUSTOM_MOB_XP.get())
                .setDefaultValue(false)
                .setTooltip(Component.literal("Set to false to disable custom mob XP values and use vanilla/other mod XP handling."))
                .setSaveConsumer(JournalConfig.ENABLE_CUSTOM_MOB_XP::set)
                .build());

        combatCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.COMBAT_XP_BASE_REQUIREMENT.get()).setDefaultValue(200).setSaveConsumer(JournalConfig.COMBAT_XP_BASE_REQUIREMENT::set).build());
        combatCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.COMBAT_XP_MULTIPLIER.get()).setDefaultValue(1.2).setSaveConsumer(JournalConfig.COMBAT_XP_MULTIPLIER::set).build());
        combatCategory.addEntry(entryBuilder.startStrList(Component.literal("Mob XP Values"), (List<String>) JournalConfig.MOB_XP_VALUES.get())
                .setDefaultValue(List.of(
                        "minecraft:ender_dragon;500", "minecraft:wither;300", "minecraft:warden;250", "minecraft:elder_guardian;50", "minecraft:ravager;30", "minecraft:evoker;20", "minecraft:vindicator;15",
                        "minecraft:piglin_brute;15", "minecraft:breeze;15", "minecraft:zoglin;15", "minecraft:enderman;15", "minecraft:ghast;15", "minecraft:shulker;15", "minecraft:creeper;10", "minecraft:witch;10",
                        "minecraft:blaze;10", "minecraft:wither_skeleton;10", "minecraft:guardian;10", "minecraft:pillager;10", "minecraft:cave_spider;10", "minecraft:hoglin;10", "minecraft:zombie;5",
                        "minecraft:skeleton;5", "minecraft:spider;8", "minecraft:husk;5", "minecraft:stray;5", "minecraft:drowned;5", "minecraft:bogged;5", "minecraft:slime;5", "minecraft:magma_cube;5",
                        "minecraft:phantom;8", "minecraft:cow;2", "minecraft:pig;2", "minecraft:sheep;2", "minecraft:chicken;2", "minecraft:mooshroom;2", "minecraft:horse;2", "minecraft:donkey;2", "minecraft:mule;2",
                        "minecraft:llama;2", "minecraft:trader_llama;2", "minecraft:goat;2", "minecraft:camel;2", "minecraft:sniffer;3", "minecraft:armadillo;2", "minecraft:wolf;2", "minecraft:cat;1", "minecraft:ocelot;2",
                        "minecraft:fox;2", "minecraft:panda;2", "minecraft:polar_bear;5", "minecraft:turtle;2", "minecraft:rabbit;1", "minecraft:squid;1", "minecraft:glow_squid;1", "minecraft:dolphin;2", "minecraft:axolotl;2",
                        "minecraft:frog;2", "minecraft:bat;1", "minecraft:bee;2", "minecraft:cod;1", "minecraft:salmon;1", "minecraft:pufferfish;1", "minecraft:tropical_fish;1", "minecraft:tadpole;1", "minecraft:villager;1",
                        "minecraft:wandering_trader;5", "minecraft:iron_golem;15", "minecraft:snow_golem;2"
                )).setSaveConsumer(JournalConfig.MOB_XP_VALUES::set).build());


        combatCategory.addEntry(entryBuilder.startStrList(Component.literal("Custom Mob XP Values"), (List<String>) JournalConfig.CUSTOM_MOB_XP_VALUES.get())
                .setDefaultValue(List.of())
                .setTooltip(Component.literal("Add custom or modded mob XP values here.\nFormat: entity_id;xp_reward\nExample: custom_mod:goblin;15"))
                .setSaveConsumer(JournalConfig.CUSTOM_MOB_XP_VALUES::set).build());

        combatCategory.addEntry(entryBuilder.startStrList(Component.literal("Weapon Restrictions"), (List<String>) JournalConfig.WEAPON_RESTRICTIONS.get())
                .setDefaultValue(List.of("minecraft:stone_sword;combat:4", "minecraft:iron_sword;combat:10", "minecraft:golden_sword;combat:7", "minecraft:diamond_sword;combat:15", "minecraft:netherite_sword;combat:20", "minecraft:mace;combat:10"))
                .setTooltip(Component.literal("Format: item_id;skill:level;skill:level")).setSaveConsumer(JournalConfig.WEAPON_RESTRICTIONS::set).build());


        // ==========================================
        //                DEFENSE TAB
        // ==========================================
        ConfigCategory defenseCategory = builder.getOrCreateCategory(Component.literal("Defense"));
        defenseCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.DEFENSE_XP_BASE_REQUIREMENT.get()).setDefaultValue(100).setSaveConsumer(JournalConfig.DEFENSE_XP_BASE_REQUIREMENT::set).build());
        defenseCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.DEFENSE_XP_MULTIPLIER.get()).setDefaultValue(1.3).setSaveConsumer(JournalConfig.DEFENSE_XP_MULTIPLIER::set).build());
        defenseCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Shield XP Per Damage"), JournalConfig.DEFENSE_SHIELD_XP_PER_DAMAGE.get()).setTooltip(Component.literal("XP gained per 1 point of damage blocked by a shield.")).setDefaultValue(8.0).setSaveConsumer(JournalConfig.DEFENSE_SHIELD_XP_PER_DAMAGE::set).build());
        defenseCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Armor XP Per Damage"), JournalConfig.DEFENSE_ARMOR_XP_PER_DAMAGE.get()).setTooltip(Component.literal("XP gained per 1 point of damage mitigated by physical armor.")).setDefaultValue(5.0).setSaveConsumer(JournalConfig.DEFENSE_ARMOR_XP_PER_DAMAGE::set).build());

        defenseCategory.addEntry(entryBuilder.startStrList(Component.literal("Armor Restrictions"), (List<String>) JournalConfig.ARMOR_RESTRICTIONS.get())
                .setDefaultValue(List.of(
                        "minecraft:leather_helmet;defense:1", "minecraft:leather_chestplate;defense:1", "minecraft:leather_leggings;defense:1", "minecraft:leather_boots;defense:1",
                        "minecraft:chainmail_helmet;defense:5", "minecraft:chainmail_chestplate;defense:5", "minecraft:chainmail_leggings;defense:5", "minecraft:chainmail_boots;defense:5",
                        "minecraft:iron_helmet;defense:15;combat:10", "minecraft:iron_chestplate;defense:15;combat:10", "minecraft:iron_leggings;defense:15;combat:10", "minecraft:iron_boots;defense:15;combat:10",
                        "minecraft:golden_helmet;defense:10;combat:5", "minecraft:golden_chestplate;defense:10;combat:5", "minecraft:golden_leggings;defense:10;combat:5", "minecraft:golden_boots;defense:10;combat:5",
                        "minecraft:diamond_helmet;defense:20;combat:15", "minecraft:diamond_chestplate;defense:20;combat:15", "minecraft:diamond_leggings;defense:20;combat:15", "minecraft:diamond_boots;defense:20;combat:15",
                        "minecraft:netherite_helmet;defense:25;combat:20", "minecraft:netherite_chestplate;defense:25;combat:20", "minecraft:netherite_leggings;defense:25;combat:20", "minecraft:netherite_boots;defense:25;combat:20"
                ))
                .setTooltip(Component.literal("Format: item_id;skill:level;skill:level")).setSaveConsumer(JournalConfig.ARMOR_RESTRICTIONS::set).build());


        // ==========================================
        //                MINING TAB
        // ==========================================
        ConfigCategory miningCategory = builder.getOrCreateCategory(Component.literal("Mining"));
        miningCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.MINING_XP_BASE_REQUIREMENT.get()).setDefaultValue(50).setSaveConsumer(JournalConfig.MINING_XP_BASE_REQUIREMENT::set).build());
        miningCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.MINING_XP_MULTIPLIER.get()).setDefaultValue(1.4).setSaveConsumer(JournalConfig.MINING_XP_MULTIPLIER::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Vanilla Tools"), (List<String>) JournalConfig.MINING_TOOL_RESTRICTIONS.get()).setTooltip(Component.literal("Format: tool_id,tool_id;skill:level")).setDefaultValue(List.of("minecraft:stone_pickaxe,minecraft:stone_shovel;mining:4", "minecraft:iron_pickaxe,minecraft:iron_shovel;mining:10", "minecraft:golden_pickaxe,minecraft:golden_shovel;mining:8", "minecraft:diamond_pickaxe,minecraft:diamond_shovel;mining:15", "minecraft:netherite_pickaxe,minecraft:netherite_shovel;mining:30")).setSaveConsumer(JournalConfig.MINING_TOOL_RESTRICTIONS::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Custom Tools"), (List<String>) JournalConfig.CUSTOM_MINING_TOOLS.get()).setTooltip(Component.literal("Format: tool_id;skill:level")).setDefaultValue(List.of()).setSaveConsumer(JournalConfig.CUSTOM_MINING_TOOLS::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Utility Blocks"), (List<String>) JournalConfig.MINING_UTILITIES.get()).setTooltip(Component.literal("Blocks that require interaction (Modded Machines, etc)")).setDefaultValue(List.of()).setSaveConsumer(JournalConfig.MINING_UTILITIES::set).build());

        Component blockTooltip = Component.literal("Format: block_id,block_id;skill:level;xp_reward\nExample: minecraft:iron_ore;mining:3;15\n(Note: The XP reward at the end is optional!)");
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Coal Blocks"), (List<String>) JournalConfig.MINING_COAL.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:coal_ore;mining:2;5", "minecraft:deepslate_coal_ore;mining:2;5", "minecraft:coal_block;mining:2")).setSaveConsumer(JournalConfig.MINING_COAL::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Copper Blocks"), (List<String>) JournalConfig.MINING_COPPER.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:copper_ore;mining:4;10", "minecraft:deepslate_copper_ore;mining:4;10", "minecraft:raw_copper_block;mining:4", "minecraft:copper_block;mining:4")).setSaveConsumer(JournalConfig.MINING_COPPER::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Iron Blocks"), (List<String>) JournalConfig.MINING_IRON.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:iron_ore;mining:8;15", "minecraft:deepslate_iron_ore;mining:8;15", "minecraft:raw_iron_block;mining:8", "minecraft:iron_block;mining:8")).setSaveConsumer(JournalConfig.MINING_IRON::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Gold Blocks"), (List<String>) JournalConfig.MINING_GOLD.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:gold_ore;mining:8;25", "minecraft:deepslate_gold_ore;mining:8;25", "minecraft:nether_gold_ore;mining:8;25", "minecraft:raw_gold_block;mining:8", "minecraft:gold_block;mining:8")).setSaveConsumer(JournalConfig.MINING_GOLD::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Lapis Blocks"), (List<String>) JournalConfig.MINING_LAPIS.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:lapis_ore;mining:12;20", "minecraft:deepslate_lapis_ore;mining:12;20", "minecraft:lapis_block;mining:12")).setSaveConsumer(JournalConfig.MINING_LAPIS::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Emerald Blocks"), (List<String>) JournalConfig.MINING_EMERALD.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:emerald_ore;mining:16;40", "minecraft:deepslate_emerald_ore;mining:16;40", "minecraft:emerald_block;mining:16")).setSaveConsumer(JournalConfig.MINING_EMERALD::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Diamond Blocks"), (List<String>) JournalConfig.MINING_DIAMOND.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:diamond_ore;mining:15;50", "minecraft:deepslate_diamond_ore;mining:15;50", "minecraft:diamond_block;mining:15")).setSaveConsumer(JournalConfig.MINING_DIAMOND::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Quartz Blocks"), (List<String>) JournalConfig.MINING_QUARTZ.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:nether_quartz_ore;mining:12;15")).setSaveConsumer(JournalConfig.MINING_QUARTZ::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Netherite Blocks"), (List<String>) JournalConfig.MINING_NETHERITE.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:ancient_debris;mining:25;250", "minecraft:netherite_block;mining:25")).setSaveConsumer(JournalConfig.MINING_NETHERITE::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Stone Variants"), (List<String>) JournalConfig.MINING_STONE.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:stone,minecraft:stone_stairs,minecraft:stone_slab,minecraft:smooth_stone,minecraft:smooth_stone_slab;mining:1;1", "minecraft:cobblestone,minecraft:cobblestone_stairs,minecraft:cobblestone_slab,minecraft:cobblestone_wall;mining:1;1", "minecraft:mossy_cobblestone,minecraft:mossy_cobblestone_stairs,minecraft:mossy_cobblestone_slab,minecraft:mossy_cobblestone_wall;mining:1;1", "minecraft:stone_bricks,minecraft:stone_brick_stairs,minecraft:stone_brick_slab,minecraft:stone_brick_wall;mining:1;1", "minecraft:mossy_stone_bricks,minecraft:mossy_stone_brick_stairs,minecraft:mossy_stone_brick_slab,minecraft:mossy_stone_brick_wall;mining:1;1", "minecraft:cracked_stone_bricks,minecraft:chiseled_stone_bricks;mining:1;1", "minecraft:andesite,minecraft:andesite_stairs,minecraft:andesite_slab,minecraft:andesite_wall;mining:1;1", "minecraft:polished_andesite,minecraft:polished_andesite_stairs,minecraft:polished_andesite_slab;mining:1;1", "minecraft:diorite,minecraft:diorite_stairs,minecraft:diorite_slab,minecraft:diorite_wall;mining:1;1", "minecraft:polished_diorite,minecraft:polished_diorite_stairs,minecraft:polished_diorite_slab;mining:1;1", "minecraft:granite,minecraft:granite_stairs,minecraft:granite_slab,minecraft:granite_wall;mining:1;1", "minecraft:polished_granite,minecraft:polished_granite_stairs,minecraft:polished_granite_slab;mining:1;1", "minecraft:sandstone,minecraft:sandstone_stairs,minecraft:sandstone_slab,minecraft:sandstone_wall,minecraft:chiseled_sandstone,minecraft:cut_sandstone,minecraft:cut_sandstone_slab,minecraft:smooth_sandstone,minecraft:smooth_sandstone_stairs,minecraft:smooth_sandstone_slab;mining:1;1", "minecraft:red_sandstone,minecraft:red_sandstone_stairs,minecraft:red_sandstone_slab,minecraft:red_sandstone_wall,minecraft:chiseled_red_sandstone,minecraft:cut_red_sandstone,minecraft:cut_red_sandstone_slab,minecraft:smooth_red_sandstone,minecraft:smooth_red_sandstone_stairs,minecraft:smooth_red_sandstone_slab;mining:1;1", "minecraft:tuff,minecraft:tuff_stairs,minecraft:tuff_slab,minecraft:tuff_wall;mining:1;1", "minecraft:tuff_bricks,minecraft:tuff_brick_stairs,minecraft:tuff_brick_slab,minecraft:tuff_brick_wall,minecraft:chiseled_tuff_bricks;mining:1;1", "minecraft:polished_tuff,minecraft:polished_tuff_stairs,minecraft:polished_tuff_slab,minecraft:polished_tuff_wall,minecraft:chiseled_tuff;mining:1;1", "minecraft:calcite,minecraft:dripstone_block,minecraft:mud_bricks,minecraft:mud_brick_stairs,minecraft:mud_brick_slab,minecraft:mud_brick_wall;mining:1;1")).setSaveConsumer(JournalConfig.MINING_STONE::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Deepslate Variants"), (List<String>) JournalConfig.MINING_DEEPSLATE.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:deepslate,minecraft:chiseled_deepslate;mining:3;2", "minecraft:cobbled_deepslate,minecraft:cobbled_deepslate_stairs,minecraft:cobbled_deepslate_slab,minecraft:cobbled_deepslate_wall;mining:3;2", "minecraft:polished_deepslate,minecraft:polished_deepslate_stairs,minecraft:polished_deepslate_slab,minecraft:polished_deepslate_wall;mining:3;2", "minecraft:deepslate_bricks,minecraft:deepslate_brick_stairs,minecraft:deepslate_brick_slab,minecraft:deepslate_brick_wall,minecraft:cracked_deepslate_bricks;mining:3;2", "minecraft:deepslate_tiles,minecraft:deepslate_tile_stairs,minecraft:deepslate_tile_slab,minecraft:deepslate_tile_wall,minecraft:cracked_deepslate_tiles;mining:3;2")).setSaveConsumer(JournalConfig.MINING_DEEPSLATE::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Prismarine Variants"), (List<String>) JournalConfig.MINING_PRISMARINE.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:prismarine,minecraft:prismarine_stairs,minecraft:prismarine_slab,minecraft:prismarine_wall;mining:10;5", "minecraft:prismarine_bricks,minecraft:prismarine_brick_stairs,minecraft:prismarine_brick_slab;mining:10;5", "minecraft:dark_prismarine,minecraft:dark_prismarine_stairs,minecraft:dark_prismarine_slab;mining:10;5")).setSaveConsumer(JournalConfig.MINING_PRISMARINE::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Nether Variants"), (List<String>) JournalConfig.MINING_NETHER.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:netherrack,minecraft:crimson_nylium,minecraft:warped_nylium,minecraft:magma_block,minecraft:bone_block;mining:3;1", "minecraft:basalt,minecraft:polished_basalt,minecraft:smooth_basalt;mining:3;1", "minecraft:blackstone,minecraft:blackstone_stairs,minecraft:blackstone_slab,minecraft:blackstone_wall,minecraft:gilded_blackstone;mining:3;2", "minecraft:polished_blackstone,minecraft:polished_blackstone_stairs,minecraft:polished_blackstone_slab,minecraft:polished_blackstone_wall,minecraft:chiseled_polished_blackstone;mining:3;2", "minecraft:polished_blackstone_bricks,minecraft:polished_blackstone_brick_stairs,minecraft:polished_blackstone_brick_slab,minecraft:polished_blackstone_brick_wall,minecraft:cracked_polished_blackstone_bricks;mining:3;2", "minecraft:nether_bricks,minecraft:nether_brick_stairs,minecraft:nether_brick_slab,minecraft:nether_brick_wall;mining:3;2", "minecraft:cracked_nether_bricks,minecraft:chiseled_nether_bricks,minecraft:red_nether_bricks,minecraft:red_nether_brick_stairs,minecraft:red_nether_brick_slab,minecraft:red_nether_brick_wall;mining:3;2", "minecraft:quartz_block,minecraft:quartz_stairs,minecraft:quartz_slab,minecraft:smooth_quartz,minecraft:smooth_quartz_stairs,minecraft:smooth_quartz_slab,minecraft:chiseled_quartz_block,minecraft:quartz_pillar,minecraft:quartz_bricks;mining:5;2", "minecraft:obsidian,minecraft:crying_obsidian,minecraft:respawn_anchor,minecraft:lodestone;mining:15;100")).setSaveConsumer(JournalConfig.MINING_NETHER::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Redstone/Machinery"), (List<String>) JournalConfig.MINING_REDSTONE.get()).setTooltip(blockTooltip).setDefaultValue(List.of("minecraft:redstone_ore,minecraft:deepslate_redstone_ore,minecraft:redstone_block;mining:10;20", "minecraft:dispenser,minecraft:dropper,minecraft:observer,minecraft:piston,minecraft:sticky_piston;mining:6;2", "minecraft:rail,minecraft:powered_rail,minecraft:detector_rail,minecraft:activator_rail;mining:4;2", "minecraft:hopper,minecraft:cauldron,minecraft:heavy_weighted_pressure_plate,minecraft:light_weighted_pressure_plate;mining:4;2")).setSaveConsumer(JournalConfig.MINING_REDSTONE::set).build());
        miningCategory.addEntry(entryBuilder.startStrList(Component.literal("Custom Blocks"), (List<String>) JournalConfig.CUSTOM_MINING_BLOCKS.get()).setTooltip(Component.literal("Format: block_id;skill:level;xp_reward")).setDefaultValue(List.of("jewelry:gem_vein;mining:18;50", "jewelry:deepslate_gem_vein;mining:18;50")).setSaveConsumer(JournalConfig.CUSTOM_MINING_BLOCKS::set).build());

        // ==========================================
        //             FARMING TAB
        // ==========================================
        ConfigCategory farmingCategory = builder.getOrCreateCategory(Component.literal("Farming"));
        farmingCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.FARMING_XP_BASE_REQUIREMENT.get()).setDefaultValue(150).setSaveConsumer(JournalConfig.FARMING_XP_BASE_REQUIREMENT::set).build());
        farmingCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.FARMING_XP_MULTIPLIER.get()).setDefaultValue(1.3).setSaveConsumer(JournalConfig.FARMING_XP_MULTIPLIER::set).build());
        farmingCategory.addEntry(entryBuilder.startStrList(Component.literal("Farming Hoes"), (List<String>) JournalConfig.FARMING_HOES.get()).setTooltip(Component.literal("Format: hoe_id;skill:level")).setDefaultValue(List.of("minecraft:wooden_hoe;farming:1", "minecraft:stone_hoe;farming:5", "minecraft:iron_hoe;farming:10", "minecraft:golden_hoe;farming:10", "minecraft:diamond_hoe;farming:15", "minecraft:netherite_hoe;farming:25")).setSaveConsumer(JournalConfig.FARMING_HOES::set).build());
        farmingCategory.addEntry(entryBuilder.startStrList(Component.literal("Farming Axes"), (List<String>) JournalConfig.FARMING_AXES.get()).setTooltip(Component.literal("Format: axe_id;skill:level")).setDefaultValue(List.of("minecraft:stone_axe;farming:3", "minecraft:iron_axe;farming:8", "minecraft:golden_axe;farming:10", "minecraft:diamond_axe;farming:15", "minecraft:netherite_axe;farming:25")).setSaveConsumer(JournalConfig.FARMING_AXES::set).build());
        farmingCategory.addEntry(entryBuilder.startStrList(Component.literal("Farming Items"), (List<String>) JournalConfig.FARMING_ITEMS.get()).setTooltip(Component.literal("Items like buckets, shears, and bone meal.")).setDefaultValue(List.of("minecraft:shears;farming:9", "minecraft:bucket,minecraft:water_bucket,minecraft:lava_bucket;farming:3", "minecraft:milk_bucket;farming:4", "minecraft:bone_meal;farming:6")).setSaveConsumer(JournalConfig.FARMING_ITEMS::set).build());
        farmingCategory.addEntry(entryBuilder.startStrList(Component.literal("Utility Blocks"), (List<String>) JournalConfig.FARMING_UTILITIES.get()).setTooltip(Component.literal("Blocks that require interaction (Composters, Beehives, etc)")).setDefaultValue(List.of("minecraft:composter;farming:2", "minecraft:smoker;farming:12", "minecraft:beehive,minecraft:bee_nest;farming:14")).setSaveConsumer(JournalConfig.FARMING_UTILITIES::set).build());
        farmingCategory.addEntry(entryBuilder.startStrList(Component.literal("Crops"), (List<String>) JournalConfig.FARMING_CROPS.get()).setTooltip(Component.literal("Format: block_id;skill:level;xp_reward\nNOTE: XP is only awarded when fully grown!")).setDefaultValue(List.of("minecraft:wheat;farming:1;3", "minecraft:carrots;farming:2;4", "minecraft:potatoes;farming:2;4", "minecraft:beetroots;farming:3;5", "minecraft:melon;farming:4;8", "minecraft:pumpkin;farming:4;8", "minecraft:sugar_cane;farming:5;2", "minecraft:cocoa;farming:6;5", "minecraft:sweet_berry_bush;farming:3;2", "minecraft:cave_vines;farming:5;3", "minecraft:nether_wart;farming:10;10",                "minecraft:wheat_seeds;farming:1",
                "minecraft:pumpkin_seeds;farming:4",
                "minecraft:melon_seeds;farming:4",
                "minecraft:torchflower_seeds;farming:8",
                "minecraft:beetroot_seeds;farming:3"
        )).setSaveConsumer(JournalConfig.FARMING_CROPS::set).build());
        farmingCategory.addEntry(entryBuilder.startStrList(Component.literal("Animal Breeding"), (List<String>) JournalConfig.FARMING_BREEDING.get()).setTooltip(Component.literal("Format: entity_id;skill:level;xp_reward")).setDefaultValue(List.of("minecraft:chicken;farming:5;5", "minecraft:pig;farming:6;8", "minecraft:sheep;farming:7;10", "minecraft:cow;farming:8;12", "minecraft:mooshroom;farming:12;20", "minecraft:horse,minecraft:donkey;farming:10;25", "minecraft:llama;farming:10;25", "minecraft:bee;farming:8;15", "minecraft:turtle;farming:12;30", "minecraft:sniffer;farming:15;50")).setSaveConsumer(JournalConfig.FARMING_BREEDING::set).build());

        // ==========================================
        //             SMITHING TAB
        // ==========================================
        ConfigCategory smithingCategory = builder.getOrCreateCategory(Component.literal("Smithing"));
        smithingCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.SMITHING_XP_BASE_REQUIREMENT.get()).setDefaultValue(100).setSaveConsumer(JournalConfig.SMITHING_XP_BASE_REQUIREMENT::set).build());
        smithingCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.SMITHING_XP_MULTIPLIER.get()).setDefaultValue(1.3).setSaveConsumer(JournalConfig.SMITHING_XP_MULTIPLIER::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Smelting XP"), (List<String>) JournalConfig.SMITHING_SMELTING.get()).setTooltip(Component.literal("Format: item_id;xp_reward")).setDefaultValue(List.of("minecraft:iron_ingot;2", "minecraft:gold_ingot;3", "minecraft:copper_ingot;1", "minecraft:netherite_scrap;15")).setSaveConsumer(JournalConfig.SMITHING_SMELTING::set).build());

        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Utility Blocks"), (List<String>) JournalConfig.SMITHING_UTILITIES.get())
                .setTooltip(Component.literal("Format: block_id;skill:level"))
                .setDefaultValue(List.of("minecraft:blast_furnace;smithing:5", "minecraft:anvil;smithing:1", "minecraft:chipped_anvil;smithing:1", "minecraft:damaged_anvil;smithing:1", "minecraft:smithing_table;smithing:13", "minecraft:stonecutter;smithing:3"))
                .setSaveConsumer(JournalConfig.SMITHING_UTILITIES::set).build());

        Component craftTooltip = Component.literal("Format: item_id,item_id;skill:level;xp_reward");
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Iron Tools Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_IRON_TOOLS.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:iron_sword,minecraft:iron_pickaxe,minecraft:iron_axe,minecraft:iron_shovel,minecraft:iron_hoe;smithing:7;15")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_IRON_TOOLS::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Iron Armor Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_IRON_ARMOR.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:iron_helmet,minecraft:iron_chestplate,minecraft:iron_leggings,minecraft:iron_boots;smithing:11;20")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_IRON_ARMOR::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Gold Tools Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_GOLD_TOOLS.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:golden_sword,minecraft:golden_pickaxe,minecraft:golden_axe,minecraft:golden_shovel,minecraft:golden_hoe;smithing:3;10")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_GOLD_TOOLS::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Gold Armor Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_GOLD_ARMOR.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:golden_helmet,minecraft:golden_chestplate,minecraft:golden_leggings,minecraft:golden_boots;smithing:6;15")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_GOLD_ARMOR::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Diamond Tools Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_DIAMOND_TOOLS.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:diamond_sword,minecraft:diamond_pickaxe,minecraft:diamond_axe,minecraft:diamond_shovel,minecraft:diamond_hoe;smithing:11;25")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_DIAMOND_TOOLS::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Diamond Armor Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_DIAMOND_ARMOR.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:diamond_helmet,minecraft:diamond_chestplate,minecraft:diamond_leggings,minecraft:diamond_boots;smithing:16;35")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_DIAMOND_ARMOR::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Netherite Tools Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_NETHERITE_TOOLS.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:netherite_sword,minecraft:netherite_pickaxe,minecraft:netherite_axe,minecraft:netherite_shovel,minecraft:netherite_hoe;smithing:16;50")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_NETHERITE_TOOLS::set).build());
        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Netherite Armor Crafting"), (List<String>) JournalConfig.SMITHING_CRAFTING_NETHERITE_ARMOR.get()).setTooltip(craftTooltip).setDefaultValue(List.of("minecraft:netherite_helmet,minecraft:netherite_chestplate,minecraft:netherite_leggings,minecraft:netherite_boots;smithing:21;60")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_NETHERITE_ARMOR::set).build());

        smithingCategory.addEntry(entryBuilder.startStrList(Component.literal("Custom Crafting Restrictions"), (List<String>) JournalConfig.SMITHING_CRAFTING_CUSTOM.get()).setDefaultValue(List.of()).setTooltip(Component.literal("Format: item_id;skill:level;xp_reward")).setSaveConsumer(JournalConfig.SMITHING_CRAFTING_CUSTOM::set).build());


        // --- IMMERSIVE CRAFTING ---
        var immersiveCraftingCategory = entryBuilder.startSubCategory(Component.literal("Immersive Machinery & Aircraft Crafting"));
        immersiveCraftingCategory.add(entryBuilder.startStrList(Component.literal("Aircraft Parts"), (List<String>) JournalConfig.SMITHING_IMMERSIVE_AIRCRAFT.get())
                .setDefaultValue(List.of(
                        "immersive_aircraft:enhanced_propeller;smithing:10;25", "immersive_aircraft:eco_engine;smithing:12;30", "immersive_aircraft:nether_engine;smithing:15;40",
                        "immersive_aircraft:steel_boiler;smithing:11;25", "immersive_aircraft:industrial_gears;smithing:13;30", "immersive_aircraft:sturdy_pipes;smithing:10;20",
                        "immersive_aircraft:gyroscope;smithing:14;35", "immersive_aircraft:gyroscope_hud;smithing:14;35", "immersive_aircraft:gyroscope_dials;smithing:14;35",
                        "immersive_aircraft:hull_reinforcement;smithing:12;30", "immersive_aircraft:improved_landing_gear;smithing:10;25"
                )).setTooltip(Component.literal("Format: modid:item;skill:level;xp_reward")).setSaveConsumer(newValue -> JournalConfig.SMITHING_IMMERSIVE_AIRCRAFT.set(newValue)).build());
        immersiveCraftingCategory.add(entryBuilder.startStrList(Component.literal("Machinery Parts"), (List<String>) JournalConfig.SMITHING_IMMERSIVE_MACHINERY.get())
                .setDefaultValue(List.of(
                        "immersive_machinery:iron_drill;smithing:7;25", "immersive_machinery:diamond_drill;smithing:12;35", "immersive_machinery:netherite_drill;smithing:17;50"
                )).setTooltip(Component.literal("Format: modid:item;skill:level;xp_reward")).setSaveConsumer(newValue -> JournalConfig.SMITHING_IMMERSIVE_MACHINERY.set(newValue)).build());

        smithingCategory.addEntry(immersiveCraftingCategory.build());

        // ==========================================
        //             ARCHERY TAB
        // ==========================================
        ConfigCategory archeryCategory = builder.getOrCreateCategory(Component.literal("Archery"));
        archeryCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.ARCHERY_XP_BASE_REQUIREMENT.get()).setDefaultValue(50).setSaveConsumer(JournalConfig.ARCHERY_XP_BASE_REQUIREMENT::set).build());
        archeryCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.ARCHERY_XP_MULTIPLIER.get()).setDefaultValue(1.2).setSaveConsumer(JournalConfig.ARCHERY_XP_MULTIPLIER::set).build());
        archeryCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Per Hit"), JournalConfig.ARCHERY_XP_PER_HIT.get()).setTooltip(Component.literal("Base XP gained per successful projectile hit.")).setDefaultValue(5.0).setSaveConsumer(JournalConfig.ARCHERY_XP_PER_HIT::set).build());

        Component archeryTooltip = Component.literal("Format: item_id;skill:level");
        archeryCategory.addEntry(entryBuilder.startStrList(Component.literal("Bows"), (List<String>) JournalConfig.ARCHERY_BOWS.get()).setTooltip(archeryTooltip).setDefaultValue(List.of("minecraft:bow;archery:1")).setSaveConsumer(JournalConfig.ARCHERY_BOWS::set).build());
        archeryCategory.addEntry(entryBuilder.startStrList(Component.literal("Crossbows"), (List<String>) JournalConfig.ARCHERY_CROSSBOWS.get()).setTooltip(archeryTooltip).setDefaultValue(List.of("minecraft:crossbow;archery:5")).setSaveConsumer(JournalConfig.ARCHERY_CROSSBOWS::set).build());
        archeryCategory.addEntry(entryBuilder.startStrList(Component.literal("Tridents"), (List<String>) JournalConfig.ARCHERY_TRIDENTS.get()).setTooltip(archeryTooltip).setDefaultValue(List.of("minecraft:trident;archery:10")).setSaveConsumer(JournalConfig.ARCHERY_TRIDENTS::set).build());
        archeryCategory.addEntry(entryBuilder.startStrList(Component.literal("Arrows"), (List<String>) JournalConfig.ARCHERY_ARROWS.get()).setTooltip(archeryTooltip).setDefaultValue(List.of("minecraft:spectral_arrow;archery:5", "minecraft:tipped_arrow;archery:10")).setSaveConsumer(JournalConfig.ARCHERY_ARROWS::set).build());

        // ==========================================
        //             FISHING TAB
        // ==========================================
        ConfigCategory fishingCategory = builder.getOrCreateCategory(Component.literal("Fishing"));
        fishingCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.FISHING_XP_BASE_REQUIREMENT.get()).setDefaultValue(100).setSaveConsumer(JournalConfig.FISHING_XP_BASE_REQUIREMENT::set).build());
        fishingCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.FISHING_XP_MULTIPLIER.get()).setDefaultValue(1.2).setSaveConsumer(JournalConfig.FISHING_XP_MULTIPLIER::set).build());
        fishingCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Per Catch"), JournalConfig.FISHING_XP_PER_CATCH.get()).setTooltip(Component.literal("Base XP gained per item successfully fished.")).setDefaultValue(15.0).setSaveConsumer(JournalConfig.FISHING_XP_PER_CATCH::set).build());

        Component fishingTooltip = Component.literal("Format: item_id;skill:level");
        fishingCategory.addEntry(entryBuilder.startStrList(Component.literal("Fishing Rods"), (List<String>) JournalConfig.FISHING_RODS.get()).setTooltip(fishingTooltip).setDefaultValue(List.of("minecraft:fishing_rod;fishing:1")).setSaveConsumer(JournalConfig.FISHING_RODS::set).build());
        fishingCategory.addEntry(entryBuilder.startStrList(Component.literal("Ocean Blocks"), (List<String>) JournalConfig.FISHING_OCEAN_BLOCKS.get()).setTooltip(Component.literal("Format: block_id;skill:level")).setDefaultValue(List.of("minecraft:sponge,minecraft:wet_sponge;fishing:5", "minecraft:sea_lantern;fishing:10", "minecraft:kelp,minecraft:dried_kelp_block;fishing:2", "minecraft:sea_pickle;fishing:3", "minecraft:conduit;fishing:15")).setSaveConsumer(JournalConfig.FISHING_OCEAN_BLOCKS::set).build());
        fishingCategory.addEntry(entryBuilder.startStrList(Component.literal("Diving Gear"), (List<String>) JournalConfig.FISHING_DIVING_GEAR.get()).setTooltip(fishingTooltip).setDefaultValue(List.of("minecraft:turtle_helmet;fishing:10", "minecraft:heart_of_the_sea;fishing:15", "minecraft:nautilus_shell;fishing:5")).setSaveConsumer(JournalConfig.FISHING_DIVING_GEAR::set).build());
        fishingCategory.addEntry(entryBuilder.startStrList(Component.literal("Custom Items"), (List<String>) JournalConfig.FISHING_CUSTOM.get()).setTooltip(Component.literal("Format: item_id;skill:level")).setDefaultValue(List.of()).setSaveConsumer(JournalConfig.FISHING_CUSTOM::set).build());

        // ==========================================
        //             ALCHEMY TAB
        // ==========================================
        ConfigCategory alchemyCategory = builder.getOrCreateCategory(Component.literal("Alchemy"));
        alchemyCategory.addEntry(entryBuilder.startIntField(Component.literal("Base XP Requirement"), JournalConfig.ALCHEMY_XP_BASE_REQUIREMENT.get()).setDefaultValue(200).setSaveConsumer(JournalConfig.ALCHEMY_XP_BASE_REQUIREMENT::set).build());
        alchemyCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP Multiplier"), JournalConfig.ALCHEMY_XP_MULTIPLIER.get()).setDefaultValue(1.2).setSaveConsumer(JournalConfig.ALCHEMY_XP_MULTIPLIER::set).build());
        alchemyCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP per Potion Brewed"), JournalConfig.ALCHEMY_BREW_XP.get()).setDefaultValue(15.0).setSaveConsumer(JournalConfig.ALCHEMY_BREW_XP::set).build());
        alchemyCategory.addEntry(entryBuilder.startDoubleField(Component.literal("XP per Potion Drank"), JournalConfig.ALCHEMY_DRINK_XP.get()).setDefaultValue(2.0).setSaveConsumer(JournalConfig.ALCHEMY_DRINK_XP::set).build());
        // --- BOOK CONSUMPTION XP ---
        alchemyCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Standard Book XP"), JournalConfig.ALCHEMY_BOOK_XP.get())
                .setDefaultValue(15.0)
                .setTooltip(Component.literal("XP gained from shift right-clicking and consuming a standard Book"))
                .setSaveConsumer(newValue -> JournalConfig.ALCHEMY_BOOK_XP.set(newValue))
                .build());

        alchemyCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Enchanted Book Base XP"), JournalConfig.ALCHEMY_ENCHANTED_BOOK_BASE_XP.get())
                .setDefaultValue(50.0)
                .setTooltip(Component.literal("Base XP gained from consuming an Enchanted Book"))
                .setSaveConsumer(newValue -> JournalConfig.ALCHEMY_ENCHANTED_BOOK_BASE_XP.set(newValue))
                .build());

        alchemyCategory.addEntry(entryBuilder.startDoubleField(Component.literal("Enchantment Level Multiplier XP"), JournalConfig.ALCHEMY_ENCHANTED_BOOK_LEVEL_MULTIPLIER.get())
                .setDefaultValue(15.0)
                .setTooltip(Component.literal("Additional XP granted per stored enchantment level on a consumed Enchanted Book"))
                .setSaveConsumer(newValue -> JournalConfig.ALCHEMY_ENCHANTED_BOOK_LEVEL_MULTIPLIER.set(newValue))
                .build());

        alchemyCategory.addEntry(entryBuilder.startStrList(Component.literal("Utility Blocks"), (List<String>) JournalConfig.ALCHEMY_UTILITIES.get())
                .setTooltip(Component.literal("Blocks that require interaction (Brewing Stands, Enchanting Tables, etc)"))
                .setDefaultValue(List.of("minecraft:brewing_stand;alchemy:5","minecraft:grindstone;alchemy:10", "minecraft:enchanting_table;alchemy:15", "spell_engine:spell_binding;alchemy:20"))
                .setSaveConsumer(JournalConfig.ALCHEMY_UTILITIES::set).build());

        alchemyCategory.addEntry(entryBuilder.startStrList(Component.literal("Potion Restrictions"), (List<String>) JournalConfig.POTION_RESTRICTIONS.get())
                .setDefaultValue(List.of(                "potion:minecraft:healing;vitality:1;alchemy:1",
                        "potion:minecraft:strong_healing;vitality:5;alchemy:1",
                        "potion:minecraft:regeneration;vitality:1;alchemy:1",
                        "potion:minecraft:strong_regeneration;vitality:5;alchemy:1",
                        "potion:minecraft:long_regeneration;vitality:8;alchemy:1",
                        "potion:minecraft:swiftness;agility:5;alchemy:1",
                        "potion:minecraft:strong_swiftness;agility:15;alchemy:1",
                        "potion:minecraft:long_swiftness;agility:10;alchemy:1"))
                .setTooltip(Component.literal("Format: potion:potion_id;skill:level")).setSaveConsumer(JournalConfig.POTION_RESTRICTIONS::set).build());

        alchemyCategory.addEntry(entryBuilder.startStrList(Component.literal("Enchantment Restrictions"), (List<String>) JournalConfig.ENCHANTMENT_RESTRICTIONS.get())
                .setDefaultValue(List.of(
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
                        "minecraft:mending;alchemy:12"))
                .setTooltip(Component.literal("Format: enchantment_id;skill:level")).setSaveConsumer(JournalConfig.ENCHANTMENT_RESTRICTIONS::set).build());


        // ==========================================
        //             RESTRICTIONS TAB
        // ==========================================
        ConfigCategory restrictionsCategory = builder.getOrCreateCategory(Component.literal("Restrictions"));

        restrictionsCategory.addEntry(entryBuilder.startStrList(Component.literal("Item Restrictions"), (List<String>) JournalConfig.ITEM_RESTRICTIONS.get())
                .setDefaultValue(List.of("minecraft:shield;defense:5;combat:5", "minecraft:totem_of_undying;vitality:20"))
                .setTooltip(Component.literal("Format: item_id;skill:level;skill:level")).setSaveConsumer(JournalConfig.ITEM_RESTRICTIONS::set).build());

        restrictionsCategory.addEntry(entryBuilder.startStrList(Component.literal("Custom Items"), (List<String>) JournalConfig.CUSTOM_ITEM_RESTRICTIONS.get())
                .setDefaultValue(List.of())
                .setTooltip(Component.literal("Format: item_id;skill:level")).setSaveConsumer(JournalConfig.CUSTOM_ITEM_RESTRICTIONS::set).build());

        restrictionsCategory.addEntry(entryBuilder.startStrList(Component.literal("Villager Restrictions"), (List<String>) JournalConfig.VILLAGER_RESTRICTIONS.get())
                .setDefaultValue(List.of("minecraft:librarian;alchemy:8", "minecraft:cleric;alchemy:5", "minecraft:cartographer;vitality:5", "minecraft:farmer;farming:5", "minecraft:butcher;farming:5", "minecraft:shepherd;farming:5", "minecraft:toolsmith;mining:5", "minecraft:fisherman;fishing:5", "minecraft:armorer;smithing:5", "minecraft:leatherworker;smithing:3", "minecraft:weaponsmith;smithing:8", "minecraft:mason;smithing:7", "minecraft:fletcher;archery:5"))
                .setTooltip(Component.literal("Format: villager_profession;skill:level")).setSaveConsumer(JournalConfig.VILLAGER_RESTRICTIONS::set).build());

        // --- USAGE TIDE ---
        var tideCategory = entryBuilder.startSubCategory(Component.literal("Tide"));
        tideCategory.add(entryBuilder.startStrList(Component.literal("Items"), (List<String>) JournalConfig.TIDE_ITEMS.get())
                .setDefaultValue(List.of(
                        "minecraft:fishing_rod;fishing:1", "tide:stone_fishing_rod;fishing:3", "tide:iron_fishing_rod;fishing:6", "tide:golden_fishing_rod;fishing:9", "tide:crystal_fishing_rod;fishing:12",
                        "tide:diamond_fishing_rod;fishing:15", "tide:midas_fishing_rod;fishing:18", "tide:netherite_fishing_rod;fishing:21", "tide:dragonfin_boots;defense:24", "tide:starlight_bow;archery:18"
                )).setTooltip(Component.literal("Format: modid:item;skill:level")).setSaveConsumer(newValue -> JournalConfig.TIDE_ITEMS.set(newValue)).build());
        restrictionsCategory.addEntry(tideCategory.build());

        // --- USAGE GLIDERS ---
        var glidersCategory = entryBuilder.startSubCategory(Component.literal("Gliders"));
        glidersCategory.add(entryBuilder.startStrList(Component.literal("Items"), (List<String>) JournalConfig.GLIDERS_ITEMS.get())
                .setDefaultValue(List.of("vc_gliders:paraglider_wood;agility:5", "vc_gliders:paraglider_iron;agility:8", "vc_gliders:paraglider_gold;agility:12", "vc_gliders:paraglider_diamond;agility:13", "vc_gliders:paraglider_netherite;agility:16"))
                .setTooltip(Component.literal("Format: modid:item;skill:level")).setSaveConsumer(newValue -> JournalConfig.GLIDERS_ITEMS.set(newValue)).build());
        restrictionsCategory.addEntry(glidersCategory.build());

        // --- USAGE LILI'S LUCKY LURES ---
        var lilisLuresCategory = entryBuilder.startSubCategory(Component.literal("Lili's Lucky Lures"));
        lilisLuresCategory.add(entryBuilder.startStrList(Component.literal("Items"), (List<String>) JournalConfig.LILIS_LUCKY_LURES_ITEMS.get())
                .setDefaultValue(List.of(
                        "lilis_lucky_lures:bamboo_fishing_rod;fishing:2", "lilis_lucky_lures:fishing_net;fishing:5", "lilis_lucky_lures:spear;combat:8", "lilis_lucky_lures:hanging_frame;fishing:7",
                        "lilis_lucky_lures:fish_net;fishing:8", "lilis_lucky_lures:fish_trap;fishing:10", "lilis_lucky_lures:fish_trophy_frame;fishing:11", "lilis_lucky_lures:anglers_hat;fishing:13"
                )).setTooltip(Component.literal("Format: modid:item;skill:level")).setSaveConsumer(newValue -> JournalConfig.LILIS_LUCKY_LURES_ITEMS.set(newValue)).build());
        restrictionsCategory.addEntry(lilisLuresCategory.build());

        // --- USAGE IMMERSIVE ---
        var immersiveCategory = entryBuilder.startSubCategory(Component.literal("Immersive Machinery & Aircraft"));
        immersiveCategory.add(entryBuilder.startStrList(Component.literal("Machinery"), (List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get())
                .setDefaultValue(List.of("immersive_machinery:tunnel_digger;mining:18", "immersive_machinery:bamboo_bee;agility:8", "immersive_machinery:redstone_sheep;farming:10", "immersive_machinery:copperfin;fishing:13"))
                .setTooltip(Component.literal("Format: modid:item;skill:level")).setSaveConsumer(newValue -> JournalConfig.IMMERSIVE_MACHINERY_ITEMS.set(newValue)).build());
        immersiveCategory.add(entryBuilder.startStrList(Component.literal("Aircraft"), (List<String>) JournalConfig.IMMERSIVE_AIRCRAFT_ITEMS.get())
                .setDefaultValue(List.of(
                        "immersive_aircraft:biplane;agility:25", "immersive_aircraft:gyrodyne;agility:17", "immersive_aircraft:quadrocopter;agility:19", "immersive_aircraft:airship;agility:22",
                        "immersive_aircraft:cargo_airship;agility:24", "immersive_aircraft:warship;agility:28;combat:30", "immersive_aircraft:bamboo_hopper;agility:16"
                )).setTooltip(Component.literal("Format: modid:item;skill:level")).setSaveConsumer(newValue -> JournalConfig.IMMERSIVE_AIRCRAFT_ITEMS.set(newValue)).build());
        restrictionsCategory.addEntry(immersiveCategory.build());

        // --- USAGE SMALL SHIPS ---
        var smallShipsCategory = entryBuilder.startSubCategory(Component.literal("Small Ships"));
        smallShipsCategory.add(entryBuilder.startStrList(Component.literal("Ships"), (List<String>) JournalConfig.SMALL_SHIPS_ITEMS.get())
                .setDefaultValue(List.of(
                        "smallships:oak_cog;agility:1", "smallships:spruce_cog;agility:8", "smallships:birch_cog;agility:8", "smallships:jungle_cog;agility:8", "smallships:acacia_cog;agility:8", "smallships:dark_oak_cog;agility:8", "smallships:mangrove_cog;agility:8", "smallships:cherry_cog;agility:8", "smallships:bamboo_cog;agility:8",
                        "smallships:oak_brigg;agility:12", "smallships:spruce_brigg;agility:12", "smallships:birch_brigg;agility:12", "smallships:jungle_brigg;agility:12", "smallships:acacia_brigg;agility:12", "smallships:dark_oak_brigg;agility:12", "smallships:mangrove_brigg;agility:12", "smallships:cherry_brigg;agility:12", "smallships:bamboo_brigg;agility:12",
                        "smallships:oak_galley;agility:10", "smallships:spruce_galley;agility:10", "smallships:birch_galley;agility:10", "smallships:jungle_galley;agility:10", "smallships:acacia_galley;agility:10", "smallships:dark_oak_galley;agility:10", "smallships:mangrove_galley;agility:10", "smallships:cherry_galley;agility:10", "smallships:bamboo_galley;agility:10",
                        "smallships:oak_drakkar;agility:16", "smallships:spruce_drakkar;agility:16", "smallships:birch_drakkar;agility:16", "smallships:jungle_drakkar;agility:16", "smallships:acacia_drakkar;agility:16", "smallships:dark_oak_drakkar;agility:16", "smallships:mangrove_drakkar;agility:16", "smallships:cherry_drakkar;agility:16", "smallships:bamboo_drakkar;agility:16"
                )).setTooltip(Component.literal("Format: modid:item;skill:level")).setSaveConsumer(newValue -> JournalConfig.SMALL_SHIPS_ITEMS.set(newValue)).build());
        restrictionsCategory.addEntry(smallShipsCategory.build());

        builder.setSavingRunnable(() -> {

            JournalConfig.SPEC.save();
        });

        return builder.build();

    }
}