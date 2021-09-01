package nl.knokko.customitems.block;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomBlockValues extends ModelValues {

    private static final byte ENCODING_1 = 1;

    public static CustomBlockValues load(
            BitInput input, Function<String, CustomItem> getItemByName,
            ExceptionSupplier<Object, UnknownEncodingException> loadResult,
            // loadTexture will simply return null on the plug-in side
            Function<String, NamedImage> loadTexture, boolean mutable, int internalId
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        CustomBlockValues result = new CustomBlockValues(mutable);
        result.internalId = internalId;
        if (encoding == ENCODING_1) {
            result.load1(input, getItemByName, loadResult, loadTexture);
        } else {
            throw new UnknownEncodingException("CustomBlockValues", encoding);
        }

        return result;
    }

    private int internalId;

    private String name;

    private Collection<CustomBlockDrop> drops;

    // Only use this in the Editor; Keep it null on the plug-in
    private TextureReference texture;

    public CustomBlockValues(boolean mutable) {
        super(mutable);

        this.name = "";
        this.drops = new ArrayList<>(0);
        this.texture = null;
    }

    public CustomBlockValues(CustomBlockValues toCopy, boolean mutable) {
        super(mutable);

        this.name = toCopy.getName();
        this.drops = toCopy.getDrops();
        this.texture = toCopy.getTexture();
    }

    @Override
    public CustomBlockValues copy(boolean mutable) {
        return new CustomBlockValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomBlockValues) {
            CustomBlockValues otherBlock = (CustomBlockValues) other;
            return otherBlock.name.equals(this.name) && otherBlock.drops.equals(this.drops) &&
                    otherBlock.texture.get().getName().equals(this.texture.get().getName());
        } else {
            return false;
        }
    }

    private void loadDrops1(
            BitInput input, Function<String, CustomItem> getItemByName,
            ExceptionSupplier<Object, UnknownEncodingException> loadResult
    ) throws UnknownEncodingException {
        int numDrops = input.readInt();
        this.drops = new ArrayList<>(numDrops);
        for (int counter = 0; counter < numDrops; counter++) {
            this.drops.add(CustomBlockDrop.load(input, getItemByName, loadResult, false));
        }
    }

    private void load1(
            BitInput input, Function<String, CustomItem> getItemByName,
            ExceptionSupplier<Object, UnknownEncodingException> loadResult,
            Function<String, NamedImage> loadTexture
    ) throws UnknownEncodingException {
        this.name = input.readString();
        this.loadDrops1(input, getItemByName, loadResult);
        this.texture = loadTexture.apply(input.readString());
    }

    public void save(BitOutput output, Consumer<Object> saveResult) {
        output.addByte(ENCODING_1);
        save1(output, saveResult);
    }

    private void saveDrops1(BitOutput output, Consumer<Object> saveResult) {
        output.addInt(drops.size());
        for (CustomBlockDrop drop : drops) {
            drop.save(output, saveResult);
        }
    }

    private void saveTexture1(BitOutput output) {
        if (texture == null) {
            output.addString(null);
        } else {
            output.addString(texture.get().getName());
        }
    }

    private void save1(BitOutput output, Consumer<Object> saveResult) {
        output.addString(name);
        saveDrops1(output, saveResult);
        saveTexture1(output);
    }

    public int getInternalID() {
        return internalId;
    }

    public String getName() {
        return name;
    }

    public Collection<CustomBlockDrop> getDrops() {
        return new ArrayList<>(drops);
    }

    public NamedImage getTexture() {
        return texture;
    }

    public void setName(String newName) {
        assertMutable();
        this.name = newName;
    }

    public void setDrops(Collection<CustomBlockDrop> newDrops) {
        assertMutable();
        this.drops = Mutability.createDeepCopy(newDrops, false);
    }

    public void setTexture(NamedImage newTexture) {
        assertMutable();
        this.texture = newTexture;
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("The name is empty");
        if (name.contains(" ")) throw new ValidationException("The name contains spaces");

        if (drops == null) throw new ProgrammingValidationException("No drops");
        for (CustomBlockDrop drop : drops) {
            if (drop == null)
                throw new ProgrammingValidationException("A drop is null");
            drop.validateIndependent();
        }

        if (texture == null) throw new ValidationException("You haven't chosen a texture");
    }

    public void validateComplete(
            SItemSet itemSet
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (itemSet.getBlocks().stream().anyMatch(block -> block.getInternalID() != internalId && block.getName().equals(name))) {
            throw new ValidationException("There exists another block with the same name");
        }

        for (CustomBlockDrop drop : drops) {
            drop.validateComplete(itemSet);
        }

        boolean containsTexture = false;
        for (NamedImage texture : textures) {
            if (texture == this.texture) {
                containsTexture = true;
                break;
            }
        }

        if (!containsTexture) {
            throw new ProgrammingValidationException("The chosen texture is not registered");
        }
    }
}
