# Changelog

All notable changes to Ethereal Portals will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.1] - 2025-11-19

### Changed
- **Configuration Structure**: Default portal texture moved from `portal.craftablePortals.defaultTexture` to `portal.defaultTexture`
  - Centralizes texture configuration for use across all portal visualizations (GUIs, craftable items, etc.)
  - Improves maintainability and reduces code duplication
  - **Note**: Existing configs need manual update or delete config.yml to regenerate with new structure
- Comprehensive inline documentation added to config.yml explaining all configuration options

### Fixed
- Portal item group names now replace spaces with underscores to prevent parsing issues
  - Ensures consistent group name formatting across the plugin
  - Prevents edge cases where spaces could cause lookup failures
- Improved portal items functionality and reliability
  - Enhanced TextDisplay entity management in VisualEffectTask
  - Better handling of portal placement and visual effects
  - More robust ArmorStand marker creation

### Technical
- Removed hardcoded `DEFAULT_TEXTURE` constant from SkullUtils.java
- Removed `createDefaultIcon()` method in favor of config-based texture parameter
- All portal icon generation now uses centralized configuration value
- Updated PlayerMoveListener to read texture from plugin configuration

---

## [1.1.0] - 2025-11-18

### Added
- **Craftable Portal Items**: Players can now craft Portal Crystals to create personal portals
  - Crafting recipe: Nether Star + 4 Ender Pearls + 2 Amethyst Shards + 2 Crying Obsidian
  - Rename items in anvil to customize portal group names
  - Right-click on any block to place portals anywhere in the world
  - Portal items are breakable - punch the invisible armor stand to retrieve the item
  - `/portal give <player> [name]` command to give portal items to players
  - Automatic portal numbering (e.g., "Home #1", "Home #2", etc.)
  - Personal portal groups with format `playername:groupname` (e.g., "steve:home")
  - Portal placement spawns visual effects and creates TextDisplay markers
  - Armor stand markers at portal center for breakable portals

### Changed
- Portal selection GUI now uses NBT PersistentDataContainer to store group and portal names
- InventoryClickListener reads NBT tags instead of parsing display names for more reliable lookups
- Portal breaking event handler now uses HIGH priority to prevent other plugin interference
- ArmorStand markers are now normal size (setSmall(false)) for better player interaction
- SkullUtils now generates deterministic UUIDs for player heads based on texture data

### Technical
- Added `formatPortalDisplayName()` method in PlayerMoveListener for GUI display formatting
- Updated PortalItemListener with improved portal number generation algorithm
- Enhanced ArmorStand creation with better properties for breakable portal detection
- Improved tag parsing to handle complex group name structures with multiple colons

---

## [1.0.3] - 2025-11-17

### Fixed
- **Critical**: Fixed massive FPS drops (down to 1 FPS) after multiple server restarts caused by TextDisplay entity duplication
  - TextDisplay entities are now properly managed as persistent entities
  - Entities are only created when portals are added, not on every server restart
  - Root cause: TextDisplays were being recreated every second in VisualEffectTask despite being persistent

### Added
- `/epdebug` command for monitoring and debugging portal TextDisplay entities
  - `/epdebug count` - Shows total count of portal TextDisplays per world
  - `/epdebug list` - Lists all portal TextDisplays and highlights duplicates
  - `/epdebug check` - Shows nearby TextDisplays within 50 blocks with coordinates and distance
  - `/epdebug cleanup` - Manually removes all portal TextDisplays (they respawn automatically)
- One-time sync on startup to create missing TextDisplays for portals created before this update

### Changed
- `VisualEffectTask` now only spawns particle effects, TextDisplay management moved to portal commands
- TextDisplays are created immediately when portals are added via `/portal group add`
- Improved chunk loading handling to prevent entity duplication in unloaded chunks

### Technical
- Removed `ensureTextDisplay()` loop from periodic task execution
- Added `syncMissingTextDisplays()` for one-time startup synchronization
- TextDisplay creation now properly handles chunk loading to avoid false negatives

---

## [1.0.2] - 2025-11-16

### Added
- CurseForge integration to publishing workflow
  - Automatic publishing to CurseForge on release
  - Support for both Modrinth and CurseForge platforms

### Changed
- Updated dependency `org.junit.jupiter:junit-jupiter` from 6.0.0 to 6.0.1
- Updated dependency `org.junit.platform:junit-platform-launcher` from 6.0.0 to 6.0.1
- Updated dependency `com.github.spotbugs` from 6.4.2 to 6.4.5
- Updated dependency `com.github.spotbugs:spotbugs-annotations` from 4.9.6 to 4.9.8

### Removed
- Deprecated `main.yml` workflow in favor of streamlined `publish.yml`

---

## [1.0.1] - 2025-11-16

### Changed
- Resolved all Checkstyle violations across the entire codebase
- Fixed all SpotBugs violations with proper null checks and resource handling
- Improved code quality and maintainability throughout all classes
- Added SpotBugs exclude filter configuration for legitimate cases

### Technical
- Enhanced null safety with proper annotations and checks
- Improved resource management and exception handling
- Refactored code to meet static analysis standards
- Better compliance with Java coding best practices

---

## [1.0.0] - 2025-11-15

