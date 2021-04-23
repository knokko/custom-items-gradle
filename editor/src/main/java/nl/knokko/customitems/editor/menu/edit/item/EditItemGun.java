package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomGun;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.gun.DirectGunAmmo;
import nl.knokko.customitems.item.gun.GunAmmo;
import nl.knokko.customitems.projectile.CIProjectile;
import nl.knokko.gui.component.text.IntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.Option;

public class EditItemGun extends EditItemBase {

    private static final AttributeModifier EXAMPLE_MODIFIER = new AttributeModifier(
            AttributeModifier.Attribute.ATTACK_DAMAGE, AttributeModifier.Slot.MAINHAND,
            AttributeModifier.Operation.ADD, 3
    );

    private final CustomGun toModify;

    private final IntEditField amountField;

    private CIProjectile projectile;
    private GunAmmo ammoSystem;

    public EditItemGun(EditMenu menu, CustomGun oldValues, CustomGun toModify) {
        super(menu, oldValues, toModify, CustomItemType.Category.GUN);
        this.toModify = toModify;

        int initialAmountPerShot;
        if (oldValues != null) {
            projectile = oldValues.projectile;
            ammoSystem = oldValues.ammo;
            initialAmountPerShot = oldValues.amountPerShot;
        } else {
            initialAmountPerShot = 1;
            ammoSystem = new DirectGunAmmo(null, 10);
        }

        amountField = new IntEditField(initialAmountPerShot, 1, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextComponent("Projectile:", EditProps.LABEL), 0.6f, 0.8f, 0.7f, 0.85f);
        addComponent(CollectionSelect.createButton(
                menu.getSet().getBackingProjectiles(),
                newProjectile -> projectile = newProjectile,
                projectile -> projectile.name,
                projectile
        ), 0.73f, 0.8f, 0.8f, 0.85f);

        addComponent(new DynamicTextComponent("Ammo system:", EditProps.LABEL), 0.6f, 0.74f, 0.7f, 0.79f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditAmmoSystem(
                    this, newAmmo -> this.ammoSystem = newAmmo, menu.getSet(), this.ammoSystem
            ));
        }), 0.73f, 0.74f, 0.8f, 0.79f);

        addComponent(new DynamicTextComponent("Amount per shot:", EditProps.LABEL), 0.6f, 0.68f, 0.72f, 0.73f);
        addComponent(amountField, 0.73f, 0.68f, 0.78f, 0.73f);

        // TODO Add help link
    }

    @Override
    protected AttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.GUN;
    }

    @Override
    protected String create(float attackRange) {

        Option.Int amountPerShot = amountField.getInt();
        if (!amountPerShot.hasValue()) return "The amount per shot must be a positive integer";

        return menu.getSet().addGun(new CustomGun(
                internalType, nameField.getText(), aliasField.getText(),
                getDisplayName(), lore, attributes, enchantments,
                textureSelect.getSelected(), itemFlags, customModel,
                playerEffects, targetEffects, equippedEffects, commands,
                conditions, op, extraNbt, attackRange, projectile, ammoSystem,
                amountPerShot.getValue()
        ));
    }

    @Override
    protected String apply(float attackRange) {

        Option.Int amountPerShot = amountField.getInt();
        if (!amountPerShot.hasValue()) return "The amount per shot must be a positive integer";

        return menu.getSet().changeGun(
                toModify, internalType, aliasField.getText(),
                getDisplayName(), lore, attributes, enchantments,
                textureSelect.getSelected(), itemFlags, customModel,
                playerEffects, targetEffects, equippedEffects, commands,
                conditions, op, extraNbt, attackRange, projectile, ammoSystem,
                amountPerShot.getValue()
        );
    }
}
