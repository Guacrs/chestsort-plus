package com.uravgcode.chestsortplus.listener;

import com.uravgcode.chestsortplus.button.SortButton;
import com.uravgcode.chestsortplus.button.SortButtonManager;
import com.uravgcode.chestsortplus.key.ChestSortKeys;
import com.uravgcode.chestsortplus.util.SortableInventory;
import org.bukkit.Material;
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
    private final SortButtonManager sortButtonManager;

    public InventoryListener(SortButtonManager sortButtonManager) {
        this.sortButtonManager = sortButtonManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (handleSortButtonClick(event, player)) return;

        final var dataContainer = player.getPersistentDataContainer();
        final var keybind = ClickType.valueOf(dataContainer.getOrDefault(ChestSortKeys.KEYBIND, PersistentDataType.STRING, "SHIFT_LEFT"));
        if (event.getClick() != keybind) return;

        final var clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) return;

        final var inventory = event.getClickedInventory();
        if (inventory == null) return;

        if (inventory.getHolder() == null) return;

        switch (inventory.getType()) {
            case PLAYER -> {
                if (event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                    SortableInventory.sortPlayerSection(inventory, 0, 8);
                } else {
                    SortableInventory.sortPlayerSection(inventory, 9, 35);
                }
                event.setCancelled(true);
            }
            case ENDER_CHEST, SHULKER_BOX, BARREL, DROPPER, DISPENSER, HOPPER, CHEST -> {
                SortableInventory.sort(inventory);
                event.setCancelled(true);
            }
        }
    }

    private boolean handleSortButtonClick(InventoryClickEvent event, Player player) {
        if (SortButton.isSortButton(event.getCurrentItem()) || SortButton.isSortButton(event.getCursor())) {
            event.setCancelled(true);
            if (SortButton.isSortButton(event.getCurrentItem())) {
                final var topInventory = event.getView().getTopInventory();
                if (SortableInventory.isSortable(topInventory.getType())) {
                    SortableInventory.sort(topInventory);
                }
            }
            return true;
        }

        final var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return false;

        if (sortButtonManager.isButtonSlot(player, event.getSlot(), clickedInventory)) {
            event.setCancelled(true);
            return true;
        }

        return false;
    }
}
