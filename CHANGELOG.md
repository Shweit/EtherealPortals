# Changelog

All notable changes to Ethereal Portals will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
