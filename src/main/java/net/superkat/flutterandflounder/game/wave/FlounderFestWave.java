package net.superkat.flutterandflounder.game.wave;

import net.minecraft.server.level.ServerLevel;
import net.superkat.flutterandflounder.game.FlounderFestGame;
import net.superkat.flutterandflounder.game.settings.WaveInfo;

public class FlounderFestWave {
    public final FlounderFestGame flounderFestGame;
    public final WaveInfo info;
    public final ServerLevel level;

    public int currentQuota;

    public FlounderFestWave(FlounderFestGame flounderFestGame, WaveInfo waveInfo) {
        this.flounderFestGame = flounderFestGame;
        this.info = waveInfo;
        this.level = flounderFestGame.level;
    }
}
