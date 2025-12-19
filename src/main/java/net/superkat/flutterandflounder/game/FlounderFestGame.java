package net.superkat.flutterandflounder.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.superkat.flounderlib.api.minigame.v1.game.SyncedFlounderGame;
import net.superkat.flounderlib.api.minigame.v1.registry.FlounderGameType;
import net.superkat.flounderlib.api.minigame.v1.sync.FlounderStateSyncer;
import net.superkat.flounderlib.api.minigame.v1.sync.FlounderSyncState;
import net.superkat.flutterandflounder.FlutterAndFlounder;
import net.superkat.flutterandflounder.game.settings.FlounderFestSettings;
import net.superkat.flutterandflounder.game.settings.HazardLevel;
import net.superkat.flutterandflounder.game.wave.FlounderFestWave;
import org.jetbrains.annotations.NotNull;

/**
 * The main FlounderFest game class. Handles ticking, player management, seed randomness, and more.
 */
public class FlounderFestGame extends SyncedFlounderGame {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(FlutterAndFlounder.MOD_ID, "flounderfest_game");

    public static final Codec<FlounderFestGame> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("ticks").forGetter(game -> game.ticks),
                    BlockPos.CODEC.fieldOf("pos").forGetter(game -> game.centerPos)
            ).apply(instance, FlounderFestGame::new)
    );

    public static final FlounderStateSyncer<FlounderFestGame, SyncState> STATE_SYNCER = FlounderStateSyncer.create(FlounderFestGame.class, SyncState::new)
            .addInteger(game -> game.ticks, (syncState, integer) -> syncState.ticks = integer)
            .addInteger(game -> game.waveCount, (syncState, integer) -> syncState.waveCount = integer);

    public final FlounderFestSettings settings;
    public final HazardLevel hazardLevel;
    protected final RandomSource seededRandom;

    public FlounderFestWave wave = null;
    public int waveCount = 0;

    public FlounderFestGame(BlockPos centerPos, FlounderFestSettings settings) {
        super(centerPos);
        this.settings = settings;
        this.seededRandom = RandomSource.create(this.settings.seed);
        this.hazardLevel = new HazardLevel(settings.hazardLevel);
    }

    public FlounderFestGame(int ticks, BlockPos centerPos) {
        super(ticks, centerPos);
        this.settings = null;
        this.seededRandom = RandomSource.create();
        this.hazardLevel = new HazardLevel(100f);
    }

    @Override
    public void init(ServerLevel world, int minigameId) {
        super.init(world, minigameId);
        this.startNextWave();
    }

    @Override
    public void tick() {
        super.tick();
        this.markDirty();

        if(this.ticks >= 500) {
            this.invalidate();
        }
    }

    public void startNextWave() {
        this.waveCount = this.seededRandom.nextInt();
    }

    @Override
    public @NotNull FlounderGameType<?> getGameType() {
        return FlutterAndFlounder.FLOUNDER_FEST_GAME_TYPE;
    }

    public static class SyncState implements FlounderSyncState {
        public int ticks = 0;
        public int waveCount = 0;
    }
}
