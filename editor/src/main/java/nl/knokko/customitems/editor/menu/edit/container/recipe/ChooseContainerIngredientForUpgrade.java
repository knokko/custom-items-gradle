package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.ContainerRecipe;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.container.slot.ContainerSlot;
import nl.knokko.customitems.container.slot.InputSlot;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.NoIngredient;
import nl.knokko.customitems.recipe.result.UpgradeResult;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseContainerIngredientForUpgrade extends GuiMenu {

    private final GuiComponent returnMenu;
    private final UpgradeResult upgrade;
    private final KciContainer container;
    private final ContainerRecipe recipe;

    public ChooseContainerIngredientForUpgrade(
            GuiComponent returnMenu, UpgradeResult upgrade,
            KciContainer container, ContainerRecipe recipe
    ) {
        this.returnMenu = returnMenu;
        this.upgrade = upgrade;
        this.container = container;
        this.recipe = recipe;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        for (int x = 0; x < container.getWidth(); x++) {
            for (int y = 0; y < container.getHeight(); y++) {
                ContainerSlot slot = container.getSlot(x, y);
                if (slot instanceof InputSlot) {
                    String slotName = ((InputSlot) slot).getName();
                    KciIngredient ingredient = recipe.getInput(slotName);
                    if (ingredient != null && !(ingredient instanceof NoIngredient)) {
                        float minX = 0.2f + 0.08f * x;
                        float minY = 0.1f + 0.08f * (container.getHeight() - y - 1);
                        addComponent(new DynamicTextButton(slotName, CHOOSE_BASE, CHOOSE_HOVER, () -> {
                            upgrade.setInputSlotName(slotName);
                            state.getWindow().setMainComponent(returnMenu);
                        }), minX, minY, minX + 0.07f, minY + 0.07f);
                    }
                }
            }
        }
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
