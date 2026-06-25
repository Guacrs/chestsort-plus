package com.uravgcode.chestsortplus.settings;

import com.uravgcode.chestsortplus.ChestSortPlus;
import com.uravgcode.chestsortplus.key.ChestSortKeys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlayerSettings {
    private PlayerSettings() {}

    public static boolean isSortingEnabled(Player player) {
        final var defaultEnabled = ChestSortPlus.instance().getConfig().getBoolean("settings.default-enabled", true);
        return player.getPersistentDataContainer().getOrDefault(
            ChestSortKeys.ENABLED,
            PersistentDataType.BOOLEAN,
            defaultEnabled
        );
    }

    public static boolean isChestSortButtonEnabled() {
        return ChestSortPlus.instance().getConfig().getBoolean("settings.chest-sort-button", true);
    }
}
