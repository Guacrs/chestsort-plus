package com.uravgcode.chestsortplus.listener;

import com.uravgcode.chestsortplus.key.ChestSortKeys;
import com.uravgcode.chestsortplus.sorter.InventorySorter;
import org.bukkit.Material;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class InventoryListener implements Listener {
    private final InventorySorter inventorySorter;

    public InventoryListener() {
        this.inventorySorter = new InventorySorter();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        final var player = event.getWhoClicked();
        if (!player.hasPermission("chestsort.use")) return;

        final var dataContainer = player.getPersistentDataContainer();
        if (!dataContainer.getOrDefault(ChestSortKeys.ENABLED, PersistentDataType.BOOLEAN, false)) return;

        final var keybind = ClickType.valueOf(dataContainer.getOrDefault(ChestSortKeys.KEYBIND, PersistentDataType.STRING, "SHIFT_LEFT"));
        if (event.getClick() != keybind) return;

        final var clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) return;

        final var inventory = event.getClickedInventory();
        if (inventory == null) return;

        switch (inventory.getType()) {
            case PLAYER -> {
                // Slot 8 should only trigger sorting while a container is open.
                if (event.getSlotType() != InventoryType.SlotType.QUICKBAR || event.getSlot() != 8 || isPlayerInventoryView(event)) {
                    return;
                }
                sortInventory(event.getView().getTopInventory());
                event.setCancelled(true);
            }
            case ENDER_CHEST, SHULKER_BOX, BARREL, DROPPER, DISPENSER, HOPPER -> {
                sortInventory(inventory);
                event.setCancelled(true);
            }
            case CHEST -> {
                sortInventory(inventory);
                event.setCancelled(true);
            }
        }
    }

    private boolean isPlayerInventoryView(InventoryClickEvent event) {
        return switch (event.getView().getTopInventory().getType()) {
            case PLAYER, CRAFTING, CREATIVE -> true;
            default -> false;
        };
    }

    private void sortInventory(Inventory inventory) {
        switch (inventory.getType()) {
            case CHEST -> {
                final var holder = inventory.getHolder();
                if (holder == null) return;
                switch (holder) {
                    case Llama llama -> inventorySorter.sortInventory(inventory, 2, llama.getStrength() * 3 + 1);
                    case ChestedHorse ignored -> inventorySorter.sortInventory(inventory, 2, 16);
                    default -> inventorySorter.sortInventory(inventory);
                }
            }
            case PLAYER -> inventorySorter.sortInventory(inventory, 0, 8);
            default -> inventorySorter.sortInventory(inventory);
        }
    }
}
