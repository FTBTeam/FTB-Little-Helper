# FTB-Little-Helper

Use https://github.com/FTBTeam/FTB-Mods-Issues for any mod issues

## Description

Just a little helper dude who flies around and chats to you. Controlled by KubeJS scripts or simple commands.

Example KubeJS:
```javascript
ItemEvents.rightClicked('minecraft:stick', event => {
        FTBLittleHelper.toggle(event.player)
})

BlockEvents.broken('minecraft:sand', event => {
        FTBLittleHelper.message(event.player, Text.gold("stop breaking my beach"), 50)
})
```
