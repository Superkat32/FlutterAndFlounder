package net.superkat.flutterandflounder.game.settings;

/**
 * {@link net.superkat.flutterandflounder.game.wave.FlounderFestWave} specific info which changes for each wave, contains info for boss spawn times and amounts, and quota targets.
 */
public class WaveInfo {

    public int targetQuota;

    public int maxActiveBosses = 15;
    public int maxActiveLessersIntro = 24; // first half
    public int maxActiveLessersEnding = 30; // last half

    public int lastBossSpawnSecond = 28;

}
