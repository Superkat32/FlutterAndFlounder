package net.superkat.flutterandflounder.game.settings;

import net.minecraft.util.Mth;

public class HazardLevel {
    public static final float MAX_HAZARD = 333f;
    public float percent;

    public HazardLevel(float percent) {
        this.percent = percent;
    }

    /**
     * @param players Players in the FlounderFest
     * @param wave The FlounderFest's current wave
     * @return The quota for that wave, accounting for player count
     */
    public int determineQuota(int players, int wave) {
        int maxQuota = this.getMaxQuotaForWave(wave);
        float playerMultiplier = this.getPlayerQuotaMultiplier(players);
        float initQuota = this.getInitQuotaForHazardLevel();
        float extraQuota = this.getExtraQuotaForWave(wave, initQuota, maxQuota);

        float quota = Math.min(playerMultiplier * initQuota + extraQuota, maxQuota);
        return Math.round(quota);
    }

    // Get the initial quota based on hazard level - assumes 4 players and wave 1
    protected float getInitQuotaForHazardLevel() {
        float magicHazardMultiplier = 0.09f; // Trial & Error'd my way to these numbers
        float magicExtraMultiplier = 0.0825f;
        float hazard = Mth.clamp(this.percent, 1, MAX_HAZARD);
        return hazard * (magicHazardMultiplier + (1 - (hazard / MAX_HAZARD)) * magicExtraMultiplier);
    }

    // Get extra quota for a wave based on the initial quota and wave number
    protected float getExtraQuotaForWave(int wave, float initQuota, int maxQuota) {
        float difference = Math.max(maxQuota - 30, 0);
        float hazard = Mth.clamp(this.percent, 1, MAX_HAZARD);

        float extra = Math.max(hazard / MAX_HAZARD, 1f / Math.max(difference, 1)) * difference;
        return Math.max(extra, wave - 1);
    }

    // Reduce or increase the quota based on the amount of players
    protected float getPlayerQuotaMultiplier(int players) {
        if (players > 4) { // All players after the first four shouldn't increase the quota by as much
            return (players - ((players - 4) / 2f)) / 4f;
        }
        return players / 4f;
    }

    // Wave 1 -> 30
    // Wave 2 -> 32
    // Wave 3 -> 35
    protected int getMaxQuotaForWave(int wave) {
        return switch (wave) {
            case 1 -> 30;
            case 2 -> 32;
            case 3 -> 35;
            default -> Mth.clamp(
                    Mth.ceil(30 + wave * (wave >= 15 ? 2.09f : 1.6f)),
                    30,
                    100
            );
        };
    }

}
