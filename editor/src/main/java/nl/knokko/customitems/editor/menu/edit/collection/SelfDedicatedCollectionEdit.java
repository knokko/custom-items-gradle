package nl.knokko.customitems.editor.menu.edit.collection;

import nl.knokko.customitems.model.ModelValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.texture.loader.GuiTextureLoader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;
import static nl.knokko.customitems.editor.menu.edit.EditProps.BACKGROUND2;

public abstract class SelfDedicatedCollectionEdit<V extends ModelValues> extends GuiMenu {

    protected final GuiComponent returnMenu;
    protected final List<V> liveCollection;
    protected final Consumer<List<V>> changeCollection;

    protected final ItemList itemList;
    protected final DynamicTextComponent errorComponent;

    public SelfDedicatedCollectionEdit(
            Collection<V> oldCollection, Consumer<List<V>> changeCollection, GuiComponent returnMenu
    ) {
        this.returnMenu = returnMenu;
        this.liveCollection = new ArrayList<>(oldCollection);
        this.changeCollection = changeCollection;

        this.itemList = new ItemList();
        this.errorComponent = new DynamicTextComponent("", ERROR);
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () ->
                state.getWindow().setMainComponent(returnMenu)
        ), 0.025f, 0.7f, 0.175f, 0.8f);
        addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
            changeCollection.accept(liveCollection);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.5f, 0.175f, 0.6f);


        addComponent(itemList, 0.25f, 0f, 1f, 0.9f);
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

    protected abstract GuiComponent createEditMenu(V oldModelValues, Consumer<V> changeModelValues);

    protected abstract boolean canDeleteModels();

    protected abstract SelfDedicatedCollectionEdit.CopyMode getCopyMode(V model);

    protected void addModel(V model) {
        this.itemList.addModel(model);
    }

    protected class ItemList extends GuiMenu {

        @Override
        @SuppressWarnings("unchecked")
        protected void addComponents() {
            float minY = 0.9f;

            boolean hasIcon = false;
            for (V model : liveCollection) {
                if (getModelIcon(model) != null) {
                    hasIcon = true;
                }
            }

            float minTextX = hasIcon ? 0.15f : 0f;
            GuiTextureLoader textureLoader = state.getWindow().getTextureLoader();

            int index = 0;
            for (V model: liveCollection) {

                int rememberIndex = index;
                float maxY = minY + 0.1f;

                BufferedImage icon = getModelIcon(model);
                if (icon != null) {
                    addComponent(new SimpleImageComponent(textureLoader.loadTexture(icon)), 0f, minY, 0.125f, maxY);
                }

                String label = getModelLabel(model);

                addComponent(
                        new DynamicTextComponent(label, LABEL),
                        minTextX, minY, Math.min(0.6f, minTextX + 0.05f * label.length()), maxY
                );

                if (canEditModel(model)) {
                    addComponent(new DynamicTextButton("Edit", BUTTON, HOVER, () ->
                            state.getWindow().setMainComponent(createEditMenu(
                                    (V) model.copy(true), newValues -> {
                                        liveCollection.set(rememberIndex, newValues);
                                        refresh();
                                    }
                            ))
                    ), 0.61f, minY, 0.72f, maxY);
                }

                CopyMode copyMode = getCopyMode(model);
                if (copyMode != CopyMode.DISABLED) {
                    addComponent(new DynamicTextButton("Copy", BUTTON, HOVER, () -> {
                        if (copyMode == SelfDedicatedCollectionEdit.CopyMode.SEPARATE_MENU) {
                            state.getWindow().setMainComponent(createEditMenu((V) model.copy(true), this::addModel));
                        } else if (copyMode == SelfDedicatedCollectionEdit.CopyMode.INSTANT) {
                            liveCollection.add((V) model.copy(true));
                            refresh();
                        }
                    }), 0.73f, minY, 0.83f, maxY);
                }
                if (canDeleteModels()) {
                    addComponent(new DynamicTextButton("Delete", QUIT_BASE, QUIT_HOVER, () -> {
                        liveCollection.remove(rememberIndex);
                        refresh();
                    }), 0.84f, minY, 0.99f, maxY);
                }

                minY -= 0.1f;
                index++;
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

        protected void addModel(V newModel) {
            liveCollection.add(newModel);
            refresh();
        }
    }

    public enum CopyMode {

        DISABLED,
        INSTANT,
        SEPARATE_MENU
    }
}
