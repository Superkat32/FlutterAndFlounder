package net.superkat.flutterandflounder.flounderfest;

import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
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
    public int ticksSinceWaveClear;
    public int maxTimeInTicks = 2000;
    public int secondsRemaining = 100;
    public int maxQuota;
    public int quotaProgress = 0;
    public int totalQuotaAcrossAllWaves = 0;
    private final Set<UUID> enemies = Sets.newHashSet();
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

    public FlounderFest(ServerWorld world, NbtCompound nbt) {
        this.world = world;
        this.id = nbt.getInt("Id");
        this.status = Status.fromName(nbt.getString("Status"));
        this.wave = nbt.getInt("Wave");
        this.maxWaves = nbt.getInt("MaxWaves");
        this.quotaProgress = nbt.getInt("QuotaProgress");
        this.maxQuota = nbt.getInt("MaxQuota");
        this.totalQuotaAcrossAllWaves = nbt.getInt("TotalQuotaAcrossAllWaves");
        this.ticksSinceStart = nbt.getInt("TicksSinceStart");
        this.ticksSinceEnd = nbt.getInt("TicksSinceEnd");
        this.ticksSinceWaveClear = nbt.getInt("TicksSinceWaveClear");
        this.ticksUntilNextEnemySpawn = nbt.getInt("TicksUntilNextEnemySpawn");
        this.ticksUntilNextBossSpawn = nbt.getInt("TicksUntilNextBossSpawn");
        this.currentEnemies = nbt.getInt("CurrentEnemies");
        this.currentBosses = nbt.getInt("CurrentBosses");
        this.spawnedEnemies = nbt.getInt("SpawnedEnemies");
        this.spawnedBosses = nbt.getInt("SpawnBosses");
        this.enemiesToBeSpawned = nbt.getInt("EnemiesToBeSpawned");
        this.totalEnemyCount = nbt.getInt("TotalEnemyCount");
        int startX = nbt.getInt("StartX");
        int startY = nbt.getInt("StartY");
        int startZ = nbt.getInt("StartZ");
        this.startingPos = new BlockPos(startX, startY, startZ);

//        if(nbt.contains("InvolvedPlayers", NbtElement.LIST_TYPE)) {
//            for (NbtElement nbtElement : nbt.getList("InvolvedPlayers", NbtElement.INT_ARRAY_TYPE)) {
//                this.involvedPlayers.add(NbtHelper.toUuid(nbtElement));
//            }
//        }

        if(nbt.contains("Enemies", NbtElement.LIST_TYPE)) {
            for (NbtElement nbtElement : nbt.getList("Enemies", NbtElement.INT_ARRAY_TYPE)) {
                this.enemies.add(NbtHelper.toUuid(nbtElement));
            }
        }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("Id", this.id);
        nbt.putString("Status", this.status.getName());
        nbt.putInt("Wave", this.wave);
        nbt.putInt("MaxWaves", this.maxWaves);
        nbt.putInt("QuotaProgress", this.quotaProgress);
        nbt.putInt("MaxQuota", this.maxQuota);
        nbt.putInt("TotalQuotaAcrossAllWaves", this.totalQuotaAcrossAllWaves);
        nbt.putInt("TicksSinceStart", this.ticksSinceStart);
        nbt.putInt("TicksSinceEnd", this.ticksSinceEnd);
        nbt.putInt("TicksSinceWaveClear", this.ticksSinceWaveClear);
        nbt.putInt("TicksUntilNextEnemySpawn", this.ticksUntilNextEnemySpawn);
        nbt.putInt("TicksUntilNextBossSpawn", this.ticksUntilNextBossSpawn);
        nbt.putInt("CurrentEnemies", this.currentEnemies);
        nbt.putInt("CurrentBosses", this.currentBosses);
        nbt.putInt("SpawnedEnemies", this.spawnedEnemies);
        nbt.putInt("SpawnBosses", this.spawnedBosses);
        nbt.putInt("EnemiesToBeSpawned", this.enemiesToBeSpawned);
        nbt.putInt("TotalEnemyCount", this.totalEnemyCount);
        nbt.putInt("StartX", this.startingPos.getX());
        nbt.putInt("StartY", this.startingPos.getY());
        nbt.putInt("StartZ", this.startingPos.getZ());

//        NbtList involvedPlayersNbtList = new NbtList();
//        for (UUID uuid : this.involvedPlayers) {
//            involvedPlayersNbtList.add(NbtHelper.fromUuid(uuid));
//        }
//        nbt.put("InvolvedPlayers", involvedPlayersNbtList);

        NbtList enemiesNbtList = new NbtList();
        for (UUID enemy : this.enemies) {
            enemiesNbtList.add(NbtHelper.fromUuid(enemy));
        }
        nbt.put("Enemies", enemiesNbtList);

        return nbt;
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
            return FlounderFestApi.getFlounderFestManager(world).getFlounderFestAt(player.getBlockPos()) == this;
        };
    }

    public void updateInvolvedPlayers() {

        List<ServerPlayerEntity> allPlayers = this.world.getPlayers();
        List<ServerPlayerEntity> players = this.world.getPlayers(this.isInFlounderFestDistance());

        for (ServerPlayerEntity player : allPlayers) {
            if(!players.contains(player)) {
                removePlayerFromFlounderFest(player);
            }
        }

        for (ServerPlayerEntity player : players) {
            if(!involvedPlayers.contains(player.getUuid())) {
                addPlayerToFlounderFest(player);
            }
            if(isGracePeriod()) {
                if(gracePeriod % 20 == 0) {
                    sendGracePeriodPacket(player);
                }
            } else {
                if((maxTimeInTicks - ticksSinceStart) % 20 == 0) {
                    if(!isFinished() && this.status != Status.WAVE_CLEAR) {
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
        if(player == null) {
            return;
        }
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

    public void removePlayerFromFlounderFest(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }

        involvedPlayers.remove(player.getUuid());
        sendDeleteHudPacket(player);
    }

    public void sendGracePeriodPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(gracePeriod / 20);
        ServerPlayNetworking.send(player, FLOUNDERFEST_GRACE_PERIOD_ID, buf);
    }

    public void sendTimerPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(secondsRemaining);
        ServerPlayNetworking.send(player, FLOUNDERFEST_TIMER_UPDATE_ID, buf);
    }

    public void sendQuotaPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(quotaProgress);
        buf.writeInt(maxQuota);
        ServerPlayNetworking.send(player, FLOUNDERFEST_QUOTA_PROGRESS_UPDATE_ID, buf);
    }

    public void sendWavePacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(wave);
        buf.writeInt(maxWaves);
        ServerPlayNetworking.send(player, FLOUNDERFEST_WAVE_UPDATE_ID, buf);
    }

    public void sendDeleteHudPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_REMOVE_HUD_ID, buf);
    }

    public void sendBossAlertPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_BOSS_ALERT_ID, buf);
    }

    public void sendVictoryPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_VICTORY_ID, buf);
    }

    public void sendDefeatPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_DEFEAT_ID, buf);
    }

    public void sendWaveClearPacket(ServerPlayerEntity player) {
        if(player == null) {
            return;
        }
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, FLOUNDERFEST_WAVE_CLEAR_ID, buf);
    }


    public void tick() {
        if (wave == 0) {
            startNextWave();
        }
        if(isGracePeriod()) {
            gracePeriod--;
        } else {
            ticksSinceStart++;
            ticksUntilNextEnemySpawn--;
            ticksUntilNextBossSpawn--;
        }

        updateInvolvedPlayers();

        this.maxEnemiesAtOnce = world.getGameRules().getInt(FlutterAndFlounderMain.FLOUNDERFEST_MAX_ENEMIES);
        this.maxBossesAtOnce = world.getGameRules().getInt(FlutterAndFlounderMain.FLOUNDERFEST_MAX_BOSSES);

        if(!this.hasStopped() && !isGracePeriod()) {
            if(this.status == Status.ONGOING) {

                //checks for all players being dead
                if(this.world.getGameRules().getBoolean(FlutterAndFlounderMain.END_FLOUNDERFEST_UPON_ALL_PLAYERS_DEAD)) {
                    List<ServerPlayerEntity> players = this.world.getPlayers(this.isInFlounderFestDistance());
                    boolean anyPlayersAlive = false;
                    for (ServerPlayerEntity player : players) {
                        anyPlayersAlive = player.isAlive();
                        if(anyPlayersAlive) {
                            break;
                        }
                    }
                    if(!anyPlayersAlive) {
                        lossFlounderFest();
                    }
                }

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
                    if(this.status == Status.ONGOING) {
                        //check for victory
                        if(quotaProgress >= maxQuota) {
                            if(wave < maxWaves) {
                                this.status = Status.WAVE_CLEAR;
                                ticksSinceWaveClear = 0;
                                totalQuotaAcrossAllWaves += quotaProgress;
                                involvedPlayers.forEach(playerUuid -> sendWaveClearPacket((ServerPlayerEntity) world.getEntity(playerUuid)));
                            } else {
                                winFlounderFest();
                            }

                        //counts as loss
                        } else {
                            lossFlounderFest();
                        }
                    }
                }
            } else if (this.status == Status.WAVE_CLEAR) {
                //next wave timer
                ticksSinceWaveClear++;
                if(ticksSinceWaveClear >= 100) { //5 second cooldown before grace period reset
                    startNextWave();
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

    public void winFlounderFest() {
        this.status = Status.VICTORY;
        involvedPlayers.forEach(playerUuid -> sendVictoryPacket((ServerPlayerEntity) world.getEntity(playerUuid)));
        totalQuotaAcrossAllWaves += quotaProgress;
        rewardPlayers();
    }

    public void lossFlounderFest() {
        this.status = Status.LOSS;
        involvedPlayers.forEach(playerUuid -> sendDefeatPacket((ServerPlayerEntity) world.getEntity(playerUuid)));
        totalQuotaAcrossAllWaves += quotaProgress;
        rewardPlayers();
    }

    public boolean shouldMobsFlee() {
        return status != Status.ONGOING;
    }

    public void startNextWave() {
        wave++;
        this.status = Status.ONGOING;
        gracePeriod = getGracePeriod();
        ticksSinceStart = 0;
        ticksUntilNextEnemySpawn = 0;
        ticksUntilNextBossSpawn = 45;
        currentEnemies = 0;
        currentBosses = 0;
        quotaProgress = 0;
        if(wave > 1) {
            maxQuota += wave - 1;
        }

        enemies.forEach(enemyUuid -> {
            Entity entity  = world.getEntity(enemyUuid);
            if(entity != null) {
                entity.remove(Entity.RemovalReason.DISCARDED);
//                enemies.remove(enemyUuid);
            }
        });

        involvedPlayers.forEach(playerUuid -> sendQuotaPacket((ServerPlayerEntity) world.getEntity(playerUuid)));
        involvedPlayers.forEach(playerUuid -> sendWavePacket((ServerPlayerEntity) world.getEntity(playerUuid)));
        involvedPlayers.forEach(playerUuid -> applyNextWaveRegen((LivingEntity) world.getEntity(playerUuid)));

        updateNbt();
    }

    public void applyNextWaveRegen(LivingEntity player) {
        if(player != null) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 3));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 200, 2));
        }
    }

    public void addEnemy() {
        if(FlounderFestApi.spawnLesserFish(this, this.world, startingPos)) {
            enemiesToBeSpawned--;
            currentEnemies++;
            spawnedEnemies++;
            ticksUntilNextEnemySpawn = world.random.nextBetween(10, 80);
            updateNbt();
        }
    }

    public void addBoss() {
        if(FlounderFestApi.spawnBossFish(this, this.world, startingPos)) {
            enemiesToBeSpawned--;
            currentBosses++;
            spawnedBosses++;
            ticksUntilNextBossSpawn = world.random.nextBetween(currentBosses * 5, 160); //scales with the current bosses
            for (UUID playerUuid : involvedPlayers) {
                sendBossAlertPacket((ServerPlayerEntity) this.world.getEntity(playerUuid));
            }
            updateNbt();
        }
    }

    public void addEntityToEnemyList(UUID entityUuid) {
        List<UUID> set = enemies.stream().toList();
        UUID livingEntity = null;

        for (UUID entityFromSetUuid : set) {
            if(entityFromSetUuid.equals(entityUuid)) {
                livingEntity = entityFromSetUuid;
                break;
            }
        }

        if(livingEntity != null) {
            enemies.remove(livingEntity);
            enemies.add(entityUuid);
        }

        enemies.add(entityUuid);
        updateNbt();
    }

    public void updateQuota(int amount) {
        if(status == Status.ONGOING) {
            quotaProgress += amount;
            currentBosses -= amount;
            for (UUID playerUuid : involvedPlayers) {
                sendQuotaPacket((ServerPlayerEntity) this.world.getEntity(playerUuid));
            }
            updateNbt();
        }
    }

    public void rewardPlayers() {
        FlounderFestApi.spawnFlounderFestRewards(this.world, this.startingPos, this.totalQuotaAcrossAllWaves, this.hasWon());
    }

    public void updateNbt() {
        FlounderFestManager flounderFestManager = FlounderFestApi.getFlounderFestManager(this.world);
        flounderFestManager.markDirty();
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

        enemies.forEach(enemyUuid -> {
            Entity entity  = world.getEntity(enemyUuid);
            if(entity != null) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        });

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
        WAVE_CLEAR,
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
