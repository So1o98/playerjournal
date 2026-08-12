package com.player.journal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.player.journal.data.JournalProgressionData;
import com.player.journal.registry.ModAttachments;
import com.player.journal.network.SyncJournalDataPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = "playerjournal")
public class JournalCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();


        PartyCommand.register(dispatcher);
        ScanModsCommand.register(dispatcher);


        dispatcher.register(Commands.literal("playerjournal")
                .requires(source -> source.hasPermission(3))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .suggests((context, builder) -> {

                                            List<String> skills = List.of(
                                                    "vitality", "agility", "combat", "defense",
                                                    "farming", "mining", "smithing", "archery",
                                                    "fishing", "alchemy", "pages"
                                            );
                                            for (String s : skills) {
                                                if (s.startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(s);
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 10000))
                                                .executes(context -> executeSet(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "skill"),
                                                        IntegerArgumentType.getInteger(context, "value")
                                                ))
                                        )
                                )
                        )
                )
        );
    }

    private static int executeSet(CommandSourceStack source, ServerPlayer target, String skill, int value) {
        JournalProgressionData data = target.getData(ModAttachments.JOURNAL_DATA);
        boolean success = true;

        switch (skill.toLowerCase()) {
            case "vitality" -> data.setVitalityLevel(value, target);
            case "agility" -> data.setAgilityLevel(value, target);
            case "combat" -> data.setCombatLevel(value);
            case "defense" -> data.setDefenseLevel(value);
            case "farming" -> data.setFarmingLevel(value);
            case "mining" -> data.setMiningLevel(value);
            case "smithing" -> data.setSmithingLevel(value);
            case "archery" -> data.setArcheryLevel(value);
            case "fishing" -> data.setFishingLevel(value);
            case "alchemy" -> data.setAlchemyLevel(value);
            case "pages" -> data.setTornPages(value);
            default -> {
                source.sendFailure(Component.literal("Unknown skill or attribute: " + skill));
                success = false;
            }
        }

        if (success) {
            PacketDistributor.sendToPlayer(target, new SyncJournalDataPayload(
                    data.getVitalityLevel(), data.getVitalityXP(),
                    data.getCombatLevel(), data.getCombatXP(),
                    data.getDefenseLevel(), data.getDefenseXP(),
                    data.getMiningLevel(), data.getMiningXP(),
                    data.getFarmingLevel(), data.getFarmingXP(),
                    data.getSmithingLevel(), data.getSmithingXP(),
                    data.getArcheryLevel(), data.getArcheryXP(),
                    data.getFishingLevel(), data.getFishingXP(),
                    data.getAgilityLevel(), data.getAgilityXP(),
                    data.getAlchemyLevel(), data.getAlchemyXP(),
                    data.getTornPages()
            ));

            source.sendSuccess(() -> Component.literal("Successfully set " + target.getScoreboardName() + "'s " + skill + " to " + value + "."), true);
        }

        return 1;
    }
}