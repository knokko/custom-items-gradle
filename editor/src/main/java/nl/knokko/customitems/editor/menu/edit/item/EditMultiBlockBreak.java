package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.MultiBlockBreakValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ActivatableTextButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.GuiTexture;

import java.util.Locale;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditMultiBlockBreak extends GuiMenu {

    private final Consumer<MultiBlockBreakValues> changeValues;
    private final GuiComponent returnMenu;

    private final MultiBlockBreakValues currentValues;

    public EditMultiBlockBreak(
            MultiBlockBreakValues oldValues, Consumer<MultiBlockBreakValues> changeValues, GuiComponent returnMenu
    ) {
        this.currentValues = oldValues.copy(true);
        this.changeValues = changeValues;
        this.returnMenu = returnMenu;
    }

    private GuiTexture getShapeTexture(MultiBlockBreakValues.Shape shape) {
        String texturePath = "nl/knokko/customitems/editor/edit/item/multiblockbreak/shape/" + shape.name().toLowerCase(Locale.ROOT) + ".png";
        return state.getWindow().getTextureLoader().loadTexture(texturePath);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            String error = Validation.toErrorString(currentValues::validate);
            if (error == null) {
                changeValues.accept(currentValues);
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        WrapperComponent<SimpleImageComponent> shapeImageWrapper = new WrapperComponent<>(
                new SimpleImageComponent(getShapeTexture(currentValues.getShape()))
        );
        addComponent(shapeImageWrapper, 0.75f, 0.5f, 0.95f, 0.8f);

        addComponent(new DynamicTextComponent("Shape:", LABEL), 0.3f, 0.8f, 0.4f, 0.9f);
        addComponent(
                createShapeButton(MultiBlockBreakValues.Shape.CUBE, shapeImageWrapper),
                0.45f, 0.8f, 0.55f, 0.9f
        );
        addComponent(
                createShapeButton(MultiBlockBreakValues.Shape.MANHATTAN, shapeImageWrapper),
                0.58f, 0.8f, 0.72f, 0.9f
        );

        addComponent(new DynamicTextComponent("Size:", LABEL), 0.3f, 0.65f, 0.4f, 0.75f);
        addComponent(
                new EagerIntEditField(currentValues.getSize(), 1, EDIT_BASE, EDIT_ACTIVE, currentValues::setSize),
                0.425f, 0.65f, 0.5f, 0.75f
        );

        addComponent(
                new CheckboxComponent(currentValues.shouldStackDurabilityCost(), currentValues::setStackDurabilityCost),
                0.3f, 0.52f, 0.325f, 0.55f
        );
        addComponent(new DynamicTextComponent("Stack durability cost", LABEL), 0.35f, 0.5f, 0.55f, 0.6f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/multi block break.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private ActivatableTextButton createShapeButton(
            MultiBlockBreakValues.Shape shape, WrapperComponent<SimpleImageComponent> shapeImageWrapper
    ) {
        return new ActivatableTextButton(shape.toString(), BUTTON, HOVER, HOVER, () -> {
            currentValues.setShape(shape);
            shapeImageWrapper.setComponent(new SimpleImageComponent(getShapeTexture(shape)));
        }, () -> currentValues.getShape() == shape);
    }
}
