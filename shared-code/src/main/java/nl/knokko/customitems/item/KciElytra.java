package nl.knokko.customitems.item;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.encoding.ItemEncoding;
import nl.knokko.customitems.item.elytra.VelocityModifier;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import static nl.knokko.customitems.texture.KciTexture.loadImage;
import static nl.knokko.customitems.texture.KciTexture.saveImage;

public class KciElytra extends KciArmor {

    public static KciElytra load(BitInput input, boolean mutable, ItemSet itemSet) throws UnknownEncodingException {
        KciElytra result = new KciElytra(mutable);
        result.load(input, itemSet);
        return result;
    }

    private Collection<VelocityModifier> velocityModifiers;
    private BufferedImage wornElytraTexture;

    public KciElytra(boolean mutable) {
        super(mutable, KciItemType.ELYTRA);
        this.velocityModifiers = new ArrayList<>();
        this.wornElytraTexture = null;
    }

    public KciElytra(KciElytra toCopy, boolean mutable) {
        super(toCopy, mutable);
        this.velocityModifiers = toCopy.getVelocityModifiers();
        this.wornElytraTexture = toCopy.getWornElytraTexture();
    }

    @Override
    public KciElytra copy(boolean mutable) {
        return new KciElytra(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof KciElytra) {
            KciElytra otherElytra = (KciElytra) other;
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
        for (VelocityModifier velocityModifier : velocityModifiers) {
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
            this.velocityModifiers.add(VelocityModifier.load(input));
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

    public Collection<VelocityModifier> getVelocityModifiers() {
        return velocityModifiers;
    }

    public BufferedImage getWornElytraTexture() {
        return wornElytraTexture;
    }

    public void setVelocityModifiers(Collection<VelocityModifier> newModifiers) {
        assertMutable();
        Checks.nonNull(newModifiers);
        velocityModifiers = Mutability.createDeepCopy(newModifiers, false);
    }

    public void setWornElytraTexture(BufferedImage newTexture) {
        assertMutable();
        wornElytraTexture = newTexture;
    }

    @Override
    public void validateIndependent() throws ValidationException, ProgrammingValidationException {
        super.validateIndependent();
        if (itemType != KciItemType.ELYTRA) throw new ProgrammingValidationException("Item type must be elytra");
        if (velocityModifiers == null) throw new ProgrammingValidationException("No velocity modifiers");
        for (VelocityModifier velocityModifier : velocityModifiers) {
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
