package nl.knokko.customitems.nms13;

import net.minecraft.server.v1_13_R2.EntityDamageSource;

class CustomEntityDamageSource extends EntityDamageSource {

    public CustomEntityDamageSource(String name, net.minecraft.server.v1_13_R2.Entity attacker) {
        super(name, attacker);
    }

    public CustomEntityDamageSource setIgnoreArmor(boolean ignoreArmor) {
        if (ignoreArmor) {
            super.setIgnoreArmor();
        }
        return this;
    }

    public CustomEntityDamageSource setFire(boolean isFire) {
        if (isFire) {
            // Ehm... yes... it looks like the deobfuscater made a mistake: setExplosion() should have been called setFire()
            super.setExplosion();
        }
        return this;
    }
}
