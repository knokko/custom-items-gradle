package nl.knokko.customitems.editor.menu.edit.block.model;

import nl.knokko.customitems.block.model.BlockModel;
import nl.knokko.customitems.block.model.SimpleBlockModel;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ManageBlockModel extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final BlockModel currentModel;
    private final Consumer<BlockModel> changeModel;

    public ManageBlockModel(
            GuiComponent returnMenu, ItemSet itemSet, BlockModel currentModel, Consumer<BlockModel> changeModel
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentModel = currentModel;
        this.changeModel = changeModel;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Back", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(
                new DynamicTextComponent("Current model: " + (currentModel != null ? currentModel.toString() : "None"), LABEL),
                0.5f, 0.8f, 0.8f, 0.9f
        );
        addComponent(new DynamicTextComponent("Change to a...", LABEL), 0.5f, 0.7f, 0.7f, 0.8f);

        addComponent(new DynamicTextButton("model with the same texture on each side", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CollectionSelect<>(itemSet.textures.references(), chosenTexture -> {
                changeModel.accept(new SimpleBlockModel(chosenTexture));
            }, candidateTexture -> candidateTexture.get().getClass() == BaseTextureValues.class,
                    candidateTexture -> candidateTexture.get().getName(), returnMenu, false
            ));
        }), 0.55f, 0.55f, 0.95f, 0.65f);

        addComponent(new DynamicTextButton("model with a different texture on each side", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CreateSidedBlockModel(this, itemSet, newModel -> {
                changeModel.accept(newModel);
                state.getWindow().setMainComponent(returnMenu);
            }));
        }), 0.55f, 0.4f, 0.95f, 0.5f);

        addComponent(new DynamicTextButton("custom model", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CreateCustomBlockModel(this, itemSet, newModel -> {
                changeModel.accept(newModel);
                state.getWindow().setMainComponent(returnMenu);
            }));
        }), 0.55f, 0.25f, 0.7f, 0.35f);

        addComponent(new DynamicTextComponent(
                "Note: all custom blocks are actually mushroom blocks, and minecraft expects them to be solid",
                LABEL), 0.025f, 0.1f, 0.975f, 0.2f);
        addComponent(new DynamicTextComponent(
                "If you use textures or custom models with transparency, you should expect some visual glitches",
                LABEL), 0.025f, 0f, 0.975f, 0.1f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
