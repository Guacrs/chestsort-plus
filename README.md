# Chestsort+

Chestsort+ is a modern, lightweight PaperMC plugin that lets players sort containers and their own inventory by
clicking an empty inventory slot. Folia is supported.

## Sorting

Sorting is opt-in and disabled by default. Each player enables it and picks their keybind in the `/chestsort`
settings dialog. Once enabled, clicking an empty slot with the chosen keybind sorts that inventory.

- **Keybind:** shift-left click (default) or shift-right click.
- **Supported inventories:** chests, ender chests, shulker boxes, barrels, droppers, dispensers, hoppers, horse and
  llama chests, and the player's own inventory.

## Permission

`chestsort.use` is required for players to use the sorting feature.

## Commands

| Command              | Description                                                | Permission        |
|----------------------|------------------------------------------------------------|-------------------|
| `/chestsort`         | Open the chestsort settings dialog                         | `chestsort.use`   |
| `/chestsort reload`  | Reloads the plugin’s configuration files                   | `chestsort.admin` |
| `/chestsort version` | Displays the current plugin version and checks for updates | `chestsort.admin` |
