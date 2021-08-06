package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ShowFirework extends ProjectileEffect {

    public static ShowFirework load1(BitInput input) {
        int numEffects = input.readInt();
        List<Effect> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            effects.add(Effect.load1(input));
        }

        return new ShowFirework(effects);
    }

    public List<Effect> effects;

    public ShowFirework(List<Effect> effects) {
        this.effects = effects;
    }

    @Override
    public void toBits(BitOutput output) {
        output.addByte(ENCODING_FIREWORK_1);
        output.addInt(effects.size());
        for (Effect effect : effects) {
            effect.save1(output);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ShowFirework) {
            ShowFirework firework = (ShowFirework) other;
            return effects.equals(firework.effects);
        } else {
            return false;
        }
    }

    @Override
    public String validate() {
        if (effects.isEmpty()) return "You need to select at least 1 effect";

        for (Effect effect : effects) {
            String effectError = effect.validate();
            if (effectError != null) {
                return "Effect error: " + effectError;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder typesStringBuilder = new StringBuilder();
        for (Effect effect : effects) {
            typesStringBuilder.append(effect.type).append(", ");
        }

        String typesString = typesStringBuilder.substring(0, typesStringBuilder.length() - 2);
        return typesString;
    }

    public static class Effect {

        public static Effect load1(BitInput input) {
            Effect result = new Effect();
            result.flicker = input.readBoolean();
            result.trail = input.readBoolean();
            result.type = EffectType.valueOf(input.readString());

            int numColors = input.readInt();
            for (int counter = 0; counter < numColors; counter++) {
                result.colors.add(new Color(input.readInt(), true));
            }

            int numFadeColors = input.readInt();
            for (int counter = 0; counter < numFadeColors; counter++) {
                result.fadeColors.add(new Color(input.readInt(), true));
            }

            return result;
        }

        public boolean flicker;
        public boolean trail;

        public EffectType type;

        public final List<Color> colors;
        public final List<Color> fadeColors;

        public Effect() {
            this.flicker = false;
            this.trail = false;
            this.type = EffectType.BALL;
            this.colors = new ArrayList<>(1);
            this.fadeColors = new ArrayList<>(0);
        }

        public Effect(
                boolean flicker, boolean trail, EffectType type, List<Color> colors, List<Color> fadeColors
        ) {
            this.flicker = flicker;
            this.trail = trail;
            this.type = type;
            this.colors = colors;
            this.fadeColors = fadeColors;
        }

        // Copy constructor
        public Effect(Effect toClone) {
            this.flicker = toClone.flicker;
            this.trail = toClone.trail;
            this.type = toClone.type;
            this.colors = new ArrayList<>(toClone.colors);
            this.fadeColors = new ArrayList<>(toClone.fadeColors);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Effect) {
                Effect effect = (Effect) other;
                return flicker == effect.flicker && trail == effect.trail && type == effect.type
                        && colors.equals(effect.colors) && fadeColors.equals(effect.fadeColors);
            } else {
                return false;
            }
        }

        public void save1(BitOutput output) {
            output.addBoolean(flicker);
            output.addBoolean(trail);
            output.addString(type.name());

            output.addInt(colors.size());
            for (Color color : colors)
                output.addInt(color.getRGB());

            output.addInt(fadeColors.size());
            for (Color color : fadeColors)
                output.addInt(color.getRGB());
        }

        public String validate() {
            if (type == null) return "You must select a type";
            if (colors.isEmpty()) return "You must pick at least 1 color";
            return null;
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
