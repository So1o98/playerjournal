package com.player.journal.block.entity;

import com.player.journal.inventory.ResearchBenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ResearchBenchBlockEntity extends BlockEntity implements MenuProvider {

    private int progress = 0;
    private int maxProgress = 100;
    protected final net.minecraft.world.inventory.ContainerData data;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public ResearchBenchBlockEntity(BlockPos pos, BlockState state) {
        // NOTE: Verify your registry reference for ModBlockEntities.RESEARCH_BENCH matches your code
        super(com.player.journal.registry.ModBlockEntities.RESEARCH_BENCH.get(), pos, state);

        this.data = new net.minecraft.world.inventory.ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ResearchBenchBlockEntity.this.progress;
                    case 1 -> ResearchBenchBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ResearchBenchBlockEntity.this.progress = value;
                    case 1 -> ResearchBenchBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, ResearchBenchBlockEntity entity) {
        ItemStack targetStack = entity.itemHandler.getStackInSlot(0);
        ItemStack bookStack = entity.itemHandler.getStackInSlot(1);

        boolean hasTargetItem = !targetStack.isEmpty();
        // NOTE: Replace ModItems.ENCHANTED_KNOWLEDGE.get() with your actual base blank knowledge book item
        boolean hasKnowledgeBook = !bookStack.isEmpty() && bookStack.is(com.player.journal.registry.ModItems.BOOK_OF_KNOWLEDGE.get());

        if (hasTargetItem && hasKnowledgeBook) {
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {
                // 1. Grab target item data
                String targetId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(targetStack.getItem()).toString();
                net.minecraft.network.chat.Component targetName = targetStack.getHoverName();

                // 2. Destroy the inputs
                targetStack.shrink(1);
                bookStack.shrink(1);

                // 3. Create the tailored Output Book
                // NOTE: You will need to register this new RESEARCH_BOOK item in your ModItems
                ItemStack researchBook = new ItemStack(com.player.journal.registry.ModItems.RESEARCH_BOOK.get());

                net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, researchBook, tag -> {
                    tag.putString("ResearchedItemID", targetId);
                });

                researchBook.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                        net.minecraft.network.chat.Component.literal(targetName.getString() + " Research Book").withStyle(net.minecraft.ChatFormatting.GOLD));

                // 4. Output into slot 1
                entity.itemHandler.setStackInSlot(1, researchBook);

                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                entity.progress = 0;
            }
            setChanged(level, pos, state);
        } else {
            // Reset progress if an item is pulled out early
            if (entity.progress > 0) {
                entity.progress = 0;
                setChanged(level, pos, state);
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Research Bench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ResearchBenchMenu(containerId, playerInventory, this, this.data);
    }
}