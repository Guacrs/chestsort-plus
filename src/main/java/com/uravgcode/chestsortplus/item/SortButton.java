package com.uravgcode.chestsortplus.item;

import com.uravgcode.chestsortplus.key.ChestSortKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class SortButton {
    private SortButton() {}

    public static ItemStack create() {
        final var item = ItemStack.of(Material.HOPPER);
        item.editMeta(meta -> {
            meta.displayName(Component.text("Sort", NamedTextColor.GREEN));
            meta.lore(List.of(Component.text("Click to sort this container", NamedTextColor.GRAY)));
            meta.getPersistentDataContainer().set(ChestSortKeys.SORT_BUTTON, PersistentDataType.BYTE, (byte) 1);
        });
        return item;
    }

    public static boolean isSortButton(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return item.getPersistentDataContainer().has(ChestSortKeys.SORT_BUTTON, PersistentDataType.BYTE);
    }
}
