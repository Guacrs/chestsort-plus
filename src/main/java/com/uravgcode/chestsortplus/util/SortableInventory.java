package com.uravgcode.chestsortplus.util;

import com.uravgcode.chestsortplus.sorter.InventorySorter;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SortableInventory {
    private static final InventorySorter SORTER = new InventorySorter();

    private SortableInventory() {}

    public static boolean isSortable(InventoryType type) {
        return switch (type) {
            case CHEST, ENDER_CHEST, SHULKER_BOX, BARREL, DROPPER, DISPENSER, HOPPER -> true;
            default -> false;
        };
    }

    public static void sort(Inventory inventory) {
        final InventoryHolder holder = inventory.getHolder();
        if (holder == null) return;

        switch (inventory.getType()) {
            case ENDER_CHEST, SHULKER_BOX, BARREL, DROPPER, DISPENSER, HOPPER -> SORTER.sortInventory(inventory);
            case CHEST -> {
                switch (holder) {
                    case Llama llama -> SORTER.sortInventory(inventory, 2, llama.getStrength() * 3 + 1);
                    case ChestedHorse ignored -> SORTER.sortInventory(inventory, 2, 16);
                    default -> SORTER.sortInventory(inventory);
                }
            }
            case PLAYER -> SORTER.sortInventory(inventory);
            default -> {}
        }
    }

    public static void sortPlayerSection(Inventory inventory, int startSlot, int endSlot) {
        SORTER.sortInventory(inventory, startSlot, endSlot);
    }
}
