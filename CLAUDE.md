# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AuctionHouse is a server-side Minecraft mod allowing players to sell items to other players. It's a multi-loader mod supporting both **Fabric** and **NeoForge** using the Architectury framework.

- **Minecraft Version**: 1.21.8
- **Java Version**: 21
- **Build System**: Gradle with Architectury plugin

## Build Commands

```bash
# Build all platforms (requires Java 21)
JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-21.jdk/Contents/Home ./gradlew build

# Build specific loader
./gradlew :fabric:build
./gradlew :neoforge:build

# Run development server for testing
./gradlew :fabric:runServer
./gradlew :neoforge:runServer
```

Output JARs are placed in `/output/` directory with classifiers: `-fabric`, `-neoforge`.

## Architecture

### Multi-Loader Structure

```
common/     → Shared code (all business logic lives here)
fabric/     → Fabric-specific entry point and mixins
neoforge/   → NeoForge-specific entry point, permissions, and mixins
```

The `common` module contains all actual functionality. Loader modules only provide entry points (`AuctionHouseFabricMod`, `AuctionHouseModNeoForge`) and inject server startup hooks via Mixins.

### Core Packages (in common/)

- **auction/**: Core auction logic - `AuctionHouse` (item collection), `AuctionItem` (individual listing), `ExpiredItems`
- **command/**: Brigadier commands - 8 commands under `/ah` (main, sell, selling, expired, cancel, return, reload, help)
- **config/**: JSON config via GSON at `./config/auctionhouse.json`
- **economy/**: Abstract `EconomyHandler` with factory pattern - supports Impactor API and RealEconomy
- **gui/**: Server-side GUIs using SGui library with pagination
- **sql/**: SQLite persistence via `DatabaseManager` interface and `SQLiteDatabaseManager`

### Key Patterns

- **Factory Pattern**: `EconomyHandler.getHandler()` selects economy implementation at runtime based on loaded mods
- **Mixin Injection**: Both loaders inject into `MinecraftServer` to hook server start/stop lifecycle
- **Interface Abstraction**: `DatabaseManager` interface allows different DB implementations

### Entry Point Flow

1. Mixin injects into `MinecraftServer.runServer()`
2. Calls `AuctionHouseMod.onServerStarted(server)`
3. Initializes SQLite DB, loads config, detects economy handler
4. Registers commands via `AuctionHouseMainCommand.register()`

### Dependencies

- **SGui**: Server-side GUI library (eu.pb4:sgui)
- **SQLite JDBC**: Database storage
- **LuckPerms API**: Permissions (Fabric)
- **Impactor/RealEconomy**: Economy integrations

## Required Runtime Mods

The built mod requires these at runtime:
- SQLite Mod (minecraft-sqlite-jdbc)
- Either Impactor or RealEconomy for economy support

## Publishing to Modrinth

The Modrinth API key is stored in `.env` (not versioned). Project ID: `TODO`

To publish a new version, create one version per loader (fabric, neoforge):

```bash
# Load API key
source .env

# Publish Fabric version
curl -X POST "https://api.modrinth.com/v2/version" \
  -H "Authorization: $MODRINTH_API_KEY" \
  -F 'data={
    "name": "AuctionHouse VERSION-fabric",
    "version_number": "VERSION-fabric",
    "changelog": "Changelog here",
    "dependencies": [],
    "game_versions": ["MC_VERSION"],
    "version_type": "release",
    "loaders": ["fabric"],
    "featured": true,
    "status": "listed",
    "project_id": "TODO",
    "file_parts": ["file"]
  }' \
  -F "file=@output/auctionhouse-VERSION-fabric.jar"

# Repeat for neoforge (loaders: ["neoforge"])
```

Replace `VERSION` with the version (e.g., `1.2.3+1.21.8`) and `MC_VERSION` with Minecraft version.

## GitHub Releases

Create a GitHub release with the build artifacts:

```bash
gh release create vMC_VERSION \
  --title "vMC_VERSION" \
  --notes "## Port to Minecraft MC_VERSION

### Changes
- List changes here

### Downloads
- **Fabric**: \`auctionhouse-VERSION-fabric.jar\`
- **NeoForge**: \`auctionhouse-VERSION-neoforge.jar\`

Also available on [Modrinth](https://modrinth.com/mod/auctionhouse/versions?g=MC_VERSION)" \
  --target BRANCH_NAME \
  output/auctionhouse-VERSION-fabric.jar \
  output/auctionhouse-VERSION-neoforge.jar
```

Replace `MC_VERSION` with Minecraft version (e.g., `1.21.8`), `VERSION` with full version (e.g., `1.2.3+1.21.8`), and `BRANCH_NAME` with the release branch.

## Publishing to CurseForge

The CurseForge API key and project ID are stored in `.env`. Project ID: `TODO`

Game version IDs (find new ones via `https://minecraft.curseforge.com/api/game/versions`):
- **Loaders**: Fabric=`7499`, NeoForge=`10150`
- **Minecraft versions**: check API for current IDs (e.g., 1.21.8=`13620`)

```bash
# Load API key
source .env

# Publish Fabric version
curl -X POST "https://minecraft.curseforge.com/api/projects/$CURSEFORGE_PROJECT_ID/upload-file" \
  -H "X-Api-Token: $CURSEFORGE_API_KEY" \
  -F 'metadata={
    "changelog": "Changelog here",
    "changelogType": "markdown",
    "displayName": "AuctionHouse VERSION-fabric",
    "gameVersions": [MC_VERSION_ID, 7499],
    "releaseType": "release"
  }' \
  -F "file=@output/auctionhouse-VERSION-fabric.jar"

# Repeat for neoforge (10150)
```

Replace `VERSION` with full version, `MC_VERSION_ID` with the numeric Minecraft version ID from the API.

## Commit/PR Guidelines

- Do not mention Claude Code or AI assistance in commit messages or PR descriptions
- Always use `git push --force-with-lease` instead of `git push --force`
