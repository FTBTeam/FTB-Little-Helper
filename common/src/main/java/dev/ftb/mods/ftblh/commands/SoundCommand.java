package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SoundCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("sound")
                .then(argument("player", EntityArgument.player())
                        .then(argument("ticks", IntegerArgumentType.integer(-150, 150))
                                .then(argument("soundevent", ResourceLocationArgument.id())
                                        .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                                        .executes(ctx -> queueSound(ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player"),
                                                ResourceLocationArgument.getId(ctx, "soundevent"),
                                                IntegerArgumentType.getInteger(ctx, "ticks"))
                                        )
                                )
                        )
                );
    }

    public static int queueSound(CommandSourceStack source, ServerPlayer target, ResourceLocation soundId, int ticks) throws CommandSyntaxException {
        if (source.getPlayer() != target && !source.hasPermission(Commands.LEVEL_GAMEMASTERS)) {
            throw CommandUtil.NO_PERMISSION.create();
        }

        return CommandUtil.getLittleHelper(source, target).map(helper -> {
            SoundEvent event = SoundEvent.createVariableRangeEvent(soundId);
            boolean ok = ticks < 0 ? helper.addPrioritySound(event, -ticks) : helper.addSound(event, ticks);
            return ok ? Command.SINGLE_SUCCESS : 0;
        }).orElse(0);
    }
}
