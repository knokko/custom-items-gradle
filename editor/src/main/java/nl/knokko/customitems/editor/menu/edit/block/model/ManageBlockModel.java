package nl.knokko.customitems.editor.menu.edit.block.model;

import nl.knokko.customitems.block.model.BlockModel;
import nl.knokko.customitems.block.model.CustomBlockModel;
import nl.knokko.customitems.block.model.SimpleBlockModel;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.item.model.ConvertModelMenu;
import nl.knokko.customitems.editor.menu.edit.item.model.SelectGeyserModel;
import nl.knokko.customitems.item.model.GeyserCustomModel;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.texture.KciTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.ConditionalTextComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ManageBlockModel extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;
    private final BlockModel currentModel;
    private final Consumer<BlockModel> changeModel;
    private final String blockName;

    public ManageBlockModel(
            GuiComponent returnMenu, ItemSet itemSet,
            BlockModel currentModel, Consumer<BlockModel> changeModel, String blockName
    ) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
        this.currentModel = currentModel;
        this.changeModel = changeModel;
        this.blockName = blockName;
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
            }, candidateTexture -> candidateTexture.get().getClass() == KciTexture.class,
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

        addComponent(new ConditionalTextComponent("Missing Geyser model", LABEL,
                () -> currentModel instanceof CustomBlockModel && ((CustomBlockModel) currentModel).getGeyserModel() == null
        ), 0.1f, 0.45f, 0.3f, 0.55f);
        addComponent(new ConditionalTextComponent("Also has Geyser model", LABEL,
                () -> currentModel instanceof CustomBlockModel && ((CustomBlockModel) currentModel).getGeyserModel() != null
        ), 0.1f, 0.45f, 0.3f, 0.55f);

        Consumer<GeyserCustomModel> changeGeyserModel = geyserModel -> {
            CustomBlockModel model = (CustomBlockModel) currentModel;
            assert model != null;
            changeModel.accept(new CustomBlockModel(
                    model.getItemModel(), model.getPrimaryTexture(), geyserModel
            ));
            state.getWindow().setMainComponent(returnMenu);
        };

        addComponent(new ConditionalTextButton("Convert Java model to Geyser model", BUTTON, HOVER, () -> {
            CustomBlockModel model = (CustomBlockModel) currentModel;
            assert model != null;

            state.getWindow().setMainComponent(new ConvertModelMenu(
                    returnMenu, changeGeyserModel, model.getItemModel(),
                    model.getPrimaryTexture().get(), "kci_block_" + blockName
            ));
        }, () -> currentModel instanceof CustomBlockModel), 0.05f, 0.325f, 0.35f, 0.425f);

        addComponent(new ConditionalTextButton("Manually choose Geyser model", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new SelectGeyserModel(
                    changeGeyserModel, returnMenu, "kci_block_" + blockName
            ));
        }, () -> currentModel instanceof CustomBlockModel), 0.05f, 0.2f, 0.35f, 0.3f);

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
