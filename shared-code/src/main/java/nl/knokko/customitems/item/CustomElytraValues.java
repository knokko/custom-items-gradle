package nl.knokko.customitems.item;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.elytra.VelocityModifierValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.texture.BaseTextureValues.loadImage;
import static nl.knokko.customitems.texture.BaseTextureValues.saveImage;

public class CustomElytraValues extends CustomArmorValues {

    public static CustomElytraValues load(BitInput input, boolean mutable, ItemSet itemSet) throws UnknownEncodingException {
        CustomElytraValues result = new CustomElytraValues(mutable);
        result.load(input, itemSet);
        return result;
    }

    private Collection<VelocityModifierValues> velocityModifiers;
    private BufferedImage wornElytraTexture;

    public CustomElytraValues(boolean mutable) {
        super(mutable, CustomItemType.ELYTRA);
        this.velocityModifiers = new ArrayList<>();
        this.wornElytraTexture = null;
    }

    public CustomElytraValues(CustomElytraValues toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.velocityModifiers = toCopy.getVelocityModifiers();
        this.wornElytraTexture = toCopy.getWornElytraTexture();
    }

    @Override
    public CustomElytraValues copy(boolean mutable) {
        return new CustomElytraValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof CustomElytraValues) {
            CustomElytraValues otherElytra = (CustomElytraValues) other;
            // Don't test the worn texture because it is Editor-only
            return this.areArmorPropertiesEqual(otherElytra) && this.velocityModifiers.equals(otherElytra.velocityModifiers);
        } else {
            return false;
        }
    }

    @Override
    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte(ItemEncoding.ENCODING_ELYTRA_12);
        output.addByte((byte) 1);
        saveArmorPropertiesNew(output, side);

        output.addInt(velocityModifiers.size());
        for (VelocityModifierValues velocityModifier : velocityModifiers) {
            velocityModifier.save(output);
        }

        if (side == ItemSet.Side.EDITOR) {
            output.addBoolean(wornElytraTexture != null);
            if (wornElytraTexture != null) {
                saveImage(output, wornElytraTexture);
            }
        }
    }

    private void load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("CustomElytra", encoding);

        loadArmorPropertiesNew(input, itemSet);

        int numVelocityModifiers = input.readInt();
        this.velocityModifiers = new ArrayList<>(numVelocityModifiers);
        for (int counter = 0; counter < numVelocityModifiers; counter++) {
            this.velocityModifiers.add(VelocityModifierValues.load(input));
        }

        if (itemSet.getSide() == ItemSet.Side.EDITOR) {
            if (input.readBoolean()) {
                this.wornElytraTexture = loadImage(input, true);
            } else {
                this.wornElytraTexture = null;
            }
        } else {
            this.wornElytraTexture = null;
        }
    }

    public Collection<VelocityModifierValues> getVelocityModifiers() {
        return velocityModifiers;
    }

    public BufferedImage getWornElytraTexture() {
        return wornElytraTexture;
    }

    public void setVelocityModifiers(Collection<VelocityModifierValues> newModifiers) {
        assertMutable();
        Checks.nonNull(newModifiers);
        velocityModifiers = Mutability.createDeepCopy(newModifiers, false);
    }

    public void setWornElytraTexture(BufferedImage newTexture) {
        assertMutable();
        wornElytraTexture = newTexture;
        System.out.println("Set wornElytraTexture to " + wornElytraTexture);
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();
        if (itemType != CustomItemType.ELYTRA) throw new ProgrammingValidationException("Item type must be elytra");
        if (velocityModifiers == null) throw new ProgrammingValidationException("No velocity modifiers");
        for (VelocityModifierValues velocityModifier : velocityModifiers) {
            velocityModifier.validate();
        }
        if (getArmorTextureReference() != null) throw new ProgrammingValidationException("Elytra's have different worn textures");
        if (wornElytraTexture != null) {
            if (wornElytraTexture.getWidth() != 2 * wornElytraTexture.getHeight()) {
                throw new ValidationException(
                        "The width of the worn texture (" + wornElytraTexture.getWidth() + ") must be twice " +
                                "as big as the height (" + wornElytraTexture.getHeight() + ")"
                );
            }
        }
    }
}
