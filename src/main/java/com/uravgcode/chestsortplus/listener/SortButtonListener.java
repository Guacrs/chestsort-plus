package com.uravgcode.chestsortplus.listener;

import com.uravgcode.chestsortplus.button.SortButton;
import com.uravgcode.chestsortplus.button.SortButtonManager;
import com.uravgcode.chestsortplus.util.SortableInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SortButtonListener implements Listener {
    private final SortButtonManager sortButtonManager;

    public SortButtonListener(SortButtonManager sortButtonManager) {
        this.sortButtonManager = sortButtonManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        sortButtonManager.showButton(player, event.getInventory());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        sortButtonManager.hideButton(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sortButtonManager.clear(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        final var view = event.getView();
        final int buttonSlot = SortButton.hotbarSlot();
        for (final int rawSlot : event.getRawSlots()) {
            if (view.getInventory(rawSlot).equals(player.getInventory())
                && view.convertSlot(rawSlot) == buttonSlot) {
                event.setCancelled(true);
                return;
            }
        }

        for (final var entry : event.getNewItems().entrySet()) {
            if (SortButton.isSortButton(entry.getValue())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
