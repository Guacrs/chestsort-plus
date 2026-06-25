package com.uravgcode.chestsortplus.sorter;

import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ContainerSorter {
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
}
