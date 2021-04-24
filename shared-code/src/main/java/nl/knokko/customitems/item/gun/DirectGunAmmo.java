package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DirectGunAmmo extends GunAmmo {

    static DirectGunAmmo load1(
            BitInput input, ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient
    ) throws UnknownEncodingException {
        return new DirectGunAmmo(loadIngredient.get(), input.readInt());
    }

    public final SCIngredient ammoItem;
    public final int cooldown;

    public DirectGunAmmo(SCIngredient ammoItem, int cooldown) {
        this.ammoItem = ammoItem;
        this.cooldown = cooldown;
    }

    @Override
    public void save(BitOutput output, Consumer<SCIngredient> saveIngredient) {
        output.addByte(ENCODING_DIRECT_1);
        saveIngredient.accept(ammoItem);
        output.addInt(cooldown);
    }

    @Override
    public String validate(Predicate<SCIngredient> allowIngredient) {
        // Both null and NoIngredient are forbidden. (Using NoIngredient would turn it into a wand)
        if (ammoItem == null) return "You must choose an ammo item";
        if (cooldown <= 0) return "The cooldown must be positive";

        if (!allowIngredient.test(ammoItem))
            return "Only vanilla items and simple custom items are allowed as ammo";

        return null;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
