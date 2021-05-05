package nl.knokko.customitems.block;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.item.CustomItem;
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

public class CustomBlockValues {

    private static final byte ENCODING_1 = 1;

    public static CustomBlockValues load(
            BitInput input, Function<String, CustomItem> getItemByName,
            ExceptionSupplier<Object, UnknownEncodingException> loadResult,
            // loadTexture will simply return null on the plug-in side
            Function<String, NamedImage> loadTexture, boolean mutable
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        CustomBlockValues result = new CustomBlockValues(mutable);
        if (encoding == ENCODING_1) {
            result.load1(input, getItemByName, loadResult, loadTexture);
        } else {
            throw new UnknownEncodingException("CustomBlockValues", encoding);
        }

        return result;
    }

    private String name;

    private Collection<CustomBlockDrop> drops;

    // Only use this in the Editor; Keep it null on the plug-in
    private NamedImage texture;

    private final boolean mutable;

    public CustomBlockValues(boolean mutable) {
        this.mutable = mutable;

        this.name = "";
        this.drops = new ArrayList<>(0);
        this.texture = null;
    }

    public CustomBlockValues(CustomBlockValues toCopy, boolean mutable) {
        this.mutable = mutable;

        this.name = toCopy.getName();
        this.drops = toCopy.getDrops();
        this.texture = toCopy.getTexture();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomBlockValues) {
            CustomBlockValues otherBlock = (CustomBlockValues) other;
            return otherBlock.name.equals(this.name) && otherBlock.drops.equals(this.drops) &&
                    otherBlock.texture.getName().equals(this.texture.getName());
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
            output.addString(texture.getName());
        }
    }

    private void save1(BitOutput output, Consumer<Object> saveResult) {
        output.addString(name);
        saveDrops1(output, saveResult);
        saveTexture1(output);
    }

    public boolean isMutable() {
        return mutable;
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

    private void assertMutable() {
        if (!mutable) {
            throw new UnsupportedOperationException("Attempting to mutate immutable custom block values");
        }
    }

    public void setName(String newName) {
        assertMutable();
        this.name = newName;
    }

    public void setDrops(Collection<CustomBlockDrop> newDrops) {
        assertMutable();
        this.drops = new ArrayList<>(newDrops.size());
        for (CustomBlockDrop newDrop : newDrops) {
            this.drops.add(new CustomBlockDrop(newDrop, false));
        }
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
            Iterable<? extends CustomItem> customItems,
            Iterable<CustomBlock> blocks,
            CustomBlock toIgnore,
            Iterable<NamedImage> textures
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        for (CustomBlock block : blocks) {
            if (block != toIgnore && block.getValues().getName().equals(this.name)) {
                throw new ValidationException("There exists another block with the same name");
            }
        }

        for (CustomBlockDrop drop : drops) {
            drop.validateComplete(customItems);
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
