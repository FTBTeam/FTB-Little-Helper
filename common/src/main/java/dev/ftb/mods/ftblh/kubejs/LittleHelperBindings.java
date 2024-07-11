package dev.ftb.mods.ftblh.kubejs;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.commands.HideCommand;
import dev.ftb.mods.ftblh.commands.MessageCommand;
import dev.ftb.mods.ftblh.commands.ShowCommand;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public enum LittleHelperBindings {
    INSTANCE;

    public void show(Player player) {
        if (player instanceof ServerPlayer sp) {
            try {
                ShowCommand.show(player.createCommandSourceStack(), sp);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void hide(Player player) {
        if (player instanceof ServerPlayer sp) {
            HideCommand.hide(player.createCommandSourceStack(), sp);
        }
    }

    public void toggle(Player player) {
        if (isActive(player)) {
            hide(player);
        } else {
            show(player);
        }
    }

    public void message(Player player, Component msg, int ticks) {
        if (player instanceof ServerPlayer sp) {
            try {
                MessageCommand.message(player.createCommandSourceStack(), sp, msg, ticks);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isActive(Player player) {
        int id = HelperTracker.INSTANCE.getHelperId(player.getUUID());
        return id != 0 && player.level().getEntity(id) instanceof LittleHelperEntity;
    }

    public boolean isPersistent(Player player) {
        int id = HelperTracker.INSTANCE.getHelperId(player.getUUID());
        return id != 0 && player.level().getEntity(id) instanceof LittleHelperEntity lh && lh.shouldStayShown();
    }
}
