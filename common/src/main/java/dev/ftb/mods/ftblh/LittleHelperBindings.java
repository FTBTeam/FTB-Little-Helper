package dev.ftb.mods.ftblh;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftblh.commands.HideCommand;
import dev.ftb.mods.ftblh.commands.MessageCommand;
import dev.ftb.mods.ftblh.commands.ShowCommand;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * Common operations, exposed both for KubeJS, and general utility
 */
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
            try {
                HideCommand.hide(player.createCommandSourceStack(), sp);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
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
        message(player, msg, ticks, false);
    }

    public void message(Player player, Component msg, int ticks, boolean priority) {
        if (player instanceof ServerPlayer sp) {
            try {
                MessageCommand.message(player.createCommandSourceStack(), sp, msg, priority ? -ticks : ticks);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isActive(Player player) {
        return getHelperEntity(player).isPresent();
    }

    public boolean isPersistent(Player player) {
        return getHelperEntity(player).map(LittleHelperEntity::shouldStayShown).orElse(false);
    }

    public void playSound(Player player, SoundEvent sound) {
        playSound(player, sound, 1f, 1f);
    }

    public void playSound(Player player, SoundEvent sound, float volume, float pitch) {
        getHelperEntity(player).ifPresentOrElse(
                lh -> lh.playSound(sound, volume, pitch),
                () -> player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, volume, pitch));
    }

    public void playPrivateSound(Player player, SoundEvent sound, float volume, float pitch) {
        getHelperEntity(player).ifPresentOrElse(
                lh -> lh.playSound(sound, volume, pitch),
                () -> player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, volume, pitch));
    }

    public void queueSound(Player player, SoundEvent sound) {
        queueSound(player, sound, 1f, 1f, 20);
    }

    public void queueSound(Player player, SoundEvent sound, float volume, float pitch, int ticks) {
        getHelperEntity(player).ifPresent(lh -> lh.addSound(sound, volume, pitch, ticks, false));
    }

    public Optional<LittleHelperEntity> getHelperEntity(Player player) {
        int id = HelperTracker.INSTANCE.getHelperId(player.getUUID());
        return id != 0 && player.level().getEntity(id) instanceof LittleHelperEntity lh ? Optional.of(lh) : Optional.empty();
    }
}
