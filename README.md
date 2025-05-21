# Auction House

A server-side mod (or plugin) that allows users to sell items to all other players. 
The mod supports [LuckPerms](https://modrinth.com/plugin/luckperms) for permissions.


This is a fork of [FabricAuctionHouse](https://github.com/UnsafeDodo/fabric-auctionhouse) that aims to maintain 
a server-side auction mod that is compatible with multiple economy mod and loader.

TodoList: 
 - [ ] NeoForge Support
 - [ ] Multi-currency support


## Installation
Put the .jar file in the "mods" folder
You also need to download and copy the required mods jar.


**Required mods:**
 -  The [SQLite Mod](https://modrinth.com/plugin/minecraft-sqlite-jdbc)
 -  One of the [supported Economy mod](#supported-economies)


## Commands and permissions
All commands can be used by default users (permission level 0), except for the `reload` command, which can be used by OP (permission level 4) or by users/groups with the specific permission


| Description                      | Command            | Permission             | 
|----------------------------------|--------------------|------------------------|
| Open the Auction House           | `/ah`              | `auctionhouse.main`    |
| Sell the holding item            | `/ah sell <price>` | `auctionhouse.sell`    |
| Open personal Auction House      | `/ah selling`      | `auctionhouse.selling` |
| Open expired Auction House       | `/guishop expired` | `auctionhouse.expired` |
| Make all items on auction expire | `/ah cancel`       | `auctionhouse.cancel`  |
| Return all expired items         | `/ah return`       | `auctionhouse.return`  |
| List all commands                | `/ah help`         | `auctionhouse.help`    |
| Reload config file               | `/ah reload`       | `auctionhouse.reload`  |



## Configuration
You can find the config file in `./config/auctionhouse.json`


### JSON example
```json5
{
  "maxItemsPerPlayer": 10, //items per player
  "auctionSecondsDuration": 604800, //duration in second for each auction
  "auctionHouseMaxPages": 50 //max pages for the Auction House
}
```

## Supported Economies:

- [Impactor](https://modrinth.com/mod/impactor) or any economy mod compatible with Impactor API
- [RealEconomy](https://modrinth.com/mod/realeconomy) Compatibility

### Discord
Join my [discord server](https://discord.gg/ZZmqwnQt3J) if you need support for one of my mods!

## Showcase
![Screenshot1](https://i.imgur.com/kM3qF1N.png)

![Screenshot2](https://i.imgur.com/0hOWk2B.png)

![Screenshot3](https://i.imgur.com/wAru9qk.png)

![Screenshot4](https://i.imgur.com/PML9LoW.png)

### Credits

Special thanks to [IAmSneak](https://github.com/IAmSneak/) and [UnsafeDodo](https://github.com/UnsafeDodo/) for the original code base
