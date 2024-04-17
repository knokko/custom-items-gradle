package nl.knokko.customitems.plugin.tasks;

import nl.knokko.customitems.item.KciGun;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.item.KciWand;
import nl.knokko.customitems.item.WandCharges;
import nl.knokko.customitems.item.gun.DirectGunAmmo;
import nl.knokko.customitems.item.gun.IndirectGunAmmo;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestMobWands {

    private void assertNoCooldownAtAll(MobWands.MobCooldown cooldown, long currentTick) {
        assertTrue(cooldown.canCleanUp(currentTick));
        assertTrue(cooldown.canShoot(currentTick));
    }

    private void assertOnCooldown(MobWands.MobCooldown cooldown, long currentTick) {
        assertFalse(cooldown.canCleanUp(currentTick));
        assertFalse(cooldown.canShoot(currentTick));
    }

    private void testCooldownOnly(KciItem item) {
        MobWands.MobCooldown cooldown = new MobWands.MobCooldown(item);
        assertNoCooldownAtAll(cooldown, 0L);
        assertNoCooldownAtAll(cooldown, 123456789L);

        cooldown.shoot(1000L);
        assertOnCooldown(cooldown, 1000L);
        assertOnCooldown(cooldown, 1009L);
        assertNoCooldownAtAll(cooldown, 1010L);
    }

    @Test
    public void testMobDirectGunCooldown() {
        DirectGunAmmo ammo = new DirectGunAmmo(true);
        ammo.setCooldown(10);
        KciGun gun = new KciGun(true);
        gun.setAmmo(ammo);

        testCooldownOnly(gun);
    }

    @Test
    public void testMobIndirectGunCooldown() {
        IndirectGunAmmo ammo = new IndirectGunAmmo(true);
        ammo.setCooldown(10);
        KciGun gun = new KciGun(true);
        gun.setAmmo(ammo);

        testCooldownOnly(gun);
    }

    @Test
    public void testMobWandCooldownWithoutCharges() {
        KciWand wand = new KciWand(true);
        wand.setCooldown(10);

        testCooldownOnly(wand);
    }

    private void assertMissingSomeCharges(MobWands.MobCooldown cooldown, long currentTick) {
        assertTrue(cooldown.canShoot(currentTick));
        assertFalse(cooldown.canCleanUp(currentTick));
    }

    @Test
    public void testMobCooldownWithCharges() {
        KciWand wand = new KciWand(true);
        wand.setCooldown(10);
        wand.setCharges(WandCharges.createQuick(5, 30));

        MobWands.MobCooldown cooldown = new MobWands.MobCooldown(wand);

        assertNoCooldownAtAll(cooldown, 0L);
        assertNoCooldownAtAll(cooldown, 100L);

        cooldown.shoot(100L);
        assertOnCooldown(cooldown, 100L);
        assertOnCooldown(cooldown, 109L);
        assertMissingSomeCharges(cooldown, 110L);
        assertMissingSomeCharges(cooldown, 129L);
        assertNoCooldownAtAll(cooldown, 130L);

        cooldown.shoot(130L);
        assertOnCooldown(cooldown, 139L);
        assertMissingSomeCharges(cooldown, 159L);
        assertNoCooldownAtAll(cooldown, 160L);

        cooldown.shoot(200L);
        cooldown.shoot(229L);
        assertMissingSomeCharges(cooldown, 259L);
        assertNoCooldownAtAll(cooldown, 260L);

        cooldown.shoot(300L);
        cooldown.shoot(350L);
        assertMissingSomeCharges(cooldown, 379L);
        assertNoCooldownAtAll(cooldown, 380L);
    }
}
