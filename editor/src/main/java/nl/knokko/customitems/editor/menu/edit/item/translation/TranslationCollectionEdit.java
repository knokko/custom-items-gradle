package nl.knokko.customitems.editor.menu.edit.item.translation;

import nl.knokko.customitems.editor.menu.edit.collection.SelfDedicatedCollectionEdit;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.TranslationEntry;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.BUTTON;
import static nl.knokko.customitems.editor.menu.edit.EditProps.HOVER;

public class TranslationCollectionEdit extends SelfDedicatedCollectionEdit<TranslationEntry> {

    public TranslationCollectionEdit(
            Collection<TranslationEntry> oldTranslations,
            Consumer<List<TranslationEntry>> updateTranslations,
            GuiComponent returnMenu
    ) {
        super(oldTranslations, updateTranslations, returnMenu);
    }

    @Override
    protected void addComponents() {
        super.addComponents();

        addComponent(new DynamicTextButton("Add...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EditTranslation(
                    new TranslationEntry(true), this::addModel, this
            ));
        }), 0.025f, 0.2f, 0.175f, 0.3f);

        HelpButtons.addHelpLink(this, "edit menu/items/edit/translations/overview.html");
    }

    @Override
    protected String getModelLabel(TranslationEntry model) {
        return model.getLanguage();
    }

    @Override
    protected BufferedImage getModelIcon(TranslationEntry model) {
        return null;
    }

    @Override
    protected boolean canEditModel(TranslationEntry model) {
        return true;
    }

    @Override
    protected GuiComponent createEditMenu(TranslationEntry oldModelValues, Consumer<TranslationEntry> changeModelValues) {
        return new EditTranslation(oldModelValues, changeModelValues, this);
    }

    @Override
    protected boolean canDeleteModels() {
        return true;
    }

    @Override
    protected CopyMode getCopyMode(TranslationEntry model) {
        return CopyMode.SEPARATE_MENU;
    }
}
