package nl.knokko.customitems.nms16;

import net.minecraft.server.v1_16_R3.EntityDamageSource;

public class CustomEntityDamageSource extends EntityDamageSource {

    public CustomEntityDamageSource(String name, net.minecraft.server.v1_16_R3.Entity attacker) {
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
