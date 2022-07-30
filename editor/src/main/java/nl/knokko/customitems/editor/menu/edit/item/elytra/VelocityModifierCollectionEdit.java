package nl.knokko.customitems.editor.menu.edit.item.elytra;

import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.item.elytra.VelocityModifierValues;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class VelocityModifierCollectionEdit extends SelfDedicatedCollectionEdit<VelocityModifierValues> {

    public VelocityModifierCollectionEdit(
            Collection<VelocityModifierValues> oldCollection,
            Consumer<List<VelocityModifierValues>> changeCollection, GuiComponent returnMenu
    ) {
        super(oldCollection, changeCollection, returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add new", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditVelocityModifier(
                    new VelocityModifierValues(true), this::addModel, this
            ));
        }), 0.025f, 0.3f, 0.175f, 0.4f);
    }

    @Override
    protected String getModelLabel(VelocityModifierValues model) {
        return model.getAccelerations().size() + " accelerations";
    }

    @Override
    protected BufferedImage getModelIcon(VelocityModifierValues model) {
        return null;
    }

    @Override
    protected boolean canEditModel(VelocityModifierValues model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(VelocityModifierValues oldModelValues, Consumer<VelocityModifierValues> changeModelValues) {
        return new EditVelocityModifier(oldModelValues, changeModelValues, this);
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(VelocityModifierValues model) {
        return CopyMode.INSTANT;
    }
}
