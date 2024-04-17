package nl.knokko.customitems.texture;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.*;

import static nl.knokko.customitems.MCVersions.VERSION1_17;

public class FancyPantsTexture extends ModelValues {

    public static FancyPantsTexture load(BitInput input, ItemSet.Side side) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("FancyPantsArmorTexture", encoding);

        FancyPantsTexture texture = new FancyPantsTexture(false);
        texture.id = new UUID(input.readLong(), input.readLong());
        texture.name = input.readString();
        texture.rgb = input.readInt();
        texture.animationSpeed = input.readInt();
        texture.interpolateAnimations = input.readBoolean();
        texture.emissivity = Emissivity.valueOf(input.readString());
        texture.leatherTint = input.readBoolean();

        if (side == ItemSet.Side.EDITOR) {
            int numFrames = input.readInt();
            List<FancyPantsFrame> frames = new ArrayList<>(numFrames);
            for (int counter = 0; counter < numFrames; counter++) {
                frames.add(FancyPantsFrame.load(input));
            }
            texture.frames = Collections.unmodifiableList(frames);
        }

        return texture;
    }

    private UUID id;
    private String name;
    private int rgb;

    private int animationSpeed;
    private boolean interpolateAnimations;
    private Emissivity emissivity;
    private boolean leatherTint;

    private List<FancyPantsFrame> frames;

    public FancyPantsTexture(boolean mutable) {
        super(mutable);
        this.id = UUID.randomUUID();
        this.name = "";
        this.rgb = 0;
        this.animationSpeed = 24;
        this.interpolateAnimations = false;
        this.emissivity = Emissivity.NONE;
        this.leatherTint = false;
        this.frames = Collections.emptyList();
    }

    public FancyPantsTexture(FancyPantsTexture toCopy, boolean mutable) {
        super(mutable);
        this.id = toCopy.getId();
        this.name = toCopy.getName();
        this.rgb = toCopy.getRgb();
        this.animationSpeed = toCopy.getAnimationSpeed();
        this.interpolateAnimations = toCopy.shouldInterpolateAnimations();
        this.emissivity = toCopy.getEmissivity();
        this.leatherTint = toCopy.usesLeatherTint();
        this.frames = toCopy.getFrames();
    }

    public void save(BitOutput output, ItemSet.Side side) {
        output.addByte((byte) 1);

        output.addLong(id.getMostSignificantBits());
        output.addLong(id.getLeastSignificantBits());
        output.addString(name);
        output.addInt(rgb);
        output.addInt(animationSpeed);
        output.addBoolean(interpolateAnimations);
        output.addString(emissivity.name());
        output.addBoolean(leatherTint);
        if (side == ItemSet.Side.EDITOR) {
            output.addInt(frames.size());
            for (FancyPantsFrame frame : frames) frame.save(output);
        }
    }

    @Override
    public FancyPantsTexture copy(boolean mutable) {
        return new FancyPantsTexture(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FancyPantsTexture) {
            FancyPantsTexture otherFp = (FancyPantsTexture) other;
            return this.id.equals(otherFp.id) && this.name.equals(otherFp.name) && this.rgb == otherFp.rgb
                    && this.animationSpeed == otherFp.animationSpeed
                    && this.interpolateAnimations == otherFp.interpolateAnimations
                    && this.emissivity == otherFp.emissivity && this.leatherTint == otherFp.leatherTint
                    && this.frames.equals(otherFp.frames);
        } else return false;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRgb() {
        return rgb;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    public boolean shouldInterpolateAnimations() {
        return interpolateAnimations;
    }

    public Emissivity getEmissivity() {
        return emissivity;
    }

    public boolean usesLeatherTint() {
        return leatherTint;
    }

    public List<FancyPantsFrame> getFrames() {
        return frames;
    }

    public void setName(String name) {
        assertMutable();
        this.name = Objects.requireNonNull(name);
    }

    public void setRgb(int rgb) {
        assertMutable();
        this.rgb = rgb;
    }

    public void setAnimationSpeed(int animationSpeed) {
        assertMutable();
        this.animationSpeed = animationSpeed;
    }

    public void setInterpolateAnimations(boolean interpolateAnimations) {
        assertMutable();
        this.interpolateAnimations = interpolateAnimations;
    }

    public void setEmissivity(Emissivity emissivity) {
        assertMutable();
        this.emissivity = Objects.requireNonNull(emissivity);
    }

    public void setLeatherTint(boolean leatherTint) {
        assertMutable();
        this.leatherTint = leatherTint;
    }

    public void setFrames(List<FancyPantsFrame> frames) {
        assertMutable();
        this.frames = Mutability.createDeepCopy(frames, false);
    }

    public void validate(ItemSet itemSet, UUID oldID) throws ValidationException, ProgrammingValidationException {
        if (id == null) throw new ProgrammingValidationException("No ID");
        if (oldID == null && itemSet.fancyPants.get(id).isPresent()) {
            throw new ProgrammingValidationException("Another FP armor texture already has this ID");
        }
        if (oldID != null && !oldID.equals(id)) throw new ProgrammingValidationException("ID is immutable");

        Validation.safeName(name);
        for (FancyPantsTexture otherTexture : itemSet.fancyPants) {
            if (!otherTexture.getId().equals(id) && otherTexture.getName().equals(name)) {
                throw new ValidationException("Another FP armor texture already has this name");
            }
        }

        if (emissivity == null) throw new ProgrammingValidationException("No emissivity");

        int alpha = (rgb >> 24) & 0xFF;
        if (alpha != 0) throw new ValidationException("Alpha component of RGB must be 0");

        if (frames == null) throw new ProgrammingValidationException("No frames");
        if (frames.isEmpty()) throw new ValidationException("You need at least 1 frame");
        if (frames.size() > 255) throw new ValidationException("You can have at most 255 frames");
        for (FancyPantsFrame frame : frames) {
            Validation.scope("Frames", () -> frame.validate(emissivity));
        }

        if (frames.size() > 1) {
            if (animationSpeed <= 0) throw new ValidationException("Animation speed must be a positive integer");
            if (animationSpeed > 255) throw new ValidationException("Animation speed can be at most 255");
        }
    }

    public void validateExportVersion(int version) throws ValidationException {
        if (version < VERSION1_17) {
            throw new ValidationException("FancyPants textures are only supported in MC 1.17 and later");
        }
    }

    public enum Emissivity {
        // Note: the order of the enum constants is important because their ordinal is used to generate the 'meta pixels'
        NONE,
        PARTIAL,
        FULL
    }
}
