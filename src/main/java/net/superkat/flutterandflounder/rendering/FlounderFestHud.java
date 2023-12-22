package net.superkat.flutterandflounder.rendering;

public class FlounderFestHud {
    public int waveNum;
    public int secondsLeft;
    public int quotaProgress;
    public FlounderFestHud(int waveNum, int secondsLeft, int quotaProgress) {
        this.secondsLeft = secondsLeft;
        this.quotaProgress = quotaProgress;
        this.waveNum = waveNum;
    }
    public void updateInfo(int waveNum, int secondsLeft, int quotaProgress) {
        this.secondsLeft = secondsLeft;
        this.quotaProgress = quotaProgress;
        this.waveNum = waveNum;
    }
}
