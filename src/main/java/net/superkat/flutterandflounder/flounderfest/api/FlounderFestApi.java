package net.superkat.flutterandflounder.flounderfest.api;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.entity.FlutterAndFlounderEntities;
import net.superkat.flutterandflounder.entity.custom.CommonBossFish;
import net.superkat.flutterandflounder.entity.custom.CommonFlyingFish;
import net.superkat.flutterandflounder.flounderfest.FlounderFest;
import net.superkat.flutterandflounder.flounderfest.FlounderFestManager;
import net.superkat.flutterandflounder.item.FlutterAndFlounderItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlounderFestApi {

    /**
     * Start a FlounderFest with infinite enemies to be spawned
     *
     * @param player The player who starts the FlounderFest. Used to determine the blockpos.
     */
    public static void startFlounderFest(ServerPlayerEntity player) {
        startFlounderFest(player, determineQuota(player.getServerWorld(), player.getBlockPos()), -1);
    }

    /**
     * Start a FlounderFest with a chosen quota and enemies to be spawned.
     *
     * @param player The player who starts the FlounderFest. Used to determine the starting block pos.
     * @param quota The amount of boss fish needed to be killed before the time is over.
     * @param enemiesToBeSpawned The amount of enemies to be spawned. Use "-1" for infinite enemies
     */
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

    /**
     * Stop a specific FlounderFest.
     *
     * @param flounderFest The FlounderFest to be stopped
     */
    public static void stopFlounderFest(FlounderFest flounderFest) {
        flounderFest.invalidate();
    }

    /**
     * Find a FlounderFest at a specific block pos. The search radius is based on the world's FlounderFest Involved Player Radius gamerule. Default is 96 blocks.
     *
     * @param world The world of the searched FlounderFest. Often the Overworld
     * @param pos The center pos of the search.
     * @return Returns the closest FlounderFest if one is found, or null if none are found.
     */
    @Nullable
    public static FlounderFest getFlounderFestAt(ServerWorld world, BlockPos pos) {
        return getFlounderFestAt(world, pos, world.getGameRules().getInt(FlutterAndFlounderMain.FLOUNDERFEST_INVOLVE_PLAYER_RADIUS));
    }

    /**
     * Find a FlounderFest at a specific block pos with a specific search distance.
     *
     * @param world The world of the searched FlounderFest. Often the Overworld
     * @param pos The center pos of the search.
     * @param searchDistance The search distance.
     * @return Returns the closest FlounderFest if one is found, or null if none are found.
     */
    @Nullable
    public static FlounderFest getFlounderFestAt(ServerWorld world, BlockPos pos, int searchDistance) {
        return getFlounderFestManager(world).getFlounderFestAt(pos, searchDistance);
    }

    /**
     * Determine the quota based on nearby players and a random number.
     *
     * @param world The world the FlounderFest should be in. Used to get a random number.
     * @param festCenterPos The center of the FlounderFest. Used to determine nearby player count.
     * @return A random quota increased by the amount of nearby players.
     */
    public static int determineQuota(ServerWorld world, BlockPos festCenterPos) {
        int quota = world.getRandom().nextBetween(3, 7);
        int distance = world.getGameRules().getInt(FlutterAndFlounderMain.FLOUNDERFEST_INVOLVE_PLAYER_RADIUS);
        for (ServerPlayerEntity player : world.getPlayers(player -> player.squaredDistanceTo(festCenterPos.getX(), festCenterPos.getY(), festCenterPos.getZ()) <= distance * distance)) {
            FlutterAndFlounderMain.LOGGER.info("Accounting for " + player.getName().getString() + " in latest FlounderFest quota determination!");
            quota += (quota / 3); //changes quota based on the amount of players present at the beginning
        }

        return quota;
    }

    /**
     * Spawns in a lesser fish(e.g. Flying Cod or Flying Salmon) for a specific FlounderFest at a random location within the FlounderFest.
     * 20 attempts to spawn in the fish in different locations before giving up and returning false.
     *
     * @param flounderFest The FlounderFest the fish should be spawned for.
     * @param world The world of the spawned fish.
     * @param festCenterPos The center of the FlounderFest.
     * @return Returns true if the fish spawned successfully or false if the fish was unable to spawn.
     */
    public static boolean spawnLesserFish(FlounderFest flounderFest, ServerWorld world, BlockPos festCenterPos) {
        LesserFish spawnedFish = LesserFish.chooseLesserFish();
        CommonFlyingFish fish = spawnedFish.type.create(world);
        if(fish != null) {
            BlockPos pos = fishSpawningPos(world, festCenterPos, 3, 20);
            if(pos != null) {
                fish.setPos(pos.getX(), pos.getY(), pos.getZ());
                fish.initialize(world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
                fish.setOnGround(true);
                ServerPlayerEntity targetPlayer = flounderFest.getRandomPlayerTarget();
                if(targetPlayer != null) {
                    fish.setTarget(targetPlayer);
                }
                world.spawnEntity(fish);
                flounderFest.addEntityToEnemyList(fish.getUuid());
                return true;
            }
        }
        return false;
    }

    /**
     * Spawns a boss fish(e.g. Salmon Ship, Hammer Cod, Whacker Salmon, Clown Cod, etc.) for a specific FlounderFest at a random location.
     * 20 attempts to spawn in the fish in different locations before giving up and returning false.
     *
     * @param flounderFest The FlounderFest the fish should be spawned for.
     * @param world The world of the spawned fish.
     * @param festCenterPos The center of the FlounderFest.
     * @return Returns true if the fish spawned successfully or false if the fish was unable to spawn.
     */
    public static boolean spawnBossFish(FlounderFest flounderFest, ServerWorld world, BlockPos festCenterPos) {
        BossFish spawnedFish = BossFish.chooseBossFish();
        CommonBossFish fish = spawnedFish.type.create(world);
        if(fish != null) {
            BlockPos pos = fishSpawningPos(world, festCenterPos, 3, 20);
            if(pos != null) {
                fish.setPos(pos.getX(), pos.getY(), pos.getZ());
                fish.initialize(world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
                fish.setOnGround(true);
                ServerPlayerEntity targetPlayer = flounderFest.getRandomPlayerTarget();
                if(targetPlayer != null) {
                    fish.setTarget(targetPlayer);
                }
                world.spawnEntity(fish);
                flounderFest.addEntityToEnemyList(fish.getUuid());
                return true;
            }
        }
        return false;
    }

    /**
     * @param world The world of the fish.
     * @param festCenterPos The center of the FlounderFest. Used to determine spawn location.
     * @param proximity Proximity used to determine if the spawn pos is loaded???
     * @param tries The amount of attempts that should be made at spawning the fish before giving up.
     * @return Returns the blockpos a fish can spawn at.
     */
    private static BlockPos fishSpawningPos(ServerWorld world, BlockPos festCenterPos, int proximity, int tries) {
        int i = proximity == 0 ? 2 : 2 - proximity;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int tryCount = 0; tryCount < tries; tryCount++) {
            float f = world.random.nextFloat() * (float) (Math.PI * 2);
            int spawnRadius = world.getGameRules().getInt(FlutterAndFlounderMain.FLOUNDERFEST_MOB_SPAWN_RADIUS);
            //additional blocks to add a small amount of randomness to the spawn location
            int spawnProximity = world.getGameRules().getInt(FlutterAndFlounderMain.FLOUNDERFEST_MOB_SPAWN_PROXIMITY);
            int x = festCenterPos.getX() + MathHelper.floor(MathHelper.cos(f) * spawnRadius) + world.random.nextInt(spawnProximity);
            int z = festCenterPos.getZ() + MathHelper.floor(MathHelper.sin(f) * spawnRadius) + world.random.nextInt(spawnProximity);
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

    /**
     * Spawn in FlounderFest rewards at the FlounderFest's starting pos. The amount is determined by the total quota and if the players won or not.
     * The rewards are predetermined in the API class.
     *
     * @param world The world to spawn the rewards in(should be the world of the FlounderFest)
     * @param festCenterPos The FlounderFest's starting block pos
     * @param totalQuota The total amount of earned quota from the FlounderFest across all waves, used to determine the amount of rewards
     * @param didWin If the players won or lost the FlounderFest, used to determine the amount of rewards
     */
    public static void spawnFlounderFestRewards(ServerWorld world, BlockPos festCenterPos, int totalQuota, boolean didWin) {
        //Quota already scales with the amount of players, naturally meaning more players = more rewards
        double quotaCalc = totalQuota / (didWin ? 3d : 7d);
        int totalRewards = (int) Math.ceil(quotaCalc);
        if(totalRewards <= 0) {
            totalRewards = 1;
        }
        //roughly 25% of the rewards will be prismarine pearls, rounded down to make them harder to get
        int prismarinePearls = (int) Math.floor((double) totalRewards / 4);

        for (int i = 0; i < totalRewards - prismarinePearls; i++) {
            dropStack(world, festCenterPos, getRandomFlounderFestReward(world));
        }
        for (int i = 0; i < prismarinePearls; i++) {
            dropStack(world, festCenterPos, FlutterAndFlounderItems.PRISMARINE_PEARL.getDefaultStack());
        }
    }

    /**
     * Spawns in a glowing item in a world.
     *
     * @param world The world to spawn the item in
     * @param dropPos The drop block pos of the item
     * @param stack The item stack itself
     */
    private static void dropStack(ServerWorld world, BlockPos dropPos, ItemStack stack) {
        if(stack.isEmpty()) {
            return;
        } else {
            ItemEntity itemEntity = new ItemEntity(world, dropPos.getX(), dropPos.getY(), dropPos.getZ(), stack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
            itemEntity.setGlowing(true);
        }
    }

    /**
     * Get a random reward from a pre-determined list
     *
     * @param world The world the items should spawn in, used to get its random for the enchanted books
     * @return Returns a random item from the list of possible rewards
     */
    public static ItemStack getRandomFlounderFestReward(ServerWorld world) {
        //FIXME - Ideally, this would be done using loot tables and data gen, but I didn't have time for that.
        ArrayList<ItemStack> rewards = new ArrayList<>();

        rewards.add(Items.GOLDEN_APPLE.getDefaultStack());
        rewards.add(Items.ENCHANTED_GOLDEN_APPLE.getDefaultStack());
        rewards.add(FlutterAndFlounderItems.FLOUNDERFEST_COFFEE.getDefaultStack());
        rewards.add(Items.GOLDEN_CARROT.getDefaultStack());
        rewards.add(Items.DIAMOND.getDefaultStack());
        rewards.add(Items.NETHERITE_SCRAP.getDefaultStack());
        rewards.add(getRandomEnchantedBookReward(world));

        return rewards.get(new Random().nextInt(rewards.size()));
    }

    /**
     * Get an enchanted book with a random enchantment.
     *
     * @param world The world the items should spawn in, used to get its random
     * @return Returns an item stack which is an enchanted book with a random enchantment using the same math as a villager trade
     */
    private static ItemStack getRandomEnchantedBookReward(ServerWorld world) {

        ArrayList<Enchantment> possibleEnchantments = new ArrayList<>();
        possibleEnchantments.add(Enchantments.EFFICIENCY);
        possibleEnchantments.add(Enchantments.UNBREAKING);
        possibleEnchantments.add(Enchantments.PROTECTION);
        possibleEnchantments.add(Enchantments.SHARPNESS);
        possibleEnchantments.add(Enchantments.SILK_TOUCH);
        possibleEnchantments.add(Enchantments.MENDING);
        possibleEnchantments.add(Enchantments.FORTUNE);

        Enchantment enchantment = possibleEnchantments.get(world.random.nextInt(possibleEnchantments.size()));
        int randomLevel = world.random.nextBetween(enchantment.getMinLevel(), enchantment.getMaxLevel());
        return EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, randomLevel));
    }

    /**
     * Get a world's FlounderFestManager. Used for all things related to FlounderFests.
     *
     * @param world The ServerWorld you want to get the FlounderFestManager for
     * @return The world's FlounderFestManager.
     */
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

    static enum BossFish {
        CODAUTOMOBILE(FlutterAndFlounderEntities.COD_AUTOMOBILE),
        SALMONSHIP(FlutterAndFlounderEntities.SALMON_SHIP),
        HAMMERCOD(FlutterAndFlounderEntities.HAMMER_COD),
        WHACKERSALMON(FlutterAndFlounderEntities.WHACKER_SALMON),
        CHILLCOD(FlutterAndFlounderEntities.CHILL_COD),
        SALMONSNIPER(FlutterAndFlounderEntities.SALMON_SNIPER),
        CLOWNCOD(FlutterAndFlounderEntities.CLOWN_COD),
        COFFEECOD(FlutterAndFlounderEntities.COFFEE_COD);

        final EntityType<? extends CommonBossFish> type;
        private static final List<BossFish> VALUES = List.of(values());
        private static final Random RANDOM = new Random();

        BossFish(EntityType<? extends CommonBossFish> type) {
            this.type = type;
        }

        public static BossFish chooseBossFish() {
            return VALUES.get(RANDOM.nextInt(VALUES.size()));
        }
    }

}
