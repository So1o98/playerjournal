package com.player.journal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanModsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("playerjournal")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("scan")
                        .then(Commands.literal("mod")
                                .then(Commands.argument("modid", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            String remaining = builder.getRemaining().toLowerCase();
                                            for (var mod : ModList.get().getMods()) {
                                                String modId = mod.getModId();
                                                if (!modId.equals("minecraft") && !modId.equals("neoforge") && modId.startsWith(remaining)) {
                                                    builder.suggest(modId);
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> executeScan(context, StringArgumentType.getString(context, "modid")))
                                )
                        )
                )
        );
    }

    private static int executeScan(CommandContext<CommandSourceStack> context, String targetModId) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("§eScanning mod: " + targetModId + "..."), false);


        List<Item> modItems = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id.getNamespace().equals(targetModId)) {
                modItems.add(item);
            }
        }

        Map<Object, Integer> setLevelConsensus = new HashMap<>();

        for (Item item : modItems) {
            int rawLevel = calculateRawLevel(item);
            if (rawLevel > 1) {
                if (item instanceof ArmorItem armor && armor.getMaterial() != null) {
                    setLevelConsensus.merge(armor.getMaterial(), rawLevel, Math::max);
                } else if (item instanceof TieredItem tiered && tiered.getTier() != null) {
                    setLevelConsensus.merge(tiered.getTier(), rawLevel, Math::max);
                }
            }
        }

        List<String> equipmentResults = new ArrayList<>();
        List<String> craftingResults = new ArrayList<>();

        for (Item item : modItems) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            String itemStr = id.toString();


            String allocation = determineAllocation(item, itemStr, setLevelConsensus);
            if (allocation != null) {
                equipmentResults.add(allocation);
            }


            String craftAllocation = determineCraftingAllocation(item, itemStr, setLevelConsensus);
            if (craftAllocation != null) {
                craftingResults.add(craftAllocation);
            }
        }


        List<String> mobXpResults = new ArrayList<>();
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            if (id.getNamespace().equals(targetModId)) {
                String mobAllocation = determineMobXpAllocation(type, id.toString());
                if (mobAllocation != null) {
                    mobXpResults.add(mobAllocation);
                }
            }
        }

        int totalEntries = equipmentResults.size() + craftingResults.size() + mobXpResults.size();

        if (totalEntries == 0) {
            source.sendFailure(Component.literal("§cNo applicable items or entities found for mod: " + targetModId));
            return 0;
        }


        List<String> combinedResults = new ArrayList<>();

        if (!equipmentResults.isEmpty()) {
            combinedResults.add("# --- Equipment Restrictions ---");
            combinedResults.addAll(equipmentResults);
        }

        if (!craftingResults.isEmpty()) {
            if (!combinedResults.isEmpty()) combinedResults.add("");
            combinedResults.add("# --- Smithing & Crafting Restrictions ---");
            combinedResults.addAll(craftingResults);
        }

        if (!mobXpResults.isEmpty()) {
            if (!combinedResults.isEmpty()) combinedResults.add("");
            combinedResults.add("# --- Mob XP Values ---");
            combinedResults.addAll(mobXpResults);
        }


        boolean onlyMobXp = equipmentResults.isEmpty() && craftingResults.isEmpty() && !mobXpResults.isEmpty();


        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient() && !onlyMobXp) {
            openClientPreview(targetModId, combinedResults);
        } else {

            try {
                Path configPath = FMLPaths.CONFIGDIR.get().resolve("journal_scanned_" + targetModId + ".txt");
                try (FileWriter writer = new FileWriter(configPath.toFile())) {
                    writer.write("# ==========================================\n");
                    writer.write("# Scanned Configurations for: " + targetModId + "\n");
                    writer.write("# ==========================================\n\n");

                    for (String line : combinedResults) {
                        if (line.startsWith("#") || line.isEmpty()) {
                            writer.write(line + "\n");
                        } else {
                            writer.write("\"" + line + "\",\n");
                        }
                    }
                }

                if (onlyMobXp) {
                    source.sendSuccess(() -> Component.literal("§a[Player Journal] Mob XP values generated for " + targetModId + "! Wrote " + mobXpResults.size() + " entries to config/journal_scanned_" + targetModId + ".txt"), false);
                } else {
                    source.sendSuccess(() -> Component.literal("§aScan complete! Wrote " + totalEntries + " entries to config/journal_scanned_" + targetModId + ".txt"), false);
                }
            } catch (IOException e) {
                source.sendFailure(Component.literal("§cFailed to write scan file: " + e.getMessage()));
            }
        }

        return 1;
    }


    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void openClientPreview(String targetModId, List<String> results) {
        net.minecraft.client.Minecraft.getInstance().execute(() -> {
            net.minecraft.client.Minecraft.getInstance().setScreen(new com.player.journal.client.PreviewJournalScreen(targetModId, results));
        });
    }


    private static String determineCraftingAllocation(Item item, String itemId, Map<Object, Integer> setLevelConsensus) {
        int reqLevel = 1;

        if (item instanceof ArmorItem || item instanceof TieredItem || item instanceof ProjectileWeaponItem) {
            if (item instanceof ArmorItem armorItem) {
                reqLevel = setLevelConsensus.getOrDefault(armorItem.getMaterial(), calculateRawLevel(item));
            } else if (item instanceof TieredItem tiered) {
                reqLevel = setLevelConsensus.getOrDefault(tiered.getTier(), calculateRawLevel(item));
            } else {
                reqLevel = calculateRawLevel(item);
            }

            int xpReward = Math.max(5, Math.round(reqLevel * 1.5f));
            return itemId + ";smithing:" + reqLevel + ";" + xpReward;
        }

        return null;
    }


    @SuppressWarnings("unchecked")
    private static String determineMobXpAllocation(EntityType<?> entityType, String entityId) {
        try {
            if (EntityType.LEASH_KNOT.equals(entityType)) return null;

            if (DefaultAttributes.hasSupplier(entityType)) {
                AttributeSupplier supplier = DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) entityType);

                double maxHealth = supplier.hasAttribute(Attributes.MAX_HEALTH) ? supplier.getBaseValue(Attributes.MAX_HEALTH) : 20.0;
                double attackDmg = supplier.hasAttribute(Attributes.ATTACK_DAMAGE) ? supplier.getBaseValue(Attributes.ATTACK_DAMAGE) : 2.0;
                double armor = supplier.hasAttribute(Attributes.ARMOR) ? supplier.getBaseValue(Attributes.ARMOR) : 0.0;

                double baseXP = (maxHealth * 0.4) + (attackDmg * 2.0) + (armor * 1.5);

                MobCategory category = entityType.getCategory();
                if (category == MobCategory.MONSTER) {
                    baseXP *= 1.2;
                } else if (category == MobCategory.CREATURE || category == MobCategory.WATER_CREATURE) {
                    baseXP *= 0.3;
                } else if (category == MobCategory.AMBIENT || category == MobCategory.UNDERGROUND_WATER_CREATURE) {
                    return null;
                }

                if (maxHealth >= 100.0) {
                    baseXP *= 2.5;
                }

                int finalXP = (int) Math.max(2, Math.round(baseXP));
                return entityId + ";" + finalXP;
            }
        } catch (Exception ignored) {}

        return null;
    }

    private static int calculateRawLevel(Item item) {
        ItemStack defaultStack = item.getDefaultInstance();

        if (item instanceof ArmorItem armorItem) {
            int armorValue = armorItem.getDefense();
            int reqLevel = 1;

            if (armorValue >= 8 || armorItem.getToughness() > 2.0f) reqLevel = 20;
            else if (armorValue >= 6) reqLevel = 10;
            else if (armorValue >= 4) reqLevel = 5;

            return reqLevel;
        }

        if (item instanceof SwordItem || item instanceof MaceItem || item instanceof TridentItem) {
            double attackDamage = 1.0;

            if (item instanceof TieredItem tiered) {
                attackDamage = tiered.getTier().getAttackDamageBonus() + 3.0f;
            } else {
                ItemAttributeModifiers modifiers = defaultStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
                for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                    if (entry.attribute().is(Attributes.ATTACK_DAMAGE)) {
                        attackDamage += entry.modifier().amount();
                    }
                }
            }

            if (attackDamage >= 8.0) return 30;
            if (attackDamage >= 7.0) return 20;
            if (attackDamage >= 6.0) return 10;
            if (attackDamage >= 4.0) return 5;
            return 1;
        }

        if (item instanceof TieredItem tiered) {
            int maxDurability = defaultStack.getMaxDamage();
            if (maxDurability >= 1500) return 30;
            if (maxDurability >= 1000) return 20;
            if (maxDurability >= 250) return 10;
            if (maxDurability >= 130) return 5;
            return 1;
        }

        if (item instanceof ProjectileWeaponItem) {
            int maxDurability = defaultStack.getMaxDamage();
            if (maxDurability > 1000) return 20;
            if (maxDurability > 300) return 10;
            return 5;
        }

        return 1;
    }

    private static String determineAllocation(Item item, String itemId, Map<Object, Integer> setLevelConsensus) {
        int reqLevel = 1;

        if (item instanceof ArmorItem armorItem) {
            reqLevel = setLevelConsensus.getOrDefault(armorItem.getMaterial(), calculateRawLevel(item));
            return itemId + ";defense:" + reqLevel;
        }

        if (item instanceof SwordItem || item instanceof MaceItem || item instanceof TridentItem) {
            if (item instanceof TieredItem tiered) {
                reqLevel = setLevelConsensus.getOrDefault(tiered.getTier(), calculateRawLevel(item));
            } else {
                reqLevel = calculateRawLevel(item);
            }
            return itemId + ";combat:" + reqLevel;
        }

        if (item instanceof TieredItem tiered) {
            reqLevel = setLevelConsensus.getOrDefault(tiered.getTier(), calculateRawLevel(item));

            if (item instanceof PickaxeItem || item instanceof ShovelItem) {
                return itemId + ";mining:" + reqLevel;
            }
            if (item instanceof AxeItem || item instanceof HoeItem) {
                return itemId + ";farming:" + reqLevel;
            }
        }

        if (item instanceof ProjectileWeaponItem) {
            reqLevel = calculateRawLevel(item);
            return itemId + ";archery:" + reqLevel;
        }

        if (item instanceof PotionItem) {
            return itemId + ";alchemy:5";
        }

        return null;
    }
}