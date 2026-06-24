package com.uravgcode.chestsortplus.key;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ChestSortKeys {
    public static NamespacedKey KEYBIND = new NamespacedKey("", "");
    public static NamespacedKey SORT_BUTTON = new NamespacedKey("", "");

    public static void init(JavaPlugin plugin) {
        KEYBIND = new NamespacedKey(plugin, "keybind");
        SORT_BUTTON = new NamespacedKey(plugin, "sort_button");
    }

    private ChestSortKeys() {}
}
