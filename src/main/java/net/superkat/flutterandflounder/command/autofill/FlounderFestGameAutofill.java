package net.superkat.flutterandflounder.command.autofill;

import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.superkat.flounderlib.api.minigame.v1.registry.command.FlounderGameCommandApi;
import net.superkat.flutterandflounder.game.FlounderFestGame;
import net.superkat.flutterandflounder.game.settings.FlounderFestSettings;

public class FlounderFestGameAutofill {

    public static void init() {
        FlounderGameCommandApi.registerGameAutofill(
                FlounderFestGame.ID,
                Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(
                                context -> FlounderGameCommandApi.executeMinigameStart(
                                        context, new FlounderFestGame(
                                                BlockPosArgument.getBlockPos(context, "pos"),
                                                FlounderFestSettings.createDefault()
                                        )
                                )
                        ).then(
                                Commands.argument("seed", LongArgumentType.longArg())
                                        .executes(
                                                context -> FlounderGameCommandApi.executeMinigameStart(
                                                        context, new FlounderFestGame(
                                                                BlockPosArgument.getBlockPos(context, "pos"),
                                                                FlounderFestSettings.create()
                                                                        .seed(LongArgumentType.getLong(context, "seed"))
                                                                        .build()
                                                        )
                                                )
                                        )
                        )
        );
    }

}
