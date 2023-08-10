# Auction House

A fabric server-side mod to allow users to sell items to all other players.
<br>The mod supports [LuckPerms](https://www.curseforge.com/minecraft/mc-mods/luckperms) for permissions.
<br>
## Installation
Put the .jar file in the "mods" folder

**(Requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [a supported Economy](#supported-economies))**
<br>

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
You can find the config file in `./config/guishop.json`


### JSON example
```json5
{
  "maxItemsPerPlayer": 10, //items per player
  "auctionSecondsDuration": 604800, //duration in second for each auction
  "auctionHouseMaxPages": 50 //max pages for the Auction House
}
```

## Supported Economies:
- [EightsEconomyP](https://legacy.curseforge.com/minecraft/mc-mods/eightseconomyp)

### Discord
Join my [discord server](https://discord.gg/tExFemXyJS) if you need support for one of my mods!

## Showcase
![Screenshot1](https://i.imgur.com/kM3qF1N.png)

![Screenshot2](https://i.imgur.com/0hOWk2B.png)

![Screenshot3](https://i.imgur.com/wAru9qk.png)

![Screenshot4](https://i.imgur.com/PML9LoW.png)

### Credits
Special thanks to [IAmSneak](https://github.com/IAmSneak/) for the original code base for Fabric Auction House
