package nl.knokko.customitems.plugin.data;

public class PlayerWandInfo {

    public final long remainingCooldown;
    public final int remainingCharges;
    public final long remainingRechargeTime;

    public PlayerWandInfo(long remainingCooldown, int remainingCharges, long remainingRechargeTime) {
        this.remainingCooldown = remainingCooldown;
        this.remainingCharges = remainingCharges;
        this.remainingRechargeTime = remainingRechargeTime;
    }
}
