package com.player.journal.registry;

import com.player.journal.item.EnchantedKnowledgeItem;
import com.player.journal.item.JournalPageItem;
import com.player.journal.item.ResearchBookItem;
import com.player.journal.item.TornPageItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModItems {

    public static final Supplier<Item> MINING_PAGE = register("mining_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.MINING));

    public static final Supplier<Item> FARMING_PAGE = register("farming_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.FARMING));

    public static final Supplier<Item> SMITHING_PAGE = register("smithing_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.SMITHING));

    public static final Supplier<Item> ARCHERY_PAGE = register("archery_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.ARCHERY));

    public static final Supplier<Item> FISHING_PAGE = register("fishing_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.FISHING));

    public static final Supplier<Item> TORN_PAGE = register("torn_page",
            () -> new TornPageItem(new Item.Properties()));

    public static final Supplier<Item> BOOK_OF_KNOWLEDGE = register("book_of_knowledge",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> ENCHANTED_KNOWLEDGE = register("enchanted_knowledge",
            () -> new EnchantedKnowledgeItem(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> KNOWLEDGE_TABLE = register("knowledge_table",
            () -> new BlockItem(ModBlocks.KNOWLEDGE_TABLE.get(), new Item.Properties()));

    public static final Supplier<Item> RESEARCH_BENCH = register("research_bench",
            () -> new BlockItem(ModBlocks.RESEARCH_BENCH.get(), new Item.Properties()));

    public static final Supplier<Item> RESEARCH_BOOK = register("research_book",
            () -> new ResearchBookItem(new Item.Properties().stacksTo(1)));

    private static <T extends Item> Supplier<T> register(String name, Supplier<T> supplier) {
        T item = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("playerjournal", name), supplier.get());
        return () -> item;
    }

    public static void register() {
        // Triggers static initialization
    }
}