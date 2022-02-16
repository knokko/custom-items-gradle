package nl.knokko.customitems.drops;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.Objects;

public class MobDropValues extends ModelValues {

    public static MobDropValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        MobDropValues result = new MobDropValues(false);

        if (encoding == 0) {
            result.load1(input, itemSet);
        } else if (encoding == 1) {
            result.load2(input, itemSet);
        } else if (encoding == 2) {
            result.load3(input, itemSet);
        } else {
            throw new UnknownEncodingException("MobDrop", encoding);
        }

        return result;
    }

    public static MobDropValues createQuick(CIEntityType entityType, String requiredName, DropValues drop) {
        MobDropValues result = new MobDropValues(true);
        result.setEntityType(entityType);
        result.setRequiredName(requiredName);
        result.setDrop(drop);
        return result;
    }

    private CIEntityType entityType;
    private String requiredName;
    private DropValues drop;

    public MobDropValues(boolean mutable) {
        super(mutable);
        this.entityType = CIEntityType.ZOMBIE;
        this.requiredName = null;
        this.drop = new DropValues(false);
    }

    public MobDropValues(MobDropValues toCopy, boolean mutable) {
        super(mutable);
        this.entityType = toCopy.getEntityType();
        this.requiredName = toCopy.getRequiredName();
        this.drop = toCopy.getDrop();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.entityType = CIEntityType.getByOrdinal(input.readInt());
        this.requiredName = input.readString();
        this.drop = DropValues.load1(input, itemSet, false);
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.entityType = CIEntityType.getByOrdinal(input.readInt());
        this.requiredName = input.readString();
        this.drop = DropValues.load2(input, itemSet, false);
    }

    private void load3(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.entityType = CIEntityType.getByOrdinal(input.readInt());
        this.requiredName = input.readString();
        this.drop = DropValues.load(input, itemSet, false);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 2);

        output.addInt(entityType.ordinal());
        output.addString(requiredName);
        drop.save(output);
    }

    @Override
    public MobDropValues copy(boolean mutable) {
        return new MobDropValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == MobDropValues.class) {
            MobDropValues otherDrop = (MobDropValues) other;
            return this.entityType == otherDrop.entityType && Objects.equals(this.requiredName, otherDrop.requiredName)
                    && this.drop.equals(otherDrop.drop);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "MobDrop(" + entityType + ", " + requiredName + ", " + drop + ")";
    }

    public CIEntityType getEntityType() {
        return entityType;
    }

    /**
     * @return The required name, or null if no specific name is required
     */
    public String getRequiredName() {
        return requiredName;
    }

    public DropValues getDrop() {
        return drop;
    }

    public void setEntityType(CIEntityType newEntityType) {
        assertMutable();
        Checks.notNull(newEntityType);
        this.entityType = newEntityType;
    }

    public void setRequiredName(String newRequiredName) {
        assertMutable();
        this.requiredName = newRequiredName;
    }

    public void setDrop(DropValues newDrop) {
        assertMutable();
        Checks.notNull(newDrop);
        this.drop = newDrop.copy(false);
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (entityType == null) throw new ProgrammingValidationException("No entity type");
        // There are no invalid values for requiredName
        if (drop == null) throw new ProgrammingValidationException("No drop");
        Validation.scope("Drop", () -> drop.validate(itemSet));
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version < entityType.firstVersion) {
            throw new ValidationException(entityType + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > entityType.lastVersion) {
            throw new ValidationException(entityType + " doesn't exist anymore in mc " + MCVersions.createString(version));
        }
        drop.validateExportVersion(version);
    }
}
