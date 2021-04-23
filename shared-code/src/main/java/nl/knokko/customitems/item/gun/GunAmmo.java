package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class GunAmmo {

    static final byte ENCODING_DIRECT_1 = 0;
    static final byte ENCODING_INDIRECT_1 = 1;

    public static GunAmmo load(
            BitInput input, ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient
    ) throws UnknownEncodingException {
        byte encoding = input.readByte();

        switch (encoding) {
            case ENCODING_DIRECT_1: return DirectGunAmmo.load1(input, loadIngredient);
            case ENCODING_INDIRECT_1: return IndirectGunAmmo.load1(input, loadIngredient);
            default: throw new UnknownEncodingException("GunAmmo", encoding);
        }
    }

    public abstract void save(BitOutput output, Consumer<SCIngredient> saveIngredient);

    public abstract String validate(Predicate<SCIngredient> allowIngredient);
}