### Added
- **Portal Group System**: Create and manage portal groups for organized teleportation
  - `/portal group create <name>` - Create new portal groups
  - `/portal group delete <name>` - Delete portal groups
  - `/portal group add <group> <name> <x> <y> <z> [world] [icon]` - Add portals with coordinates
  - `/portal group remove <group> <name>` - Remove portals from groups
  - `/portal group list [group]` - List all groups or portals in a specific group

- **Smart Teleportation**:
  - Direct teleportation between 2 portals in a group
  - GUI selection menu for 3+ portals in a group
  - Configurable teleportation cooldown (default 3 seconds)
  - Configurable message cooldown to prevent spam
  - Teleportation delay with animation (0.5 seconds)

- **Custom Icon System**:
  - `/portal icon add <name> <base64>` - Add custom player head textures
  - `/portal icon remove <name>` - Remove custom icons
  - `/portal icon list` - View all icons in an interactive GUI
  - Assign icons to portals when creating them
  - Default custom icon for portals without specific icons

- **Visual Effects**:
  - Animated particle spirals around each portal using END_ROD particles
  - Floating text displays above portals showing portal names
  - Portal particles and sounds on teleportation
  - Text displays automatically cleaned up when portals are deleted

- **Tab Completion**:
  - Complete suggestions for all command arguments
  - Dynamic group name suggestions
  - Dynamic portal name suggestions based on selected group
  - Dynamic icon name suggestions
  - Dynamic world name suggestions
  - Current coordinate suggestions for X, Y, Z positions
  - Support for relative coordinates with `~` in tab completion

- **Coordinate System**:
  - Absolute coordinates: `/portal group add cities spawn 100 64 200`
  - Relative coordinates: `/portal group add cities spawn ~ ~-1 ~`
  - Mixed absolute and relative: `/portal group add cities spawn 100 ~ 200`
  - Coordinate parsing with proper error handling

- **Configuration System**:
  - Adjustable portal hitbox dimensions (width, depth, height)
  - Configurable teleportation cooldown
  - Configurable message cooldown
  - Default config file generation on first run

- **Permissions**:
  - `portal.use` - Use portals and view lists (default: true)
  - `portal.group.create` - Create portal groups (default: op)
  - `portal.group.delete` - Delete portal groups (default: op)
  - `portal.group.add` - Add portals to groups (default: op)
  - `portal.group.remove` - Remove portals from groups (default: op)
  - `portal.icon.add` - Add custom icons (default: op)
  - `portal.icon.remove` - Remove custom icons (default: op)
  - `portal.icon.list` - View icon list (default: op)

- **Data Persistence**:
  - JSON-based storage for portal groups
  - JSON-based storage for custom icons
  - Automatic saving on changes
  - Automatic loading on server startup

- **User Interface**:
  - Color-coded chat messages (green for success, red for errors, yellow for warnings)
  - Consistent message formatting with plugin prefix
  - Informative error messages with usage hints
  - Portal selection GUI with coordinates and world information
  - Click-to-teleport functionality in GUI

- **Case Sensitivity**:
  - Portal and group names preserve original capitalization
  - Case-insensitive lookups for commands
  - Case-insensitive comparison for duplicate detection

- **Multi-World Support**:
  - Teleport across different worlds (Overworld, Nether, End, custom worlds)
  - Optional world parameter when creating portals
  - World name display in portal selection GUI

### Technical
- Built with Gradle using Shadow plugin for fat JAR creation
- Compatible with Minecraft 1.21+ (Paper/Spigot)
- Uses modern Bukkit PlayerProfile API for custom heads
- SpotBugs and Checkstyle integration for code quality
- Proper thread safety with volatile variables
- Async teleportation for better performance
- Entity cleanup on portal/group deletion
- Unique text display tags per group+portal combination

### Fixed
- Text displays now properly deleted when portals or groups are removed
- Portal group association bug where same-named portals showed wrong group
- Duplicate portal names in different groups now each get their own text display
- Color codes in messages now properly maintained after variables
- Item meta return values properly checked
- IOException handling in data manager
- Directory creation checks in data manager
- Thread-safe task ID management

---

## Release Notes

This is the initial release of Ethereal Portals, a mystical portal plugin for Minecraft servers. The plugin provides a complete portal system with visual effects, custom icons, and flexible configuration options.

### Highlights

1. **Easy Setup**: Simple commands to create portal networks
2. **Beautiful Visuals**: Animated particles and floating text displays
3. **Flexible Configuration**: Adjustable hitboxes and cooldowns
4. **Custom Icons**: Use any player head texture for portal icons
5. **Smart Teleportation**: Automatic behavior based on number of portals
6. **Tab Completion**: Full command completion for better UX
7. **Multi-World**: Seamless teleportation across different worlds

### Known Limitations

- Portal hitbox is always axis-aligned (no rotation support)
- Visual effects run on a fixed 1-second interval
- Text displays are always 3 blocks above portal base
- No economy integration for portal creation costs
- No per-portal permissions (only per-group via external permission plugins)

### Future Considerations

- Configurable visual effects (particle type, density, colors)
- Portal linking/pairing system
- Portal activation items or conditions
- Portal animation customization
- Economy integration for portal costs
- Per-player portal visibility
- Portal network visualization
- Configurable text display height and style
