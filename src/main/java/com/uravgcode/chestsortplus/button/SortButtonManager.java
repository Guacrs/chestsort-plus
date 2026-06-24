package com.uravgcode.chestsortplus.button;

import com.uravgcode.chestsortplus.util.SortableInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class SortButtonManager {
    private final Map<UUID, ItemStack> savedHotbarItems = new ConcurrentHashMap<>();

    public void showButton(Player player, Inventory topInventory) {
        if (!SortableInventory.isSortable(topInventory.getType())) return;

        final PlayerInventory inventory = player.getInventory();
        final int slot = SortButton.hotbarSlot();
        savedHotbarItems.putIfAbsent(player.getUniqueId(), cloneOrNull(inventory.getItem(slot)));
        inventory.setItem(slot, SortButton.create());
    }

    public void hideButton(Player player) {
        final UUID playerId = player.getUniqueId();
        final ItemStack savedItem = savedHotbarItems.remove(playerId);
        if (savedItem == null) return;

        player.getInventory().setItem(SortButton.hotbarSlot(), savedItem);
    }

    public void clear(Player player) {
        savedHotbarItems.remove(player.getUniqueId());
    }

    public boolean isButtonSlot(Player player, int slot, Inventory clickedInventory) {
        if (!(clickedInventory instanceof PlayerInventory playerInventory)) return false;
        if (!playerInventory.equals(player.getInventory())) return false;
        return slot == SortButton.hotbarSlot();
    }

    private static @Nullable ItemStack cloneOrNull(@Nullable ItemStack item) {
        return item == null ? null : item.clone();
    }
}
