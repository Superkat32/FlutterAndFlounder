package net.superkat.flutterandflounder.flounderfest.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import net.superkat.flutterandflounder.flounderfest.FlounderFest;
import net.superkat.flutterandflounder.flounderfest.FlounderFestManager;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestApi;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestServerWorld;

import static net.superkat.flutterandflounder.network.FlutterAndFlounderPackets.*;

public class FlounderFestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("flounderfest")
                        .requires(source -> source.hasPermissionLevel(3))
                        .then(CommandManager.literal("start")
                                .executes(context -> executeStart(context.getSource()))
                                .then(CommandManager.argument("quota", IntegerArgumentType.integer())
                                        .executes(context -> executeStart(context.getSource(), IntegerArgumentType.getInteger(context, "quota"), -1))
                                        .then(CommandManager.argument("enemycount", IntegerArgumentType.integer())
                                                .executes(context -> executeStart(context.getSource(), IntegerArgumentType.getInteger(context, "quota"), IntegerArgumentType.getInteger(context, "enemycount"))))))
                        .then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
                        .then(CommandManager.literal("hud")
                                .then(CommandManager.literal("create")
                                        .executes(context -> executeFakeHud(context.getSource()))
                                        .then(CommandManager.literal("fakeVictory")
                                                .executes(context -> executeFakeVictory(context.getSource())))
                                        .then(CommandManager.literal("fakeDefeat")
                                                .executes(context -> executeFakeDefeat(context.getSource())))
                                        .then(CommandManager.literal("fakeWaveClear")
                                                .executes(context -> executeFakeWaveClear(context.getSource())))
                                        .then(CommandManager.literal("fakeBossAlert")
                                                .executes(context -> executeFakeBossAlert(context.getSource())))
                                )
                                .then(CommandManager.literal("delete").executes(context -> executeDeleteFakeHud(context.getSource())))
                        )
                        .then(CommandManager.literal("reward")
                                .executes(context -> executeFakeReward(context.getSource(), 30, true))
                                .then(CommandManager.argument("totalQuotaEarned", IntegerArgumentType.integer())
                                        .executes(context -> executeFakeReward(context.getSource(), IntegerArgumentType.getInteger(context, "totalQuotaEarned"), true))
                                        .then(CommandManager.argument("didWin", BoolArgumentType.bool())
                                                .executes(context -> executeFakeReward(context.getSource(), IntegerArgumentType.getInteger(context, "totalQuotaEarned"), BoolArgumentType.getBool(context, "didWin"))))))
        );
    }
    private static int executeStart(ServerCommandSource source) throws CommandSyntaxException {
        return executeStart(source, FlounderFestApi.determineQuota(source.getWorld(), source.getPlayerOrThrow().getBlockPos()), -1);
    }
    private static int executeStart(ServerCommandSource source, int quota, int enemycount) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = player.getServerWorld();
        if(world != null) {
            if(world.getRegistryKey() == World.OVERWORLD && world.getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)) {
                BlockPos playerPos = player.getBlockPos();
                if(FlounderFestApi.getFlounderFestAt(world, playerPos, 100) != null) {
                    source.sendError(Text.literal("Failed to create FlounderFest. There is already one nearby!"));
                    return -1;
                } else {
                    FlounderFestApi.startFlounderFest(player, quota, enemycount);
                    source.sendFeedback(() -> Text.literal("Flounderfest created! Enjoy the chaos!"), false);
                    return 1;
                }
            } else {
                source.sendError(Text.literal("Failed to create FlounderFest. Are you in the overworld?"));
            }
        }

        return -1;
    }

    private static int executeStop(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        BlockPos blockPos = player.getBlockPos();
        FlounderFestServerWorld world = (FlounderFestServerWorld) player.getServerWorld();
        FlounderFestManager flounderFestManager = world.flutterAndFlounder$getFlounderFestManager();
        FlounderFest closetestFlounderFest = flounderFestManager.getFlounderFestAt(blockPos);
        if(closetestFlounderFest != null) {
            closetestFlounderFest.invalidate();
            source.sendFeedback(() -> Text.literal("Stopped Flounderfest!"), false);
            return 1;
        } else {
            source.sendError(Text.literal("No Flounderfest here..."));
            return -1;
        }
    }

    private static int executeFakeHud(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PacketByteBuf buf1 = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_REMOVE_HUD_ID, buf1);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(1);
        buf.writeInt(3);
        buf.writeInt(57);
        buf.writeInt(3);
        buf.writeInt(7);
        buf.writeBlockPos(player.getBlockPos());
        ServerPlayNetworking.send(player, FLOUNDERFEST_CREATE_HUD_ID, buf);
        source.sendFeedback(() -> Text.literal("Created fake hud!"), false);
        return 1;
    }

    private static int executeDeleteFakeHud(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_REMOVE_HUD_ID, buf);
        source.sendFeedback(() -> Text.literal("Removed fake hud!"), false);
        return 1;
    }

    private static int executeFakeVictory(ServerCommandSource source) throws CommandSyntaxException {
        executeFakeHud(source);
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_VICTORY_ID, buf);
        source.sendFeedback(() -> Text.literal("Fake victory shown!"), false);
        return 1;
    }

    private static int executeFakeDefeat(ServerCommandSource source) throws CommandSyntaxException {
        executeFakeHud(source);
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_DEFEAT_ID, buf);
        source.sendFeedback(() -> Text.literal("Fake defeat shown!"), false);
        return 1;
    }

    private static int executeFakeWaveClear(ServerCommandSource source) throws CommandSyntaxException {
        executeFakeHud(source);
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_WAVE_CLEAR_ID, buf);
        source.sendFeedback(() -> Text.literal("Fake wave clear shown!"), false);
        return 1;
    }

    private static int executeFakeBossAlert(ServerCommandSource source) throws CommandSyntaxException {
        executeFakeHud(source);
        ServerPlayerEntity player = source.getPlayerOrThrow();
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_BOSS_ALERT_ID, buf);
        source.sendFeedback(() -> Text.literal("Fake boss alert shown!"), false);
        return 1;
    }

    private static int executeFakeReward(ServerCommandSource source, int totalQuota, boolean didWin) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        FlounderFestApi.spawnFlounderFestRewards(player.getServerWorld(), player.getBlockPos(), totalQuota, didWin);
        source.sendFeedback(() -> Text.literal("Fake reward given!"), false);
        return 1;
    }
}
