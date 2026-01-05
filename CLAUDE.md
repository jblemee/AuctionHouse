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
```

Output JARs are placed in `/output/` directory.

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
