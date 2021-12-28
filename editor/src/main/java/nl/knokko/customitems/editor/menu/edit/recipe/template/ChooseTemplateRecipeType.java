package nl.knokko.customitems.editor.menu.edit.recipe.template;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.itemset.SItemSet;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.util.List;
import java.util.function.Function;

public class ChooseTemplateRecipeType extends GuiMenu {

    private final GuiComponent returnMenu;
    private final SItemSet set;

    public ChooseTemplateRecipeType(GuiComponent returnMenu, SItemSet set) {
        this.returnMenu = returnMenu;
        this.set = set;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.15f, 0.8f);

        addToolTemplateButton("Sword", 0, (stick, gem) -> new IngredientValues[]{
                null, gem, null,
                null, gem, null,
                null, stick, null
        });
        addToolTemplateButton("Pickaxe", 1, (stick, gem) -> new IngredientValues[]{
                gem, gem, gem,
                null, stick, null,
                null, stick, null
        });
        addToolTemplateButton("Axe", 2, (stick, gem) -> new IngredientValues[]{
                gem, gem, null,
                gem, stick, null,
                null, stick, null
        });
        addToolTemplateButton("Shovel", 3, (stick, gem) -> new IngredientValues[]{
                null, gem, null,
                null, stick, null,
                null, stick, null
        });
        addToolTemplateButton("Hoe", 4, (stick, gem) -> new IngredientValues[]{
                gem, gem, null,
                null, stick, null,
                null, stick, null
        });

        addArmorTemplateButton("Helmet", 0, gem -> new IngredientValues[]{
                gem, gem, gem,
                gem, null, gem,
                null, null, null
        });
        addArmorTemplateButton("Chestplate", 1, gem -> new IngredientValues[]{
                gem, null, gem,
                gem, gem, gem,
                gem, gem, gem
        });
        addArmorTemplateButton("Leggings", 2, gem -> new IngredientValues[]{
                gem, gem, gem,
                gem, null, gem,
                gem, null, gem
        });
        addArmorTemplateButton("Boots", 3, gem -> new IngredientValues[]{
                null, null, null,
                gem, null, gem,
                gem, null, gem
        });

        addTemplateButton("Bow", 0, 2, arrayOf("Stick", "String"), chosenIngredients -> {
            IngredientValues stick = chosenIngredients.get(0);
            IngredientValues string = chosenIngredients.get(1);
            return new IngredientValues[] {
                    string, stick, null,
                    string, null, stick,
                    string, stick, null
            };
        });
    }

    private String[] arrayOf(String...elements) {
        return elements;
    }

    @FunctionalInterface
    private interface ToolTemplateConsumer {
        IngredientValues[] shape(IngredientValues stick, IngredientValues gem);
    }

    private void addToolTemplateButton(String name, int rowIndex, ToolTemplateConsumer template) {
        addTemplateButton(name, rowIndex, 0, arrayOf("Stick", "Gem"),
                chosenIngredients -> template.shape(chosenIngredients.get(0), chosenIngredients.get(1)));
    }

    private void addArmorTemplateButton(String name, int rowIndex, Function<IngredientValues, IngredientValues[]> template) {
        addTemplateButton(name, rowIndex, 1, arrayOf("Gem"),
                chosenIngredients -> template.apply(chosenIngredients.get(0)));
    }

    private void addTemplateButton(
            String name, int rowIndex, int columnIndex, String[] materialNames,
            Function<List<IngredientValues>, IngredientValues[]> shapeIngredients
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
