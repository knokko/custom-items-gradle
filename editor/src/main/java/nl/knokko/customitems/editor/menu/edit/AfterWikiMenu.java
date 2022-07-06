package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.util.HelpButtons.openWebpage;

public class AfterWikiMenu extends GuiMenu {

    private final String itemSetName;
    private final GuiComponent returnMenu;

    public AfterWikiMenu(String itemSetName, GuiComponent returnMenu) {
        this.itemSetName = itemSetName;
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        File destFolder = new File(EditorFileManager.FOLDER + "/wiki/" + itemSetName);
        addComponent(new DynamicTextButton("Your wiki has been exported to " + destFolder.getAbsolutePath(), LINK_BASE, LINK_HOVER, () -> {
            try {
                Desktop.getDesktop().open(destFolder);
            } catch (IOException e) {
                System.err.println("Couldn't open wiki export destination folder: " + e.getLocalizedMessage());
            }
        }), 0.05f, 0.85f, 0.95f, 0.9f);
        addComponent(new DynamicTextComponent(
                "You can view your wiki by opening index.html in a browser (double-clicking that file probably works)", LABEL
        ), 0.05f, 0.8f, 0.9f, 0.85f);
        addComponent(new DynamicTextComponent(
                "If you know how to write HTML and/or CSS, you can also modify it to your liking", LABEL
        ), 0.05f, 0.75f, 0.7f, 0.8f);
        addComponent(new DynamicTextComponent(
                "But note that re-generating the wiki will overwrite all your changes, so please edit a copy of the wiki instead", LABEL
        ), 0.05f, 0.7f, 0.95f, 0.75f);

        addComponent(new DynamicTextComponent(
                "The next step is to distribute the wiki to your users", LABEL
        ), 0.05f, 0.6f, 0.5f, 0.65f);
        addComponent(new DynamicTextComponent(
                "You could simply ZIP it and publish it on Dropbox or some similar site", LABEL
        ), 0.05f, 0.55f, 0.6f, 0.6f);
        addComponent(new DynamicTextButton("But you could also use a static website host like W3C School Spaces", LINK_BASE, LINK_HOVER, () -> {
            openWebpage("https://www.w3schools.com/howto/howto_website_host_staticwebsite.asp");
        }), 0.05f, 0.5f, 0.7f, 0.55f);
        addComponent(
                new DynamicTextComponent("Unfortunately, getting your own wiki domain is probably not free and more complicated", LABEL
        ), 0.05f, 0.45f, 0.95f, 0.5f);

        addComponent(new DynamicTextButton("Go back", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.4f, 0.2f, 0.6f, 0.3f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
