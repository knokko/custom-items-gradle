package nl.knokko.customitems.plugin.data;

public class PlayerWandInfo {

    public final long remainingCooldown;
    public final int remainingCharges;
    public final long remainingRechargeTime;
    public final int currentMana, maxMana;

    public PlayerWandInfo(
            long remainingCooldown,
            int remainingCharges, long remainingRechargeTime,
            int currentMana, int maxMana
    ) {
        this.remainingCooldown = remainingCooldown;
        this.remainingCharges = remainingCharges;
        this.remainingRechargeTime = remainingRechargeTime;
        this.currentMana = currentMana;
        this.maxMana = maxMana;
    }
}
