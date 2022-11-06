package nl.knokko.customitems.nms15;

import net.minecraft.server.v1_15_R1.EntityDamageSource;

public class CustomEntityDamageSource extends EntityDamageSource {

    public CustomEntityDamageSource(String name, net.minecraft.server.v1_15_R1.Entity attacker) {
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
