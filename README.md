# Chestsort+

Chestsort+ is a modern, lightweight PaperMC plugin that lets players sort chests, other containers, and their own
inventory.

## Sorting

**Sort button (all players, including Bedrock via Geyser):** When you open a chest or other supported container, a
**Sort Chest** button appears in your hotbar. Click it to sort the container.

**Shift-click (Java Edition):** Shift-click an empty slot in the container or your inventory to sort that section.
Customize the shift-click binding with `/chestsort`.

Supported containers: chests, ender chests, shulker boxes, barrels, droppers, dispensers, and hoppers.

## Commands

| Command              | Description                                                | Permission        |
|----------------------|------------------------------------------------------------|-------------------|
| `/chestsort`         | Open the chestsort settings dialog (shift-click keybind)   | None              |
| `/chestsort reload`  | Reloads the plugin’s configuration files                   | `chestsort.admin` |
| `/chestsort version` | Displays the current plugin version and checks for updates | `chestsort.admin` |

## Configuration

Button appearance can be customized in `config.yml` under the `button` section (material, display name, hotbar slot).
