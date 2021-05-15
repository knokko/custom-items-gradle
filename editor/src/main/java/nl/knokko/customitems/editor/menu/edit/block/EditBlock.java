package nl.knokko.customitems.editor.menu.edit.block;

import nl.knokko.customitems.block.BlockConstants;
import nl.knokko.customitems.block.CustomBlockValues;
import nl.knokko.customitems.block.CustomBlockView;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.main.MainMenu;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.texture.NamedImage;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.net.MalformedURLException;
import java.net.URL;

public class EditBlock extends GuiMenu  {

    private final CustomBlockView toModify;
    private final CustomBlockValues currentValues;

    private final GuiComponent returnMenu;
    private final ItemSet set;

    public EditBlock(CustomBlockView blockToModify, CustomBlockValues valuesToModify, GuiComponent returnMenu, ItemSet set) {
        this.toModify = blockToModify;
        this.currentValues = valuesToModify;
        this.returnMenu = returnMenu;
        this.set = set;
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () ->
            state.getWindow().setMainComponent(returnMenu)
        ), 0.025f, 0.7f, 0.175f, 0.8f);

        DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
        addComponent(errorComponent, 0.025f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
            String error;
            if (toModify == null) {
                error = set.addBlock(currentValues);
            } else {
                error = set.changeBlock(toModify, currentValues);
            }

            if (error == null) {
                state.getWindow().setMainComponent(returnMenu);
            } else {
                errorComponent.setText(error);
            }
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        addComponent(new DynamicTextComponent("Name:", EditProps.LABEL),
                0.35f, 0.8f, 0.44f, 0.9f);
        addComponent(new EagerTextEditField(
                currentValues.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, currentValues::setName
        ), 0.45f, 0.8f, 0.65f, 0.9f);

        addComponent(new DynamicTextComponent("Drops:", EditProps.LABEL),
                0.35f, 0.65f, 0.44f, 0.75f);
        addComponent(new DynamicTextButton("Change...", EditProps.BUTTON, EditProps.HOVER, () ->
                state.getWindow().setMainComponent(new CustomBlockDropCollectionEdit(
                        currentValues.getDrops(), currentValues::setDrops, set, this
                ))
        ), 0.45f, 0.65f, 0.6f, 0.75f);

        addComponent(new DynamicTextComponent("Texture:", EditProps.LABEL),
                0.3f, 0.5f, 0.44f, 0.6f);
        addComponent(CollectionSelect.createButton(
                set.getBackingTextures(),
                currentValues::setTexture,
                candidateTexture -> candidateTexture.getClass() == NamedImage.class,
                NamedImage::getName,
                currentValues.getTexture()
        ), 0.45f, 0.5f, 0.6f, 0.6f);

        addComponent(new DynamicTextComponent(
                "The custom block texture system is based on the resourcepack of LapisDemon:", EditProps.LABEL),
                0.2f, 0.3f, 1f, 0.4f
        );
        addComponent(new DynamicTextButton(
                "https://www.youtube.com/watch?v=d_08KIvg7TM", EditProps.LINK_BASE, EditProps.LINK_HOVER, () -> {
            URL url = null;
            try {
                url = new URL("https://www.youtube.com/watch?v=d_08KIvg7TM");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            MainMenu.openWebpage(url);
        }
        ), 0.2f, 0.2f, 0.8f, 0.3f);

        addComponent(new DynamicTextComponent(
                "Note: you can create at most " + BlockConstants.MAX_NUM_BLOCKS + " custom blocks",
                EditProps.LABEL), 0.2f, 0.05f, 0.8f, 0.15f);

        // TODO Test this help link after merging v9 docs into master
        HelpButtons.addHelpLink(this, "edit menu/blocks/edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }
}
