package nl.knokko.customitems.editor.menu.edit.worldgen;

import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class WorldGenerationPortal extends GuiMenu {

    private final GuiComponent returnMenu;
    private final ItemSet itemSet;

    public WorldGenerationPortal(GuiComponent returnMenu, ItemSet itemSet) {
        this.returnMenu = returnMenu;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Back", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Ore veins", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new OreVeinGeneratorCollectionEdit(this, itemSet));
        }), 0.7f, 0.7f, 0.85f, 0.8f);
        addComponent(new DynamicTextButton("Trees (1.13+ except 1.16)", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new TreeGeneratorCollectionEdit(this, itemSet));
        }), 0.7f, 0.55f, 0.9f, 0.65f);

        HelpButtons.addHelpLink(this, "edit menu/worldgen/index.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
