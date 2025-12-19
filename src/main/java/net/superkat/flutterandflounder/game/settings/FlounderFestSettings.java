package net.superkat.flutterandflounder.game.settings;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.superkat.flutterandflounder.game.FlounderFestGame;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for containing settings which influence a {@link FlounderFestGame}. This will be passed by the item starting the FlounderFest.
 */
public class FlounderFestSettings {
    public final long seed;

    public float hazardLevel;

    public int maxWaves;
    public int waveSeconds;

    public boolean hardcore;

    public static Builder create() {
        return new Builder();
    }

    public static FlounderFestSettings createDefault() {
        return new Builder().build();
    }

    public FlounderFestSettings(
            long seed, float hazardLevel,
            int maxWaves, int waveSeconds,
            boolean hardcore
    ) {
        this.seed = seed;
        this.hazardLevel = hazardLevel;
        this.maxWaves = maxWaves;
        this.waveSeconds = waveSeconds;
        this.hardcore = hardcore;

        if(this.hazardLevel == -1f) {
            this.hazardLevel = RandomSource.create().nextIntBetweenInclusive(50, 200);
        }
    }

    /**
     * @see WorldOptions#parseSeed(String)
     */
    public static long parseSeed(String seedString) {
        String seed = seedString.trim();
        if(StringUtils.isEmpty(seed)) {
            return generateSeed();
        } else {
            try {
                return Long.parseLong(seed);
            } catch (NumberFormatException e) {
                return seed.hashCode();
            }
        }
    }

    public static long generateSeed() {
        return RandomSource.create().nextLong();
    }

    public static class Builder {
        public long seed = generateSeed();
        public float hazardLevel = -1f;
        public int maxWaves = 3;
        public int waveSeconds = 100;
        public boolean hardcore = false;

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Builder hazardLevel(float hazardLevel) {
            this.hazardLevel = hazardLevel;
            return this;
        }

        public Builder maxWaves(int maxWaves) {
            this.maxWaves = maxWaves;
            return this;
        }

        public Builder waveSeconds(int waveSeconds) {
            this.waveSeconds = waveSeconds;
            return this;
        }

        public Builder hardcore(boolean hardcore) {
            this.hardcore = hardcore;
            return this;
        }

        public FlounderFestSettings build() {
            return new FlounderFestSettings(
                    this.seed, this.hazardLevel,
                    this.maxWaves, this.waveSeconds,
                    this.hardcore
            );
        }
    }
}
