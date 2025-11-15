# EtherealPortals Plugin - Spezifikation

## Übersicht

EtherealPortals ist ein Minecraft Portal-Plugin für Paper 1.21+ das es Spielern ermöglicht, zwischen verschiedenen Locations zu teleportieren. Portale werden in **Gruppen** organisiert, innerhalb derer Spieler zwischen den Portalen teleportieren können.

## Kern-Konzept

### Portal-Gruppen
- Portale werden in **Gruppen** organisiert
- Spieler können nur zwischen Portalen **innerhalb derselben Gruppe** teleportieren
- Jede Gruppe kann 2+ Portale haben
- Beispiel: Gruppe "Städte" könnte Portale "Berlin", "München", "Hamburg" enthalten

### Teleportations-Mechanik

#### 2 Portale in einer Gruppe
- **Direkte Teleportation** ohne GUI
- Spieler betritt Portal A → wird automatisch zu Portal B teleportiert

#### 3+ Portale in einer Gruppe
- **GUI-Auswahl** mit allen verfügbaren Zielen
- Spieler betritt Portal A → GUI öffnet sich mit Portalen B, C, D, etc.
- Spieler wählt Ziel aus → Teleportation

### Portal-Erkennung

#### Hitbox
- **Basis**: Der Block auf dem das Portal erstellt wurde
- **Höhe**: 2 Blocks hoch
- **Breite/Tiefe**: Etwas größer als 1 Block (z.B. 2x2x3) für einfacheres Triggern
- Spieler muss in der Hitbox sein um zu teleportieren

#### Trigger-Verhalten
- Portal wird getriggert wenn Spieler die Hitbox betritt
- Funktioniert sowohl beim Bewegen ALS AUCH beim Stillstehen
- Portal sollte NUR beim **ersten Eintritt** triggern
- Nicht erneut triggern während Spieler noch in der Hitbox ist

## Features

### 1. Portal-Management

#### Portal erstellen
```
/portal group add <gruppe> <name> <x> <y> <z> [welt] [icon]
```
- Erstellt ein neues Portal an den angegebenen Koordinaten
- Gruppe wird automatisch erstellt wenn sie nicht existiert
- Unterstützt **relative Koordinaten** mit `~`:
  - `~` = aktuelle Position
  - `~5` = 5 Blocks in positive Richtung
  - `~-3` = 3 Blocks in negative Richtung
- World ist optional (Standard: aktuelle Welt des Spielers)
- Icon ist optional (Standard: Default-Icon aus config)

#### Portal löschen
```
/portal group remove <gruppe> <name>
```

#### Gruppe löschen
```
/portal group delete <gruppe>
```
- Löscht alle Portale in der Gruppe

#### Portale auflisten
```
/portal group list [gruppe]
```
- Ohne Parameter: Zeigt alle Gruppen
- Mit Gruppe: Zeigt alle Portale in der Gruppe

### 2. Icon-System

Portale haben Custom-Icons die in der GUI angezeigt werden.

#### Icon hinzufügen
```
/portal icon add <name> <base64>
```
- Fügt ein Custom-Icon hinzu
- Base64 = Minecraft Skull Texture Value

#### Icon löschen
```
/portal icon remove <name>
```

#### Icons auflisten
```
/portal icon list
```
- Zeigt alle verfügbaren Icons in einer GUI

### 3. Visuelle Effekte

#### Text Display
- **Über jedem Portal** schwebt der Portal-Name
- Verwendet Text Display Entity (Minecraft 1.19.4+)
- Eigenschaften:
  - Immer zum Spieler gedreht (Billboard)
  - Sichtbar durch Blöcke (See Through)
  - Schatten für bessere Lesbarkeit
  - Fett formatiert
  - Konfigurierbare Farbe (Standard: lila)
  - Konfigurierbare Höhe über Portal (Standard: 3 Blocks)
  - Konfigurierbare Größe/Scale (Standard: 2.0)
  - View Range 50 Blocks

#### Partikel-Effekte
- **Spirale** aus Partikeln um das Portal herum
- Empfehlung: 3 verschlungene Spiralen für schönen Effekt
- Dichte konfigurierbar
- Höhe sollte Text Display nicht verdecken

#### Teleport-Animation
- Beim Teleportieren:
  - Partikel-Burst am Start-Portal
  - Sound-Effekt (ENDERMAN_TELEPORT)
  - Kurze Verzögerung (0.5s) für Animation
  - Sound-Effekt am Ziel-Portal

