package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.ftb.mods.ftblh.FTBLittleHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandUtil {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(FTBLittleHelper.MOD_ID)
                .then(ShowCommand.register())
                .then(HideCommand.register())
                .then(MessageCommand.register())
        );
    }
}
