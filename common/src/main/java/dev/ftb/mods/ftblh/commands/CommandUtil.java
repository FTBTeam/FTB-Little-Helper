package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ftb.mods.ftblh.FTBLittleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class CommandUtil {
    public static final SimpleCommandExceptionType NO_PERMISSION
            = new SimpleCommandExceptionType(Component.translatable("ftblh.message.no_permission").withStyle(ChatFormatting.RED));

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(FTBLittleHelper.MOD_ID)
                .then(ShowCommand.register())
                .then(HideCommand.register())
                .then(MessageCommand.register())
        );
    }
}
