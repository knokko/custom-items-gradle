package nl.knokko.customitems.editor.menu.edit.recipe.template;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.List;
import java.util.function.Function;

public class ChooseTemplateRecipeType extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet set;

    public ChooseTemplateRecipeType(GuiComponent returnMenu, ItemSet set) {
        this.returnMenu = returnMenu;
        this.set = set;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        addToolTemplateButton("Sword", 0, (stick, gem) -> new KciIngredient[]{
                null, gem, null,
                null, gem, null,
                null, stick, null
        });
        addToolTemplateButton("Pickaxe", 1, (stick, gem) -> new KciIngredient[]{
                gem, gem, gem,
                null, stick, null,
                null, stick, null
        });
        addToolTemplateButton("Axe", 2, (stick, gem) -> new KciIngredient[]{
                gem, gem, null,
                gem, stick, null,
                null, stick, null
        });
        addToolTemplateButton("Shovel", 3, (stick, gem) -> new KciIngredient[]{
                null, gem, null,
                null, stick, null,
                null, stick, null
        });
        addToolTemplateButton("Hoe", 4, (stick, gem) -> new KciIngredient[]{
                gem, gem, null,
                null, stick, null,
                null, stick, null
        });

        addArmorTemplateButton("Helmet", 0, gem -> new KciIngredient[]{
                gem, gem, gem,
                gem, null, gem,
                null, null, null
        });
        addArmorTemplateButton("Chestplate", 1, gem -> new KciIngredient[]{
                gem, null, gem,
                gem, gem, gem,
                gem, gem, gem
        });
        addArmorTemplateButton("Leggings", 2, gem -> new KciIngredient[]{
                gem, gem, gem,
                gem, null, gem,
                gem, null, gem
        });
        addArmorTemplateButton("Boots", 3, gem -> new KciIngredient[]{
                null, null, null,
                gem, null, gem,
                gem, null, gem
        });

        addTemplateButton("Bow", 0, 2, arrayOf("Stick", "String"), chosenIngredients -> {
            KciIngredient stick = chosenIngredients.get(0);
            KciIngredient string = chosenIngredients.get(1);
            return new KciIngredient[] {
                    string, stick, null,
                    string, null, stick,
                    string, stick, null
            };
        });

        addTemplateButton("Block", 1, 2, arrayOf("Material"), chosenIngredients -> {
            KciIngredient material = chosenIngredients.get(0);
            return new KciIngredient[] {
                    material, material, material,
                    material, material, material,
                    material, material, material
            };
        });
    }

    private String[] arrayOf(String...elements) {
        return elements;
    }

    @FunctionalInterface
    private interface ToolTemplateConsumer {
        KciIngredient[] shape(KciIngredient stick, KciIngredient gem);
    }

    private void addToolTemplateButton(String name, int rowIndex, ToolTemplateConsumer template) {
        addTemplateButton(name, rowIndex, 0, arrayOf("Stick", "Gem"),
                chosenIngredients -> template.shape(chosenIngredients.get(0), chosenIngredients.get(1)));
    }

    private void addArmorTemplateButton(String name, int rowIndex, Function<KciIngredient, KciIngredient[]> template) {
        addTemplateButton(name, rowIndex, 1, arrayOf("Gem"),
                chosenIngredients -> template.apply(chosenIngredients.get(0)));
    }

    private void addTemplateButton(
            String name, int rowIndex, int columnIndex, String[] materialNames,
            Function<List<KciIngredient>, KciIngredient[]> shapeIngredients
    ) {
        addComponent(new DynamicTextButton(name, EditProps.BUTTON, EditProps.HOVER,
                        () -> state.getWindow().setMainComponent(
                                new CreateTemplateRecipe(materialNames, shapeIngredients, returnMenu, set)
                        )),
                0.25f + columnIndex * 0.2f, 0.8f - 0.15f * rowIndex,
                0.4f + columnIndex * 0.2f, 0.9f - 0.15f * rowIndex
        );
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
