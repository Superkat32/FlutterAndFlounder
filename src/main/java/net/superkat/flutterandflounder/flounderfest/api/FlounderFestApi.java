package net.superkat.flutterandflounder.flounderfest.api;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.custom.CommonFlyingFish;
import net.superkat.flutterandflounder.flounderfest.FlounderFest;
import net.superkat.flutterandflounder.flounderfest.FlounderFestManager;

import java.util.List;
import java.util.Random;

public class FlounderFestApi {
    public static void startFlounderFest(ServerPlayerEntity player) {
        startFlounderFest(player, 10, -1);
    }
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
            BlockPos pos = fishSpawningPos(world, festCenterPos, 3, 20);
            if(pos != null) {
                fish.setPos(pos.getX(), pos.getY(), pos.getZ());
                fish.initialize(world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
                fish.setOnGround(true);
                if(flounderFest.hasPlayers()) { //game otherwise crashes when in single player and the player dies
                    fish.setTarget(flounderFest.getRandomPlayerTarget());
                }
                world.spawnEntity(fish);
                flounderFest.addEntityToEnemyList(fish);
                return true;
            }
        }
        return false;
    }

    private static BlockPos fishSpawningPos(ServerWorld world, BlockPos festCenterPos, int proximity, int tries) {
        int i = proximity == 0 ? 2 : 2 - proximity;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int tryCount = 0; tryCount < tries; tryCount++) {
            float f = world.random.nextFloat() * (float) (Math.PI * 2);
            int x = festCenterPos.getX() + MathHelper.floor(MathHelper.cos(f) * 32f * (float) i) + world.random.nextInt(5);
            int z = festCenterPos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32f * (float) i) + world.random.nextInt(5);
            int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
            mutable.set(x, y, z);
            if(!world.isNearOccupiedPointOfInterest(mutable) || proximity >= 2) {
                int n = 10;
                if(world.isRegionLoaded(mutable.getX() - n, mutable.getZ() - n, mutable.getX() + n, mutable.getZ() + n)
                    && world.shouldTickEntity(mutable)
                    && (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, mutable, EntityType.RAVAGER)
                            || world.getBlockState(mutable.down()).isOf(Blocks.SNOW) && world.getBlockState(mutable).isAir()
                        )) {
                    return mutable;
                }
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
