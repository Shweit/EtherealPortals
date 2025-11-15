# Ethereal Portals

A mystical portal plugin for Minecraft that allows players to create custom portal groups for fast travel with stunning visual effects.

## Features

- **Portal Groups**: Organize portals into logical groups for seamless travel
- **Smart Teleportation**: Automatic teleport between 2 portals, GUI selection for 3+ portals
- **Custom Icons**: Use custom player head textures for portal icons in the GUI
- **Visual Effects**: Beautiful particle spirals and floating text displays at portal locations
- **Cooldown System**: Configurable teleportation cooldowns to prevent spam
- **Flexible Hitbox**: Adjustable portal detection area (width, depth, height)
- **Tab Completion**: Full tab completion for all commands including coordinates
- **Permissions**: Granular permission system for all features
- **Case-Sensitive Names**: Portal and group names preserve original capitalization
- **World Support**: Teleport across different worlds (Overworld, Nether, End, etc.)
- **Data Persistence**: Automatic saving and loading of portals and icons

## Installation

1. Download the latest `EtherealPortals-1.0-all.jar` from the releases
2. Place the JAR file in your server's `plugins` folder
3. Restart or reload your server
4. Configure the plugin in `plugins/EtherealPortals/config.yml` if needed

## Requirements

- **Minecraft Version**: 1.21+ (Paper/Spigot)
- **Java Version**: 17+

## Commands

### Portal Groups

| Command | Description | Permission |
|---------|-------------|------------|
| `/portal group create <name>` | Create a new portal group | `portal.group.create` |
| `/portal group delete <name>` | Delete a portal group | `portal.group.delete` |
| `/portal group add <group> <name> <x> <y> <z> [world] [icon]` | Add a portal to a group | `portal.group.add` |
| `/portal group remove <group> <name>` | Remove a portal from a group | `portal.group.remove` |
| `/portal group list [group]` | List all groups or portals in a group | `portal.use` |

### Custom Icons

| Command | Description | Permission |
|---------|-------------|------------|
| `/portal icon add <name> <base64>` | Add a custom icon texture | `portal.icon.add` |
| `/portal icon remove <name>` | Remove a custom icon | `portal.icon.remove` |
| `/portal icon list` | View all custom icons in a GUI | `portal.icon.list` |

### Coordinate Syntax

When adding portals, you can use:
- **Absolute coordinates**: `/portal group add cities spawn 100 64 200`
- **Relative coordinates**: `/portal group add cities spawn ~ ~-1 ~` (relative to your position)
- **Mixed coordinates**: `/portal group add cities spawn 100 ~ 200`

Tab completion will suggest your current coordinates for convenience!

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `portal.use` | Use portals and view lists | All players |
| `portal.group.create` | Create portal groups | Op |
| `portal.group.delete` | Delete portal groups | Op |
| `portal.group.add` | Add portals to groups | Op |
| `portal.group.remove` | Remove portals from groups | Op |
| `portal.icon.add` | Add custom icons | Op |
| `portal.icon.remove` | Remove custom icons | Op |
| `portal.icon.list` | View icon list | Op |

## Configuration

The plugin's configuration file is located at `plugins/EtherealPortals/config.yml`:

```yaml
portal:
  hitbox:
    width: 2.0    # Portal detection width (blocks)
    depth: 2.0    # Portal detection depth (blocks)
    height: 2.0   # Portal detection height (blocks)
  teleport:
    cooldownSeconds: 3        # Seconds between teleports
    messageCooldownSeconds: 1 # Seconds between cooldown messages
```

## Usage Examples

### Creating a Simple Portal Network

1. **Create a portal group**:
   ```
   /portal group create cities
   ```

2. **Add two portals** (one at spawn, one at a city):
   ```
   /portal group add cities Spawn 0 64 0
   /portal group add cities CityCenter 1000 70 500 world
   ```

3. **Walk into either portal** to teleport directly to the other!

### Creating a Multi-Portal Hub

1. **Create a hub group**:
   ```
   /portal group create hub
   ```

2. **Add multiple destinations**:
   ```
   /portal group add hub Spawn 0 64 0
   /portal group add hub Shopping 500 65 -200
   /portal group add hub Arena -300 70 400
   /portal group add hub Farm 200 64 800
   ```

3. **Walk into any portal** to see a GUI with all destinations!

### Using Custom Icons

1. **Get a base64 texture** from a player head generator (e.g., minecraft-heads.com)

2. **Add the custom icon**:
   ```
   /portal icon add castle eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv...
   ```

3. **Use it in a portal**:
   ```
   /portal group add kingdoms MainCastle 100 65 100 world castle
   ```

## How It Works

### Portal Detection

Portals are detected when a player enters the configured hitbox area around a portal's base location. The default hitbox is 2x2x2 blocks, but this can be adjusted in the config.

### Teleportation Behavior

- **2 Portals in Group**: Direct teleport to the other portal (0.5s delay)
- **3+ Portals in Group**: Opens a GUI to select destination
- **Cooldown**: Prevents repeated teleportation (configurable, default 3s)

### Visual Effects

- **Particle Spirals**: Animated END_ROD particles spiral around each portal
- **Text Displays**: Floating portal names above each portal (3 blocks up)
- **Teleport Effects**: Portal particles and enderman teleport sound on use
- **Update Rate**: Visual effects update every second

### Case Sensitivity

Portal and group names are stored with their original capitalization, but lookups are case-insensitive. This means:
- You can create a group called "MainHub" and it will display as "MainHub"
- Commands like `/portal group list mainhub` will still work
- Each portal name in a group must be unique (case-insensitive)

## Data Storage

All data is stored in JSON format:
- **Portals**: `plugins/EtherealPortals/groups.json`
- **Icons**: `plugins/EtherealPortals/icons.json`

Data is automatically saved when changes are made and loaded on server startup.

## Building from Source

```bash
git clone <repository-url>
cd portal-plugin
./gradlew shadowJar
```

The compiled JAR will be in `build/libs/EtherealPortals-1.0-all.jar`

## Support

If you encounter any issues or have feature requests, please open an issue on the GitHub repository.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Enjoy your mystical portal adventures!**
