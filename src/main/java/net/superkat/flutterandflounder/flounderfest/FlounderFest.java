package net.superkat.flutterandflounder.flounderfest;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.superkat.flutterandflounder.FlutterAndFlounderMain;
import net.superkat.flutterandflounder.flounderfest.api.FlounderFestApi;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static net.superkat.flutterandflounder.network.FlutterAndFlounderPackets.*;

public class FlounderFest {
    private final Set<UUID> involvedPlayers = Sets.newHashSet();
    private final int id;
    private final ServerWorld world;
    //The status is always in the player's favor. E.g. "Status.VICTORY" means the player won, not the flounder fest enemies
    private FlounderFest.Status status;

    private final BlockPos startingPos;
    public int wave = 0;
    public int maxWaves = 3;
    public int ticksSinceStart;
    public int ticksSinceEnd;
    public int maxTimeInTicks = 2000;
    public int secondsRemaining = 100;
    public int maxQuota;
    public int quotaProgress = 0;
    private final Set<LivingEntity> enemies = Sets.newHashSet();
    public int enemiesToBeSpawned;
    public int totalEnemyCount;
    public int maxEnemiesAtOnce = 30;
    public int defeatedEnemies = 0;
    public int spawnedEnemies = 0;
    public int currentEnemies = 0;
    public int ticksUntilNextEnemySpawn = 0;
    public int maxBossesAtOnce = 15; //can't have too little for gameplay, can't have too much for lag
    public int spawnedBosses = 0;
    public int currentBosses = 0;
    public int ticksUntilNextBossSpawn = 60;
    public int gracePeriod = getGracePeriod();

    public FlounderFest(int id, ServerWorld world, BlockPos startingPos, int quota, int totalEntityCount) {
        this.id = id;
        this.status = FlounderFest.Status.ONGOING;
        this.world = world;
        this.startingPos = startingPos;
        this.maxQuota = quota;
        this.enemiesToBeSpawned = totalEntityCount; //setting to -1 = infinite
        this.totalEnemyCount = totalEntityCount;
    }

    public boolean hasPlayers() {
        return !involvedPlayers.isEmpty();
    }

