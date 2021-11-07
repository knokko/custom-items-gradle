package nl.knokko.customitems.projectile.cover;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ProjectileCoverValues extends ModelValues {

    static final byte ENCODING_SPHERE1 = 0;
    static final byte ENCODING_CUSTOM1 = 1;

    public static ProjectileCoverValues load(BitInput input, SItemSet itemSet) throws UnknownEncodingException {

        if (itemSet.getSide() == SItemSet.Side.EDITOR) {
            byte encoding = input.readByte();
            if (encoding == ENCODING_SPHERE1) {
                return SphereProjectileCoverValues.load(input, encoding, itemSet);
            } else if (encoding == ENCODING_CUSTOM1) {
                return CustomProjectileCoverValues.load(input, encoding);
            } else {
                throw new UnknownEncodingException("EditorProjectileCover", encoding);
            }
        } else {
            ProjectileCoverValues result = new ProjectileCoverValues(false);
            result.loadSharedProperties1(input);
            return result;
        }
    }

    protected CustomItemType itemType;
    protected short itemDamage;
    protected String name;

    public ProjectileCoverValues(boolean mutable) {
        super(mutable);
        this.itemType = CustomItemType.DIAMOND_SHOVEL;
        // itemDamage will be set right before exporting
        this.name = "";
    }

    public ProjectileCoverValues(ProjectileCoverValues toCopy, boolean mutable) {
        super(mutable);
        this.itemType = toCopy.getItemType();
        this.itemDamage = toCopy.getItemDamage();
        this.name = toCopy.getName();
    }

    protected void loadSharedProperties1(BitInput input) {
        this.itemType = CustomItemType.valueOf(input.readString());
        this.itemDamage = input.readShort();
        this.name = input.readString();
    }

    protected void saveSharedProperties1(BitOutput output) {
        output.addString(itemType.name());
        output.addShort(itemDamage);
        output.addString(name);
    }

    protected final void export(BitOutput output) {
        saveSharedProperties1(output);
    }

    protected void save(BitOutput output) {
        throw new UnsupportedOperationException("This is only for Editor projectile covers");
    }

    public final void save(BitOutput output, SItemSet.Side side) {
        if (side == SItemSet.Side.EDITOR) {
            save(output);
        } else {
            export(output);
        }
    }

    @Override
    public ProjectileCoverValues copy(boolean mutable) {
        return new ProjectileCoverValues(this, mutable);
    }

    public CustomItemType getItemType() {
        return itemType;
    }

    public short getItemDamage() {
        return itemDamage;
    }

    public String getName() {
        return name;
    }

    public void setItemType(CustomItemType newItemType) {
        assertMutable();
        Checks.notNull(newItemType);
        this.itemType = newItemType;
    }

    public void setItemDamage(short newItemDamage) {
        assertMutable();
        this.itemDamage = newItemDamage;
    }

    public void setName(String newName) {
        assertMutable();
        this.name = newName;
    }

    public void writeModel(ZipOutputStream output) throws IOException {
        throw new UnsupportedOperationException("This can only be done on the Editor side");
    }

    public void validate(SItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        // item damage is nowadays picked right before exporting and thus doesn't really need validation
        Validation.safeName(name);
        if (!name.equals(oldName) && itemSet.getProjectileCover(name).isPresent()) {
            throw new ValidationException("Another projectile with this name already exists");
        }
    }
}