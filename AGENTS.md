# AGENTS.md

## Cursor Cloud specific instructions

Chestsort+ is a single PaperMC (Minecraft) server plugin written in Java, built with Gradle (`paperweight-userdev`). There is one buildable artifact and no separate services.

- **Toolchain:** The build requires JDK 25 (Gradle toolchain pinned to language version 25). Temurin JDK 25 is installed at `/usr/lib/jvm/temurin-25` and is auto-detected by Gradle's toolchain detection (the default system `java` may still be JDK 21, which is fine — Gradle itself runs on it and uses JDK 25 for compilation). No `JAVA_HOME` export is needed.
- **Build / lint / test:** `./gradlew build` compiles, runs `check`, and produces the plugin jar in `build/libs/`. There is no separate lint task and there are currently no unit tests (`:test` is `NO-SOURCE`); `check` is the verification entry point and runs as part of `build`.
- **First build is slow:** `paperweightUserdevSetup` decompiles/patches the Paper server (~1 min) on the first run, then is cached in the workspace `.gradle`/project cache. Subsequent builds are fast.
- **Running the plugin:** `./gradlew runServer` downloads and launches a real Paper dev server in `run/` with the plugin installed. It requires EULA acceptance: create `run/eula.txt` containing `eula=true` before the first run. `./gradlew runFolia` runs a Folia server instead. The server reads commands from stdin, so run it in an interactive/tmux session to type console commands (e.g. `chestsort version`, `chestsort reload`). `stop` shuts it down cleanly.
- **In-game features vs. console:** The core sort-on-shift-click feature and the `/chestsort` settings dialog require a connected Minecraft client (a player executor). The admin subcommands `chestsort version` and `chestsort reload` can be run from the server console for smoke testing without a client.
