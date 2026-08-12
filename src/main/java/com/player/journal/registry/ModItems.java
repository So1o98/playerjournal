package com.player.journal.registry;

import com.player.journal.item.EnchantedKnowledgeItem;
import com.player.journal.item.JournalPageItem;
import com.player.journal.item.TornPageItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("playerjournal");

    public static final DeferredItem<Item> MINING_PAGE = ITEMS.register("mining_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.MINING));

    public static final DeferredItem<Item> FARMING_PAGE = ITEMS.register("farming_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.FARMING));

    public static final DeferredItem<Item> SMITHING_PAGE = ITEMS.register("smithing_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.SMITHING));

    public static final DeferredItem<Item> ARCHERY_PAGE = ITEMS.register("archery_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.ARCHERY));

    public static final DeferredItem<Item> FISHING_PAGE = ITEMS.register("fishing_page",
            () -> new JournalPageItem(new Item.Properties(), JournalPageItem.SkillType.FISHING));

    public static final DeferredItem<Item> TORN_PAGE = ITEMS.register("torn_page",
            () -> new TornPageItem(new Item.Properties()));


    public static final DeferredItem<Item> BOOK_OF_KNOWLEDGE = ITEMS.register("book_of_knowledge",
            () -> new Item(new Item.Properties().stacksTo(1)));


    public static final DeferredItem<Item> ENCHANTED_KNOWLEDGE = ITEMS.register("enchanted_knowledge",
            () -> new EnchantedKnowledgeItem(new Item.Properties().stacksTo(1)));


    public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.Item> KNOWLEDGE_TABLE = ITEMS.register("knowledge_table",
            () -> new net.minecraft.world.item.BlockItem(com.player.journal.registry.ModBlocks.KNOWLEDGE_TABLE.get(), new net.minecraft.world.item.Item.Properties()));
}