package com.player.journal.client;

import com.player.journal.config.JournalConfig;
import com.player.journal.network.ClientPayloadHandler;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class JournalScreen extends Screen {

    private static final ResourceLocation[] PAGES = {
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/contents.png"), // 0
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/trading.png"),  // 1 (Trading shifted to Page 1)
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/health.png"),   // 2 (Vitality)
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/agility.png"),  // 3 (Agility)
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/combat.png"),   // 4 (Combat)
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/defense.png"),  // 5 (Defense)
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/farming.png"),  // 6
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/mining.png"),   // 7
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/smithing.png"), // 8
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/archery.png"),  // 9
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/fishing.png"),  // 10
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/alchemy.png")   // 11
    };

    private static final ResourceLocation PADLOCK_TEXTURE = ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/padlock.png");

    private final int imageWidth = 400;
    private final int imageHeight = 200;

    private int currentPage = 0;
    private PageButton forwardButton;
    private PageButton backButton;

    private double scrollOffset = 0;
    private int maxLeftScroll = 0;

    private long lastFrameTime = Util.getMillis();

    public static final Set<Integer> PINNED_PAGES = new HashSet<>();
    private static boolean pinsLoaded = false;

    public static void savePins() {
        try {
            File pinFile = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().resolve("playerjournal_pinned.txt").toFile();

            if (pinFile.getParentFile() != null && !pinFile.getParentFile().exists()) {
                pinFile.getParentFile().mkdirs();
            }

            if (!pinFile.exists()) {
                pinFile.createNewFile();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(pinFile))) {
                for (Integer page : PINNED_PAGES) {
                    writer.write(page.toString());
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadPins() {
        if (pinsLoaded) return;
        try {
            File pinFile = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().resolve("playerjournal_pinned.txt").toFile();
            if (!pinFile.exists()) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(pinFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        PINNED_PAGES.add(Integer.parseInt(line.trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
            pinsLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean listsBuilt = false;

    public static final List<UnlockEntry> cachedVitalityUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedAgilityUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedCombatUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedDefenseUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedFarmingUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedMiningUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedMiningBlocks = new ArrayList<>();
    public static boolean showMiningBlocks = false;
    public static final List<UnlockEntry> cachedSmithingUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedArcheryUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedFishingUnlocks = new ArrayList<>();
    public static final List<UnlockEntry> cachedAlchemyUnlocks = new ArrayList<>();
    public static final List<VillagerEntry> cachedVillagers = new ArrayList<>();

    public static class UnlockEntry {
        public ItemStack stack;
        public int requiredLevel;
        public boolean isCrafting;
        public boolean isAnvil;

        public UnlockEntry(ItemStack stack, int requiredLevel, boolean isCrafting, boolean isAnvil) {
            this.stack = stack;
            this.requiredLevel = requiredLevel;
            this.isCrafting = isCrafting;
            this.isAnvil = isAnvil;
        }
    }

    public static class VillagerEntry {
        public String displayName;
        public String requiredSkill;
        public int requiredLevel;

        public VillagerEntry(String name, String skill, int level) {
            this.displayName = name;
            this.requiredSkill = skill;
            this.requiredLevel = level;
        }
    }

    public JournalScreen() {
        super(Component.literal("Player Journal"));
    }

    public static List<UnlockEntry> getListForPage(int page) {
        return switch (page) {
            case 2 -> cachedVitalityUnlocks;
            case 3 -> cachedAgilityUnlocks;
            case 4 -> cachedCombatUnlocks;
            case 5 -> cachedDefenseUnlocks;
            case 6 -> cachedFarmingUnlocks;
            case 7 -> showMiningBlocks ? cachedMiningBlocks : cachedMiningUnlocks;
            case 8 -> cachedSmithingUnlocks;
            case 9 -> cachedArcheryUnlocks;
            case 10 -> cachedFishingUnlocks;
            case 11 -> cachedAlchemyUnlocks;
            default -> new ArrayList<>();
        };
    }

    @SuppressWarnings("unchecked")
    public static void rebuildUnlocksIfNeeded() {
        // Removed the static cache lock so the GUI always rebuilds when opened, ensuring datapack reloads are caught instantly!

        cachedVitalityUnlocks.clear();
        cachedAgilityUnlocks.clear();
        cachedCombatUnlocks.clear();
        cachedDefenseUnlocks.clear();
        cachedFarmingUnlocks.clear();
        cachedMiningUnlocks.clear();
        cachedMiningBlocks.clear();
        cachedSmithingUnlocks.clear();
        cachedArcheryUnlocks.clear();
        cachedFishingUnlocks.clear();
        cachedAlchemyUnlocks.clear();
        cachedVillagers.clear();

        Set<String> allRestrictions = new java.util.LinkedHashSet<>();

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            allRestrictions.addAll((List<String>) JournalConfig.ARMOR_RESTRICTIONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.POTION_RESTRICTIONS.get());
            allRestrictions.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
            allRestrictions.addAll((List<String>) JournalConfig.JEWELRY_RESTRICTIONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.FARMERS_DELIGHT_RESTRICTIONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.PALADINS_PRIESTS_ARMORS.get());
            allRestrictions.addAll((List<String>) JournalConfig.PALADINS_PRIESTS_WEAPONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.PALADINS_PRIESTS_SHIELDS.get());
            allRestrictions.addAll((List<String>) JournalConfig.ROGUES_WARRIORS_ARMORS.get());
            allRestrictions.addAll((List<String>) JournalConfig.ROGUES_WARRIORS_WEAPONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.ARCHERS_ARMORS.get());
            allRestrictions.addAll((List<String>) JournalConfig.ARCHERS_WEAPONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.WIZARDS_ARMORS.get());
            allRestrictions.addAll((List<String>) JournalConfig.WIZARDS_WEAPONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.ARSENAL_WEAPONS.get());
            allRestrictions.addAll((List<String>) JournalConfig.ARTIFACTS_ITEMS.get());
            allRestrictions.addAll((List<String>) JournalConfig.TIDE_ITEMS.get());
            allRestrictions.addAll((List<String>) JournalConfig.GLIDERS_ITEMS.get());
            allRestrictions.addAll((List<String>) JournalConfig.LILIS_LUCKY_LURES_ITEMS.get());
            allRestrictions.addAll((List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get());
            allRestrictions.addAll((List<String>) JournalConfig.IMMERSIVE_AIRCRAFT_ITEMS.get());
            allRestrictions.addAll((List<String>) JournalConfig.SMALL_SHIPS_ITEMS.get());
            allRestrictions.addAll((List<String>) JournalConfig.ALCHEMY_UTILITIES.get());
            allRestrictions.addAll((List<String>) JournalConfig.ENCHANTMENT_RESTRICTIONS.get());
        } else {
            allRestrictions.addAll(ClientPayloadHandler.serverArmorRestrictions);
            allRestrictions.addAll(ClientPayloadHandler.serverPotionRestrictions);
            allRestrictions.addAll(ClientPayloadHandler.serverItemRestrictions);
            allRestrictions.addAll(ClientPayloadHandler.serverJewelryRestrictions);
            allRestrictions.addAll(ClientPayloadHandler.serverFarmersDelightRestrictions);
            allRestrictions.addAll(ClientPayloadHandler.serverPaladinsPriestsArmors);
            allRestrictions.addAll(ClientPayloadHandler.serverPaladinsPriestsWeapons);
            allRestrictions.addAll(ClientPayloadHandler.serverPaladinsPriestsShields);
            allRestrictions.addAll(ClientPayloadHandler.serverRoguesWarriorsArmors);
            allRestrictions.addAll(ClientPayloadHandler.serverRoguesWarriorsWeapons);
            allRestrictions.addAll(ClientPayloadHandler.serverArchersArmors);
            allRestrictions.addAll(ClientPayloadHandler.serverArchersWeapons);
            allRestrictions.addAll(ClientPayloadHandler.serverWizardsArmors);
            allRestrictions.addAll(ClientPayloadHandler.serverWizardsWeapons);
            allRestrictions.addAll(ClientPayloadHandler.serverArsenalWeapons);
            allRestrictions.addAll(ClientPayloadHandler.serverArtifactsItems);
            allRestrictions.addAll(ClientPayloadHandler.serverTideItems);
            allRestrictions.addAll(ClientPayloadHandler.serverGlidersItems);
            allRestrictions.addAll(ClientPayloadHandler.serverLilisLuckyLuresItems);
            allRestrictions.addAll(ClientPayloadHandler.serverImmersiveMachineryItems);
            allRestrictions.addAll(ClientPayloadHandler.serverImmersiveAircraftItems);
            allRestrictions.addAll(ClientPayloadHandler.serverSmallShipsItems);
        }

        for (String restriction : allRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int vitalityReq = -1;
                int combatReq = -1;
                int defenseReq = -1;
                int alchemyReq = -1;

                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2) {
                        if (skillReq[0].equalsIgnoreCase("vitality")) vitalityReq = Integer.parseInt(skillReq[1].trim());
                        else if (skillReq[0].equalsIgnoreCase("combat")) combatReq = Integer.parseInt(skillReq[1].trim());
                        else if (skillReq[0].equalsIgnoreCase("defense")) defenseReq = Integer.parseInt(skillReq[1].trim());
                        else if (skillReq[0].equalsIgnoreCase("alchemy")) alchemyReq = Integer.parseInt(skillReq[1].trim());
                    }
                }

                if (vitalityReq == -1 && combatReq == -1 && defenseReq == -1 && alchemyReq == -1) continue;

                for (String itemId : groupedItems) {
                    itemId = itemId.trim();
                    try {
                        ItemStack displayStack = ItemStack.EMPTY;
                        if (itemId.startsWith("potion:")) {
                            String actualPotionId = itemId.substring(7);
                            var potionHolder = BuiltInRegistries.POTION.getHolder(ResourceLocation.parse(actualPotionId));
                            if (potionHolder.isPresent()) {
                                displayStack = new ItemStack(Items.POTION);
                                displayStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potionHolder.get()));
                            }
                        } else {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
                            if (item != Items.AIR) displayStack = new ItemStack(item);
                        }

                        if (!displayStack.isEmpty()) {
                            if (vitalityReq != -1) cachedVitalityUnlocks.add(new UnlockEntry(displayStack, vitalityReq, false, false));
                            if (combatReq != -1) cachedCombatUnlocks.add(new UnlockEntry(displayStack, combatReq, false, false));
                            if (defenseReq != -1) cachedDefenseUnlocks.add(new UnlockEntry(displayStack, defenseReq, false, false));
                            if (alchemyReq != -1) cachedAlchemyUnlocks.add(new UnlockEntry(displayStack, alchemyReq, false, false));
                        }
                    } catch (Exception e) {}
                }
            }
        }

        Set<String> agilityConfigStrings = new java.util.LinkedHashSet<>();
        agilityConfigStrings.addAll((List<String>) JournalConfig.AGILITY_ITEMS.get());

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            agilityConfigStrings.addAll((List<String>) JournalConfig.JEWELRY_RESTRICTIONS.get());
            agilityConfigStrings.addAll((List<String>) JournalConfig.GLIDERS_ITEMS.get());
            agilityConfigStrings.addAll((List<String>) JournalConfig.IMMERSIVE_AIRCRAFT_ITEMS.get());
            agilityConfigStrings.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            agilityConfigStrings.addAll(ClientPayloadHandler.serverJewelryRestrictions);
            agilityConfigStrings.addAll(ClientPayloadHandler.serverGlidersItems);
            agilityConfigStrings.addAll(ClientPayloadHandler.serverImmersiveAircraftItems);
            agilityConfigStrings.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        for (String restriction : agilityConfigStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("agility")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String itemId : groupedItems) {
                        try {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId.trim()));
                            if (item != Items.AIR) cachedAgilityUnlocks.add(new UnlockEntry(new ItemStack(item), req, false, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> agilityMountStrings = new java.util.LinkedHashSet<>();
        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            agilityMountStrings.addAll((List<String>) JournalConfig.AGILITY_MOUNTS.get());
        } else {
            agilityMountStrings.addAll(ClientPayloadHandler.serverAgilityMounts);
        }

        for (String restriction : agilityMountStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedAnimals = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("agility")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String entityId : groupedAnimals) {
                        try {
                            String eggId = entityId.trim() + "_spawn_egg";
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(eggId));
                            if (item != Items.AIR) cachedAgilityUnlocks.add(new UnlockEntry(new ItemStack(item), req, false, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> farmingConfigStrings = new java.util.LinkedHashSet<>();
        farmingConfigStrings.addAll((List<String>) JournalConfig.FARMING_HOES.get());
        farmingConfigStrings.addAll((List<String>) JournalConfig.FARMING_AXES.get());
        farmingConfigStrings.addAll((List<String>) JournalConfig.FARMING_ITEMS.get());
        farmingConfigStrings.addAll((List<String>) JournalConfig.FARMING_UTILITIES.get());
        farmingConfigStrings.addAll((List<String>) JournalConfig.FARMING_CROPS.get());

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            farmingConfigStrings.addAll((List<String>) JournalConfig.FARMERS_DELIGHT_RESTRICTIONS.get());
            farmingConfigStrings.addAll((List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get());
            farmingConfigStrings.addAll((List<String>) JournalConfig.SMALL_SHIPS_ITEMS.get());
            farmingConfigStrings.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            farmingConfigStrings.addAll(ClientPayloadHandler.serverFarmersDelightRestrictions);
            farmingConfigStrings.addAll(ClientPayloadHandler.serverImmersiveMachineryItems);
            farmingConfigStrings.addAll(ClientPayloadHandler.serverSmallShipsItems);
            farmingConfigStrings.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        for (String restriction : farmingConfigStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("farming")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String itemId : groupedItems) {
                        try {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId.trim()));
                            if (item != Items.AIR) cachedFarmingUnlocks.add(new UnlockEntry(new ItemStack(item), req, false, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        for (String restriction : (List<String>) JournalConfig.FARMING_BREEDING.get()) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedAnimals = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("farming")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String entityId : groupedAnimals) {
                        try {
                            String eggId = entityId.trim() + "_spawn_egg";
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(eggId));
                            if (item != Items.AIR) cachedFarmingUnlocks.add(new UnlockEntry(new ItemStack(item), req, false, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> miningToolStrings = new java.util.LinkedHashSet<>();
        miningToolStrings.addAll((List<String>) JournalConfig.MINING_TOOL_RESTRICTIONS.get());
        miningToolStrings.addAll((List<String>) JournalConfig.CUSTOM_MINING_TOOLS.get());

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            miningToolStrings.addAll((List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get());
            miningToolStrings.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            miningToolStrings.addAll(ClientPayloadHandler.serverImmersiveMachineryItems);
            miningToolStrings.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        Set<String> addedToolIds = new HashSet<>();
        for (String restriction : miningToolStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int miningReq = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("mining")) {
                        miningReq = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (miningReq != -1) {
                    for (String itemId : groupedItems) {
                        String cleanId = itemId.trim();
                        if (addedToolIds.add(cleanId)) {
                            try {
                                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(cleanId));
                                if (item != Items.AIR) cachedMiningUnlocks.add(new UnlockEntry(new ItemStack(item), miningReq, false, false));
                            } catch (Exception e) {}
                        }
                    }
                }
            }
        }


        Set<String> miningBlockStrings = new java.util.LinkedHashSet<>();
        miningBlockStrings.addAll(JournalConfig.getAllBlockRestrictions());
        miningBlockStrings.addAll(JournalConfig.getUtilityBlockRestrictions());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_COAL.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_COPPER.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_IRON.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_GOLD.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_LAPIS.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_EMERALD.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_DIAMOND.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_QUARTZ.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.MINING_NETHERITE.get());
        miningBlockStrings.addAll((List<String>) JournalConfig.CUSTOM_MINING_BLOCKS.get());

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            miningBlockStrings.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            miningBlockStrings.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        Set<String> addedBlockIds = new HashSet<>();
        for (String restriction : miningBlockStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int miningReq = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("mining")) {
                        miningReq = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (miningReq != -1) {
                    for (String itemId : groupedItems) {
                        String cleanId = itemId.trim();
                        if (addedBlockIds.add(cleanId)) {
                            try {
                                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(cleanId));
                                if (item != Items.AIR) cachedMiningBlocks.add(new UnlockEntry(new ItemStack(item), miningReq, false, false));
                            } catch (Exception e) {}
                        }
                    }
                }
            }
        }

        Set<String> smithingRestrictions = new java.util.LinkedHashSet<>();
        smithingRestrictions.addAll(JournalConfig.getSmithingUtilities());

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            smithingRestrictions.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            smithingRestrictions.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        for (String restriction : smithingRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("smithing")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String itemId : groupedItems) {
                        try {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId.trim()));
                            if (item != Items.AIR) cachedSmithingUnlocks.add(new UnlockEntry(new ItemStack(item), req, false, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> craftingRestrictions = new java.util.LinkedHashSet<>();
        craftingRestrictions.addAll(ClientPayloadHandler.serverCraftingRestrictions);

        for (String restriction : craftingRestrictions) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("smithing")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String itemId : groupedItems) {
                        try {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId.trim()));
                            if (item != Items.AIR) cachedSmithingUnlocks.add(new UnlockEntry(new ItemStack(item), req, true, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> archeryConfigStrings = new java.util.LinkedHashSet<>();
        archeryConfigStrings.addAll((List<String>) JournalConfig.ARCHERY_BOWS.get());
        archeryConfigStrings.addAll((List<String>) JournalConfig.ARCHERY_CROSSBOWS.get());
        archeryConfigStrings.addAll((List<String>) JournalConfig.ARCHERY_TRIDENTS.get());
        archeryConfigStrings.addAll((List<String>) JournalConfig.ARCHERY_ARROWS.get());

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            archeryConfigStrings.addAll((List<String>) JournalConfig.JEWELRY_RESTRICTIONS.get());
            archeryConfigStrings.addAll((List<String>) JournalConfig.ARCHERS_ARMORS.get());
            archeryConfigStrings.addAll((List<String>) JournalConfig.ARCHERS_WEAPONS.get());
            archeryConfigStrings.addAll((List<String>) JournalConfig.ARSENAL_WEAPONS.get());
            archeryConfigStrings.addAll((List<String>) JournalConfig.TIDE_ITEMS.get());
            archeryConfigStrings.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            archeryConfigStrings.addAll(ClientPayloadHandler.serverJewelryRestrictions);
            archeryConfigStrings.addAll(ClientPayloadHandler.serverArchersArmors);
            archeryConfigStrings.addAll(ClientPayloadHandler.serverArchersWeapons);
            archeryConfigStrings.addAll(ClientPayloadHandler.serverArsenalWeapons);
            archeryConfigStrings.addAll(ClientPayloadHandler.serverTideItems);
            archeryConfigStrings.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        for (String restriction : archeryConfigStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("archery")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String itemId : groupedItems) {
                        try {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId.trim()));
                            if (item != Items.AIR) cachedArcheryUnlocks.add(new UnlockEntry(new ItemStack(item), req, false, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> fishingConfigStrings = new java.util.LinkedHashSet<>();
        fishingConfigStrings.addAll((List<String>) JournalConfig.FISHING_RODS.get());
        fishingConfigStrings.addAll((List<String>) JournalConfig.FISHING_OCEAN_BLOCKS.get());
        fishingConfigStrings.addAll((List<String>) JournalConfig.FISHING_DIVING_GEAR.get());
        fishingConfigStrings.addAll((List<String>) JournalConfig.FISHING_CUSTOM.get());

        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            fishingConfigStrings.addAll((List<String>) JournalConfig.JEWELRY_RESTRICTIONS.get());
            fishingConfigStrings.addAll((List<String>) JournalConfig.ARCHERS_ARMORS.get());
            fishingConfigStrings.addAll((List<String>) JournalConfig.ARCHERS_WEAPONS.get());
            fishingConfigStrings.addAll((List<String>) JournalConfig.ARSENAL_WEAPONS.get());
            fishingConfigStrings.addAll((List<String>) JournalConfig.LILIS_LUCKY_LURES_ITEMS.get());
            fishingConfigStrings.addAll((List<String>) JournalConfig.IMMERSIVE_MACHINERY_ITEMS.get());
            fishingConfigStrings.addAll((List<String>) JournalConfig.TIDE_ITEMS.get());
            fishingConfigStrings.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            fishingConfigStrings.addAll(ClientPayloadHandler.serverJewelryRestrictions);
            fishingConfigStrings.addAll(ClientPayloadHandler.serverArchersArmors);
            fishingConfigStrings.addAll(ClientPayloadHandler.serverArchersWeapons);
            fishingConfigStrings.addAll(ClientPayloadHandler.serverArsenalWeapons);
            fishingConfigStrings.addAll(ClientPayloadHandler.serverLilisLuckyLuresItems);
            fishingConfigStrings.addAll(ClientPayloadHandler.serverImmersiveMachineryItems);
            fishingConfigStrings.addAll(ClientPayloadHandler.serverTideItems);
            fishingConfigStrings.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        for (String restriction : fishingConfigStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("fishing")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String itemId : groupedItems) {
                        try {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId.trim()));
                            if (item != Items.AIR) cachedFishingUnlocks.add(new UnlockEntry(new ItemStack(item), req, false, false));
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> alchemyConfigStrings = new java.util.LinkedHashSet<>();
        if (ClientPayloadHandler.serverArmorRestrictions.isEmpty()) {
            alchemyConfigStrings.addAll((List<String>) JournalConfig.ALCHEMY_UTILITIES.get());
            alchemyConfigStrings.addAll((List<String>) JournalConfig.ENCHANTMENT_RESTRICTIONS.get());
            alchemyConfigStrings.addAll(com.player.journal.network.ClientPayloadHandler.serverItemRestrictions);
        } else {
            alchemyConfigStrings.addAll(ClientPayloadHandler.serverAlchemyUtilities);
            alchemyConfigStrings.addAll(ClientPayloadHandler.serverEnchantmentRestrictions);
            alchemyConfigStrings.addAll(ClientPayloadHandler.serverItemRestrictions);
        }

        for (String restriction : alchemyConfigStrings) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String[] groupedItems = parts[0].split(",");
                int req = -1;
                for (int i = 1; i < parts.length; i++) {
                    String[] skillReq = parts[i].split(":");
                    if (skillReq.length == 2 && skillReq[0].equalsIgnoreCase("alchemy")) {
                        req = Integer.parseInt(skillReq[1].trim());
                        break;
                    }
                }
                if (req != -1) {
                    for (String itemId : groupedItems) {
                        itemId = itemId.trim();
                        try {
                            ItemStack displayStack = ItemStack.EMPTY;

                            var level = net.minecraft.client.Minecraft.getInstance().level;
                            if (level != null) {
                                var enchantReg = level.registryAccess().registry(net.minecraft.core.registries.Registries.ENCHANTMENT);
                                ResourceLocation loc = ResourceLocation.parse(itemId);
                                if (enchantReg.isPresent() && enchantReg.get().containsKey(loc)) {
                                    var holder = enchantReg.get().getHolder(loc);
                                    if (holder.isPresent()) {
                                        displayStack = net.minecraft.world.item.EnchantedBookItem.createForEnchantment(new net.minecraft.world.item.enchantment.EnchantmentInstance(holder.get(), 1));
                                    }
                                }
                            }

                            if (displayStack.isEmpty()) {
                                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
                                if (item != Items.AIR) displayStack = new ItemStack(item);
                            }

                            if (!displayStack.isEmpty()) {
                                boolean isEnchantedBook = displayStack.is(Items.ENCHANTED_BOOK);
                                cachedAlchemyUnlocks.add(new UnlockEntry(displayStack, req, false, isEnchantedBook));
                            }
                        } catch (Exception e) {}
                    }
                }
            }
        }

        Set<String> villagerConfig = new java.util.LinkedHashSet<>((List<String>) JournalConfig.VILLAGER_RESTRICTIONS.get());
        for (String restriction : villagerConfig) {
            String[] parts = restriction.split(";");
            if (parts.length >= 2) {
                String fullId = parts[0];
                String profName = fullId.contains(":") ? fullId.split(":")[1] : fullId;
                profName = profName.substring(0, 1).toUpperCase() + profName.substring(1);

                String[] skillReq = parts[1].split(":");
                if (skillReq.length == 2) {
                    cachedVillagers.add(new VillagerEntry(profName, skillReq[0].toLowerCase(), Integer.parseInt(skillReq[1].trim())));
                }
            }
        }

        cachedVitalityUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedAgilityUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedCombatUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedDefenseUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedFarmingUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedMiningUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedMiningBlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedSmithingUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedArcheryUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedFishingUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedAlchemyUnlocks.sort(Comparator.comparingInt(entry -> entry.requiredLevel));
        cachedVillagers.sort(Comparator.comparing(v -> v.displayName));

        cachedVillagers.add(new VillagerEntry("Wandering Trader", "all_classes", 1));

        listsBuilt = true;
    }

    private boolean isPageUnlocked(int pageIndex) {
        return switch (pageIndex) {
            case 0, 1, 2, 3, 4, 5 -> true; // Pages 0 to 5 (Trading, Vitality, Agility, Combat, Defense) are unlocked by default
            case 6 -> ClientPayloadHandler.farmingLevel >= 1;
            case 7 -> ClientPayloadHandler.miningLevel >= 1;
            case 8 -> ClientPayloadHandler.smithingLevel >= 1;
            case 9 -> ClientPayloadHandler.archeryLevel >= 1;
            case 10 -> ClientPayloadHandler.fishingLevel >= 1;
            case 11 -> ClientPayloadHandler.alchemyLevel >= 1;
            default -> false;
        };
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.backButton = new PageButton(x + 20, y + 160, false, button -> {
            if (this.currentPage > 0) {
                this.currentPage--;
                this.scrollOffset = 0;
                this.updateButtons();
                this.init(this.minecraft, this.width, this.height);
            }
        }, true);

        this.forwardButton = new PageButton(x + 355, y + 160, true, button -> {
            if (this.currentPage < PAGES.length - 1) {
                this.currentPage++;
                this.scrollOffset = 0;
                this.updateButtons();
                this.init(this.minecraft, this.width, this.height);
            }
        }, true);

        this.addRenderableWidget(this.backButton);
        this.addRenderableWidget(this.forwardButton);
        this.updateButtons();

        loadPins();
        rebuildUnlocksIfNeeded();

        if (this.currentPage >= 1 && this.currentPage <= 11 && this.currentPage != 1 && isPageUnlocked(this.currentPage)) {
            boolean isPinned = PINNED_PAGES.contains(this.currentPage);
            int pinX = x + 340;
            int pinY = y + 15;

            this.addRenderableWidget(Button.builder(Component.literal(isPinned ? "📌 Pinned" : "📍 Pin"), button -> {
                if (isPinned) PINNED_PAGES.remove(this.currentPage);
                else PINNED_PAGES.add(this.currentPage);

                savePins();
                this.init(this.minecraft, this.width, this.height);
            }).bounds(pinX, pinY, 50, 20).build());

            // --- MINING SUB-TAB TOGGLE BUTTON ---
            if (this.currentPage == 7) {
                int tabX = pinX;
                int tabY = pinY + 23;
                String tabLabel = showMiningBlocks ? "Tools" : "Blocks";

                this.addRenderableWidget(Button.builder(Component.literal(tabLabel), button -> {
                    showMiningBlocks = !showMiningBlocks;
                    this.scrollOffset = 0;
                    this.init(this.minecraft, this.width, this.height);
                }).bounds(tabX, tabY, 50, 20).build());
            }
        }

        // --- NEW: PARTY INVITE BUTTONS ---
        if (ClientPayloadHandler.pendingInvites != null && !ClientPayloadHandler.pendingInvites.isEmpty()) {
            int headSize = 32;
            int leftPageCenter = x + 114;
            int startX = leftPageCenter - (headSize / 2);
            int topY = y + 25; // Same Y level as the party heads

            // Accept Button (Green Tick)
            this.addRenderableWidget(Button.builder(Component.literal("§a✔"), button -> {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                        new com.player.journal.network.PartyInviteResponsePayload(true, ClientPayloadHandler.pendingInvites.get(0).name)
                );
                ClientPayloadHandler.pendingInvites.remove(0); // Clear the invite from the client
                this.init(this.minecraft, this.width, this.height); // Refresh the screen
            }).bounds(startX - 25, topY + 6, 20, 20).build());

            // Decline Button (Red Cross)
            this.addRenderableWidget(Button.builder(Component.literal("§c✖"), button -> {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                        new com.player.journal.network.PartyInviteResponsePayload(false, ClientPayloadHandler.pendingInvites.get(0).name)
                );
                ClientPayloadHandler.pendingInvites.remove(0); // Clear the invite from the client
                this.init(this.minecraft, this.width, this.height); // Refresh the screen
            }).bounds(startX + headSize + 5, topY + 6, 20, 20).build());
        }
    }

    private void updateButtons() {
        this.backButton.visible = this.currentPage > 0;
        this.forwardButton.visible = this.currentPage < PAGES.length - 1;
    }

    private String getLockedText(int pageIndex) {
        return switch (pageIndex) {
            case 6 -> "Write these pages to start your farming chapter";
            case 7 -> "Write these pages to start your mining chapter";
            case 8 -> "Write these pages to start your smithing chapter";
            case 9 -> "Write these pages to start your archery chapter";
            case 10 -> "Write these pages to start your fishing chapter";
            case 11 -> "Write these pages to start your alchemy chapter";
            default -> "Write these pages to unlock chapter";
        };
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.currentPage >= 0 && this.currentPage <= 11) {
            int maxScroll = 0;
            if (this.currentPage == 0) {
                maxScroll = this.maxLeftScroll;
            } else if (this.currentPage == 1) {
                int listHeight = 135;
                maxScroll = Math.max(0, (cachedVillagers.size() * 12) - listHeight);
            } else {
                List<UnlockEntry> activeList = getListForPage(this.currentPage);
                Map<Integer, List<UnlockEntry>> groupedItems = new TreeMap<>();
                for (UnlockEntry entry : activeList) {
                    groupedItems.putIfAbsent(entry.requiredLevel, new ArrayList<>());
                    groupedItems.get(entry.requiredLevel).add(entry);
                }

                int itemsPerRow = 6;
                int totalHeight = 0;
                for (Map.Entry<Integer, List<UnlockEntry>> entry : groupedItems.entrySet()) {
                    totalHeight += 12;
                    int itemCount = entry.getValue().size();
                    int rows = (int) Math.ceil((double) itemCount / itemsPerRow);
                    totalHeight += (rows * 20);
                    totalHeight += 5;
                }

                int listHeight = 135;
                maxScroll = Math.max(0, totalHeight - listHeight);
            }

            this.scrollOffset = Mth.clamp(this.scrollOffset - (scrollY * 15.0), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x = (this.width - this.imageWidth) / 2;
            int y = (this.height - this.imageHeight) / 2;

            if (this.currentPage == 0) {
                int rightPageX = x + 235;
                int startY = y + 39;

                // Existing chapter clicks
                if (mouseX >= rightPageX && mouseX <= rightPageX + 120) {
                    if (mouseY >= startY && mouseY < startY + (11 * 11)) {
                        int clickedIndex = (int) ((mouseY - startY) / 11);
                        int targetPage = clickedIndex + 1;

                        if (targetPage >= 1 && targetPage <= 11) {
                            this.currentPage = targetPage;
                            this.scrollOffset = 0;
                            this.updateButtons();
                            this.init(this.minecraft, this.width, this.height);

                            if (this.minecraft != null) {
                                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            }
                            return true;
                        }
                    }
                }

                // --- NEW: CREATE BOOK TEXT CLICK LOGIC ---
                int rightListY = startY + (10 * 11); // After the 11 list items are drawn
                rightListY += 14; // Space for Pages Available
                int createBtnY = rightListY + 12; // Space for the Create Book button
                String createBtnText = "• Create Book (5 Pages)";
                int textWidth = this.minecraft.font.width(createBtnText);

                if (mouseX >= rightPageX && mouseX <= rightPageX + textWidth && mouseY >= createBtnY && mouseY <= createBtnY + 11) {
                    if (ClientPayloadHandler.tornPages >= 5) {
                        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.player.journal.network.RipPagesPayload());
                        if (this.minecraft != null) {
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        }
                    } else {
                        if (this.minecraft != null && this.minecraft.player != null) {
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.VILLAGER_NO, 1.0F));
                        }
                    }
                    return true;
                }
            }

            // --- PADLOCK CLICK LOGIC ---
            if (this.currentPage >= 6 && this.currentPage <= 11 && !isPageUnlocked(this.currentPage)) {
                int padSize = 48;
                int padX = x + (imageWidth / 2) - (padSize / 2);
                int padY = y + imageHeight + 10;

                if (mouseX >= padX && mouseX <= padX + padSize && mouseY >= padY && mouseY <= padY + padSize) {
                    int cost = 5;
                    if (ClientPayloadHandler.tornPages >= cost) {
                        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.player.journal.network.UnlockPagePayload(this.currentPage));
                        if (this.minecraft != null) {
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        }
                    } else {
                        if (this.minecraft != null && this.minecraft.player != null) {
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.VILLAGER_NO, 1.0F));
                        }
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x90000000);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        boolean isUnlocked = isPageUnlocked(currentPage);
        float brightness = isUnlocked ? 1.0F : 0.8F;

        guiGraphics.setColor(brightness, brightness, brightness, 1.0F);
        guiGraphics.blit(PAGES[currentPage], x, y, imageWidth, imageHeight, 0.0f, 0.0f, 1000, 500, 1000, 500);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawContentRow(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, String name, int level, boolean isUnlocked) {
        boolean isHovered = mouseX >= x && mouseX <= x + 120 && mouseY >= y && mouseY < y + 11;

        if (!isUnlocked) {
            guiGraphics.drawString(this.font, "• " + name + " (Locked)", x, y, isHovered ? 0xFFFF5555 : 0xFF8B0000, false);
        } else {
            String text = name.equals("Trading") ? "• Trading Directory" : "• " + name + " (Lvl " + level + ")";
            guiGraphics.drawString(this.font, text, x, y, isHovered ? 0xFF009900 : 0xFF006400, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        long currentTime = Util.getMillis();
        double deltaTime = (currentTime - this.lastFrameTime) / 1000.0;
        this.lastFrameTime = currentTime;

        if (deltaTime > 0.1) deltaTime = 0.1;
        double autoScrollSpeed = 30.0;

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        boolean isUnlocked = isPageUnlocked(currentPage);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        ItemStack hoveredStack = null;

        // ----------------------------------------------------
        // PAGE 0: CONTENTS SCREEN (Classic Two-Page)
        // ----------------------------------------------------
        if (this.currentPage == 0) {
            int rightPageX = x + 235;
            int rightPageY = y + 25;

            guiGraphics.drawString(this.font, "Journal Contents:", rightPageX, rightPageY, 0xFF333333, false);

            int rightListY = rightPageY + 14;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Trading", 0, true); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Vitality", ClientPayloadHandler.vitalityLevel, true); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Agility", ClientPayloadHandler.agilityLevel, true); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Combat", ClientPayloadHandler.combatLevel, true); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Defense", ClientPayloadHandler.defenseLevel, true); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Farming", ClientPayloadHandler.farmingLevel, ClientPayloadHandler.hasFarming); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Mining", ClientPayloadHandler.miningLevel, ClientPayloadHandler.hasMining); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Smithing", ClientPayloadHandler.smithingLevel, ClientPayloadHandler.hasSmithing); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Archery", ClientPayloadHandler.archeryLevel, ClientPayloadHandler.hasArchery); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Fishing", ClientPayloadHandler.fishingLevel, ClientPayloadHandler.hasFishing); rightListY += 11;
            drawContentRow(guiGraphics, rightPageX, rightListY, mouseX, mouseY, "Alchemy", ClientPayloadHandler.alchemyLevel, ClientPayloadHandler.hasAlchemy);

            rightListY += 14; // Give some breathing room
            guiGraphics.drawString(this.font, "Pages Available: " + ClientPayloadHandler.tornPages, rightPageX, rightListY, 0xFF8B0000, false);

            // --- NEW: CREATE BOOK TEXT BUTTON ---
            rightListY += 12;
            String createBtnText = "• Create Book (5 Pages)";
            boolean canAfford = ClientPayloadHandler.tornPages >= 5;
            int textWidth = this.font.width(createBtnText);

            boolean isHoveringBtn = mouseX >= rightPageX && mouseX <= rightPageX + textWidth && mouseY >= rightListY && mouseY < rightListY + 11;
            int btnColor = canAfford ? (isHoveringBtn ? 0xFF009900 : 0xFF006400) : (isHoveringBtn ? 0xFFFF5555 : 0xFF8B0000);

            guiGraphics.drawString(this.font, createBtnText, rightPageX, rightListY, btnColor, false);

            int leftPageX = x + 58;
            int leftPageCenter = x + 114;
            int topY = y + 25;

            net.minecraft.client.player.LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null) {

                // --- NEW: CHECK FOR PENDING INVITES FIRST ---
                if (ClientPayloadHandler.pendingInvites != null && !ClientPayloadHandler.pendingInvites.isEmpty()) {
                    ClientPayloadHandler.PartyMember inviter = ClientPayloadHandler.pendingInvites.get(0);
                    int headSize = 32;
                    int startX = leftPageCenter - (headSize / 2);

                    String inviteText = "Invite From:";
                    guiGraphics.drawString(this.font, inviteText, leftPageCenter - (this.font.width(inviteText) / 2), topY - 12, 0xFF333333, false);
                    net.minecraft.client.gui.components.PlayerFaceRenderer.draw(guiGraphics, inviter.skin, startX, topY, headSize);

                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(startX + (headSize / 2f), topY + headSize + 4, 0);
                    int nameWidth = this.font.width(inviter.name);
                    guiGraphics.drawString(this.font, inviter.name, -(nameWidth / 2), 0, 0xFF006400, false);
                    guiGraphics.pose().popPose();
                }
                // --- IF NO INVITES, DRAW NORMAL PARTY MEMBERS ---
                else {
                    List<ClientPayloadHandler.PartyMember> party = ClientPayloadHandler.partyMembers;
                    if (party == null || party.isEmpty()) {
                        party = List.of(new ClientPayloadHandler.PartyMember(player.getName().getString(), player.getSkin().texture(), true));
                    }

                    int partySize = Math.min(4, party.size());
                    int headSize = partySize >= 3 ? 20 : 32; // Shrink to fit 3 or 4 across the page
                    int spacing = 5;
                    int totalWidth = (partySize * headSize) + ((partySize - 1) * spacing);
                    int startX = leftPageCenter - (totalWidth / 2);

                    for (int i = 0; i < partySize; i++) {
                        ClientPayloadHandler.PartyMember member = party.get(i);
                        int currentHeadX = startX + (i * (headSize + spacing));

                        net.minecraft.client.gui.components.PlayerFaceRenderer.draw(guiGraphics, member.skin, currentHeadX, topY, headSize);

                        // Dynamically scale the usernames so they don't overlap!
                        guiGraphics.pose().pushPose();
                        float fontScale = partySize >= 3 ? 0.65f : 1.0f;
                        guiGraphics.pose().translate(currentHeadX + (headSize / 2f), topY + headSize + 4, 0);
                        guiGraphics.pose().scale(fontScale, fontScale, 1.0f);

                        int nameWidth = this.font.width(member.name);
                        guiGraphics.drawString(this.font, member.name, -(nameWidth / 2), 0, 0xFF006400, false);
                        guiGraphics.pose().popPose();
                    }
                }

                // Always use 32 for the spacing here so the attributes list doesn't jump up and down
                // when switching between large heads (invites) and small heads (full party).
                int attrTitleY = topY + 32 + 26;

                guiGraphics.drawString(this.font, "Player Attributes:", leftPageX, attrTitleY, 0xFF333333, false);

                double maxHealth = player.getMaxHealth();
                double speed = player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
                double jump = player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH);
                double attack = player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                double armor = player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR);

                int speedPct = (int) Math.round((speed / 0.1) * 100);
                int jumpPct = (int) Math.round((jump / 0.42) * 100);

                List<String> attrList = new ArrayList<>();
                attrList.add("• Max Health: " + (int)maxHealth + " HP");
                attrList.add("• Speed: " + speedPct + "%");
                attrList.add("• Jump Height: " + jumpPct + "%");
                attrList.add("• Base Damage: " + String.format("%.1f", attack));
                attrList.add("• Armor Rating: " + (int)armor);

                if (net.neoforged.fml.ModList.get().isLoaded("puffish_attributes")) {
                    String[] puffishIds = {"melee_damage", "ranged_damage", "magic_damage", "healing", "resistance", "mining_speed", "sprinting_speed", "life_steal", "fortune", "experience"};
                    String[] puffishNames = {"Melee Dmg", "Ranged Dmg", "Magic Dmg", "Healing", "Resistance", "Mining Spd", "Sprint Spd", "Life Steal", "Fortune", "Exp Bonus"};

                    for (int i = 0; i < puffishIds.length; i++) {
                        try {
                            net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("puffish_attributes", puffishIds[i]);
                            var attrHolder = BuiltInRegistries.ATTRIBUTE.getHolder(rl);
                            if (attrHolder.isPresent() && player.getAttributes().hasAttribute(attrHolder.get())) {
                                double val = player.getAttributeValue(attrHolder.get());
                                if (val > 0 || val < 0) {
                                    attrList.add("• " + puffishNames[i] + ": " + String.format("%.2f", val));
                                }
                            }
                        } catch (Exception e) {}
                    }
                }

                int listTop = attrTitleY + 16;
                int listBottom = y + 175;
                this.maxLeftScroll = Math.max(0, (attrList.size() * 12) - (listBottom - listTop));

                boolean isHoveringLeftList = mouseX >= leftPageX - 5 && mouseX <= leftPageCenter + 55 && mouseY >= listTop && mouseY <= listBottom;
                if (!isHoveringLeftList && this.maxLeftScroll > 0) {
                    this.scrollOffset += autoScrollSpeed * deltaTime;
                    if (this.scrollOffset > this.maxLeftScroll + 20) this.scrollOffset = -10;
                }

                double renderOffset = Mth.clamp(this.scrollOffset, 0, this.maxLeftScroll);
                guiGraphics.enableScissor(leftPageX - 5, listTop, leftPageCenter + 55, listBottom);

                int currentY = listTop - (int) renderOffset;

                for (String attrLine : attrList) {
                    if (currentY > listBottom || currentY < listTop - 12) {
                        currentY += 12;
                        continue;
                    }
                    guiGraphics.drawString(this.font, attrLine, leftPageX, currentY, 0xFF555555, false);
                    currentY += 12;
                }

                guiGraphics.disableScissor();
            }
        }
        // ----------------------------------------------------
        // PAGE 1: TRADING DIRECTORY
        // ----------------------------------------------------
        else if (this.currentPage == 1) {
            int leftPageX = x + 70;
            int rightPageX = x + 225;

            guiGraphics.drawString(this.font, "Trading Directory:", leftPageX, y + 20, 0xFF000000, false);

            guiGraphics.drawString(this.font, "Trade with villagers to", leftPageX, y + 40, 0xFF333333, false);
            guiGraphics.drawString(this.font, "obtain rare items.", leftPageX, y + 51, 0xFF333333, false);

            guiGraphics.drawString(this.font, "Different professions", leftPageX, y + 73, 0xFF333333, false);
            guiGraphics.drawString(this.font, "require different", leftPageX, y + 84, 0xFF333333, false);
            guiGraphics.drawString(this.font, "skill levels to unlock.", leftPageX, y + 95, 0xFF333333, false);

            guiGraphics.drawString(this.font, "Check the list on the", leftPageX, y + 117, 0xFF333333, false);
            guiGraphics.drawString(this.font, "right for requirements.", leftPageX, y + 128, 0xFF333333, false);

            int listTop = y + 40;
            int listBottom = y + 175;
            int listLeft = rightPageX - 5;
            int listWidth = 155;

            int listHeight = listBottom - listTop;
            int maxScroll = Math.max(0, (cachedVillagers.size() * 12) - listHeight);
            boolean isHoveringList = mouseX >= listLeft && mouseX <= listLeft + listWidth && mouseY >= listTop && mouseY <= listBottom;

            if (!isHoveringList && maxScroll > 0) {
                this.scrollOffset += autoScrollSpeed * deltaTime;
                if (this.scrollOffset > maxScroll + 20) this.scrollOffset = -10;
            }

            double renderOffset = Mth.clamp(this.scrollOffset, 0, maxScroll);
            guiGraphics.enableScissor(listLeft, listTop, listLeft + listWidth, listBottom);

            int currentY = listTop - (int) renderOffset;

            for (VillagerEntry entry : cachedVillagers) {
                if (currentY > listBottom || currentY < listTop - 12) {
                    currentY += 12;
                    continue;
                }

                boolean isVillagerUnlocked = false;
                switch (entry.requiredSkill) {
                    case "vitality" -> isVillagerUnlocked = ClientPayloadHandler.vitalityLevel >= entry.requiredLevel;
                    case "agility" -> isVillagerUnlocked = ClientPayloadHandler.agilityLevel >= entry.requiredLevel;
                    case "combat" -> isVillagerUnlocked = ClientPayloadHandler.combatLevel >= entry.requiredLevel;
                    case "defense" -> isVillagerUnlocked = ClientPayloadHandler.defenseLevel >= entry.requiredLevel;
                    case "farming" -> isVillagerUnlocked = ClientPayloadHandler.farmingLevel >= entry.requiredLevel;
                    case "mining" -> isVillagerUnlocked = ClientPayloadHandler.miningLevel >= entry.requiredLevel;
                    case "smithing" -> isVillagerUnlocked = ClientPayloadHandler.smithingLevel >= entry.requiredLevel;
                    case "archery" -> isVillagerUnlocked = ClientPayloadHandler.archeryLevel >= entry.requiredLevel;
                    case "fishing" -> isVillagerUnlocked = ClientPayloadHandler.fishingLevel >= entry.requiredLevel;
                    case "alchemy" -> isVillagerUnlocked = ClientPayloadHandler.alchemyLevel >= entry.requiredLevel;
                    case "all_classes" -> isVillagerUnlocked = ClientPayloadHandler.farmingLevel >= 1 &&
                            ClientPayloadHandler.miningLevel >= 1 &&
                            ClientPayloadHandler.smithingLevel >= 1 &&
                            ClientPayloadHandler.archeryLevel >= 1 &&
                            ClientPayloadHandler.fishingLevel >= 1;
                }

                int textColor = isVillagerUnlocked ? 0xFF006400 : 0xFF8B0000;
                String skillDisplay = entry.requiredSkill.equals("all_classes") ? "All" : entry.requiredSkill.substring(0, 1).toUpperCase() + entry.requiredSkill.substring(1);

                // --- FORMATTING: Removed "Lvl" so it now displays cleanly as (Smithing 8) at full 100% font size! ---
                String displayString = "• " + entry.displayName + " (" + skillDisplay + " " + entry.requiredLevel + ")";

                int stringWidth = this.font.width(displayString);
                if (stringWidth > listWidth) {
                    guiGraphics.pose().pushPose();
                    float scale = (float) listWidth / (float) stringWidth;
                    guiGraphics.pose().translate(listLeft, currentY, 0);
                    guiGraphics.pose().scale(scale, scale, 1.0f);
                    guiGraphics.drawString(this.font, displayString, 0, 0, textColor, false);
                    guiGraphics.pose().popPose();
                } else {
                    guiGraphics.drawString(this.font, displayString, listLeft, currentY, textColor, false);
                }

                currentY += 12;
            }

            guiGraphics.disableScissor();
        }
        // ----------------------------------------------------
        // RESTRICTION SCREENS (Double Page)
        // ----------------------------------------------------
        else if (this.currentPage >= 2 && this.currentPage <= 11) {

            int leftPageX = x + 70;
            int leftPageY = y + 25;
            int rightPageX = x + 225;

            int currentLevel = 0;
            float currentXP = 0;
            List<UnlockEntry> activeList = new ArrayList<>();
            int baseReq = 1;
            double multiplier = 1;
            String titleLine1 = "";
            String titleLine2 = "";
            String titleLine3 = "";
            String statName = "";
            String customDescription = "";

            if (this.currentPage == 2) {
                currentLevel = ClientPayloadHandler.vitalityLevel;
                currentXP = ClientPayloadHandler.vitalityXP;
                activeList = cachedVitalityUnlocks;
                baseReq = JournalConfig.XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.XP_MULTIPLIER.get();
                titleLine1 = "Heal to increase";
                titleLine2 = "maximum HP";
                statName = "Vitality Level: ";
            } else if (this.currentPage == 3) {
                currentLevel = ClientPayloadHandler.agilityLevel;
                currentXP = ClientPayloadHandler.agilityXP;
                activeList = cachedAgilityUnlocks;
                baseReq = JournalConfig.AGILITY_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.AGILITY_XP_MULTIPLIER.get();
                titleLine1 = "Explore the world";
                titleLine2 = "to increase agility";
                statName = "Agility Level: ";
            } else if (this.currentPage == 4) {
                currentLevel = ClientPayloadHandler.combatLevel;
                currentXP = ClientPayloadHandler.combatXP;
                activeList = cachedCombatUnlocks;
                baseReq = JournalConfig.COMBAT_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.COMBAT_XP_MULTIPLIER.get();
                titleLine1 = "Kill mobs to";
                titleLine2 = "level up combat to";
                titleLine3 = "unlock new weapons ";
                statName = "Combat Level: ";
            } else if (this.currentPage == 5) {
                currentLevel = ClientPayloadHandler.defenseLevel;
                currentXP = ClientPayloadHandler.defenseXP;
                activeList = cachedDefenseUnlocks;
                baseReq = JournalConfig.DEFENSE_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.DEFENSE_XP_MULTIPLIER.get();
                titleLine1 = "Absorb hits & block to";
                titleLine2 = "level up defense";
                titleLine3 = "and unlock new armor";
                statName = "Defense Level: ";
            } else if (this.currentPage == 6) {
                currentLevel = ClientPayloadHandler.farmingLevel;
                currentXP = ClientPayloadHandler.farmingXP;
                activeList = cachedFarmingUnlocks;
                baseReq = JournalConfig.FARMING_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.FARMING_XP_MULTIPLIER.get();
                titleLine1 = "Harvest crops and";
                titleLine2 = "breed animals to";
                titleLine3 = "level up farming";
                statName = "Farming Level: ";
            } else if (this.currentPage == 7) {
                currentLevel = ClientPayloadHandler.miningLevel;
                currentXP = ClientPayloadHandler.miningXP;
                activeList = showMiningBlocks ? cachedMiningBlocks : cachedMiningUnlocks;
                baseReq = JournalConfig.MINING_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.MINING_XP_MULTIPLIER.get();
                titleLine1 = "Mine ores to ";
                titleLine2 = "level up mining";
                titleLine3 = "and unlock new tools";
                statName = "Mining Level: ";
            } else if (this.currentPage == 8) {
                currentLevel = ClientPayloadHandler.smithingLevel;
                currentXP = ClientPayloadHandler.smithingXP;
                activeList = cachedSmithingUnlocks;
                baseReq = JournalConfig.SMITHING_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.SMITHING_XP_MULTIPLIER.get();
                titleLine1 = "Forge gear to ";
                titleLine2 = "level up smithing and";
                titleLine3 = "unlock crafting recipes";
                statName = "Smithing Level: ";
            } else if (this.currentPage == 9) {
                currentLevel = ClientPayloadHandler.archeryLevel;
                currentXP = ClientPayloadHandler.archeryXP;
                activeList = cachedArcheryUnlocks;
                baseReq = JournalConfig.ARCHERY_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.ARCHERY_XP_MULTIPLIER.get();
                titleLine1 = "Land shots to";
                titleLine2 = "level up archery";
                titleLine3 = "and unlock new gear";
                statName = "Archery Level: ";
            } else if (this.currentPage == 10) {
                currentLevel = ClientPayloadHandler.fishingLevel;
                currentXP = ClientPayloadHandler.fishingXP;
                activeList = cachedFishingUnlocks;
                baseReq = JournalConfig.FISHING_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.FISHING_XP_MULTIPLIER.get();
                titleLine1 = "Reel in catches to";
                titleLine2 = "level up fishing";
                titleLine3 = "and unlock new gear";
                statName = "Fishing Level: ";
            } else if (this.currentPage == 11) {
                currentLevel = ClientPayloadHandler.alchemyLevel;
                currentXP = ClientPayloadHandler.alchemyXP;
                activeList = cachedAlchemyUnlocks;
                baseReq = JournalConfig.ALCHEMY_XP_BASE_REQUIREMENT.get();
                multiplier = JournalConfig.ALCHEMY_XP_MULTIPLIER.get();
                titleLine1 = "Brew & consume books";
                titleLine2 = "to level up alchemy";
                titleLine3 = "and unlock new enchants";
                statName = "Alchemy Level: ";
            }

            int nextLevelXP = (int) (baseReq * Math.pow(multiplier, currentLevel - 1));

            guiGraphics.drawString(this.font, titleLine1, leftPageX, leftPageY + 15, 0xFF000000, false);
            guiGraphics.drawString(this.font, titleLine2, leftPageX, leftPageY + 26, 0xFF000000, false);
            if (!titleLine3.isEmpty()) {
                guiGraphics.drawString(this.font, titleLine3, leftPageX, leftPageY + 37, 0xFF000000, false);
            }
            guiGraphics.drawString(this.font, statName + currentLevel, leftPageX, leftPageY + 50, 0xFF000000, false);
            guiGraphics.drawString(this.font, (int)currentXP + " / " + nextLevelXP + " XP", leftPageX, leftPageY + 62, 0xFF000000, false);

            guiGraphics.drawString(this.font, customDescription, leftPageX, leftPageY + 77, 0xFF555555, false);

            Map<Integer, List<UnlockEntry>> groupedItems = new TreeMap<>();
            for (UnlockEntry entry : activeList) {
                groupedItems.putIfAbsent(entry.requiredLevel, new ArrayList<>());
                groupedItems.get(entry.requiredLevel).add(entry);
            }

            // --- NEXT UNLOCKS PREVIEW (Bottom of Left Page) ---
            int nextLevel = -1;
            for (Integer lvl : groupedItems.keySet()) {
                if (lvl > currentLevel) {
                    nextLevel = lvl;
                    break;
                }
            }

            if (nextLevel != -1) {
                guiGraphics.drawString(this.font, "Next Unlocks (Lvl " + nextLevel + "):", leftPageX, y + 115, 0xFF333333, false);
                int nextX = leftPageX;
                int nextY = y + 130;
                int drawnItems = 0;

                for (UnlockEntry entry : groupedItems.get(nextLevel)) {
                    if (drawnItems >= 6) break;
                    guiGraphics.renderItem(entry.stack, nextX, nextY);

                    if (entry.isCrafting) {
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(nextX + 8, nextY + 8, 0);
                        guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
                        guiGraphics.renderItem(new ItemStack(Items.CRAFTING_TABLE), 0, 0);
                        guiGraphics.pose().popPose();
                    }

                    if (entry.isAnvil) {
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(nextX + 8, nextY + 8, 0);
                        guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
                        guiGraphics.renderItem(new ItemStack(Items.ANVIL), 0, 0);
                        guiGraphics.pose().popPose();
                    }

                    if (mouseX >= nextX && mouseX <= nextX + 16 && mouseY >= nextY && mouseY <= nextY + 16) {
                        hoveredStack = entry.stack;
                    }
                    nextX += 18;
                    drawnItems++;
                }
            } else {
                guiGraphics.drawString(this.font, "All items unlocked!", leftPageX, y + 115, 0xFF006400, false);
            }

            // --- SCROLLING LIST (Right Page Only) ---
            int listTop = y + 40;
            int listBottom = y + 175;
            int listLeft = rightPageX;
            int itemsPerRow = 6;

            int totalHeight = 0;
            for (Map.Entry<Integer, List<UnlockEntry>> entry : groupedItems.entrySet()) {
                totalHeight += 12;
                int itemCount = entry.getValue().size();
                int rows = (int) Math.ceil((double) itemCount / itemsPerRow);
                totalHeight += (rows * 20);
                totalHeight += 5;
            }

            int listHeight = listBottom - listTop;
            int maxScroll = Math.max(0, totalHeight - listHeight);
            boolean isHoveringList = mouseX >= listLeft && mouseX <= listLeft + 155 && mouseY >= listTop && mouseY <= listBottom;

            if (!isHoveringList && maxScroll > 0) {
                this.scrollOffset += autoScrollSpeed * deltaTime;
                if (this.scrollOffset > maxScroll + 40) this.scrollOffset = -10;
            }

            double renderOffset = Mth.clamp(this.scrollOffset, 0, maxScroll);
            guiGraphics.enableScissor(listLeft, listTop, listLeft + 155, listBottom);

            int currentY = listTop - (int) renderOffset;

            for (Map.Entry<Integer, List<UnlockEntry>> group : groupedItems.entrySet()) {
                int reqLvl = group.getKey();
                List<UnlockEntry> items = group.getValue();

                if (currentY > listTop - 12 && currentY < listBottom) {
                    Component text = Component.literal("Lvl " + reqLvl);
                    int textColor = (currentLevel >= reqLvl) ? 0xFF006400 : 0xFF8B0000;
                    guiGraphics.drawString(this.font, text, listLeft, currentY, textColor, false);
                }

                currentY += 12;

                int startX = listLeft;
                int currentX = startX;
                int itemIndex = 0;

                for (UnlockEntry entry : items) {
                    if (itemIndex > 0 && itemIndex % itemsPerRow == 0) {
                        currentX = startX;
                        currentY += 20;
                    }

                    if (currentY > listTop - 20 && currentY < listBottom) {
                        guiGraphics.renderItem(entry.stack, currentX, currentY);

                        if (entry.isCrafting) {
                            guiGraphics.pose().pushPose();
                            guiGraphics.pose().translate(currentX + 8, currentY + 8, 0);
                            guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
                            guiGraphics.renderItem(new ItemStack(Items.CRAFTING_TABLE), 0, 0);
                            guiGraphics.pose().popPose();
                        }

                        if (entry.isAnvil) {
                            guiGraphics.pose().pushPose();
                            guiGraphics.pose().translate(currentX + 8, currentY + 8, 0);
                            guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
                            guiGraphics.renderItem(new ItemStack(Items.ANVIL), 0, 0);
                            guiGraphics.pose().popPose();
                        }

                        if (mouseX >= currentX && mouseX <= currentX + 16 && mouseY >= currentY && mouseY <= currentY + 16) {
                            if (mouseY >= listTop && mouseY <= listBottom) hoveredStack = entry.stack;
                        }
                    }
                    currentX += 18;
                    itemIndex++;
                }
                currentY += 25;
            }

            guiGraphics.flush();
            guiGraphics.disableScissor();
        }

        // ==========================================
        //         PADLOCK OVERLAY RENDERING
        // ==========================================
        if (!isUnlocked) {
            int padSize = 48;
            int padX = x + (imageWidth / 2) - (padSize / 2);
            int padY = y + imageHeight + 10;

            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(PADLOCK_TEXTURE, padX, padY, padSize, padSize, 0.0f, 0.0f, 256, 256, 256, 256);
            guiGraphics.drawCenteredString(this.font, getLockedText(currentPage), x + (imageWidth / 2), padY + padSize + 5, 0xFFFFFFFF);

            if (mouseX >= padX && mouseX <= padX + padSize && mouseY >= padY && mouseY <= padY + padSize) {
                int cost = 5;
                List<Component> tooltipLines = new ArrayList<>();
                tooltipLines.add(Component.literal("Click to Unlock").withStyle(net.minecraft.ChatFormatting.GOLD));
                tooltipLines.add(Component.literal("Cost: " + cost + " Pages").withStyle(net.minecraft.ChatFormatting.GRAY));

                net.minecraft.ChatFormatting balanceColor = ClientPayloadHandler.tornPages >= cost ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED;
                tooltipLines.add(Component.literal("Your Balance: " + ClientPayloadHandler.tornPages).withStyle(balanceColor));

                guiGraphics.renderComponentTooltip(this.font, tooltipLines, mouseX, mouseY);
            }
        }

        if (hoveredStack != null && isUnlocked) {
            guiGraphics.renderTooltip(this.font, hoveredStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}