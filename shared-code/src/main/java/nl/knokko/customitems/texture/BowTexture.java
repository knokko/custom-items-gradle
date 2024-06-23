package nl.knokko.customitems.texture;

import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class BowTexture extends KciTexture {

    protected List<BowTextureEntry> pullTextures;

    public BowTexture(boolean mutable) {
        super(mutable);

        this.pullTextures = new ArrayList<>(3);
        this.pullTextures.add(BowTextureEntry.createQuick(null, 0.0).copy(false));
        this.pullTextures.add(BowTextureEntry.createQuick(null, 0.65).copy(false));
        this.pullTextures.add(BowTextureEntry.createQuick(null, 0.9).copy(false));
    }

    public BowTexture(BowTexture toCopy, boolean mutable) {
        super(toCopy, mutable);

        this.pullTextures = toCopy.getPullTextures();
    }

    protected void loadBow1(BitInput input, boolean expectCompressed) {
        loadBase1(input, expectCompressed);
        loadEntries1(input, expectCompressed);
    }

    protected void loadEntries1(BitInput input, boolean expectCompressed) {
        int numEntries = input.readInt();
        this.pullTextures = new ArrayList<>(numEntries);
        for (int counter = 0; counter < numEntries; counter++) {
            pullTextures.add(BowTextureEntry.load1(input, expectCompressed, false));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == BowTexture.class) {
            BowTexture otherTexture = (BowTexture) other;
            return this.name.equals(otherTexture.name) && areImagesEqual(this.image, otherTexture.image)
                    && this.pullTextures.equals(otherTexture.pullTextures);
        } else {
            return false;
        }
    }

    @Override
    public BowTexture copy(boolean mutable) {
        return new BowTexture(this, mutable);
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_BOW_1);
        saveBow1(output);
    }

    protected void saveBow1(BitOutput output) {
        saveBase1(output);
        saveEntries1(output);
    }

    protected void saveEntries1(BitOutput output) {
        output.addInt(pullTextures.size());
        for (BowTextureEntry pullTexture : pullTextures) {
            pullTexture.save(output);
        }
    }

    public List<BowTextureEntry> getPullTextures() {
        return new ArrayList<>(pullTextures);
    }

    public void setPullTextures(List<BowTextureEntry> newPullTextures) {
        assertMutable();
        Checks.nonNull(newPullTextures);
        this.pullTextures = Mutability.createDeepCopy(newPullTextures, false);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();
        if (pullTextures == null) throw new ProgrammingValidationException("No pull textures");
        Double previousPull = null;
        for (BowTextureEntry entry : pullTextures) {
            if (entry == null) throw new ProgrammingValidationException("Missing a pull texture");
            Validation.scope("Pull texture", entry::validate);
            if (previousPull != null && entry.getPull() <= previousPull) {
                throw new ValidationException("Pull values must be sorted in ascending order from top to bottom");
            }
            previousPull = entry.getPull();
        }
    }
}
