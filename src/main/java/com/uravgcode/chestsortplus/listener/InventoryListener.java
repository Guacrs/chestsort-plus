package com.uravgcode.chestsortplus.listener;

import com.uravgcode.chestsortplus.ChestSortPlus;
import com.uravgcode.chestsortplus.item.SortButton;
import com.uravgcode.chestsortplus.key.ChestSortKeys;
import com.uravgcode.chestsortplus.settings.PlayerSettings;
import com.uravgcode.chestsortplus.sorter.ContainerSorter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class InventoryListener implements Listener {
    private final ContainerSorter containerSorter;

    public InventoryListener() {
        this.containerSorter = new ContainerSorter();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!PlayerSettings.isSortingEnabled(player) || !PlayerSettings.isChestSortButtonEnabled()) return;

        final var inventory = event.getInventory();
        if (!containerSorter.supportsSortButton(inventory)) return;

        Bukkit.getScheduler().runTask(ChestSortPlus.instance(), () -> {
            if (!player.isOnline()) return;
            if (!player.getOpenInventory().getTopInventory().equals(inventory)) return;
            containerSorter.placeSortButtonIfEmpty(inventory);
            player.updateInventory();
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        containerSorter.removeSortButtonIfPresent(event.getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!PlayerSettings.isSortingEnabled(player)) return;

        if (trySortContainerFromChestButton(event)) return;
        if (trySortContainerFromHotbarButton(event)) return;

        final var keybind = ClickType.valueOf(player.getPersistentDataContainer().getOrDefault(
            ChestSortKeys.KEYBIND,
            PersistentDataType.STRING,
            "SHIFT_LEFT"
        ));
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
                    containerSorter.sortHotbar(inventory);
                } else {
                    containerSorter.sortMainInventory(inventory);
                }
                event.setCancelled(true);
                syncInventory(player);
            }
            default -> {
                if (!containerSorter.sort(inventory, holder)) return;
                event.setCancelled(true);
                syncInventory(player);
            }
        }
    }

    private boolean trySortContainerFromChestButton(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT) return false;

        final var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || !containerSorter.supportsSortButton(clickedInventory)) return false;
        if (event.getSlot() != ContainerSorter.CHEST_SORT_BUTTON_SLOT) return false;
        if (!SortButton.isSortButton(event.getCurrentItem())) return false;

        final var holder = clickedInventory.getHolder();
        if (holder == null || !containerSorter.sortPreservingButton(clickedInventory, holder)) return false;

        event.setCancelled(true);
        syncInventory((Player) event.getWhoClicked());
        return true;
    }

    private boolean trySortContainerFromHotbarButton(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT) return false;

        final var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || clickedInventory.getType() != InventoryType.PLAYER) return false;
        if (event.getSlotType() != InventoryType.SlotType.QUICKBAR
            || event.getSlot() != ContainerSorter.HOTBAR_SORT_BUTTON_SLOT) {
            return false;
        }

        final var topInventory = event.getView().getTopInventory();
        if (topInventory.getType() == InventoryType.CRAFTING) return false;

        final var holder = topInventory.getHolder();
        if (holder == null || !containerSorter.sortPreservingButton(topInventory, holder)) return false;

        event.setCancelled(true);
        syncInventory((Player) event.getWhoClicked());
        return true;
    }

    private static boolean isPlayerOnlyView(InventoryClickEvent event) {
        return event.getView().getTopInventory().getType() == InventoryType.CRAFTING;
    }

    private static void syncInventory(Player player) {
        Bukkit.getScheduler().runTask(ChestSortPlus.instance(), player::updateInventory);
    }
}
