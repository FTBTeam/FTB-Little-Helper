# FTB-Little-Helper

Use https://github.com/FTBTeam/FTB-Mods-Issues for any mod issues

## Description

Just a little helper dude who flies around and chats to you. Controlled by KubeJS scripts or simple commands.

Example KubeJS:
```javascript
BlockEvents.broken('minecraft:sand', event => {
        FTBLittleHelper.message(event.player, Text.gold("stop breaking my beach"), 50)
        FTBLittleHelper.playSound(event.player, "entity.vex.hurt")
})

ItemEvents.rightClicked('minecraft:stick', event => {
        if (FTBLittleHelper.isActive(event.player)) {
                FTBLittleHelper.playSound(event.player, "entity.evoker.death", 1, 2)
        } else {
                FTBLittleHelper.playSound(event.player, "block.enchantment_table.use", 1, 2)
        }
        FTBLittleHelper.toggle(event.player)
})
```

## KubeJS Reference

| Call                                                          | Return type | Description                                                                                                                                                                                                                                                                        |
|---------------------------------------------------------------|:-----------:|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `FTBLittleHelper.show(player)`                                |    void     | Pops the helper up in front of the player; it will say "Hi!", then fly behind the player after a second or so                                                                                                                                                                      |
| `FTBLittleHelper.hide(player)`                                |    void     | Pops the helper down; it will move to the front of player, say "Bye!", and vanish shortly after                                                                                                                                                                                    |
| `FTBLittleHelper.toggle(player)`                              |    void     | If the helper is up, hide it; if the helper is down, show it                                                                                                                                                                                                                       |
| `FTBLittleHelper.message(player, msg, ticks)`                 |    void     | Queues up a message for the helper to show the player for a given number of ticks. Helper will fly to the front of the player and display the front message in the queue. If the helper wasn't up, it will appear, but disappear again when there are no more messages to display. |
| `FTBLittleHelper.priorityMessage(player, msg, ticks)`         |    void     | Like `addMessage()`, but the message will go straight to the front of the queue and override any existing message display. Use sparingly, for urgent messages only.                                                                                                                |
| `FTBLittleHelper.isActive(player)`                            |   boolean   | Returns true if the helper is currently up. Note that this will return true after `hide()` is called if the helper is still displaying messages                                                                                                                                    |
| `FTBLittleHelper.isPersistent(player)`                        |   boolean   | Returns true if the helper stays around when there are no message to display. Will be true if helper was raised with the `show()` command, false if raised with the `message` command.                                                                                             |
| `FTBLittleHelper.playSound(player, soundName)`                |    void     | Plays a sound at the helper's location (or the player's location if the helper isn't up). Sounds are standard Minecraft sound event identifiers, e.g. "block.iron_door.open"                                                                                                       | 
| `FTBLittleHelper.playSound(player, soundName, volume, pitch)` |    void     | Like `playSound()` but allows specifying a custom volume and pitch.                                                                                                                                                                                                                | 

## Commands

To run these commands on another player requires op (level 2) permissions. No special perms are required if the target player is the same as the executing player.

| Command                                 | Description                                                                                                                                                                                                            | Example                         |
|-----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| `/ftblh show <player>`                  | Show the helper for the given player                                                                                                                                                                                   | `/ftblh show @s`                |
| `/ftblh hide <player>`                  | Hide the helper for the given player                                                                                                                                                                                   | `/ftblh hide @s`                |
| `/ftblh message <player> <ticks> <msg>` | Show a message to the player for the given duration. If `ticks` is negative, act as a priority message. The message can be a simple string, or [JSON raw text](https://minecraft.fandom.com/wiki/Raw_JSON_text_format) | `/ftblh message @s 50 "hello!"` |
