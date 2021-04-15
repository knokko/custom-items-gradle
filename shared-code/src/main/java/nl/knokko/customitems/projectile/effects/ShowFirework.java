package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ShowFirework extends ProjectileEffect {

    public static ShowFirework load1(BitInput input) {
        ShowFirework result = new ShowFirework();
        result.power = input.readInt();

        int numEffects = input.readInt();
        List<Effect> effects = new ArrayList<>(numEffects);
        for (int counter = 0; counter < numEffects; counter++) {
            effects.add(Effect.load1(input));
        }

        result.effects = effects;
        return result;
    }

    public int power;

    public List<Effect> effects;

    public ShowFirework() {
        this.power = 1;
        this.effects = new ArrayList<>(1);
        this.effects.add(new Effect());
    }

    @Override
    public void toBits(BitOutput output) {
        output.addByte(ENCODING_FIREWORK_1);
        output.addInt(power);
        output.addInt(effects.size());
        for (Effect effect : effects) {
            effect.save1(output);
        }
    }

    @Override
    public String validate() {
        if (power < 1) return "The power must be at least 1";
        if (effects.isEmpty()) return "You need to select at least 1 effect";
        return null;
    }

    @Override
    public String toString() {
        StringBuilder typesStringBuilder = new StringBuilder();
        for (Effect effect : effects) {
            typesStringBuilder.append(effect.type).append(", ");
        }

        String typesString = typesStringBuilder.substring(0, typesStringBuilder.length() - 2);
        return typesString + " (" + power + ")";
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
