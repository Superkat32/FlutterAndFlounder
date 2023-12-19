package net.superkat.flutterandflounder.flounderfest.api;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.custom.CommonFlyingFish;
import net.superkat.flutterandflounder.flounderfest.FlounderFest;
import net.superkat.flutterandflounder.flounderfest.FlounderFestManager;

import java.util.List;
import java.util.Random;

public class FlounderFestApi {
    public static void startFlounderFest(ServerPlayerEntity player, int quota, int enemiesToBeSpawned) {
        FlutterAndFlounderMain.LOGGER.info("Starting a FlounderFest!");
        FlutterAndFlounderMain.LOGGER.info("Player responsible: " + player);
        FlutterAndFlounderMain.LOGGER.info("Quota: " + quota);
        FlutterAndFlounderMain.LOGGER.info("Enemies to be spawned: " + enemiesToBeSpawned);
        FlounderFestManager flounderFestManager = getFlounderFestManager(player.getServerWorld());
        FlounderFest newFlounderFest = newFlounderFest(player, quota, enemiesToBeSpawned);
        flounderFestManager.createFlounderFest(newFlounderFest, player);
        FlutterAndFlounderMain.LOGGER.info("Flounder Fest " + newFlounderFest.getId() + " has been created!");
    }

    private static FlounderFest newFlounderFest(ServerPlayerEntity player, int quota, int enemiesToBeSpawned) {
        FlounderFestManager flounderFestManager = getFlounderFestManager(player.getServerWorld());
        return flounderFestManager.createNewFlounderFest(player.getServerWorld(), player.getBlockPos(), quota, enemiesToBeSpawned);
    }


    public void stopFlounderFest(FlounderFest flounderFest) {
        flounderFest.invalidate();
    }

    public FlounderFest getFlounderFestAt(BlockPos pos) {
        return null;
    }

    public static boolean spawnLesserFish(FlounderFest flounderFest, ServerWorld world, BlockPos festCenterPos) {
        LesserFish spawnedFish = LesserFish.chooseLesserFish();
        CommonFlyingFish fish = spawnedFish.type.create(world);
        if(fish != null) {
            BlockPos pos = fishSpawningPos(world, festCenterPos, SpawnRestriction.Location.ON_GROUND);
            if(pos != null) {
                fish.setPos(pos.getX(), pos.getY(), pos.getZ());
                fish.initialize(world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
                fish.setOnGround(true);
                fish.setTarget(flounderFest.getRandomPlayerTarget());
                world.spawnEntity(fish);
                return true;
            }
        }
        return false;
    }

    private static BlockPos fishSpawningPos(ServerWorld world, BlockPos festCenterPos, SpawnRestriction.Location location) {
        BlockPos pos = new BlockPos(festCenterPos.getX() + world.getRandom().nextInt(15),
                festCenterPos.getY(),
                festCenterPos.getZ() + world.getRandom().nextInt(15));
        for (int tries = 0; tries < 20; tries++) {
            if(SpawnHelper.canSpawn(location, world, pos, FlutterAndFlounderEntities.FLYING_COD)) {
                return pos;
            }
        }
        return null;
    }

    public static FlounderFestManager getFlounderFestManager(ServerWorld world) {
        FlounderFestServerWorld flounderWorld = (FlounderFestServerWorld) world;
        return flounderWorld.flutterAndFlounder$getFlounderFestManager();
    }

    static enum LesserFish {
        FLYINGCOD(FlutterAndFlounderEntities.FLYING_COD),
        FLYINGSALMON(FlutterAndFlounderEntities.FLYING_SALMON);
        final EntityType<? extends CommonFlyingFish> type;
        private static final List<LesserFish> VALUES = List.of(values());
        private static final Random RANDOM = new Random();

        LesserFish(EntityType<? extends CommonFlyingFish> type) {
            this.type = type;
        }

        public static LesserFish chooseLesserFish() {
            LesserFish returnFish = VALUES.get(RANDOM.nextInt(VALUES.size()));
            if(returnFish == FLYINGSALMON && RANDOM.nextBoolean()) {
                returnFish = FLYINGCOD;
            }
            return returnFish;
        }
    }

}
