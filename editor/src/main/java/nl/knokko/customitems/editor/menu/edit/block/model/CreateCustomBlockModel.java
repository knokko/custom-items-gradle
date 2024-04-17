package nl.knokko.customitems.editor.menu.edit.block.model;

import nl.knokko.customitems.block.model.BlockModel;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.item.model.ChooseCustomModel;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.item.model.ModernCustomItemModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateCustomBlockModel extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final Consumer<BlockModel> changeModel;

    public CreateCustomBlockModel(GuiComponent returnMenu, ItemSet itemSet, Consumer<BlockModel> changeModel) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.changeModel = changeModel;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Load texture...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(
                    new TextureEdit(itemSet, this, null, new KciTexture(true))
            );
        }), 0.025f, 0.4f, 0.2f, 0.5f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        ModernCustomItemModel[] pModel = { null };
        addComponent(new DynamicTextButton("Model...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new ChooseCustomModel(newModel -> pModel[0] = newModel, this));
        }), 0.4f, 0.7f, 0.5f, 0.8f);

        TextureReference[] pTexture = { null };
        addComponent(new DynamicTextComponent("Editor texture:", LABEL), 0.4f, 0.55f, 0.6f, 0.65f);
        addComponent(CollectionSelect.createButton(
                itemSet.textures.references(), newTexture -> pTexture[0] = newTexture,
                candidateTexture -> candidateTexture.get().getName(), null, false
        ), 0.625f, 0.55f, 0.8f, 0.65f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            CustomBlockModel model = new CustomBlockModel(pModel[0], pTexture[0]);
            String error = Validation.toErrorString(() -> model.validate(itemSet));

            if (error == null) {
                changeModel.accept(model);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/model/custom.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