### 4. GUI-System

#### Portal-Auswahl GUI
- Titel konfigurierbar (Standard: "§d§lSelect Portal")
- Zeigt alle Ziel-Portale der Gruppe
- Jedes Portal wird dargestellt durch:
  - **Icon**: Custom Skull oder Default-Icon
  - **Name**: Fett + lila formatiert
  - **Lore**:
    - Leerzeile
    - Koordinaten (wenn aktiviert)
    - Welt-Name (wenn aktiviert)
    - Leerzeile
    - "§aClick to teleport!"

#### Icon-Liste GUI
- Zeigt alle verfügbaren Icons
- Nur zur Ansicht (read-only)

#### GUI-Schutz
- **Kritisch**: Items dürfen NICHT aus der GUI genommen werden können
- ALLE Click-Typen müssen geblockt werden:
  - Links-Click
  - Rechts-Click
  - Shift-Click
  - Drop
  - Number-Keys
  - etc.

### 5. Permissions

```yaml
# Admin (alle Rechte)
portal.admin

# Basis-Nutzung
portal.use

# Gruppen-Management
portal.group.create
portal.group.delete
portal.group.add
portal.group.remove

# Icon-Management
portal.icon.add
portal.icon.remove
portal.icon.list

# Portal-Nutzung (per Gruppe)
portal.group.use.<gruppenname>
portal.group.use.*  # Alle Gruppen
```

### 6. Cooldown-System

#### Teleport-Cooldown
- Verhindert Spam-Teleportation
- Konfigurierbar (Standard: 3 Sekunden)
- Wird gesetzt nach erfolgreicher Teleportation

#### Message-Cooldown
- Separater Cooldown für "Noch X Sekunden" Nachricht
- Verhindert Message-Spam
- Kürzer als Teleport-Cooldown (z.B. 1 Sekunde)

## Technische Details

### Datenpersistenz
- **YAML-Dateien** im plugins/EtherealPortals Ordner:
  - `groups.yml` - Alle Portal-Gruppen und ihre Portale
  - `icons.yml` - Custom Icons
  - `config.yml` - Plugin-Konfiguration

### Location-Serialisierung
```yaml
location:
  world: world
  x: 100.5
  y: 64.0
  z: -200.5
  yaw: 0.0
  pitch: 0.0
```

### Portal-Position
- Portale teleportieren zum **Center** des Ziel-Portals
- Center = Base Location + 0.5 X, + 0.5 Z (Mitte des Blocks)
- Y bleibt gleich wie Base Location

## Commands Übersicht

| Command | Beschreibung | Permission |
|---------|-------------|------------|
| `/portal group create <name>` | Erstellt Gruppe | `portal.group.create` |
| `/portal group delete <name>` | Löscht Gruppe | `portal.group.delete` |
| `/portal group add <gruppe> <name> <x> <y> <z> [welt] [icon]` | Fügt Portal hinzu | `portal.group.add` |
| `/portal group remove <gruppe> <name>` | Entfernt Portal | `portal.group.remove` |
| `/portal group list [gruppe]` | Listet Gruppen/Portale | `portal.use` |
| `/portal icon add <name> <base64>` | Fügt Icon hinzu | `portal.icon.add` |
| `/portal icon remove <name>` | Löscht Icon | `portal.icon.remove` |
| `/portal icon list` | Zeigt Icons | `portal.icon.list` |
| `/epdebug check` | Debug Info | `portal.admin` |
| `/epdebug hitbox` | Visualisiert Hitboxen | `portal.admin` |
| `/epdebug location` | Zeigt Position | `portal.admin` |

## Verwendungsbeispiel

### Setup einer Stadt-Gruppe
```bash
# Gruppe mit 3 Städten erstellen
/portal group add cities berlin 100 64 200 world city_icon
/portal group add cities munich 300 65 -100 world city_icon
/portal group add cities hamburg -50 63 150 world city_icon
```

### Spieler nutzt das Portal
1. Spieler geht in Berlin-Portal
2. GUI öffnet sich mit München und Hamburg
3. Spieler wählt München
4. Teleport-Animation spielt ab
5. Spieler wird nach München teleportiert
6. 3 Sekunden Cooldown bevor erneute Nutzung möglich
