package com.player.journal.inventory;

import com.player.journal.block.entity.ResearchBenchBlockEntity;
import com.player.journal.registry.ModBlocks;
import com.player.journal.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ResearchBenchMenu extends AbstractContainerMenu {

    private final ResearchBenchBlockEntity blockEntity;
    private final ContainerData data;


    public ResearchBenchMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(pos), new SimpleContainerData(2));
    }

    public ResearchBenchMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public ResearchBenchMenu(int containerId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.RESEARCH_BENCH.get(), containerId);
        this.blockEntity = (ResearchBenchBlockEntity) entity;
        this.data = data;

        this.addDataSlots(this.data);


        this.addSlot(new Slot(this.blockEntity.getItemHandler(), 0, 43, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.getItemHandler().canPlaceItem(0, stack);
            }
        });


        this.addSlot(new Slot(this.blockEntity.getItemHandler(), 1, 112, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.getItemHandler().canPlaceItem(1, stack);
            }
        });

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public int getProgressFrame() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);

        if (maxProgress == 0 || progress == 0) {
            return 0;
        }
        return (int) Math.ceil(((double) progress / maxProgress) * 16.0);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, ModBlocks.RESEARCH_BENCH.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < 2) {
                if (!this.moveItemStackTo(slotStack, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, 2, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}