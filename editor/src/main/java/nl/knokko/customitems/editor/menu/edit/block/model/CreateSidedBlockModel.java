package nl.knokko.customitems.editor.menu.edit.block.model;

import nl.knokko.customitems.block.model.BlockModel;
import nl.knokko.customitems.block.model.SidedBlockModel;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.texture.TextureEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CreateSidedBlockModel extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final Consumer<BlockModel> changeModel;

    public CreateSidedBlockModel(GuiComponent returnMenu, ItemSet itemSet, Consumer<BlockModel> changeModel) {
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
                    new TextureEdit(itemSet, this, null, new BaseTextureValues(true))
            );
        }), 0.025f, 0.4f, 0.2f, 0.5f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 1f, 1f);

        TextureReference[] chosenTextures = new TextureReference[6];
        addTextureButton("north", 0, chosenTextures);
        addTextureButton("east", 1, chosenTextures);
        addTextureButton("south", 2, chosenTextures);
        addTextureButton("west", 3, chosenTextures);
        addTextureButton("up", 4, chosenTextures);
        addTextureButton("down", 5, chosenTextures);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            SidedBlockModel model = new SidedBlockModel(
                    chosenTextures[0], chosenTextures[1], chosenTextures[2], chosenTextures[3],
                    chosenTextures[4], chosenTextures[5]
            );
            String error = Validation.toErrorString(() -> model.validate(itemSet));
            if (error == null) {
                changeModel.accept(model);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.1f, 0.175f, 0.2f);

        HelpButtons.addHelpLink(this, "edit menu/blocks/model/sided.html");
    }

    private void addTextureButton(String description, int index, TextureReference[] chosenTextures) {
        float minY = 0.8f - index * 0.125f;
        float maxY = minY + 0.1f;
        addComponent(new DynamicTextComponent(description, LABEL), 0.3f, minY, 0.4f, maxY);
        addComponent(CollectionSelect.createButton(
                itemSet.getTextures().references(), chosenTexture -> chosenTextures[index] = chosenTexture,
                candidateTexture -> candidateTexture.get().getName(), null, false
        ), 0.425f, minY, 0.6f, maxY);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
