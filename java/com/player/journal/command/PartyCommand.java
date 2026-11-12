package com.player.journal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.player.journal.party.PartyManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class PartyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("party")
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer inviter = context.getSource().getPlayerOrException();
                                    ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                    PartyManager.invitePlayer(inviter, target);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("accept")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            PartyManager.acceptInvite(player);
                            return 1;
                        })
                )
                .then(Commands.literal("leave")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            PartyManager.leaveParty(player);
                            return 1;
                        })
                )
        );
    }
}