    @Nullable
    public ServerPlayerEntity getRandomPlayerTarget() {
        List<ServerPlayerEntity> players = this.world.getPlayers(this.isInFlounderFestDistance());
        if(!players.isEmpty()) {
            return players.get(world.random.nextInt(players.size()));
        }
        return null;
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
            if(!involvedPlayers.contains(player.getUuid())) {
                addPlayerToFlounderFest(player);
            }
            if(isGracePeriod()) {
//                player.sendMessage(Text.literal("FlounderFest Starting In " + ((gracePeriod) / 20)), true);
                if(gracePeriod % 20 == 0) {
                    sendGracePeriodPacket(player);
                }
            } else {
                if((maxTimeInTicks - ticksSinceStart) % 20 == 0) {
                    if(!isFinished()) {
                        secondsRemaining = (maxTimeInTicks - ticksSinceStart) / 20;
                    }
                    sendTimerPacket(player);
                }
                if(secondsRemaining == 100) { //needs one extra second to send the last grace period second update
                    sendGracePeriodPacket(player);
                }
            }
        }
    }

    public void addPlayerToFlounderFest(ServerPlayerEntity player) {
        involvedPlayers.add(player.getUuid());

        PacketByteBuf buf = PacketByteBufs.create();
        /*
        currentWave/waves
        secondsRemaining
        quotaProgress/quota
         */
        buf.writeInt(wave);
        buf.writeInt(maxWaves);
        buf.writeInt(secondsRemaining);
        buf.writeInt(quotaProgress);
        buf.writeInt(maxQuota);
        buf.writeBlockPos(startingPos);
        ServerPlayNetworking.send(player, FLOUNDERFEST_CREATE_HUD_ID, buf);
    }

    public void sendGracePeriodPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(gracePeriod / 20);
        ServerPlayNetworking.send(player, FLOUNDERFEST_GRACE_PERIOD_ID, buf);
    }

    public void sendTimerPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(secondsRemaining);
        ServerPlayNetworking.send(player, FLOUNDERFEST_TIMER_UPDATE_ID, buf);
    }

    public void sendQuotaPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(quotaProgress);
        buf.writeInt(maxQuota);
        ServerPlayNetworking.send(player, FLOUNDERFEST_QUOTA_PROGRESS_UPDATE_ID, buf);
    }

    public void sendDeleteHudPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_REMOVE_HUD_ID, buf);
    }

    public void sendVictoryPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_VICTORY_ID, buf);
    }

    public void sendDefeatPacket(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_DEFEAT_ID, buf);
    }


    public void tick() {
        if(isGracePeriod()) {
            gracePeriod--;
        } else {
            ticksSinceStart++;
            ticksUntilNextEnemySpawn--;
            ticksUntilNextBossSpawn--;
        }

        updateInvolvedPlayers();

        if(!this.hasStopped() && !isGracePeriod()) {
            if(this.status == Status.ONGOING) {
                //spawns in enemies every few seconds
                if(currentEnemies < maxEnemiesAtOnce && (spawnedEnemies < totalEnemyCount || totalEnemyCount == -1)) {
                    if(ticksUntilNextEnemySpawn <= 0) {
                        addEnemy();
                    }
                }
                if(currentBosses < maxBossesAtOnce && ticksUntilNextBossSpawn <= 0) {
                    addBoss();
                }

                //time up
                if(ticksSinceStart >= maxTimeInTicks) {
                    //FIXME - wave detection stuff
                    if(this.status == Status.ONGOING) {
                        //check for victory
                        if(quotaProgress >= maxQuota) {
                            this.status = Status.VICTORY;
                            involvedPlayers.forEach(playerUuid -> sendVictoryPacket((ServerPlayerEntity) world.getEntity(playerUuid)));

                        //counts as loss
                        } else {
                            this.status = Status.LOSS;
                            involvedPlayers.forEach(playerUuid -> sendDefeatPacket((ServerPlayerEntity) world.getEntity(playerUuid)));
                        }
                    }
                }
            } else if (isFinished()) {
                ticksSinceEnd++;
                if(ticksSinceEnd >= 300) {
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
            spawnedEnemies++;
            ticksUntilNextEnemySpawn = world.random.nextBetween(10, 80);
        }
    }

    public void addBoss() {
        if(FlounderFestApi.spawnBossFish(this, this.world, startingPos)) {
            enemiesToBeSpawned--;
            currentBosses++;
            spawnedBosses++;
            ticksUntilNextBossSpawn = world.random.nextBetween(currentBosses * 5, 160); //scales with the current bosses
            //FIXME - boss alert packet here
        }
    }

    public void addEntityToEnemyList(LivingEntity entity) {
        List<LivingEntity> set = enemies.stream().toList();
        LivingEntity livingEntity = null;

        for (LivingEntity entityFromSet : set) {
            if(entityFromSet.getUuid().equals(entity.getUuid())) {
                livingEntity = entityFromSet;
                break;
            }
        }

        if(livingEntity != null) {
            enemies.remove(livingEntity);
            enemies.add(entity);
        }

        enemies.add(entity);
    }

    public void updateQuota(int amount) {
        if(!isFinished()) {
            quotaProgress += amount;
            currentBosses -= amount;
            for (UUID playerUuid : involvedPlayers) {
                sendQuotaPacket((ServerPlayerEntity) this.world.getEntity(playerUuid));
            }
        }
    }

    public void updateEnemyCount(int amount) {
        currentEnemies -= amount;
    }


    public int getGracePeriod() {
        //seconds in ticks
        //grace period is 10 + 5 ticks seconds by default
        return 205;
    }

    public boolean isGracePeriod() {
        return gracePeriod > 0;
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

        enemies.forEach(entityFromSet -> (entityFromSet).remove(Entity.RemovalReason.DISCARDED));

        involvedPlayers.forEach(playerUuid -> sendDeleteHudPacket((ServerPlayerEntity) world.getEntity(playerUuid)));
    }

    public BlockPos getStartingPos() {
        return startingPos;
    }
    public int getId() {
        return id;
    }

    static enum Status {
        ONGOING,
        PAUSED,
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
