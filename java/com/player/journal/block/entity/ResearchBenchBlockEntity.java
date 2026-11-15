package com.player.journal.block.entity;

import com.player.journal.inventory.ResearchBenchMenu;
import com.player.journal.registry.ModBlockEntities;
import com.player.journal.registry.ModItems;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ResearchBenchBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {

    private int progress = 0;
    private int maxProgress = 100;
    protected final ContainerData data;

    private final SimpleContainer itemHandler = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            ResearchBenchBlockEntity.this.setChanged();
        }
    };

    public ResearchBenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESEARCH_BENCH.get(), pos, state);

        this.data = new ContainerData() {
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

    public SimpleContainer getItemHandler() {
        return itemHandler;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return this.worldPosition;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NonNullList<ItemStack> stacks = NonNullList.withSize(itemHandler.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < itemHandler.getContainerSize(); i++) {
            stacks.set(i, itemHandler.getItem(i));
        }
        CompoundTag invTag = new CompoundTag();
        ContainerHelper.saveAllItems(invTag, stacks, registries);
        tag.put("inventory", invTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            CompoundTag invTag = tag.getCompound("inventory");
            NonNullList<ItemStack> stacks = NonNullList.withSize(itemHandler.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(invTag, stacks, registries);
            for (int i = 0; i < stacks.size(); i++) {
                itemHandler.setItem(i, stacks.get(i));
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ResearchBenchBlockEntity entity) {
        ItemStack targetStack = entity.itemHandler.getItem(0);
        ItemStack bookStack = entity.itemHandler.getItem(1);

        boolean hasTargetItem = !targetStack.isEmpty();
        boolean hasKnowledgeBook = !bookStack.isEmpty() && bookStack.is(ModItems.BOOK_OF_KNOWLEDGE.get());

        if (hasTargetItem && hasKnowledgeBook) {
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {

                String targetId = BuiltInRegistries.ITEM.getKey(targetStack.getItem()).toString();
                Component targetName = targetStack.getHoverName();


                targetStack.shrink(1);
                bookStack.shrink(1);


                ItemStack researchBook = new ItemStack(ModItems.RESEARCH_BOOK.get());

                CustomData.update(DataComponents.CUSTOM_DATA, researchBook, tag -> {
                    tag.putString("ResearchedItemID", targetId);
                });

                researchBook.set(DataComponents.CUSTOM_NAME,
                        Component.literal(targetName.getString() + " Research Book").withStyle(ChatFormatting.GOLD));

                // 4. Output into slot 1
                entity.itemHandler.setItem(1, researchBook);

                level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                entity.progress = 0;
            }
            setChanged(level, pos, state);
        } else {
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