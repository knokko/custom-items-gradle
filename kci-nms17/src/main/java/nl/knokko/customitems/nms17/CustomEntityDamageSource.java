package nl.knokko.customitems.nms17;

import net.minecraft.world.damagesource.EntityDamageSource;

class CustomEntityDamageSource extends EntityDamageSource {

    public CustomEntityDamageSource(String name, net.minecraft.world.entity.Entity attacker) {
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
            super.setFire();
        }
        return this;
    }
}
