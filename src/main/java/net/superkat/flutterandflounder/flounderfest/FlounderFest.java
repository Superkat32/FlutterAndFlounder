package net.superkat.flutterandflounder.flounderfest;

import com.google.common.collect.Sets;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestApi;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class FlounderFest {
    private final Set<UUID> involvedPlayers = Sets.newHashSet();
    private final int id;
    private final ServerWorld world;
    //The status is always in the player's favor. E.g. "Status.VICTORY" means the player won, not the flounder fest enemies
    private FlounderFest.Status status;


    private final BlockPos startingPos;
    private final int quota;
    public int ticksSinceStart;
    public int ticksSinceEnd;
    public int maxTimeInTicks = 2000;
    public int enemiesToBeSpawned;
    public int maxEnemiesAtOnce = 30;
    public int defeatedEnemies = 0;
    public int currentEnemies = 0;
    //10 seconds of grace period
    public int ticksUntilNextEnemySpawn = 200;


    public FlounderFest(int id, ServerWorld world, BlockPos startingPos, int quota, int totalEntityCount) {
        this.id = id;
        this.status = FlounderFest.Status.ONGOING;
        this.world = world;
        this.startingPos = startingPos;
        this.quota = quota;
        this.enemiesToBeSpawned = totalEntityCount;
    }

    public ServerPlayerEntity getRandomPlayerTarget() {
        List<ServerPlayerEntity> players = this.world.getPlayers(this.isInFlounderFestDistance());
        return players.get(world.random.nextInt(players.size()));
    }

    private Predicate<ServerPlayerEntity> isInFlounderFestDistance() {
        return player -> {
            BlockPos pos = player.getBlockPos();
            return player.isAlive() && FlounderFestApi.getFlounderFestManager(world).getFlounderFestAt(startingPos, 75) == this;
        };
    }

    public void updateInvolvedPlayers() {
        List<ServerPlayerEntity> players = this.world.getPlayers(this.isInFlounderFestDistance());

        for (ServerPlayerEntity player : players) {
            involvedPlayers.add(player.getUuid());
            player.sendMessage(Text.literal("FlounderFest Time Remaining - " + ((maxTimeInTicks - ticksSinceStart) / 20)), true);
        }
    }

    public void tick() {
        ticksSinceStart++;
        ticksUntilNextEnemySpawn--;

        updateInvolvedPlayers();

        if(!this.hasStopped()) {
            if(this.status == Status.ONGOING) {
                //spawns in enemies every few seconds
                if(enemiesToBeSpawned < maxEnemiesAtOnce) {
                    if(ticksUntilNextEnemySpawn <= 0) {
                        addEnemy();
                    }
                }

                //time up
                if(ticksSinceStart >= maxTimeInTicks) {
                    //check for victory
                    if(defeatedEnemies >= quota) {
                        this.status = Status.VICTORY;

                    //counts as loss
                    } else {
                        this.status = Status.LOSS;
                    }
                }
            } else if (isFinished()) {
                ticksSinceEnd++;
                if(ticksSinceStart >= 600) {
                    FlutterAndFlounderMain.LOGGER.info("Flounder Fest " + id + " has finished!");
                    this.invalidate();
                    return;
                }


            }
        }
    }

    public void addEnemy() {
        if(FlounderFestApi.spawnLesserFish(this, this.world, startingPos)) {
            enemiesToBeSpawned--;
            currentEnemies++;
            ticksUntilNextEnemySpawn = world.random.nextBetween(20, 200);
        }
    }

    public void updateEnemyCount(boolean didYouDie) {

    }

    public boolean shouldStop() {
        return false;
    }

    public boolean hasStopped() {
        return this.status == Status.STOPPED;
    }

    public boolean isFinished() {
        return this.hasWon() || hasLost();
    }

    public boolean hasWon() {
        return this.status == Status.VICTORY;
    }

    public boolean hasLost() {
        return this.status == Status.LOSS;
    }

    public void invalidate() {
        this.status = Status.STOPPED;
    }

    public BlockPos getStartingPos() {
        return startingPos;
    }
    public int getId() {
        return id;
    }

    static enum Status {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final FlounderFest.Status[] VALUES = values();

        static FlounderFest.Status fromName(String name) {
            for(FlounderFest.Status status : VALUES) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }

            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
