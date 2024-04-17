package nl.knokko.customitems.editor.menu.edit.item.elytra;

import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.item.elytra.VelocityModifier;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class VelocityModifierCollectionEdit extends SelfDedicatedCollectionEdit<VelocityModifier> {

    public VelocityModifierCollectionEdit(
            Collection<VelocityModifier> oldCollection,
            Consumer<List<VelocityModifier>> changeCollection, GuiComponent returnMenu
    ) {
        super(oldCollection, changeCollection, returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add new", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditVelocityModifier(
                    new VelocityModifier(true), this::addModel, this
            ));
        }), 0.025f, 0.3f, 0.175f, 0.4f);
    }

    @Override
    protected String getModelLabel(VelocityModifier model) {
        return model.getAccelerations().size() + " accelerations";
    }

    @Override
    protected BufferedImage getModelIcon(VelocityModifier model) {
        return null;
    }

    @Override
    protected boolean canEditModel(VelocityModifier model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(VelocityModifier oldModelValues, Consumer<VelocityModifier> changeModelValues) {
        return new EditVelocityModifier(oldModelValues, changeModelValues, this);
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(VelocityModifier model) {
        return CopyMode.INSTANT;
    }
}
