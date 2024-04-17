package nl.knokko.customitems.block;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.sound.KciSound;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;

public class BlockSounds extends ModelValues {

    public static BlockSounds load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("BlockSounds", encoding);

        BlockSounds sounds = new BlockSounds(false);
        sounds.leftClickSound = loadSound(input, itemSet);
        sounds.rightClickSound = loadSound(input, itemSet);
        sounds.breakSound = loadSound(input, itemSet);
        sounds.stepSound = loadSound(input, itemSet);
        return sounds;
    }

    private static KciSound loadSound(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        if (input.readBoolean()) return KciSound.load(input, itemSet);
        else return null;
    }

    private KciSound leftClickSound, rightClickSound, breakSound, stepSound;

    public BlockSounds(boolean mutable) {
        super(mutable);
        this.leftClickSound = null;
        this.rightClickSound = null;
        this.breakSound = null;
        this.stepSound = null;
    }

    public BlockSounds(BlockSounds toCopy, boolean mutable) {
        super(mutable);
        this.leftClickSound = toCopy.getLeftClickSound();
        this.rightClickSound = toCopy.getRightClickSound();
        this.breakSound = toCopy.getBreakSound();
        this.stepSound = toCopy.getStepSound();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        saveSound(output, leftClickSound);
        saveSound(output, rightClickSound);
        saveSound(output, breakSound);
        saveSound(output, stepSound);
    }

    private void saveSound(BitOutput output, KciSound sound) {
        output.addBoolean(sound != null);
        if (sound != null) sound.save(output);
    }

    @Override
    public BlockSounds copy(boolean mutable) {
        return new BlockSounds(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BlockSounds) {
            BlockSounds otherSounds = (BlockSounds) other;
            return Objects.equals(this.leftClickSound, otherSounds.leftClickSound) &&
                    Objects.equals(this.rightClickSound, otherSounds.rightClickSound) &&
                    Objects.equals(this.breakSound, otherSounds.breakSound) &&
                    Objects.equals(this.stepSound, otherSounds.stepSound);
        } else return false;
    }

    @Override
    public String toString() {
        return "BlockSounds(" + leftClickSound + ", " + rightClickSound + ", " + breakSound + ", " + stepSound + ")";
    }

    public KciSound getLeftClickSound() {
        return leftClickSound;
    }

    public KciSound getRightClickSound() {
        return rightClickSound;
    }

    public KciSound getBreakSound() {
        return breakSound;
    }

    public KciSound getStepSound() {
        return stepSound;
    }

    public void setLeftClickSound(KciSound leftClickSound) {
        assertMutable();
        this.leftClickSound = leftClickSound != null ? leftClickSound.copy(false) : null;
    }

    public void setRightClickSound(KciSound rightClickSound) {
        assertMutable();
        this.rightClickSound = rightClickSound != null ? rightClickSound.copy(false) : null;
    }

    public void setBreakSound(KciSound breakSound) {
        assertMutable();
        this.breakSound = breakSound != null ? breakSound.copy(false) : null;
    }

    public void setStepSound(KciSound stepSound) {
        assertMutable();
        this.stepSound = stepSound != null ? stepSound.copy(false) : null;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (leftClickSound != null) Validation.scope("Left-click", leftClickSound::validate, itemSet);
        if (rightClickSound != null) Validation.scope("Right-click", rightClickSound::validate, itemSet);
        if (breakSound != null) Validation.scope("Break", breakSound::validate, itemSet);
        if (stepSound != null) Validation.scope("Step", stepSound::validate, itemSet);
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (leftClickSound != null) Validation.scope("Left-click", leftClickSound::validateExportVersion, version);
        if (rightClickSound != null) Validation.scope("Right-click", rightClickSound::validateExportVersion, version);
        if (breakSound != null) Validation.scope("Break", breakSound::validateExportVersion, version);
        if (stepSound != null) Validation.scope("Step", stepSound::validateExportVersion, version);
    }
}
