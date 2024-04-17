package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PEShowFireworks extends ProjectileEffect {

    static PEShowFireworks load(BitInput input, byte encoding) throws UnknownEncodingException {
        PEShowFireworks result = new PEShowFireworks(false);

        if (encoding == ENCODING_FIREWORK_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("FireworkProjectileEffect", encoding);
        }

        return result;
    }

    public static PEShowFireworks createQuick(List<EffectValues> effects) {
        PEShowFireworks result = new PEShowFireworks(true);
        result.setEffects(effects);
        return result;
    }

    private List<EffectValues> effects;

    public PEShowFireworks(boolean mutable) {
        super(mutable);
        this.effects = new ArrayList<>(1);
        this.effects.add(new EffectValues(false));
    }

    public PEShowFireworks(PEShowFireworks toCopy, boolean mutable) {
        super(mutable);
        this.effects = toCopy.getEffects();
    }

    @Override
    public String toString() {
        // Including the actual effects would make this String too long
        return "ShowFireworksEffect";
    }

    private void load1(BitInput input) {
        int numEffects = input.readInt();
        this.effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            EffectValues loadedEffect = new EffectValues(false);
            loadedEffect.load1(input);
            this.effects.add(loadedEffect);
        }
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_FIREWORK_1);
        output.addInt(effects.size());
        for (EffectValues effect : effects) {
            effect.save1(output);
        }
    }

    @Override
    public PEShowFireworks copy(boolean mutable) {
        return new PEShowFireworks(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == PEShowFireworks.class) {
            PEShowFireworks otherEffect = (PEShowFireworks) other;
            return this.effects.equals(otherEffect.effects);
        } else {
            return false;
        }
    }

    public List<EffectValues> getEffects() {
        return new ArrayList<>(effects);
    }

    public void setEffects(List<EffectValues> effects) {
        assertMutable();
        Checks.notNull(effects);
        this.effects = Mutability.createDeepCopy(effects, false);
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (effects == null) throw new ProgrammingValidationException("No effects");
        if (effects.isEmpty()) throw new ValidationException("You need at least 1 effect");
        for (EffectValues effect : effects) {
            if (effect == null) throw new ProgrammingValidationException("Missing an effect");
            Validation.scope("Effect", effect::validate);
        }
    }

    public static class EffectValues extends ModelValues {

        public static EffectValues createQuick(
                boolean flicker, boolean trail, EffectType type, List<Color> colors, List<Color> fadeColors
        ) {
            EffectValues result = new EffectValues(true);
            result.setFlicker(flicker);
            result.setTrail(trail);
            result.setType(type);
            result.setColors(colors);
            result.setFadeColors(fadeColors);
            return result;
        }

        private boolean flicker, trail;
        private EffectType type;
        private List<Color> colors, fadeColors;

        public EffectValues(boolean mutable) {
            super(mutable);
            this.flicker = false;
            this.trail = false;
            this.type = EffectType.BALL;
            this.colors = new ArrayList<>(0);
            this.fadeColors = new ArrayList<>(0);
        }

        public EffectValues(EffectValues toCopy, boolean mutable) {
            super(mutable);
            this.flicker = toCopy.hasFlicker();
            this.trail = toCopy.hasTrail();
            this.type = toCopy.getType();
            this.colors = toCopy.getColors();
            this.fadeColors = toCopy.getFadeColors();
        }

        private void load1(BitInput input) {
            this.flicker = input.readBoolean();
            this.trail = input.readBoolean();
            this.type = EffectType.valueOf(input.readString());

            int numColors = input.readInt();
            this.colors = new ArrayList<>(numColors);
            for (int counter = 0; counter < numColors; counter++) {
                this.colors.add(new Color(input.readInt(), true));
            }

            int numFadeColors = input.readInt();
            this.fadeColors = new ArrayList<>(numFadeColors);
            for (int counter = 0; counter < numFadeColors; counter++) {
                this.fadeColors.add(new Color(input.readInt(), true));
            }
        }

        private void save1(BitOutput output) {
            output.addBooleans(flicker, trail);
            output.addString(type.name());
            output.addInt(colors.size());
            for (Color color : colors) {
                output.addInt(color.getRGB());
            }
            output.addInt(fadeColors.size());
            for (Color fadeColor : fadeColors) {
                output.addInt(fadeColor.getRGB());
            }
        }

        @Override
        public EffectValues copy(boolean mutable) {
            return new EffectValues(this, mutable);
        }

        @Override
        public boolean equals(Object other) {
            if (other.getClass() == EffectValues.class) {
                EffectValues otherEffect = (EffectValues) other;
                return this.flicker == otherEffect.flicker && this.trail == otherEffect.trail && this.type == otherEffect.type
                        && this.colors.equals(otherEffect.colors) && this.fadeColors.equals(otherEffect.fadeColors);
            } else {
                return false;
            }
        }

        public boolean hasFlicker() {
            return flicker;
        }

        public boolean hasTrail() {
            return trail;
        }

        public EffectType getType() {
            return type;
        }

        public List<Color> getColors() {
            return new ArrayList<>(colors);
        }

        public List<Color> getFadeColors() {
            return new ArrayList<>(fadeColors);
        }

        public void setFlicker(boolean shouldFlicker) {
            assertMutable();
            this.flicker = shouldFlicker;
        }

        public void setTrail(boolean shouldHaveTrail) {
            assertMutable();
            this.trail = shouldHaveTrail;
        }

        public void setType(EffectType newType) {
            assertMutable();
            Checks.notNull(newType);
            this.type = newType;
        }

        public void setColors(List<Color> newColors) {
            assertMutable();
            Checks.nonNull(newColors);
            this.colors = new ArrayList<>(newColors);
        }

        public void setFadeColors(List<Color> newFadeColors) {
            assertMutable();
            Checks.nonNull(newFadeColors);
            this.fadeColors = new ArrayList<>(newFadeColors);
        }

        public void validate() throws ValidationException, ProgrammingValidationException {
            if (type == null) throw new ProgrammingValidationException("No effect type");
            if (colors == null) throw new ProgrammingValidationException("No colors");
            if (colors.isEmpty()) throw new ValidationException("You must pick at least 1 color");
            for (Color color : colors) {
                if (color == null) throw new ProgrammingValidationException("Missing a color");
            }
            if (fadeColors == null) throw new ProgrammingValidationException("No fade colors");
            for (Color fadeColor : fadeColors) {
                if (fadeColor == null) throw new ProgrammingValidationException("Missing a fade color");
            }
        }
    }

    public enum EffectType {
        BALL,
        BALL_LARGE,
        STAR,
        BURST,
        CREEPER
    }
}
