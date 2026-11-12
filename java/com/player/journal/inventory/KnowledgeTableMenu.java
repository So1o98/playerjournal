package com.player.journal.inventory;

import com.player.journal.registry.ModItems;
import com.player.journal.registry.ModMenuTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public class KnowledgeTableMenu extends AbstractContainerMenu {
    private final Container container;

    public KnowledgeTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(2));
    }

    public KnowledgeTableMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.KNOWLEDGE_TABLE_MENU.get(), containerId);
        this.container = container;
        checkContainerSize(container, 2);
        container.startOpen(playerInventory.player);


        this.addSlot(new Slot(container, 0, 15, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.BOOK_OF_KNOWLEDGE.get());
            }
        });


        this.addSlot(new Slot(container, 1, 35, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.AMETHYST_SHARD);
            }
        });


        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        ItemStack bookSlot = this.container.getItem(0);
        ItemStack amethystSlot = this.container.getItem(1);

        if (bookSlot.isEmpty() || !bookSlot.is(ModItems.BOOK_OF_KNOWLEDGE.get())) return false;
        if (amethystSlot.isEmpty() || !amethystSlot.is(Items.AMETHYST_SHARD)) return false;

        int costLevels = 0;
        int costAmethyst = 1;
        int buffType = buttonId;
        String baseBuffName = "";

        if (buttonId == 0) { costLevels = 3; baseBuffName = "Personal XP Buff"; }
        else if (buttonId == 1) { costLevels = 6; baseBuffName = "Party XP Buff"; }
        else if (buttonId == 2) { costLevels = 9; baseBuffName = "Global XP Buff"; }
        else { return false; }

        if (player.experienceLevel < costLevels && !player.isCreative()) return false;
        if (amethystSlot.getCount() < costAmethyst) return false;

        if (!player.level().isClientSide) {
            if (!player.isCreative()) player.giveExperienceLevels(-costLevels);
            amethystSlot.shrink(costAmethyst);


            int randomMinutes = 15 + player.getRandom().nextInt(46);
            int durationTicks = 20 * 60 * randomMinutes;

            ItemStack enchantedBook = new ItemStack(ModItems.ENCHANTED_KNOWLEDGE.get());

            final int finalDurationTicks = durationTicks;
            final int finalBuffType = buffType;


            CustomData.update(DataComponents.CUSTOM_DATA, enchantedBook, tag -> {
                tag.putInt("BuffDuration", finalDurationTicks);
                tag.putInt("BuffType", finalBuffType);
            });


            String fullName = baseBuffName + " (" + randomMinutes + " Mins)";
            enchantedBook.set(DataComponents.CUSTOM_NAME,
                    net.minecraft.network.chat.Component.literal(fullName).withStyle(net.minecraft.ChatFormatting.GOLD));

            this.container.setItem(0, enchantedBook);
            this.broadcastChanges();
            player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.ENCHANTMENT_TABLE_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();


            if (index < 2) {
                if (!this.moveItemStackTo(slotStack, 2, 38, true)) return ItemStack.EMPTY;
            }

            else {

                if (slotStack.is(ModItems.BOOK_OF_KNOWLEDGE.get()) || slotStack.is(ModItems.ENCHANTED_KNOWLEDGE.get())) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) return ItemStack.EMPTY;
                }

                else if (slotStack.is(Items.AMETHYST_SHARD)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) return ItemStack.EMPTY;
                }

                else if (index >= 2 && index < 29) {
                    if (!this.moveItemStackTo(slotStack, 29, 38, false)) return ItemStack.EMPTY;
                } else if (index >= 29 && index < 38) {
                    if (!this.moveItemStackTo(slotStack, 2, 29, false)) return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
            if (slotStack.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, slotStack);
        }
        return itemstack;
    }
}