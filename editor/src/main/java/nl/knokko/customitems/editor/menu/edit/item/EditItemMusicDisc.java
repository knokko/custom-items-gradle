package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.sound.EditSound;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.item.CustomMusicDiscValues;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemMusicDisc extends EditItemBase<CustomMusicDiscValues> {

    private static final AttributeModifierValues EXAMPLE_MODIFIER = AttributeModifierValues.createQuick(
            AttributeModifierValues.Attribute.MAX_HEALTH,
            AttributeModifierValues.Slot.OFFHAND,
            AttributeModifierValues.Operation.ADD,
            4.0
    );

    public EditItemMusicDisc(EditMenu menu, CustomMusicDiscValues oldValues, ItemReference toModify) {
        super(menu, oldValues, toModify);
    }

    @Override
    protected AttributeModifierValues getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.MUSIC_DISC;
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Music...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditSound(
                    currentValues.getMusic(), currentValues::setMusic, this, menu.getSet()
            ));
        }), 0.7f, 0.35f, 0.8f, 0.45f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/music disc.html");
    }
}
