package com.player.journal.client;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class PreviewJournalScreen extends Screen {

    private static final ResourceLocation[] PAGES = {
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/contents.png"), // 0
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/health.png"),   // 1
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/agility.png"),  // 2
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/combat.png"),   // 3
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/defense.png"),  // 4
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/trading.png"),  // 5
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/farming.png"),  // 6
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/mining.png"),   // 7
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/smithing.png"), // 8
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/archery.png"),  // 9
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/fishing.png"),  // 10
            ResourceLocation.fromNamespaceAndPath("playerjournal", "textures/gui/alchemy.png")   // 11
    };

    private static final ItemStack NETHER_STAR_ICON = new ItemStack(Items.NETHER_STAR);

    private final int imageWidth = 400;
    private final int imageHeight = 200;
    private int currentPage = 0;
    private PageButton forwardButton;
    private PageButton backButton;
    private double scrollOffset = 0;
    private long lastFrameTime = Util.getMillis();

    private final String targetModId;
    private final List<String> scanResults;
    private final List<String> scannedMobXp = new ArrayList<>();

    private final List<UnlockEntry> vitalityUnlocks = new ArrayList<>();
    private final List<UnlockEntry> agilityUnlocks = new ArrayList<>();
    private final List<UnlockEntry> combatUnlocks = new ArrayList<>();
    private final List<UnlockEntry> defenseUnlocks = new ArrayList<>();
    private final List<UnlockEntry> farmingUnlocks = new ArrayList<>();
    private final List<UnlockEntry> miningUnlocks = new ArrayList<>();
    private final List<UnlockEntry> smithingUnlocks = new ArrayList<>();
    private final List<UnlockEntry> archeryUnlocks = new ArrayList<>();
    private final List<UnlockEntry> fishingUnlocks = new ArrayList<>();
    private final List<UnlockEntry> alchemyUnlocks = new ArrayList<>();

    private ContextMenu activeContextMenu = null;

    private static class ContextMenu {
        public final UnlockEntry targetEntry;
        public final int x;
        public final int y;
        public final int width = 80;
        public final int rowHeight = 11;
        public final String[] options = {"Vitality", "Agility", "Combat", "Defense", "Farming", "Mining", "Smithing", "Archery", "Fishing", "Alchemy"};

        public ContextMenu(UnlockEntry targetEntry, int x, int y) {
            this.targetEntry = targetEntry;
            this.x = x;
            this.y = y;
        }
    }

    public static class UnlockEntry {
        public ItemStack stack;
        public int requiredLevel;
        public boolean isNew;
        public boolean isCrafting;
        public boolean isAnvil;
        public final String registryId;
        public final Object setMaterialGroup;
        public int xpReward;

        public UnlockEntry(ItemStack stack, int requiredLevel, boolean isNew, boolean isCrafting, boolean isAnvil, String registryId) {
            this(stack, requiredLevel, isNew, isCrafting, isAnvil, registryId, 0);
        }

        public UnlockEntry(ItemStack stack, int requiredLevel, boolean isNew, boolean isCrafting, boolean isAnvil, String registryId, int xpReward) {
            this.stack = stack;
            this.requiredLevel = requiredLevel;
            this.isNew = isNew;
            this.isCrafting = isCrafting;
            this.isAnvil = isAnvil;
            this.registryId = registryId;
            this.xpReward = xpReward;

            if (stack.getItem() instanceof net.minecraft.world.item.ArmorItem armor) {
                this.setMaterialGroup = armor.getMaterial();
            } else if (stack.getItem() instanceof net.minecraft.world.item.TieredItem tiered) {
                this.setMaterialGroup = tiered.getTier();
            } else {
                this.setMaterialGroup = null;
            }
        }
    }

    public PreviewJournalScreen(String targetModId, List<String> scanResults) {
        super(Component.literal("Previewing Scan: " + targetModId));
        this.targetModId = targetModId;
        this.scanResults = scanResults;
        loadAllItems();
    }

    private void loadAllItems() {
        JournalScreen.rebuildUnlocksIfNeeded();

        copyFromMain(JournalScreen.cachedVitalityUnlocks, vitalityUnlocks);
        copyFromMain(JournalScreen.cachedAgilityUnlocks, agilityUnlocks);
        copyFromMain(JournalScreen.cachedCombatUnlocks, combatUnlocks);
        copyFromMain(JournalScreen.cachedDefenseUnlocks, defenseUnlocks);
        copyFromMain(JournalScreen.cachedFarmingUnlocks, farmingUnlocks);
        copyFromMain(JournalScreen.cachedMiningUnlocks, miningUnlocks);
        copyFromMain(JournalScreen.cachedSmithingUnlocks, smithingUnlocks);
        copyFromMain(JournalScreen.cachedArcheryUnlocks, archeryUnlocks);
        copyFromMain(JournalScreen.cachedFishingUnlocks, fishingUnlocks);
        copyFromMain(JournalScreen.cachedAlchemyUnlocks, alchemyUnlocks);

        parseScanResults();

        vitalityUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        agilityUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        combatUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        defenseUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        farmingUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        miningUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        smithingUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        archeryUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        fishingUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
        alchemyUnlocks.sort(Comparator.comparingInt(e -> e.requiredLevel));
    }

    private void copyFromMain(List<JournalScreen.UnlockEntry> source, List<UnlockEntry> dest) {
        for (JournalScreen.UnlockEntry e : source) {
            ResourceLocation idLoc = BuiltInRegistries.ITEM.getKey(e.stack.getItem());
            String idString = idLoc != null ? idLoc.toString() : "";
            dest.add(new UnlockEntry(e.stack, e.requiredLevel, false, e.isCrafting, e.isAnvil, idString));
        }
    }

    private void parseScanResults() {
        for (String line : scanResults) {
            if (line.startsWith("#") || line.isEmpty()) continue;
            String[] parts = line.split(";");
            if (parts.length >= 2) {

                if (!parts[1].contains(":")) {
                    scannedMobXp.add(line);
                    continue;
                }

                String itemId = parts[0].trim();
                try {
                    Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
                    if (item == Items.AIR) continue;
                    ItemStack stack = new ItemStack(item);

                    for (int i = 1; i < parts.length; i++) {
                        String part = parts[i].trim();
                        if (part.contains(":")) {
                            String[] req = part.split(":");
                            if (req.length == 2) {
                                int level = Integer.parseInt(req[1].trim());
                                String skill = req[0].toLowerCase();


                                int xpReward = 0;
                                if (i + 1 < parts.length && !parts[i + 1].contains(":")) {
                                    try { xpReward = Integer.parseInt(parts[i + 1].trim()); } catch (NumberFormatException ignored) {}
                                }

                                UnlockEntry entry = new UnlockEntry(stack, level, true, skill.equals("smithing"), false, itemId, xpReward);
                                switch (skill) {
                                    case "vitality" -> vitalityUnlocks.add(entry);
                                    case "agility" -> agilityUnlocks.add(entry);
                                    case "combat" -> combatUnlocks.add(entry);
                                    case "defense" -> defenseUnlocks.add(entry);
                                    case "farming" -> farmingUnlocks.add(entry);
                                    case "mining" -> miningUnlocks.add(entry);
                                    case "smithing" -> smithingUnlocks.add(entry);
                                    case "archery" -> archeryUnlocks.add(entry);
                                    case "fishing" -> fishingUnlocks.add(entry);
                                    case "alchemy" -> alchemyUnlocks.add(entry);
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private int getNewCount(List<UnlockEntry> list) {
        int count = 0;
        for (UnlockEntry e : list) {
            if (e.isNew) count++;
        }
        return count;
    }

    private String getChapterName(int page) {
        return switch (page) {
            case 1 -> "Vitality";
            case 2 -> "Agility";
            case 3 -> "Combat";
            case 4 -> "Defense";
            case 5 -> "Trading";
            case 6 -> "Farming";
            case 7 -> "Mining";
            case 8 -> "Smithing";
            case 9 -> "Archery";
            case 10 -> "Fishing";
            case 11 -> "Alchemy";
            default -> "Unknown";
        };
    }

    private List<UnlockEntry> getListForPage(int page) {
        return switch (page) {
            case 1 -> vitalityUnlocks;
            case 2 -> agilityUnlocks;
            case 3 -> combatUnlocks;
            case 4 -> defenseUnlocks;
            case 6 -> farmingUnlocks;
            case 7 -> miningUnlocks;
            case 8 -> smithingUnlocks;
            case 9 -> archeryUnlocks;
            case 10 -> fishingUnlocks;
            case 11 -> alchemyUnlocks;
            default -> new ArrayList<>();
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

        int buttonWidth = 120;
        int genX = x + (this.imageWidth / 2) - (buttonWidth / 2);
        int genY = y + this.imageHeight + 10;
        this.addRenderableWidget(Button.builder(Component.literal("Generate Config"), button -> {
            generateConfigFile();
        }).bounds(genX, genY, buttonWidth, 20).build());
    }

    private void generateConfigFile() {
        try {
            Path configPath = FMLPaths.CONFIGDIR.get().resolve("journal_scanned_" + targetModId + ".txt");
            try (FileWriter writer = new FileWriter(configPath.toFile())) {
                writer.write("# --- Scanned Items for " + targetModId + " ---\n");
                writer.write("# Copy and paste these lines directly into your TOML config lists!\n\n");

                Set<String> processedKeys = new HashSet<>();


                List<List<UnlockEntry>> equipLists = List.of(vitalityUnlocks, agilityUnlocks, combatUnlocks, defenseUnlocks, farmingUnlocks, miningUnlocks, archeryUnlocks, fishingUnlocks, alchemyUnlocks);
                String[] equipKeys = {"vitality", "agility", "combat", "defense", "farming", "mining", "archery", "fishing", "alchemy"};

                boolean hasEquip = false;
                StringBuilder equipBuilder = new StringBuilder();
                for (int i = 0; i < equipLists.size(); i++) {
                    String currentSkillName = equipKeys[i];
                    for (UnlockEntry entry : equipLists.get(i)) {
                        if (entry.isNew) {
                            String configString = entry.registryId + ";" + currentSkillName + ":" + entry.requiredLevel;
                            if (processedKeys.add(configString)) {
                                equipBuilder.append("\"").append(configString).append("\",\n");
                                hasEquip = true;
                            }
                        }
                    }
                }
                if (hasEquip) {
                    writer.write("# --- Equipment Restrictions ---\n");
                    writer.write(equipBuilder.toString());
                }


                boolean hasSmithing = false;
                StringBuilder smithBuilder = new StringBuilder();
                for (UnlockEntry entry : smithingUnlocks) {
                    if (entry.isNew) {
                        String configString = entry.registryId + ";smithing:" + entry.requiredLevel;
                        if (entry.xpReward > 0) {
                            configString += ";" + entry.xpReward;
                        }
                        if (processedKeys.add(configString)) {
                            smithBuilder.append("\"").append(configString).append("\",\n");
                            hasSmithing = true;
                        }
                    }
                }
                if (hasSmithing) {
                    if (hasEquip) writer.write("\n");
                    writer.write("# --- Smithing & Crafting Restrictions ---\n");
                    writer.write(smithBuilder.toString());
                }


                if (!scannedMobXp.isEmpty()) {
                    if (hasEquip || hasSmithing) writer.write("\n");
                    writer.write("# --- Mob XP Values ---\n");
                    for (String mobLine : scannedMobXp) {
                        writer.write("\"" + mobLine + "\",\n");
                    }
                }
            }
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("§a[Player Journal] Custom restrictions compiled and exported successfully!"), false);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            this.onClose();
        } catch (IOException e) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.literal("§cFailed to write scan file: " + e.getMessage()), false);
            }
        }
    }

    private void updateButtons() {
        this.backButton.visible = this.currentPage > 0;
        this.forwardButton.visible = this.currentPage < PAGES.length - 1;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.currentPage >= 1 && this.currentPage <= 11 && this.currentPage != 5) {
            List<UnlockEntry> activeList = getListForPage(this.currentPage);
            Map<Integer, List<UnlockEntry>> groupedItems = new TreeMap<>();
            for (UnlockEntry entry : activeList) {
                groupedItems.putIfAbsent(entry.requiredLevel, new ArrayList<>());
                groupedItems.get(entry.requiredLevel).add(entry);
            }

            int itemsPerRow = 7;
            int totalHeight = 0;
            for (Map.Entry<Integer, List<UnlockEntry>> entry : groupedItems.entrySet()) {
                totalHeight += 12;
                int itemCount = entry.getValue().size();
                int rows = (int) Math.ceil((double) itemCount / itemsPerRow);
                totalHeight += (rows * 20) + 5;
            }

            int maxScroll = Math.max(0, totalHeight - 135);
            this.scrollOffset = Mth.clamp(this.scrollOffset - (scrollY * 15.0), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.activeContextMenu != null) {
            ContextMenu menu = this.activeContextMenu;
            if (mouseX >= menu.x && mouseX <= menu.x + menu.width &&
                    mouseY >= menu.y && mouseY <= menu.y + (menu.options.length * menu.rowHeight)) {

                int clickedIndex = (int) ((mouseY - menu.y) / menu.rowHeight);
                if (clickedIndex >= 0 && clickedIndex < menu.options.length) {
                    String targetSkill = menu.options[clickedIndex].toLowerCase();
                    changeItemSkillClass(menu.targetEntry, targetSkill);
                    if (this.minecraft != null) {
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }
                this.activeContextMenu = null;
                return true;
            }
            this.activeContextMenu = null;
        }

        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (button == 0 && this.currentPage == 0) {
            int rightPageX = x + 235;
            int startY = y + 39;

            if (mouseX >= rightPageX && mouseX <= rightPageX + 120 && mouseY >= startY && mouseY < startY + (11 * 11)) {
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

        if (this.currentPage >= 1 && this.currentPage <= 11 && this.currentPage != 5) {
            int listTop = y + 40;
            int listBottom = y + 175;
            int listLeft = x + 225;
            int itemsPerRow = 7;

            if (mouseX >= listLeft && mouseX <= listLeft + 155 && mouseY >= listTop && mouseY <= listBottom) {
                List<UnlockEntry> activeList = getListForPage(this.currentPage);
                Map<Integer, List<UnlockEntry>> groupedItems = new TreeMap<>();
                for (UnlockEntry entry : activeList) {
                    groupedItems.putIfAbsent(entry.requiredLevel, new ArrayList<>());
                    groupedItems.get(entry.requiredLevel).add(entry);
                }

                int currentY = listTop - (int) this.scrollOffset;

                for (Map.Entry<Integer, List<UnlockEntry>> group : groupedItems.entrySet()) {
                    currentY += 12;
                    int currentX = listLeft;
                    int itemIndex = 0;

                    for (UnlockEntry entry : group.getValue()) {
                        if (itemIndex > 0 && itemIndex % itemsPerRow == 0) {
                            currentX = listLeft;
                            currentY += 20;
                        }

                        if (mouseX >= currentX && mouseX <= currentX + 16 && mouseY >= currentY && mouseY <= currentY + 16) {
                            if (entry.isNew) {
                                if (Screen.hasShiftDown()) {
                                    this.activeContextMenu = new ContextMenu(entry, (int) mouseX, (int) mouseY);
                                    return true;
                                }

                                int currentTier = entry.requiredLevel;
                                int newTier = currentTier;
                                int step = Screen.hasControlDown() ? 5 : 1;

                                if (button == 0) {
                                    newTier = Math.min(100, currentTier + step);
                                } else if (button == 1) {
                                    newTier = Math.max(1, currentTier - step);
                                }

                                if (newTier != currentTier) {
                                    applyLiveSetConsensus(entry.setMaterialGroup, newTier);
                                    if (this.minecraft != null) {
                                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2F));
                                    }
                                    return true;
                                }
                            }
                        }
                        currentX += 18;
                        itemIndex++;
                    }
                    currentY += 25;
                }
            }
        }
        return false;
    }

    private void applyLiveSetConsensus(Object materialGroup, int assignedLevel) {
        if (materialGroup == null) return;

        List<List<UnlockEntry>> allSkillLists = List.of(vitalityUnlocks, agilityUnlocks, combatUnlocks, defenseUnlocks, farmingUnlocks, miningUnlocks, smithingUnlocks, archeryUnlocks, fishingUnlocks, alchemyUnlocks);

        for (List<UnlockEntry> list : allSkillLists) {
            for (UnlockEntry entry : list) {
                if (entry.isNew && materialGroup.equals(entry.setMaterialGroup)) {
                    entry.requiredLevel = assignedLevel;
                }
            }
        }

        for (List<UnlockEntry> list : allSkillLists) {
            list.sort(Comparator.comparingInt(e -> e.requiredLevel));
        }
    }

    private void changeItemSkillClass(UnlockEntry entry, String targetSkill) {
        List<List<UnlockEntry>> allSkillLists = List.of(vitalityUnlocks, agilityUnlocks, combatUnlocks, defenseUnlocks, farmingUnlocks, miningUnlocks, smithingUnlocks, archeryUnlocks, fishingUnlocks, alchemyUnlocks);
        String[] skillKeys = {"vitality", "agility", "combat", "defense", "farming", "mining", "smithing", "archery", "fishing", "alchemy"};

        List<UnlockEntry> targetList = null;
        for (int i = 0; i < skillKeys.length; i++) {
            if (skillKeys[i].equals(targetSkill)) {
                targetList = allSkillLists.get(i);
                break;
            }
        }
        if (targetList == null) return;

        Object setGroup = entry.setMaterialGroup;

        for (List<UnlockEntry> sourceList : allSkillLists) {
            if (sourceList == targetList) continue;

            Iterator<UnlockEntry> iterator = sourceList.iterator();
            while (iterator.hasNext()) {
                UnlockEntry current = iterator.next();
                if (current.isNew && ((setGroup != null && setGroup.equals(current.setMaterialGroup)) || current.registryId.equals(entry.registryId))) {
                    targetList.add(new UnlockEntry(current.stack, current.requiredLevel, true, current.isCrafting, current.isAnvil, current.registryId, current.xpReward));
                    iterator.remove();
                }
            }
        }

        for (List<UnlockEntry> list : allSkillLists) {
            list.sort(Comparator.comparingInt(e -> e.requiredLevel));
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x90000000);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(PAGES[currentPage], x, y, imageWidth, imageHeight, 0.0f, 0.0f, 1000, 500, 1000, 500);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        long currentTime = Util.getMillis();
        double deltaTime = (currentTime - this.lastFrameTime) / 1000.0;
        this.lastFrameTime = currentTime;
        if (deltaTime > 0.1) deltaTime = 0.1;

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ItemStack hoveredStack = null;

        if (this.currentPage == 0) {
            int rightPageX = x + 235;
            int rightPageY = y + 25;
            guiGraphics.drawString(this.font, "Preview Directory:", rightPageX, rightPageY, 0xFF333333, false);

            int rightListY = rightPageY + 14;
            String[] skills = {"Vitality", "Agility", "Combat", "Defense", "Trading", "Farming", "Mining", "Smithing", "Archery", "Fishing", "Alchemy"};
            for (int i = 0; i < skills.length; i++) {
                boolean isHovered = mouseX >= rightPageX && mouseX <= rightPageX + 120 && mouseY >= rightListY && mouseY < rightListY + 11;
                List<UnlockEntry> list = (i == 4) ? Collections.emptyList() : getListForPage(i + 1);
                int total = list.size();
                int newCount = getNewCount(list);

                String text = (i == 4) ? "• Trading (N/A)" : "• " + skills[i] + " (" + total + " | +" + newCount + ")";
                guiGraphics.drawString(this.font, text, rightPageX, rightListY, isHovered ? 0xFF009900 : 0xFF006400, false);
                rightListY += 11;
            }

            int leftPageX = x + 58;
            int topY = y + 35;
            guiGraphics.drawString(this.font, "Scan Summary For:", leftPageX, topY, 0xFF000000, false);
            guiGraphics.drawString(this.font, "Mod ID:", leftPageX, topY + 14, 0xFF333333, false);
            guiGraphics.drawString(this.font, targetModId, leftPageX, topY + 25, 0xFF333333, false);
            guiGraphics.drawString(this.font, "New Items Scanned: " + scanResults.size(), leftPageX, topY + 38, 0xFF333333, false);

            guiGraphics.drawString(this.font, "Total Items (+New):", leftPageX, topY + 54, 0xFF000000, false);
            guiGraphics.drawString(this.font, "• Combat: " + combatUnlocks.size() + " total (+" + getNewCount(combatUnlocks) + " new)", leftPageX, topY + 65, 0xFF333333, false);
            guiGraphics.drawString(this.font, "• Defense: " + defenseUnlocks.size() + " total (+" + getNewCount(defenseUnlocks) + " new)", leftPageX, topY + 75, 0xFF333333, false);
            guiGraphics.drawString(this.font, "• Mining: " + miningUnlocks.size() + " total (+" + getNewCount(miningUnlocks) + " new)", leftPageX, topY + 85, 0xFF333333, false);
            guiGraphics.drawString(this.font, "• Farming: " + farmingUnlocks.size() + " total (+" + getNewCount(farmingUnlocks) + " new)", leftPageX, topY + 95, 0xFF333333, false);
            guiGraphics.drawString(this.font, "• Archery: " + archeryUnlocks.size() + " total (+" + getNewCount(archeryUnlocks) + " new)", leftPageX, topY + 105, 0xFF333333, false);
            guiGraphics.drawString(this.font, "• Fishing: " + fishingUnlocks.size() + " total (+" + getNewCount(fishingUnlocks) + " new)", leftPageX, topY + 115, 0xFF333333, false);
            guiGraphics.drawString(this.font, "• Alchemy: " + alchemyUnlocks.size() + " total (+" + getNewCount(alchemyUnlocks) + " new)", leftPageX, topY + 125, 0xFF333333, false);
        }
        else if (this.currentPage >= 1 && this.currentPage <= 11 && this.currentPage != 5) {
            int leftPageX = x + 70;
            int rightPageX = x + 225;

            List<UnlockEntry> activeList = getListForPage(this.currentPage);
            int newCount = getNewCount(activeList);
            String chapterName = getChapterName(this.currentPage);

            guiGraphics.drawString(this.font, "Preview: " + chapterName, leftPageX, y + 25, 0xFF000000, false);
            guiGraphics.drawString(this.font, "Total Items: " + activeList.size(), leftPageX, y + 42, 0xFF333333, false);
            guiGraphics.drawString(this.font, "New Scanned: +" + newCount, leftPageX, y + 54, 0xFF006400, false);

            guiGraphics.drawString(this.font, "Legend:", leftPageX, y + 72, 0xFF000000, false);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(leftPageX, y + 85, 0);
            guiGraphics.pose().scale(0.65f, 0.65f, 1.0f);
            guiGraphics.renderItem(NETHER_STAR_ICON, 0, 0);
            guiGraphics.pose().popPose();

            guiGraphics.drawString(this.font, "= New Scanned", leftPageX + 13, y + 87, 0xFF333333, false);
            guiGraphics.drawString(this.font, "(" + targetModId + ")", leftPageX + 13, y + 97, 0xFF555555, false);

            guiGraphics.drawString(this.font, "Review tiers & balance", leftPageX, y + 118, 0xFF333333, false);
            guiGraphics.drawString(this.font, "before saving config.", leftPageX, y + 128, 0xFF333333, false);

            Map<Integer, List<UnlockEntry>> groupedItems = new TreeMap<>();
            for (UnlockEntry entry : activeList) {
                groupedItems.putIfAbsent(entry.requiredLevel, new ArrayList<>());
                groupedItems.get(entry.requiredLevel).add(entry);
            }

            int listTop = y + 40;
            int listBottom = y + 175;
            int listLeft = rightPageX;
            int itemsPerRow = 7;

            int totalHeight = 0;
            for (Map.Entry<Integer, List<UnlockEntry>> entry : groupedItems.entrySet()) {
                totalHeight += 12 + ((int) Math.ceil((double) entry.getValue().size() / itemsPerRow) * 20) + 5;
            }

            int maxScroll = Math.max(0, totalHeight - (listBottom - listTop));
            boolean isHoveringList = mouseX >= listLeft && mouseX <= listLeft + 155 && mouseY >= listTop && mouseY <= listBottom;
            if (!isHoveringList && maxScroll > 0) {
                this.scrollOffset += 30.0 * deltaTime;
                if (this.scrollOffset > maxScroll + 40) this.scrollOffset = -10;
            }

            double renderOffset = Mth.clamp(this.scrollOffset, 0, maxScroll);
            guiGraphics.enableScissor(listLeft, listTop, listLeft + 155, listBottom);
            int currentY = listTop - (int) renderOffset;

            for (Map.Entry<Integer, List<UnlockEntry>> group : groupedItems.entrySet()) {
                int reqLvl = group.getKey();
                List<UnlockEntry> items = group.getValue();

                if (currentY > listTop - 12 && currentY < listBottom) {
                    guiGraphics.drawString(this.font, Component.literal("Assigned Level " + reqLvl), listLeft, currentY, 0xFF006400, false);
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

                        if (entry.isNew) {
                            guiGraphics.pose().pushPose();
                            guiGraphics.pose().translate(currentX + 9, currentY - 3, 200);
                            guiGraphics.pose().scale(0.6f, 0.6f, 1.0f);
                            guiGraphics.renderItem(NETHER_STAR_ICON, 0, 0);
                            guiGraphics.pose().popPose();
                        }

                        if (mouseX >= currentX && mouseX <= currentX + 16 && mouseY >= currentY && mouseY <= currentY + 16 && mouseY >= listTop && mouseY <= listBottom) {
                            hoveredStack = entry.stack;
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

        if (this.activeContextMenu != null) {
            ContextMenu menu = this.activeContextMenu;
            int totalHeight = menu.options.length * menu.rowHeight;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 500);

            guiGraphics.fill(menu.x, menu.y, menu.x + menu.width, menu.y + totalHeight, 0xFA0B0B0B);
            guiGraphics.renderOutline(menu.x, menu.y, menu.width, totalHeight, 0xFF5A5A5A);

            int optionY = menu.y;
            for (String option : menu.options) {
                boolean isHovered = mouseX >= menu.x && mouseX <= menu.x + menu.width && mouseY >= optionY && mouseY < optionY + menu.rowHeight;
                if (isHovered) {
                    guiGraphics.fill(menu.x + 1, optionY, menu.x + menu.width - 1, optionY + menu.rowHeight, 0xFF2A2A2A);
                }
                guiGraphics.drawString(this.font, option, menu.x + 5, optionY + 2, isHovered ? 0xFFFFFF55 : 0xFFBBBBBB, false);
                optionY += menu.rowHeight;
            }

            guiGraphics.pose().popPose();
        }

        if (hoveredStack != null && this.activeContextMenu == null) {
            guiGraphics.renderTooltip(this.font, hoveredStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}