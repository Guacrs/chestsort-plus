package com.uravgcode.chestsortplus.sorter;

import com.uravgcode.chestsortplus.item.SortButton;
import org.bukkit.Material;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ContainerSorter {
    public static final int CHEST_SORT_BUTTON_SLOT = 8;
    public static final int HOTBAR_SORT_BUTTON_SLOT = 8;

    private final InventorySorter inventorySorter;

    public ContainerSorter() {
        this.inventorySorter = new InventorySorter();
    }

    public void sortHotbar(Inventory inventory) {
        inventorySorter.sortInventory(inventory, 0, 8);
    }

    public void sortMainInventory(Inventory inventory) {
        inventorySorter.sortInventory(inventory, 9, 35);
    }

    public boolean supportsSortButton(Inventory inventory) {
        if (inventory.getSize() <= CHEST_SORT_BUTTON_SLOT) return false;

        return switch (inventory.getType()) {
            case CHEST, ENDER_CHEST, SHULKER_BOX, BARREL, DROPPER, DISPENSER -> true;
            default -> false;
        };
    }

    public boolean sort(Inventory inventory, InventoryHolder holder) {
        return switch (inventory.getType()) {
            case ENDER_CHEST, SHULKER_BOX, BARREL, DROPPER, DISPENSER, HOPPER -> {
                inventorySorter.sortInventory(inventory);
                yield true;
            }
            case CHEST -> {
                switch (holder) {
                    case Llama llama -> inventorySorter.sortInventory(inventory, 2, llama.getStrength() * 3 + 1);
                    case ChestedHorse ignored -> inventorySorter.sortInventory(inventory, 2, 16);
                    default -> inventorySorter.sortInventory(inventory);
                }
                yield true;
            }
            default -> false;
        };
    }

    public boolean sortPreservingButton(Inventory inventory, InventoryHolder holder) {
        final boolean hadSortButton = removeSortButton(inventory);
        if (!sort(inventory, holder)) {
            restoreSortButton(inventory, hadSortButton);
            return false;
        }

        restoreSortButton(inventory, hadSortButton);
        return true;
    }

    public void placeSortButtonIfEmpty(Inventory inventory) {
        if (!supportsSortButton(inventory)) return;

        final var current = inventory.getItem(CHEST_SORT_BUTTON_SLOT);
        if (current != null && current.getType() != Material.AIR) return;

        inventory.setItem(CHEST_SORT_BUTTON_SLOT, SortButton.create());
    }

    public void removeSortButtonIfPresent(Inventory inventory) {
        if (!supportsSortButton(inventory)) return;

        final var current = inventory.getItem(CHEST_SORT_BUTTON_SLOT);
        if (SortButton.isSortButton(current)) {
            inventory.setItem(CHEST_SORT_BUTTON_SLOT, null);
        }
    }

    private static boolean removeSortButton(Inventory inventory) {
        final var current = inventory.getItem(CHEST_SORT_BUTTON_SLOT);
        if (!SortButton.isSortButton(current)) return false;

        inventory.setItem(CHEST_SORT_BUTTON_SLOT, null);
        return true;
    }

    private static void restoreSortButton(Inventory inventory, boolean hadSortButton) {
        if (!hadSortButton) return;

        final @Nullable ItemStack current = inventory.getItem(CHEST_SORT_BUTTON_SLOT);
        if (current == null || current.getType() == Material.AIR) {
            inventory.setItem(CHEST_SORT_BUTTON_SLOT, SortButton.create());
        }
    }
}
