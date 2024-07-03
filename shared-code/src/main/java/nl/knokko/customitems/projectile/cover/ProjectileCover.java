package nl.knokko.customitems.projectile.cover;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ProjectileCover extends ModelValues {

    static final byte ENCODING_SPHERE1 = 0;
    static final byte ENCODING_CUSTOM1 = 1;
    static final byte ENCODING_CUSTOM2 = 2;
    static final byte ENCODING_SPHERE2 = 3;

    public static ProjectileCover load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            byte encoding = input.readByte();
            if (encoding == ENCODING_SPHERE1 || encoding == ENCODING_SPHERE2) {
                return SphereProjectileCover.load(input, encoding, itemSet);
            } else if (encoding == ENCODING_CUSTOM1 || encoding == ENCODING_CUSTOM2) {
                return CustomProjectileCover.load(input, itemSet, encoding);
            } else {
                throw new UnknownEncodingException("EditorProjectileCover", encoding);
            }
        } else {
            ProjectileCover result = new ProjectileCover(false);
            result.loadSharedProperties1(input);
            return result;
        }
    }

    protected KciItemType itemType;
    protected short itemDamage;
    protected String name;
    protected TextureReference geyserTexture;

    public ProjectileCover(boolean mutable) {
        super(mutable);
        this.itemType = KciItemType.DIAMOND_SHOVEL;
        // itemDamage will be set right before exporting
        this.name = "";
        this.geyserTexture = null;
    }

    public ProjectileCover(ProjectileCover toCopy, boolean mutable) {
        super(mutable);
        this.itemType = toCopy.getItemType();
        this.itemDamage = toCopy.getItemDamage();
        this.name = toCopy.getName();
        this.geyserTexture = toCopy.getGeyserTextureReference();
    }

    protected void loadSharedProperties1(BitInput input) {
        this.itemType = KciItemType.valueOf(input.readString());
        this.itemDamage = input.readShort();
        this.name = input.readString();
    }

    protected void loadSharedPropertiesNew(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ProjectileCoverShared", encoding);

        loadSharedProperties1(input);
        if (input.readBoolean()) this.geyserTexture = itemSet.textures.getReference(input.readString());
        else this.geyserTexture = null;
    }

    protected void saveSharedProperties1(BitOutput output) {
        output.addString(itemType.name());
        output.addShort(itemDamage);
        output.addString(name);
    }

    protected void saveSharedPropertiesNew(BitOutput output) {
        output.addByte((byte) 1);
        saveSharedProperties1(output);
        output.addBoolean(geyserTexture != null);
        if (geyserTexture != null) output.addString(geyserTexture.get().getName());
    }

    protected final void export(BitOutput output) {
        saveSharedProperties1(output);
    }

    protected void save(BitOutput output) {
        throw new UnsupportedOperationException("This is only for Editor projectile covers");
    }

    public final void save(BitOutput output, ItemSet.Side side) {
        if (side == ItemSet.Side.EDITOR) {
            save(output);
        } else {
            export(output);
        }
    }

    protected boolean areBasePropertiesEqual(ProjectileCover other) {
        return this.itemType == other.itemType && this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return "ProjectileCover(" + name + ")";
    }

    @Override
    public ProjectileCover copy(boolean mutable) {
        return new ProjectileCover(this, mutable);
    }

    public KciItemType getItemType() {
        return itemType;
    }

    public short getItemDamage() {
        return itemDamage;
    }

    public String getName() {
        return name;
    }

    public TextureReference getGeyserTextureReference() {
        return geyserTexture;
    }

    public KciTexture getGeyserTexture() {
        return geyserTexture != null ? geyserTexture.get() : null;
    }

    public void setItemType(KciItemType newItemType) {
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

    public void setGeyserTexture(TextureReference texture) {
        assertMutable();
        this.geyserTexture = texture;
    }

    public void writeModel(ZipOutputStream output) throws IOException {
        throw new UnsupportedOperationException("This can only be done on the Editor side");
    }

    public void validate(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (itemType == null) throw new ProgrammingValidationException("No item type");
        if (!itemType.canServe(KciItemType.Category.PROJECTILE_COVER)) {
            throw new ProgrammingValidationException("Item type can't be used for projectile covers");
        }
        // item damage is nowadays picked right before exporting and thus doesn't really need validation
        Validation.safeName(name);
        if (!name.equals(oldName) && itemSet.projectileCovers.get(name).isPresent()) {
            throw new ValidationException("Another projectile with this name already exists");
        }

        if (geyserTexture != null && !itemSet.textures.isValid(geyserTexture)) {
            throw new ProgrammingValidationException("Geyser texture is not valid (anymore)");
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (version < itemType.firstVersion) {
            throw new ValidationException(itemType + " doesn't exist yet in mc " + MCVersions.createString(version));
        }
        if (version > itemType.lastVersion) {
            throw new ValidationException(itemType + " doesn't exist anymore in mc " + MCVersions.createString(version));
        }
    }
}
