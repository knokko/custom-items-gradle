package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.editor.util.FixedPointEditField;
import nl.knokko.customitems.effect.ChancePotionEffect;
import nl.knokko.customitems.effect.VEffectType;
import nl.knokko.customitems.util.Chance;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChanceEffectsCollectionEdit extends InlineCollectionEdit<ChancePotionEffect> {
    public ChanceEffectsCollectionEdit(GuiComponent returnMenu, Collection<ChancePotionEffect> currentCollection, Consumer<Collection<ChancePotionEffect>> onApply) {
        super(returnMenu, currentCollection, onApply);
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        ChancePotionEffect effect = ownCollection.get(itemIndex);
        GuiComponent effectButton = EnumSelect.createSelectButton(VEffectType.class, effect::setType, effect.getType());

        addComponent(new ImageButton(deleteBase, deleteHover, () -> {
            removeItem(itemIndex);
        }), 0.26f, minY, 0.3f, maxY);
        addComponent(effectButton, 0.31f, minY, 0.4f, maxY);
        addComponent(new DynamicTextComponent("Duration:", EditProps.LABEL), 0.41f, minY, 0.5f, maxY);
        addComponent(
                new EagerIntEditField(effect.getDuration(), 1, EDIT_BASE, EDIT_ACTIVE, effect::setDuration),
                0.51f, minY, 0.56f, maxY
        );
        addComponent(
                new DynamicTextComponent("Level: ", EditProps.LABEL),
                0.57f, minY, 0.63f, maxY
        );
        addComponent(
                new EagerIntEditField(effect.getLevel(), 1, EDIT_BASE, EDIT_ACTIVE, effect::setLevel),
                0.64f, minY, 0.7f, maxY
        );
        addComponent(
                new DynamicTextComponent("Chance: ", LABEL),
                0.71f, minY, 0.78f, maxY
        );
        addComponent(
                new FixedPointEditField(
                        Chance.NUM_BACK_DIGITS, effect.getChance().getRawValue(), 0, 100,
                        newRawChance -> effect.setChance(new Chance(newRawChance))
                ), 0.79f, minY, 0.87f, maxY
        );
        addComponent(new DynamicTextComponent("%", LABEL), 0.88f, minY, 0.9f, maxY);
    }

    @Override
    protected ChancePotionEffect addNew() {
        return new ChancePotionEffect(true);
    }

    @Override
    protected String getHelpPage() {
        return "edit menu/items/edit/chance effects.html";
    }
}
