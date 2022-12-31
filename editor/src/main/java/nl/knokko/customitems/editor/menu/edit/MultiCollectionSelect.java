package nl.knokko.customitems.editor.menu.edit;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class MultiCollectionSelect<T> extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Iterable<T> fullCollection;
    private final Consumer<Collection<T>> confirmSelection;
    private final Collection<T> selectedElements;

    private final SelectedElementList selectedElementsComponent;
    private final UnselectedElementList unselectedElementsComponent;
    private final EagerTextEditField searchField;

    public MultiCollectionSelect(
            GuiComponent returnMenu, Iterable<T> fullCollection,
            Consumer<Collection<T>> confirmSelection, Collection<T> originalSelection
    ) {
        this.returnMenu = returnMenu;
        this.fullCollection = fullCollection;
        this.confirmSelection = confirmSelection;
        this.selectedElements = new ArrayList<>(originalSelection);

        this.selectedElementsComponent = new SelectedElementList();
        this.unselectedElementsComponent = new UnselectedElementList();
        this.searchField = new EagerTextEditField("", EDIT_BASE, EDIT_ACTIVE, newText -> refresh());
    }

    private void refresh() {
        selectedElementsComponent.clearComponents();
        unselectedElementsComponent.clearComponents();
        selectedElementsComponent.addComponents();
        unselectedElementsComponent.addComponents();
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.175f, 0.8f);

        addComponent(new DynamicTextButton("Done", SAVE_BASE, SAVE_HOVER, () -> {
            confirmSelection.accept(selectedElements);
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.2f, 0.15f, 0.3f);

        addComponent(new DynamicTextComponent("Search:", LABEL), 0.05f, 0.5f, 0.15f, 0.6f);
        addComponent(searchField, 0.05f, 0.4f, 0.2f, 0.5f);

        addComponent(new DynamicTextComponent("Currently selected:", LABEL), 0.25f, 0.9f, 0.45f, 1f);
        addComponent(selectedElementsComponent, 0.25f, 0f, 0.5f, 0.9f);

        addComponent(new DynamicTextComponent("Select...", LABEL), 0.6f, 0.9f, 0.75f, 1f);
        addComponent(unselectedElementsComponent, 0.6f, 0f, 0.85f, 0.9f);
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class SelectedElementList extends GuiMenu {

        @Override
        protected void addComponents() {
            int index = 0;
            for (T element : selectedElements) {
                float minY = 0.9f - index * 0.125f;
                addComponent(new DynamicTextComponent(element.toString(), LABEL), 0f, minY, 0.8f, minY + 0.1f);
                addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                    selectedElements.remove(element);
                    refresh();
                }), 0.85f, minY, 1f, minY + 0.1f);
                index += 1;
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }

    private class UnselectedElementList extends GuiMenu {

        @Override
        protected void addComponents() {
            int index = 0;
            for (T element : fullCollection) {
                if (
                        element.toString().toLowerCase(Locale.ROOT).contains(searchField.getText().toLowerCase(Locale.ROOT))
                                && !selectedElements.contains(element)
                ) {
                    float minY = 0.9f - index * 0.125f;
                    addComponent(new DynamicTextButton(element.toString(), SELECT_BASE, SELECT_HOVER, () -> {
                        selectedElements.add(element);
                        refresh();
                    }), 0f, minY, 1f, minY + 0.1f);
                    index += 1;
                }
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }
}
