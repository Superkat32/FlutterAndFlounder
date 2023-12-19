package net.superkat.flutterandflounder.flounderfest.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

public class FlounderFestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("flounderfest")
                        .requires(source -> source.hasPermissionLevel(3))
                        .then(CommandManager.literal("start")
                                .then(CommandManager.argument("quota", IntegerArgumentType.integer())
                                        .executes(context -> executeStart(context.getSource(), IntegerArgumentType.getInteger(context, "quota"), 10))
                                        .then(CommandManager.argument("enemycount", IntegerArgumentType.integer())
                                                .executes(context -> executeStart(context.getSource(), IntegerArgumentType.getInteger(context, "quota"), IntegerArgumentType.getInteger(context, "enemycount"))))))
                        .then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
//                        .then(CommandManager.literal("start")
//                                .then(CommandManager.argument("quota", IntegerArgumentType.integer(1))
//                                    .executes(context -> executeStart(context.getSource(), IntegerArgumentType.getInteger(context, "quota"), IntegerArgumentType.getInteger(context, "quota")))
//                                    .then(CommandManager.argument("enemycount", IntegerArgumentType.integer(1)))
//                                        .executes(context -> executeStart(context.getSource(), IntegerArgumentType.getInteger(context, "quota"), IntegerArgumentType.getInteger(context, "enemycount"))))
//                        )
//                        .then(CommandManager.literal("stop").executes(context -> executeStop(context.getSource())))
//                        .then(CommandManager.literal("check").executes(context -> executeCheck(context.getSource())))
//                        .then(
//                                CommandManager.literal("sound")
//                                        .then(
//                                                CommandManager.argument("type", TextArgumentType.text())
//                                                        .executes(context -> executeSound(context.getSource(), TextArgumentType.getTextArgument(context, "type")))
//                                        )
//                        )
//                        .then(CommandManager.literal("spawnleader").executes(context -> executeSpawnLeader(context.getSource())))
//                        .then(
//                                CommandManager.literal("setomen")
//                                        .then(
//                                                CommandManager.argument("level", IntegerArgumentType.integer(0))
//                                                        .executes(context -> executeSetOmen(context.getSource(), IntegerArgumentType.getInteger(context, "level")))
//                                        )
//                        )
//                        .then(CommandManager.literal("glow").executes(context -> executeGlow(context.getSource())))
        );
    }
    private static int executeStart(ServerCommandSource source, int quota, int enemycount) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        ServerWorld world = player.getServerWorld();
        if(world != null) {
            if(world.getRegistryKey() == World.OVERWORLD && world.getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)) {
                FlounderFestApi.startFlounderFest(player, quota, enemycount);
                source.sendFeedback(() -> Text.literal("Flounderfest created! Enjoy the chaos!"), false);
                return 1;
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


//
//    private static int executeGlow(ServerCommandSource source) throws CommandSyntaxException {
//        FlounderFest flounderFest = getFlounderFest(source.getPlayerOrThrow());
//        if (flounderFest != null) {
//            for(RaiderEntity raiderEntity : flounderFest.getAllRaiders()) {
//                raiderEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 1000, 1));
//            }
//        }
//
//        return 1;
//    }
//
//    private static int executeSetOmen(ServerCommandSource source, int level) throws CommandSyntaxException {
//        Raid raid = getFlounderFest(source.getPlayerOrThrow());
//        if (raid != null) {
//            int i = raid.getMaxAcceptableBadOmenLevel();
//            if (level > i) {
//                source.sendError(Text.literal("Sorry, the max bad omen level you can set is " + i));
//            } else {
//                int j = raid.getBadOmenLevel();
//                raid.setBadOmenLevel(level);
//                source.sendFeedback(() -> Text.literal("Changed village's bad omen level from " + j + " to " + level), false);
//            }
//        } else {
//            source.sendError(Text.literal("No raid found here"));
//        }
//
//        return 1;
//    }
//
//    private static int executeSpawnLeader(ServerCommandSource source) {
//        source.sendFeedback(() -> Text.literal("Spawned a raid captain"), false);
//        RaiderEntity raiderEntity = EntityType.PILLAGER.create(source.getWorld());
//        if (raiderEntity == null) {
//            source.sendError(Text.literal("Pillager failed to spawn"));
//            return 0;
//        } else {
//            raiderEntity.setPatrolLeader(true);
//            raiderEntity.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
//            raiderEntity.setPosition(source.getPosition().x, source.getPosition().y, source.getPosition().z);
//            raiderEntity.initialize(source.getWorld(), source.getWorld().getLocalDifficulty(BlockPos.ofFloored(source.getPosition())), SpawnReason.COMMAND, null, null);
//            source.getWorld().spawnEntityAndPassengers(raiderEntity);
//            return 1;
//        }
//    }
//
//    private static int executeSound(ServerCommandSource source, @Nullable Text type) {
//        if (type != null && type.getString().equals("local")) {
//            ServerWorld serverWorld = source.getWorld();
//            Vec3d vec3d = source.getPosition().add(5.0, 0.0, 0.0);
//            serverWorld.playSound(null, vec3d.x, vec3d.y, vec3d.z, SoundEvents.EVENT_RAID_HORN, SoundCategory.NEUTRAL, 2.0F, 1.0F, serverWorld.random.nextLong());
//        }
//
//        return 1;
//    }
//
//
//
//    private static int executeCheck(ServerCommandSource source) throws CommandSyntaxException {
//        FlounderFest flounderFest = getFlounderFest(source.getPlayerOrThrow());
//        if (flounderFest != null) {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("Found a started flounderFest! ");
//            source.sendFeedback(() -> Text.literal(stringBuilder.toString()), false);
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append("Num groups spawned: ");
//            stringBuilder2.append(flounderFest.getGroupsSpawned());
//            stringBuilder2.append(" Bad omen level: ");
//            stringBuilder2.append(flounderFest.getBadOmenLevel());
//            stringBuilder2.append(" Num mobs: ");
//            stringBuilder2.append(flounderFest.getRaiderCount());
//            stringBuilder2.append(" Raid health: ");
//            stringBuilder2.append(flounderFest.getCurrentRaiderHealth());
//            stringBuilder2.append(" / ");
//            stringBuilder2.append(flounderFest.getTotalHealth());
//            source.sendFeedback(() -> Text.literal(stringBuilder2.toString()), false);
//            return 1;
//        } else {
//            source.sendError(Text.literal("Found no started raids"));
//            return 0;
//        }
//    }
//
//    @Nullable
//    private static FlounderFest getFlounderFest(ServerPlayerEntity player) {
//        var world = (FlounderFestWorld) player.getServerWorld();
//        System.out.println("yay");
//        return world.flutterAndFlounder$getFlounderFestAt(player.getBlockPos());
//    }

}