package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciGun;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemGun extends EditItemBase<KciGun> {

    private static final KciAttributeModifier EXAMPLE_MODIFIER = KciAttributeModifier.createQuick(
            KciAttributeModifier.Attribute.ATTACK_DAMAGE,
            KciAttributeModifier.Slot.MAINHAND,
            KciAttributeModifier.Operation.ADD,
            3
    );

    public EditItemGun(EditMenu menu, KciGun oldValues, ItemReference toModify) {
        super(menu, oldValues, toModify);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(
                new DynamicTextComponent("Projectile:", EditProps.LABEL),
                0.6f, 0.8f, 0.7f, 0.85f
        );
        addComponent(CollectionSelect.createButton(
                menu.getSet().projectiles.references(),
                currentValues::setProjectile,
                projectileReference -> projectileReference.get().getName(),
                currentValues.getProjectileReference(), false
        ), 0.73f, 0.8f, 0.8f, 0.85f);

        addComponent(
                new DynamicTextComponent("Ammo system:", EditProps.LABEL),
                0.6f, 0.74f, 0.7f, 0.79f
        );
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new EditAmmoSystem(
                    this, currentValues::setAmmo, menu.getSet(), currentValues.getAmmo()
            ));
        }), 0.73f, 0.74f, 0.8f, 0.79f);

        addComponent(
                new DynamicTextComponent("Amount per shot:", EditProps.LABEL),
                0.6f, 0.68f, 0.72f, 0.73f
        );
        addComponent(
                new EagerIntEditField(currentValues.getAmountPerShot(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setAmountPerShot),
                0.73f, 0.68f, 0.78f, 0.73f
        );

        addComponent(new DynamicTextComponent("Requires permission", LABEL), 0.6f, 0.62f, 0.72f, 0.67f);
        addComponent(
                new CheckboxComponent(currentValues.requiresPermission(), currentValues::setRequiresPermission),
                0.57f, 0.63f, 0.59f, 0.65f
        );

        HelpButtons.addHelpLink(this, "edit menu/items/edit/gun.html");
    }

    @Override
    protected KciAttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected KciItemType.Category getCategory() {
        return KciItemType.Category.GUN;
    }
}
