package nl.knokko.customitems.item.gun;

import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.sound.CISound;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class IndirectGunAmmo extends GunAmmo {

    static IndirectGunAmmo load1(
            BitInput input, ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient
    ) throws UnknownEncodingException {

        SCIngredient ingredient = loadIngredient.get();
        int cooldown = input.readInt();
        int storedAmmo = input.readInt();
        int reloadTime = input.readInt();

        CISound startReloadSound = null;
        if (input.readBoolean()) {
            startReloadSound = CISound.valueOf(input.readString());
        }

        CISound finishReloadSound = null;
        if (input.readBoolean()) {
            finishReloadSound = CISound.valueOf(input.readString());
        }

        return new IndirectGunAmmo(ingredient, cooldown, storedAmmo, reloadTime, startReloadSound, finishReloadSound);
    }

    public final SCIngredient reloadItem;
    public final int cooldown;
    public final int storedAmmo;
    public final int reloadTime;

    public final CISound startReloadSound;
    public final CISound finishReloadSound;

    public IndirectGunAmmo(
            SCIngredient reloadItem, int cooldown, int storedAmmo, int reloadTime,
            CISound startReloadSound, CISound finishReloadSound
    ) {
        this.reloadItem = reloadItem;
        this.cooldown = cooldown;
        this.storedAmmo = storedAmmo;
        this.reloadTime = reloadTime;
        this.startReloadSound = startReloadSound;
        this.finishReloadSound = finishReloadSound;
    }

    @Override
    public void save(BitOutput output, Consumer<SCIngredient> saveIngredient) {
        output.addByte(ENCODING_INDIRECT_1);
        saveIngredient.accept(reloadItem);
        output.addInt(cooldown);
        output.addInt(storedAmmo);
        output.addInt(reloadTime);

        output.addBoolean(startReloadSound != null);
        if (startReloadSound != null) {
            output.addString(startReloadSound.name());
        }

        output.addBoolean(finishReloadSound != null);
        if (finishReloadSound != null) {
            output.addString(finishReloadSound.name());
        }
    }

    @Override
    public String validate(Predicate<SCIngredient> allowIngredient) {
        // Note: null is forbidden, but NoIngredient is allowed
        if (reloadItem == null) return "You need to select a reload item";
        if (cooldown <= 0) return "The cooldown must be positive";
        if (storedAmmo <= 0) return "The stored ammo must be positive";
        if (reloadTime <= 0) return "The reload time must be positive";

        // The sounds are allowed to be null

        if (!allowIngredient.test(reloadItem))
            return "Only vanilla items and simple custom items are allowed as ammo";

        return null;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
