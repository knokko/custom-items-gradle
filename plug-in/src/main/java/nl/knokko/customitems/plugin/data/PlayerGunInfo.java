package nl.knokko.customitems.plugin.data;

public class PlayerGunInfo {

    public static PlayerGunInfo directCooldown(long remainingCooldown) {
        return new PlayerGunInfo(remainingCooldown, null, null);
    }

    public static PlayerGunInfo indirect(long remainingCooldown, int remainingStoredAmmo) {
        return new PlayerGunInfo(remainingCooldown, remainingStoredAmmo, null);
    }

    public static PlayerGunInfo indirectReloading(int remainingReloadTime) {
        return new PlayerGunInfo(0, null, remainingReloadTime);
    }

    public final long remainingCooldown;
    public final Integer remainingStoredAmmo;
    public final Integer remainingReloadTime;

    private PlayerGunInfo(long remainingCooldown, Integer remainingStoredAmmo, Integer remainingReloadTime) {
        this.remainingCooldown = remainingCooldown;
        this.remainingStoredAmmo = remainingStoredAmmo;
        this.remainingReloadTime = remainingReloadTime;
    }
}
