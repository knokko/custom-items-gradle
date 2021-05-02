package nl.knokko.customitems.block;

import nl.knokko.customitems.block.drop.CustomBlockDrop;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;

public class CustomBlockValues {

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
        this(mutable);

        this.name = toCopy.getName();
        this.drops = toCopy.getDrops();
        this.texture = toCopy.getTexture();
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
        this.drops = new ArrayList<>(newDrops);
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
        // TODO Validate the individual drops

        if (texture == null) throw new ValidationException("You haven't chosen a texture");
    }

    public void validateComplete(
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
