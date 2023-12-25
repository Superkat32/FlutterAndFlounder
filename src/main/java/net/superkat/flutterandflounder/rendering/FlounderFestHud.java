package net.superkat.flutterandflounder.rendering;

public class FlounderFestHud {
    public int maxWaves;
    public int waveNum;
    public int secondsLeft;
    public int quotaProgress;
    public int maxQuota;
    public int gracePeriod;
    public Status status;
    public FlounderFestHud(int waveNum, int maxWaves, int secondsLeft, int quotaProgress, int maxQuota) {
        this.waveNum = waveNum;
        this.maxWaves = maxWaves;
        this.secondsLeft = secondsLeft;
        this.quotaProgress = quotaProgress;
        this.maxQuota = maxQuota;
        this.status = Status.ONGOING;
    }
    public void updateInfo(int waveNum, int maxWaves, int secondsLeft, int quotaProgress, int maxQuota) {
        this.waveNum = waveNum;
        this.maxWaves = maxWaves;
        this.secondsLeft = secondsLeft;
        this.quotaProgress = quotaProgress;
        this.maxQuota = maxQuota;
    }

    public void updateWave(int waveNum, int maxWaves) {
        this.waveNum = waveNum;
        this.maxWaves = maxWaves;
    }

    public void updateTimer(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public void updateQuota(int quotaProgress, int maxWaves) {
        this.quotaProgress = quotaProgress;
        this.maxQuota = maxQuota;
        FlutterAndFlounderRendering.startQuotaUpdateAnimation();
    }

    public void updateGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
    public void updateStatus(Status statusUpdate) {
        this.status = statusUpdate;
        FlutterAndFlounderRendering.renewTextTypeWriter = true;
    }

    public static enum Status {
        ONGOING,
        WAVE_CLEAR,
        VICTORY,
        DEFEAT;
    }
}
