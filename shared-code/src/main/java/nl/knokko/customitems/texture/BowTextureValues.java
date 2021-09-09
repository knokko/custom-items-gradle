package nl.knokko.customitems.texture;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.ArrayList;
import java.util.List;

public class BowTextureValues extends BaseTextureValues {

    protected List<BowTextureEntry> pullTextures;

    public BowTextureValues(boolean mutable) {
        super(mutable);
    }

    public BowTextureValues(BowTextureValues toCopy, boolean mutable) {
        super(toCopy, mutable);
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
    public BowTextureValues copy(boolean mutable) {
        return new BowTextureValues(this, mutable);
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
}