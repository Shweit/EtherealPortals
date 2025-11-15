# EtherealPortals

A mystical portal plugin for Minecraft Paper servers that allows players to create custom portal groups for fast travel between locations.

## Features

- **Portal Groups**: Organize portals into groups for easy teleportation
- **Visual Effects**: Beautiful purple particles and text displays above portals
- **Smart Teleportation**:
  - 2 portals in a group: Direct teleport to the other
  - 3+ portals: GUI opens for destination selection
- **Custom Icons**: Add custom skull textures for portals in the GUI
- **Cooldown System**: Prevents teleport spam
- **World Support**: Teleport across different worlds (Overworld, Nether, End, etc.)
- **Permission System**: Per-group permissions for access control
- **Sound & Animation**: Teleport sounds and particle animations

## Requirements

- Minecraft Paper 1.21+ (or compatible fork)
- Java 21

## Installation

1. Download the latest `EtherealPortals.jar` from releases
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/EtherealPortals/config.yml`

## Commands

All commands start with `/portal` (aliases: `/portals`, `/ep`, `/etherealportals`)

### Group Management

| Command | Permission | Description |
|---------|------------|-------------|
| `/portal group create <name>` | `portal.group.create` | Create a new portal group |
| `/portal group delete <name>` | `portal.group.delete` | Delete a portal group and all its portals |
| `/portal group add <group> <name> <x> <y> <z> [icon]` | `portal.group.add` | Add a portal to a group (supports `~` for relative coordinates) |
| `/portal group remove <group> <name>` | `portal.group.remove` | Remove a portal from a group |

**Coordinate Support:**
- Absolute: `/portal group add Spawn Portal1 100 64 -50`
- Relative: `/portal group add Spawn Portal2 ~ ~ ~` (current position)
- Relative with offset: `/portal group add Spawn Portal3 ~5 ~-2 ~10`

### Icon Management

| Command | Permission | Description |
|---------|------------|-------------|
| `/portal icon add <name> <base64>` | `portal.icon.add` | Add a custom icon |
| `/portal icon remove <name>` | `portal.icon.remove` | Remove an icon |
| `/portal icon list` | `portal.icon.list` | View all available icons (GUI) |

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `portal.admin` | op | Grants all admin permissions |
| `portal.use` | true | Allows using the /portal command |
| `portal.group.create` | op | Create portal groups |
| `portal.group.delete` | op | Delete portal groups |
| `portal.group.add` | op | Add portals to groups |
| `portal.group.remove` | op | Remove portals from groups |
| `portal.icon.add` | op | Add custom icons |
| `portal.icon.remove` | op | Remove icons |
| `portal.icon.list` | op | View icon list |
| `portal.group.use.<group>` | true | Use a specific portal group |
| `portal.group.use.*` | true | Use all portal groups |

## Configuration

The config file (`config.yml`) allows you to customize:

- **Teleportation**: Cooldown time, sounds, animations
- **Visuals**: Particle type, density, text display color and size
- **GUI**: Title, coordinate display, world display
- **Messages**: All plugin messages are customizable

### Example Config

```yaml
teleport:
  cooldown: 3  # Seconds between teleports
  sound-enabled: true
  animation-enabled: true

visuals:
  particles:
    enabled: true
    type: PORTAL  # Recommended: PORTAL, END_ROD, WITCH, ENCHANT
    density: 20
  text-display:
    enabled: true
    color: "#C77DFF"  # Purple color (hex format)
    offset-y: 3.0
    scale: 1.5
```

**Note:** Some particle types like `DRAGON_BREATH` require additional data parameters. Stick to simple particles like `PORTAL`, `END_ROD`, `WITCH`, or `ENCHANT` for best results.

## Usage Example

### Creating a Portal Network

1. **Create a group:**
   ```
   /portal group create Spawn
   ```

2. **Add portals to the group:**
   ```
   /portal group add Spawn MainSpawn 0 64 0
   /portal group add Spawn ShopSpawn 100 64 100
   /portal group add Spawn FarmSpawn -50 64 50
   ```

3. **Players walk into any portal in the "Spawn" group and can teleport to the others!**

### Using Custom Icons

1. **Get a Base64 skull texture** from sites like minecraft-heads.com

2. **Add the icon:**
   ```
   /portal icon add MyIcon <base64_value>
   ```

3. **Use it when creating a portal:**
   ```
   /portal group add Spawn FancyPortal 0 64 0 MyIcon
   ```

## Portal Mechanics

- **Hitbox**: Each portal has a 3-block-high hitbox (base block + 2 blocks up)
- **Detection**: Portals detect players entering the hitbox
- **Teleportation**:
  - With 2 portals: Instant teleport to the other portal
  - With 3+ portals: GUI opens for destination selection
- **Cooldown**: Default 3 seconds (configurable)

## Support

For issues, feature requests, or questions:
- GitHub Issues: https://github.com/shweit/EtherealPortals/issues

## License

This project is licensed under the MIT License.

## Credits

Developed by **shweit**
Built with Paper API and PaperLib
