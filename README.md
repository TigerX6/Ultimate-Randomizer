# Ultimate Randomizer - Minecraft randomizer plugin

### Challenge yourself or your friends to beat the Ender Dragon with Ultimate Randomizer! Ultimate Randomizer is a simple yet highly configurable Minecraft plugin that randomizes block and Mob drops.



## Features
- every block drops a new random item. Some blocks could drop the same item, while others could drop nothing. The dropped amount can be configured in the config
- mobs drop random items as well! The drop chance stays the same and mobs that normally drop multiple items will still do that
- mobs drop a random amount of experience, also configurable in the config
- a timer starts when you use `/randomizer start`. This can be disabled in the config (enabled by default)
- customize and translate the plugin messages
- a shuffle command to re-randomize all block and mob drops
- only players that are added to the player list will be affected by the randomizer
  
# Commands

| Command                 | Permission             | Description             
|-------------------------|------------------------|-------------------------
| /randomizer             | no permission          | displays the randomizer's current status |
| /randomizer **start**   | `randomizer.start`     | enables the randomizer |
| /randomizer **stop**    | `randomizer.stop`      | disables the randomizer |
| /randomizer **shuffle** | `randomizer.shuffle`   | shuffles the randomizer |
| /randomizer **players** | `randomizer.players`   | displays the current player list |
| /randomizer **players add** \<player>  |`randomizer.players.add` | adds the given player to the player list |
| /randomizer **players remove** \<player>  |`randomizer.players.remove` | removes the given player from the player list |

## Links
[![GitHub Issues]](https://github.com/TigerX6/Ultimate-Randomizer/issues)

[Download on Modrinth](https://modrinth.com/plugin/ultimate-randomizer)

[//]: # (Data)

[GitHub Issues]: https://img.shields.io/github/issues/TigerX6/Ultimate-Randomizer

## Upcoming Features
- timer formatting options
- shuffle mob and block drops individually
- world list
- randomized chest loot
- settings GUI to edit config options in-game
