package nl.knokko.customitems.nms18;

import net.minecraft.world.damagesource.EntityDamageSource;

public class CustomEntityDamageSource extends EntityDamageSource {

    public CustomEntityDamageSource(String name, net.minecraft.world.entity.Entity attacker) {
        super(name, attacker);
    }

    public CustomEntityDamageSource setIgnoreArmor(boolean ignoreArmor) {
        if (ignoreArmor) {
            super.m();
        }
        return this;
    }

    public CustomEntityDamageSource setFire(boolean isFire) {
        if (isFire) {
            super.q();
        }
        return this;
    }
}
