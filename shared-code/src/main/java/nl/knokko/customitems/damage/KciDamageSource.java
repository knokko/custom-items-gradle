package nl.knokko.customitems.damage;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;
import java.util.UUID;

public class KciDamageSource extends ModelValues {

    public static KciDamageSource load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomDamageSource", encoding);

        KciDamageSource damageSource = new KciDamageSource(false);
        damageSource.id = new UUID(input.readLong(), input.readLong());
        damageSource.name = input.readString();
        return damageSource;
    }

    private UUID id;
    private String name;

    public KciDamageSource(boolean mutable) {
        super(mutable);
        this.id = UUID.randomUUID();
        this.name = "";
    }

    public KciDamageSource(KciDamageSource toCopy, boolean mutable) {
        super(mutable);
        this.id = toCopy.getId();
        this.name = toCopy.getName();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addLong(id.getMostSignificantBits());
        output.addLong(id.getLeastSignificantBits());
        output.addString(name);
    }

    @Override
    public KciDamageSource copy(boolean mutable) {
        return new KciDamageSource(this, mutable);
    }

    @Override
    public String toString() {
        return "CustomDamageSourceValues(" + id + ", " + name + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof KciDamageSource) {
            KciDamageSource otherSource = (KciDamageSource) other;
            return this.id.equals(otherSource.id) && this.name.equals(otherSource.name);
        } else return false;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        assertMutable();
        this.name = Objects.requireNonNull(name);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (id == null) throw new ProgrammingValidationException("No ID");
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
    }

    public void validateComplete(ItemSet itemSet, UUID previousID) throws ValidationException, ProgrammingValidationException {
        validateIndependent();
        if (previousID == null && itemSet.damageSources.get(this.id).isPresent()) {
            throw new ProgrammingValidationException("Another damage source has this ID");
        }
        if (previousID != null && !previousID.equals(this.id)) {
            throw new ProgrammingValidationException("ID is immutable");
        }
    }
}
