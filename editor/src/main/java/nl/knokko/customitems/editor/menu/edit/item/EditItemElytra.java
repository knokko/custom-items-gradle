package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.item.elytra.VelocityModifierCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.texture.ArmorTexturesEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciElytra;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemElytra extends EditItemArmor<KciElytra> {

    public EditItemElytra(ItemSet itemSet, GuiComponent returnMenu, KciElytra oldValues, ItemReference toModify) {
        super(itemSet, returnMenu, oldValues, toModify);
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(
                new DynamicTextComponent("Worn texture [OPTIFINE]:", LABEL),
                0.6f, 0.19f, 0.84f, 0.25f
        );
        addComponent(new DynamicTextButton("Edit...", BUTTON, HOVER, () -> {
            ArmorTexturesEdit.selectArmorImage(currentValues::setWornElytraTexture, errorComponent);
        }), 0.85f, 0.19f, 0.99f, 0.25f);

        addComponent(
                new DynamicTextComponent("Glide velocity modifiers:", LABEL),
                0.55f, 0.11f, 0.84f, 0.17f
        );
        addComponent(new DynamicTextButton("Change...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new VelocityModifierCollectionEdit(
                    currentValues.getVelocityModifiers(), currentValues::setVelocityModifiers, this
            ));
        }), 0.85f, 0.11f, 0.99f, 0.17f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/elytra.html");
    }
}
