package nl.knokko.customitems.item;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class WandChargeValues extends ModelValues {

    public static WandChargeValues load1(BitInput input) {
        WandChargeValues result = new WandChargeValues(false);

        result.maxCharges = input.readInt();
        result.rechargeTime = input.readInt();

        return result;
    }

    private int maxCharges;
    private int rechargeTime;

    public WandChargeValues(boolean mutable) {
        super(mutable);

        this.maxCharges = 3;
        this.rechargeTime = 60;
    }

    public WandChargeValues(WandChargeValues toCopy, boolean mutable) {
        super(mutable);

        this.maxCharges = toCopy.getMaxCharges();
        this.rechargeTime = toCopy.getRechargeTime();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == WandChargeValues.class) {
            WandChargeValues otherCharges = (WandChargeValues) other;
            return this.maxCharges == otherCharges.maxCharges && this.rechargeTime == otherCharges.rechargeTime;
        } else {
            return false;
        }
    }

    @Override
    public WandChargeValues copy(boolean mutable) {
        return new WandChargeValues(this, mutable);
    }

    public void save1(BitOutput output) {
        output.addInt(maxCharges);
        output.addInt(rechargeTime);
    }

    public int getMaxCharges() {
        return maxCharges;
    }

    public int getRechargeTime() {
        return rechargeTime;
    }

    public void setMaxCharges(int newMaxCharges) {
        assertMutable();
        this.maxCharges = newMaxCharges;
    }

    public void setRechargeTime(int newRechargeTime) {
        assertMutable();
        this.rechargeTime = newRechargeTime;
    }

    public void validate() throws ValidationException {
        if (maxCharges <= 1) throw new ValidationException("Maximum charges must be greater than 1");
        if (rechargeTime <= 0) throw new ValidationException("Recharge time must be positive");
    }
}
