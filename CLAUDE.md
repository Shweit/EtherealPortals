# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

EtherealPortals is a Minecraft plugin for Paper/Spigot 1.21+ that enables mystical portal networks for fast travel. Players can organize portals into groups and teleport between them with visual effects.

## Build System & Commands

**Build Tool**: Gradle

### Essential Commands

```bash
# Build the plugin JAR (creates shadowJar)
./gradlew build

# Build without running tests
./gradlew build -x test

# Run tests only
./gradlew test

# Run checkstyle
./gradlew checkstyle

# Run SpotBugs static analysis
./gradlew spotbugsMain

# Create release build (strips version from JAR name)
./gradlew release -Pver=v1.0.0

# Clean build artifacts
./gradlew clean
```

**Output Location**: `build/libs/EtherealPortals-<version>.jar` (or `EtherealPortals.jar` for releases)

### Running Tests

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.shweit.etherealportals.EtherealPortalsTest"

# Run tests with detailed output
./gradlew test --info
```

**Test Framework**: JUnit 5 (Jupiter)
**Test Location**: `src/test/java/`

## Architecture

### Core Components

1. **EtherealPortals (Main Plugin Class)** - `src/main/java/com/shweit/etherealportals/EtherealPortals.java`
   - Central plugin instance that coordinates all managers
   - Manages lifecycle (onEnable/onDisable)
   - Registers commands and listeners
   - Starts/stops visual effect task
   - Holds configuration values (hitbox dimensions, cooldowns)

2. **Managers** - `src/main/java/com/shweit/etherealportals/manager/`
   - **PortalManager**: In-memory storage of portal groups and portals, spatial queries to find portals at locations
   - **IconManager**: Manages custom player head textures (base64) for portal GUI icons
   - **DataManager**: Handles YAML persistence (groups.yml, icons.yml) - loads on startup, saves on shutdown
   - **CooldownManager**: Prevents teleport spam with configurable cooldowns

3. **Models** - `src/main/java/com/shweit/etherealportals/model/`
   - **PortalGroup**: Named collection of portals (uses LinkedHashMap with case-insensitive keys)
   - **Portal**: Single portal location with optional custom icon
   - **PortalIcon**: Custom player head texture (base64 encoded)

4. **Listeners** - `src/main/java/com/shweit/etherealportals/listener/`
   - **PlayerMoveListener**: Detects portal entry via hitbox collision, handles teleportation logic
     - 2 portals in group → direct teleport with 0.5s delay
     - 3+ portals → opens GUI for selection
   - **InventoryClickListener**: Handles portal selection from GUI
   - **InventoryCloseListener**: Cleanup when inventory closes

5. **Visual Effects** - `src/main/java/com/shweit/etherealportals/visual/VisualEffectTask.java`
   - Runs every 20 ticks (1 second)
   - Spawns particle spirals (END_ROD) around portals
   - Creates/maintains TextDisplay entities above portals (3 blocks up)
   - Uses scoreboard tags for entity tracking: `ep_portal:<groupname>:<portalname>`

### Key Design Patterns

**Case-Insensitive Storage**: Portal and group names are stored with original capitalization but use lowercase keys in maps for lookups. This ensures `MainHub` displays correctly but `/portal group list mainhub` still works.

**State Tracking**: PlayerMoveListener uses a `Set<UUID>` to track which players are inside portal hitboxes, preventing duplicate teleportation triggers.

**Async Teleportation**: Uses `player.teleportAsync()` for cross-world teleportation to avoid blocking the main thread.

**Visual Entity Management**: TextDisplay entities are tagged and checked before spawning to prevent duplicates. Removed when portals/groups are deleted.

### Data Flow

**Portal Creation Flow**:
1. Command → PortalCommand validates input
2. PortalManager adds portal to group (creates group if needed)
3. DataManager saves groups.yml
4. VisualEffectTask spawns TextDisplay on next tick

**Teleportation Flow**:
1. PlayerMoveListener detects player in hitbox via findPortalAt()
2. Checks cooldown via CooldownManager
3. For 2-portal groups: direct teleport with particle effects + 10-tick delay
4. For 3+ portal groups: opens GUI with InventoryClickListener handling selection
5. Updates cooldown after successful teleport

## Configuration

**Location**: `src/main/resources/config.yml`

Key settings:
- `portal.hitbox.{width,depth,height}`: Portal detection area (default: 2.0 blocks)
- `portal.teleport.cooldownSeconds`: Time between teleports (default: 3s)
- `portal.teleport.messageCooldownSeconds`: Cooldown message throttle (default: 1s)

**Runtime**: Config values are cached in EtherealPortals instance fields for performance.

## Important Implementation Details

- **Java Version**: Java 21 (sourceCompatibility and targetCompatibility)
- **Minecraft Version**: Paper API 1.21.10
- **Dependencies**: PaperLib (shaded), Mojang authlib (for GameProfile in SkullUtils)
- **Shadow JAR**: PaperLib is relocated to `shadow.io.papermc.paperlib` and minimized
- **Plugin.yml**: Uses Gradle property expansion for NAME, VERSION, and PACKAGE
- **Checkstyle**: Max warnings = 0 (enforced)
- **SpotBugs**: Uses excludeFilter at `config/spotbugs/excludeFilter.xml`

## Code Quality Standards

- All code must pass Checkstyle validation (Google style)
- SpotBugs analysis must complete without critical issues
- All managers should be documented with Javadoc
- Use Paper's modern APIs (Adventure Component, async teleport) where available
- Avoid deprecated Bukkit methods
