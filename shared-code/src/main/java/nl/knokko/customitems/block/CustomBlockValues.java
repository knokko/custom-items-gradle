package nl.knokko.customitems.block;

import nl.knokko.customitems.block.drop.CustomBlockDropValues;
import nl.knokko.customitems.block.miningspeed.MiningSpeedValues;
import nl.knokko.customitems.block.model.BlockModel;
import nl.knokko.customitems.block.model.SimpleBlockModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class CustomBlockValues extends ModelValues {

    public static CustomBlockValues load(
            BitInput input, ItemSet itemSet, int internalId
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        CustomBlockValues result = new CustomBlockValues(false);
        result.internalId = internalId;
        result.loadNew(input, itemSet, encoding);

        return result;
    }

    private int internalId;

    private String name;

    private Collection<CustomBlockDropValues> drops;
    private MiningSpeedValues miningSpeed;

    // Only use this in the Editor; Keep it null on the plug-in
    private BlockModel model;

    public CustomBlockValues(boolean mutable) {
        super(mutable);

        this.internalId = 0;
        this.name = "";
        this.drops = new ArrayList<>(0);
        this.miningSpeed = new MiningSpeedValues(false);
        this.model = null;
    }

    public CustomBlockValues(CustomBlockValues toCopy, boolean mutable) {
        super(mutable);

        this.internalId = toCopy.getInternalID();
        this.name = toCopy.getName();
        this.drops = toCopy.getDrops();
        this.miningSpeed = toCopy.getMiningSpeed();
        this.model = toCopy.getModel();
    }

    @Override
    public CustomBlockValues copy(boolean mutable) {
        return new CustomBlockValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomBlockValues) {
            CustomBlockValues otherBlock = (CustomBlockValues) other;
            return otherBlock.internalId == this.internalId && otherBlock.name.equals(this.name)
                    && otherBlock.drops.equals(this.drops) && otherBlock.miningSpeed.equals(this.miningSpeed);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.name + "[" + this.internalId + "]";
    }

    @Override
    public int hashCode() {
        return this.internalId;
    }

    private void loadDrops1(
            BitInput input, ItemSet itemSet
    ) throws UnknownEncodingException {
        int numDrops = input.readInt();
        this.drops = new ArrayList<>(numDrops);
        for (int counter = 0; counter < numDrops; counter++) {
            this.drops.add(CustomBlockDropValues.load(input, itemSet, false));
        }
    }

    private void loadNew(
            BitInput input, ItemSet itemSet, byte encoding
    ) throws UnknownEncodingException {
        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomBlock", encoding);

        this.name = input.readString();
        this.loadDrops1(input, itemSet);
        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            if (encoding == 1) this.model = new SimpleBlockModel(itemSet.getTextureReference(input.readString()));
            else this.model = BlockModel.load(input, itemSet);
        } else {
            this.model = null;
            if (encoding == 1) input.readString();
        }
        if (encoding >= 2) {
            this.miningSpeed = MiningSpeedValues.load(input, itemSet);
        } else {
            this.miningSpeed = new MiningSpeedValues(false);
        }
    }

    public void save(BitOutput output, ItemSet.Side targetSide) {
        output.addByte((byte) 2);

        output.addString(name);
        saveDrops1(output);
        if (targetSide == ItemSet.Side.EDITOR) model.save(output);
        miningSpeed.save(output);
    }

    private void saveDrops1(BitOutput output) {
        output.addInt(drops.size());
        for (CustomBlockDropValues drop : drops) {
            drop.save(output);
        }
    }

    public int getInternalID() {
        return internalId;
    }

    public String getName() {
        return name;
    }

    public Collection<CustomBlockDropValues> getDrops() {
        return new ArrayList<>(drops);
    }

    public BlockModel getModel() {
        return model;
    }

    public MiningSpeedValues getMiningSpeed() {
        return miningSpeed;
    }

    public void setInternalId(int newId) {
        assertMutable();
        this.internalId = newId;
    }

    public void setName(String newName) {
        assertMutable();
        this.name = newName;
    }

    public void setDrops(Collection<CustomBlockDropValues> newDrops) {
        assertMutable();
        this.drops = Mutability.createDeepCopy(newDrops, false);
    }

    public void setMiningSpeed(MiningSpeedValues miningSpeed) {
        assertMutable();
        this.miningSpeed = miningSpeed.copy(false);
    }

    public void setModel(BlockModel newModel) {
        assertMutable();
        this.model = Objects.requireNonNull(newModel);
    }

    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        if (!MushroomBlockMapping.isValidId(internalId)) throw new ProgrammingValidationException("Invalid id " + internalId);

        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("The name is empty");
        if (name.contains(" ")) throw new ValidationException("The name contains spaces");

        if (drops == null) throw new ProgrammingValidationException("No drops");
        for (CustomBlockDropValues drop : drops) {
            if (drop == null) throw new ProgrammingValidationException("Missing a drop");
            Validation.scope("Drop", drop::validateIndependent);
        }

        if (miningSpeed == null) throw new ProgrammingValidationException("No mining speed");

        if (model == null) throw new ValidationException("You haven't chosen a texture");
    }

    public void validateComplete(
            ItemSet itemSet, Integer oldInternalId
    ) throws ValidationException, ProgrammingValidationException {
        validateIndependent();

        if (oldInternalId != null && internalId != oldInternalId) {
            throw new ProgrammingValidationException("Can't change internal id");
        }
        if (oldInternalId == null && itemSet.getBlock(internalId).isPresent()) {
            throw new ProgrammingValidationException("Block with id " + internalId + " already exists");
        }
        if (itemSet.getBlocks().stream().anyMatch(block -> block.getInternalID() != internalId && block.getName().equals(name))) {
            throw new ValidationException("Block with name " + name + " already exists");
        }

        for (CustomBlockDropValues drop : drops) {
            if (drop == null) throw new ProgrammingValidationException("Missing a drop");
            Validation.scope("Drop", () -> drop.validateComplete(itemSet));
        }

        Validation.scope("Mining speed", miningSpeed::validate, itemSet);

        Validation.scope("Model", model::validate, itemSet);
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (CustomBlockDropValues drop : drops) {
            Validation.scope("Drops", () -> drop.validateExportVersion(version));
        }
    }
}
