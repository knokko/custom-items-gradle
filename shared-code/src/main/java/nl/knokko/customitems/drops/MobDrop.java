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

public class MobDrop extends ModelValues {

    public static MobDrop load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        MobDrop result = new MobDrop(false);

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

    public static MobDrop createQuick(VEntityType entityType, String requiredName, KciDrop drop) {
        MobDrop result = new MobDrop(true);
        result.setEntityType(entityType);
        result.setRequiredName(requiredName);
        result.setDrop(drop);
        return result;
    }

    private VEntityType entityType;
    private String requiredName;
    private KciDrop drop;

    public MobDrop(boolean mutable) {
        super(mutable);
        this.entityType = VEntityType.ZOMBIE;
        this.requiredName = null;
        this.drop = new KciDrop(false);
    }

    public MobDrop(MobDrop toCopy, boolean mutable) {
        super(mutable);
        this.entityType = toCopy.getEntityType();
        this.requiredName = toCopy.getRequiredName();
        this.drop = toCopy.getDrop();
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.entityType = VEntityType.getByOrdinal(input.readInt());
        this.requiredName = input.readString();
        this.drop = KciDrop.load1(input, itemSet, false);
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.entityType = VEntityType.getByOrdinal(input.readInt());
        this.requiredName = input.readString();
        this.drop = KciDrop.load2(input, itemSet, false);
    }

    private void load3(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.entityType = VEntityType.getByOrdinal(input.readInt());
        this.requiredName = input.readString();
        this.drop = KciDrop.load(input, itemSet, false);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 2);

        output.addInt(entityType.ordinal());
        output.addString(requiredName);
        drop.save(output);
    }

    @Override
    public MobDrop copy(boolean mutable) {
        return new MobDrop(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == MobDrop.class) {
            MobDrop otherDrop = (MobDrop) other;
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

    public VEntityType getEntityType() {
        return entityType;
    }

    /**
     * @return The required name, or null if no specific name is required
     */
    public String getRequiredName() {
        return requiredName;
    }

    public KciDrop getDrop() {
        return drop;
    }

    public void setEntityType(VEntityType newEntityType) {
        assertMutable();
        Checks.notNull(newEntityType);
        this.entityType = newEntityType;
    }

    public void setRequiredName(String newRequiredName) {
        assertMutable();
        this.requiredName = newRequiredName;
    }

    public void setDrop(KciDrop newDrop) {
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
