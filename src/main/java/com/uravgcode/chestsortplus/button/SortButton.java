package com.uravgcode.chestsortplus.button;

import com.uravgcode.chestsortplus.ChestSortPlus;
import com.uravgcode.chestsortplus.key.ChestSortKeys;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class SortButton {
    private SortButton() {}

    public static ItemStack create() {
        final var plugin = ChestSortPlus.instance();
        final var materialName = plugin.getConfig().getString("button.material", "HOPPER");
        final var material = Material.matchMaterial(materialName);
        final var item = new ItemStack(material == null ? Material.HOPPER : material);

        item.editMeta(meta -> {
            final var displayName = plugin.getConfig().getString("button.name", "&a&lSort Chest");
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(displayName));
            meta.getPersistentDataContainer().set(ChestSortKeys.SORT_BUTTON, PersistentDataType.BOOLEAN, true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        });

        return item;
    }

    public static boolean isSortButton(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().has(ChestSortKeys.SORT_BUTTON, PersistentDataType.BOOLEAN);
    }

    public static int hotbarSlot() {
        final int slot = ChestSortPlus.instance().getConfig().getInt("button.slot", 8);
        return Math.clamp(slot, 0, 8);
    }
}
