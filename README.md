# FTB-Little-Helper

Use https://github.com/FTBTeam/FTB-Mods-Issues for any mod issues

## Description

Just a little helper dude who flies around and chats to you. Controlled by KubeJS scripts or simple commands.

Example KubeJS:
```javascript
BlockEvents.broken('minecraft:sand', event => {
        // plays the sound, then 20 ticks later sends the message
        FTBLittleHelper.queueSound(event.player, "entity.vex.hurt", 1.0, 1.0, 20)
        // sends the message, then hangs around in front of the player for 50 ticks
        FTBLittleHelper.message(event.player, Text.gold("stop breaking my beach"), 50)
})

ItemEvents.rightClicked('minecraft:stick', event => {
        if (FTBLittleHelper.isActive(event.player)) {
                FTBLittleHelper.playSound(event.player, "entity.evoker.death", 1, 2)
        } else {
                FTBLittleHelper.playSound(event.player, "block.enchantment_table.use", 1, 2)
        }
        // hides if shown, or vice versa
        FTBLittleHelper.toggle(event.player)
})
```

## KubeJS Reference

| Call                                                                   | Return type | Description                                                                                                                                                                                                                                                                        |
|------------------------------------------------------------------------|:-----------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `FTBLittleHelper.show(player)`                                         |    void     | Pops the helper up in front of the player; it will say "Hi!", then fly behind the player after a second or so                                                                                                                                                                      |
| `FTBLittleHelper.hide(player)`                                         |    void     | Pops the helper down; it will move to the front of player, say "Bye!", and vanish shortly after                                                                                                                                                                                    |
| `FTBLittleHelper.toggle(player)`                                       |    void     | If the helper is up, hide it; if the helper is down, show it                                                                                                                                                                                                                       |
| `FTBLittleHelper.message(player, msg, ticks)`                          |    void     | Queues up a message for the helper to show the player for a given number of ticks. Helper will fly to the front of the player and display the front message in the queue. If the helper wasn't up, it will appear, but disappear again when there are no more messages to display. |
| `FTBLittleHelper.priorityMessage(player, msg, ticks)`                  |    void     | Like `addMessage()`, but the message will go straight to the front of the queue and override any existing message display. Use sparingly, for urgent messages only.                                                                                                                |
| `FTBLittleHelper.isActive(player)`                                     |   boolean   | Returns true if the helper is currently up. Note that this will return true after `hide()` is called if the helper is still displaying messages                                                                                                                                    |
| `FTBLittleHelper.isPersistent(player)`                                 |   boolean   | Returns true if the helper stays around when there are no message to display. Will be true if helper was raised with the `show()` command, false if raised with the `message` command.                                                                                             |
| `FTBLittleHelper.playSound(player, soundName)`                         |    void     | Plays a sound at the helper's location (or the player's location if the helper isn't up). Sounds are standard Minecraft sound event identifiers, e.g. "block.iron_door.open"                                                                                                       | 
| `FTBLittleHelper.playSound(player, soundName, volume, pitch)`          |    void     | Like `playSound()` but allows specifying a custom volume and pitch.                                                                                                                                                                                                                | 
| `FTBLittleHelper.queueSound(player, soundName)`                        |    void     | Like `playSound()` but enqueues the sound in the message queue instead of playing it straight away.                                                                                                                                                                                | 
| `FTBLittleHelper.queueSound(player, soundName, volume, pitch, ticks)`  |    void     | Like `queueSound()` but allows specifying a custom volume and pitch and tick duration in the queue.                                                                                                                                                                                | 
| `FTBLittleHelper.queueCommand(player, command, silent, ticks)`         |    void     | Queues up a command to be run. The command will be run as the owner of the helper, with permission level 2. If `silent` is true, the command is run with no chat output.                                                                                                           | 
| `FTBLittleHelper.queuePriorityCommand(player, command, silent, ticks)` |    void     | Like `queueCommand()` but adds the command to the front of the queue.                                                                                                                                                                                                              | 

## Commands

To run these commands on another player requires op (level 2) permissions. No special perms are required if the target player is the same as the executing player, except `/ftblh command ...`, which always requires level 2 permissions (since it runs the queued command at permission level 2).

| Command                                                | Description                                                                                                                                                                                                                                                                                                   | Example                                          |
|--------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------|
| `/ftblh show <player>`                                 | Show the helper for the given player                                                                                                                                                                                                                                                                          | `/ftblh show @s`                                 |
| `/ftblh hide <player>`                                 | Hide the helper for the given player                                                                                                                                                                                                                                                                          | `/ftblh hide @s`                                 |
| `/ftblh message <player> <ticks> <msg>`                | Enqueues a message to be sent to the player for the given duration. If `ticks` is negative, act as a priority message. The message can be a simple quoted string, or [JSON raw text](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)                                                                  | `/ftblh message @s 50 "hello!"`                  |
| `/ftblh sound <player> <ticks> <sound>`                | Enqueues a sound to be played to the player, with a delay of `ticks` until the next queued operation is done. If `ticks` is negative, acts as a priority message.                                                                                                                                             | `/ftblh sound @s 20 "minecraft:ui.button.click"` |
| `/ftblh command <player> <ticks> <command> [<silent>]` | Enqueues a command to be run by the player with permission level 2, with a delay of `ticks` until the next queued operation is done. If `ticks` is negative, acts as a priority op. The `command` should be a quoted string. `<silent>` is an optional boolean; if true, the command produces no chat output. | `/ftblh command @s 20 "/give @s diamond" true`   |

## Support

- For **Modpack** issues, please go here: https://go.ftb.team/support-modpack
- For **Mod** issues, please go here: https://go.ftb.team/support-mod-issues
- Just got a question? Check out our Discord: https://go.ftb.team/discord

## Licence

All Rights Reserved to Feed The Beast Ltd. Source code is `visible source`, please see our [LICENSE.md](/LICENSE.md) for more information. Any Pull Requests made to this mod must have the CLA (Contributor Licence Agreement) signed and agreed to before the request will be considered.

## Keep up to date

[![](https://cdn.feed-the-beast.com/assets/socials/icons/social-discord.webp)](https://go.ftb.team/discord) [![](https://cdn.feed-the-beast.com/assets/socials/icons/social-github.webp)](https://go.ftb.team/github) [![](https://cdn.feed-the-beast.com/assets/socials/icons/social-twitter-x.webp)](https://go.ftb.team/twitter) [![](https://cdn.feed-the-beast.com/assets/socials/icons/social-youtube.webp)](https://go.ftb.team/youtube) [![](https://cdn.feed-the-beast.com/assets/socials/icons/social-twitch.webp)](https://go.ftb.team/twitch) [![](https://cdn.feed-the-beast.com/assets/socials/icons/social-instagram.webp)](https://go.ftb.team/instagram) [![](https://cdn.feed-the-beast.com/assets/socials/icons/social-facebook.webp)](https://go.ftb.team/facebook) [![](https://cdn.feed-the-beast.com/assets/socials/icons/social-tiktok.webp)](https://go.ftb.team/tiktok)