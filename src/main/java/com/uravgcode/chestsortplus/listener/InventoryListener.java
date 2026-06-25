package com.uravgcode.chestsortplus.listener;

import com.uravgcode.chestsortplus.ChestSortPlus;
import com.uravgcode.chestsortplus.key.ChestSortKeys;
import com.uravgcode.chestsortplus.sorter.InventorySorter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class InventoryListener implements Listener {
    private static final int HOTBAR_SORT_BUTTON_SLOT = 8;

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

        if (trySortContainerFromHotbarButton(event)) return;

        final var keybind = ClickType.valueOf(dataContainer.getOrDefault(ChestSortKeys.KEYBIND, PersistentDataType.STRING, "SHIFT_LEFT"));
        if (event.getClick() != keybind) return;

        final var clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) return;

        final var inventory = event.getClickedInventory();
        if (inventory == null) return;

        final var holder = inventory.getHolder();
        if (holder == null) return;

        switch (inventory.getType()) {
            case PLAYER -> {
                if (!isPlayerOnlyView(event)) return;

                if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                    inventorySorter.sortInventory(inventory, 0, 8);
                } else {
                    inventorySorter.sortInventory(inventory, 9, 35);
                }
                event.setCancelled(true);
                syncInventory(event);
            }
            default -> {
                if (!sortContainerInventory(inventory, holder)) return;
                event.setCancelled(true);
                syncInventory(event);
            }
        }
    }

    private boolean trySortContainerFromHotbarButton(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT) return false;

        final var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) return false;
        if (event.getSlotType() != InventoryType.SlotType.QUICKBAR || event.getSlot() != HOTBAR_SORT_BUTTON_SLOT) {
            return false;
        }

        final var topInventory = event.getView().getTopInventory();
        if (topInventory.getType() == InventoryType.CRAFTING) return false;

        final var holder = topInventory.getHolder();
        if (holder == null || !sortContainerInventory(topInventory, holder)) return false;

        event.setCancelled(true);
        syncInventory(event);
        return true;
    }

    private boolean sortContainerInventory(Inventory inventory, InventoryHolder holder) {
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

    private static boolean isPlayerOnlyView(InventoryClickEvent event) {
        return event.getView().getTopInventory().getType() == InventoryType.CRAFTING;
    }

    private static void syncInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Bukkit.getScheduler().runTask(ChestSortPlus.instance(), player::updateInventory);
    }
}
