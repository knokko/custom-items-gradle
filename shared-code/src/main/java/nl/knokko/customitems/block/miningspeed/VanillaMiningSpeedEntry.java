package nl.knokko.customitems.block.miningspeed;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import static nl.knokko.customitems.block.miningspeed.MiningSpeedValues.validateValue;

public class VanillaMiningSpeedEntry extends ModelValues {

    static VanillaMiningSpeedEntry load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("VanillaMiningSpeedEntry", encoding);

        VanillaMiningSpeedEntry result = new VanillaMiningSpeedEntry(false);
        result.value = input.readInt();
        result.material = CIMaterial.valueOf(input.readString());
        result.acceptCustomItems = input.readBoolean();
        return result;
    }

    private int value;
    private CIMaterial material;
    private boolean acceptCustomItems;

    public VanillaMiningSpeedEntry(boolean mutable) {
        super(mutable);
        this.value = -1;
        this.material = CIMaterial.STONE_PICKAXE;
        this.acceptCustomItems = true;
    }

    public VanillaMiningSpeedEntry(VanillaMiningSpeedEntry toCopy, boolean mutable) {
        super(mutable);
        this.value = toCopy.getValue();
        this.material = toCopy.getMaterial();
        this.acceptCustomItems = toCopy.shouldAcceptCustomItems();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(value);
        output.addString(material.name());
        output.addBoolean(acceptCustomItems);
    }

    @Override
    public VanillaMiningSpeedEntry copy(boolean mutable) {
        return new VanillaMiningSpeedEntry(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VanillaMiningSpeedEntry) {
            VanillaMiningSpeedEntry otherEntry = (VanillaMiningSpeedEntry) other;
            return this.value == otherEntry.value && this.material == otherEntry.material && this.acceptCustomItems == otherEntry.acceptCustomItems;
        } else {
            return false;
        }
    }

    public int getValue() {
        return value;
    }

    public CIMaterial getMaterial() {
        return material;
    }

    public boolean shouldAcceptCustomItems() {
        return acceptCustomItems;
    }

    public void setValue(int value) {
        assertMutable();
        this.value = value;
    }

    public void setMaterial(CIMaterial material) {
        assertMutable();
        Checks.notNull(material);
        this.material = material;
    }

    public void setAcceptCustomItems(boolean acceptCustomItems) {
        assertMutable();
        this.acceptCustomItems = acceptCustomItems;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        validateValue(value);
        if (material == null) throw new ProgrammingValidationException("No material");
    }
}
