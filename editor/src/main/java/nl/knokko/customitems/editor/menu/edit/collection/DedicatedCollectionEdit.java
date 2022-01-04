package nl.knokko.customitems.editor.menu.edit.collection;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.util.Checks;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.BACKGROUND2;

public abstract class DedicatedCollectionEdit<V extends ModelValues, R extends Supplier<V>> extends GuiMenu {

    protected final GuiComponent returnMenu;
    protected final Iterable<R> liveCollection;
    protected final Function<V, String> attemptAddModel;

    protected final ItemList itemList;
    protected final EagerTextEditField searchField;
    protected final DynamicTextComponent errorComponent;

    public DedicatedCollectionEdit(
            GuiComponent returnMenu, Iterable<R> liveCollection, Function<V, String> attemptAddModel
    ) {
        Checks.notNull(returnMenu);
        this.returnMenu = returnMenu;
        this.liveCollection = liveCollection;
        this.attemptAddModel = attemptAddModel;

        this.itemList = new ItemList();
        this.searchField = new EagerTextEditField("", EDIT_BASE, EDIT_ACTIVE, newText -> itemList.refresh());
        this.errorComponent = new DynamicTextComponent("", ERROR);
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Back", CANCEL_BASE, CANCEL_HOVER, () ->
                state.getWindow().setMainComponent(returnMenu)
        ), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(
                new DynamicTextComponent("Search:", LABEL),
                0.025f, 0.6f, 0.15f, 0.7f
        );
        addComponent(searchField, 0.025f, 0.5f, 0.28f, 0.6f);

        addComponent(itemList, 0.3f, 0f, 1f, 0.9f);
        addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    @Override
    public void init() {
        if(didInit) itemList.refresh();
        super.init();
        errorComponent.setText("");
    }

    protected abstract String getModelLabel(V model);

    protected abstract BufferedImage getModelIcon(V model);

    protected abstract boolean canEditModel(V model);

    protected abstract GuiComponent createEditMenu(R modelReference);

    protected abstract String deleteModel(R modelReference);

    protected abstract boolean canDeleteModels();

    protected abstract CopyMode getCopyMode(R modelReference);

    protected abstract GuiComponent createCopyMenu(R modelReference);

    protected class ItemList extends GuiMenu {

        @Override
        protected void addComponents() {
            float minY = 0.9f;

            boolean hasIcon = false;
            for (R modelReference: liveCollection) {
                if (getModelIcon(modelReference.get()) != null) {
                    hasIcon = true;
                }
            }

            float minTextX = hasIcon ? 0.15f : 0f;
            GuiTextureLoader textureLoader = state.getWindow().getTextureLoader();

            for (R modelReference : liveCollection) {
                V model = modelReference.get();
                String label = getModelLabel(model);
                if (label.toLowerCase(Locale.ROOT).contains(searchField.getText().toLowerCase(Locale.ROOT))) {

                    float maxY = minY + 0.1f;
                    BufferedImage icon = getModelIcon(model);
                    if (icon != null) {
                        addComponent(new SimpleImageComponent(textureLoader.loadTexture(icon)), 0f, minY, 0.125f, maxY);
                    }

                    addComponent(
                            new DynamicTextComponent(label, LABEL),
                            minTextX, minY, Math.min(0.6f, minTextX + 0.05f * label.length()), maxY
                    );

                    if (canEditModel(model)) {
                        addComponent(new DynamicTextButton("Edit", BUTTON, HOVER, () ->
                                state.getWindow().setMainComponent(createEditMenu(modelReference))
                        ), 0.61f, minY, 0.72f, maxY);
                    }

                    CopyMode copyMode = getCopyMode(modelReference);
                    if (copyMode != CopyMode.DISABLED) {
                        addComponent(new DynamicTextButton("Copy", BUTTON, HOVER, () -> {
                            if (copyMode == CopyMode.SEPARATE_MENU) {
                                state.getWindow().setMainComponent(createCopyMenu(modelReference));
                            } else if (copyMode == CopyMode.INSTANT) {
                                String error = attemptAddModel.apply(model);
                                if (error == null) {
                                    refresh();
                                } else {
                                    errorComponent.setText(error);
                                }
                            }
                        }), 0.73f, minY, 0.83f, maxY);
                    }
                    if (canDeleteModels()) {
                        addComponent(new DynamicTextButton("Delete", QUIT_BASE, QUIT_HOVER, () -> {
                            String error = deleteModel(modelReference);
                            if (error == null) {
                                refresh();
                            } else {
                                errorComponent.setText(error);
                            }
                        }), 0.84f, minY, 0.99f, maxY);
                    }
                    minY -= 0.1f;
                }
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }

        public void refresh() {
            clearComponents();
            addComponents();
        }
    }

    public enum CopyMode {

        DISABLED,
        INSTANT,
        SEPARATE_MENU
    }
}
