package com.uravgcode.chestsortplus.listener;

import com.uravgcode.chestsortplus.ChestSortPlus;
import com.uravgcode.chestsortplus.key.ChestSortKeys;
import com.uravgcode.chestsortplus.sorter.InventorySorter;
import org.bukkit.Material;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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

        final var holder = inventory.getHolder();
        if (holder == null) return;

        switch (inventory.getType()) {
            case PLAYER -> {
                if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                    inventorySorter.sortInventory(inventory, 0, 8);
                } else {
                    inventorySorter.sortInventory(inventory, 9, 35);
                }
                event.setCancelled(true);
            }
            case ENDER_CHEST, SHULKER_BOX, BARREL, DROPPER, DISPENSER, HOPPER -> {
                inventorySorter.sortInventory(inventory);
                event.setCancelled(true);
            }
            case CHEST -> {
                switch (holder) {
                    case Llama llama -> inventorySorter.sortInventory(inventory, 2, llama.getStrength() * 3 + 1);
                    case ChestedHorse ignored -> inventorySorter.sortInventory(inventory, 2, 16);
                    default -> inventorySorter.sortInventory(inventory);
                }
                event.setCancelled(true);
            }
        }

        if (event.isCancelled()) {
            resyncInventory(player);
        }
    }

    // Cancelling the click makes the client revert its predicted slot changes, which
    // then disagree with the contents we rewrote during the sort. Force a resync on the
    // next tick (the cancel-revert is applied after this handler) to avoid ghost items,
    // e.g. an item stuck in a hotbar slot after closing the inventory.
    private void resyncInventory(HumanEntity player) {
        if (!(player instanceof Player target)) return;
        target.getScheduler().run(ChestSortPlus.instance(), task -> target.updateInventory(), null);
    }
}
