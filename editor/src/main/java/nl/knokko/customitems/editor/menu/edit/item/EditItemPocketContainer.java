package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciItemType;
import nl.knokko.customitems.item.KciPocketContainer;
import nl.knokko.customitems.itemset.ContainerReference;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.util.TextBuilder;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class EditItemPocketContainer extends EditItemBase<KciPocketContainer> {

    private static final KciAttributeModifier EXAMPLE_MODIFIER = KciAttributeModifier.createQuick(
            KciAttributeModifier.Attribute.ATTACK_DAMAGE,
            KciAttributeModifier.Slot.MAINHAND,
            KciAttributeModifier.Operation.ADD,
            5.0
    );

    public EditItemPocketContainer(EditMenu menu, KciPocketContainer oldValues, ItemReference toModify) {
        super(menu, oldValues, toModify);
    }

    @Override
    protected KciAttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(
                new DynamicTextComponent("Containers:", LABEL),
                0.71f, 0.35f, 0.895f, 0.45f
        );
        addComponent(new DynamicTextButton("Choose...", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new SelectContainers(
                    this, menu.getSet().containers.references(), currentValues.getContainerReferences(), currentValues::setContainers
            ));
        }), 0.9f, 0.35f, 0.975f, 0.45f);

        HelpButtons.addHelpLink(this, "edit%20menu/items/edit/pocket container.html");
    }

    @Override
    protected KciItemType.Category getCategory() {
        return KciItemType.Category.DEFAULT;
    }

    private static class SelectContainers extends GuiMenu {

        private final GuiComponent returnMenu;
        private final Iterable<ContainerReference> allContainers;
        private final Consumer<Set<ContainerReference>> applySelection;

        private final Set<ContainerReference> selectedContainers;

        private SelectContainers(
                GuiComponent returnMenu, Iterable<ContainerReference> allContainers,
                Collection<ContainerReference> currentSelection,
                Consumer<Set<ContainerReference>> applySelection
        ) {
            this.returnMenu = returnMenu;
            this.allContainers = allContainers;
            this.selectedContainers = new HashSet<>(currentSelection);
            this.applySelection = applySelection;
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
                state.getWindow().setMainComponent(returnMenu);
            }), 0.025f, 0.7f, 0.15f, 0.8f);

            addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                applySelection.accept(selectedContainers);
                state.getWindow().setMainComponent(returnMenu);
            }), 0.025f, 0.2f, 0.15f, 0.3f);

            int index = 0;
            for (ContainerReference container : allContainers) {
                addComponent(new ContainerButton(container, selectedContainers),
                        0.5f, 0.9f - 0.12f * index, 0.8f, 1.0f - 0.12f * index);
                index++;
            }

            if (!allContainers.iterator().hasNext()) {
                addComponent(
                        new DynamicTextComponent("You don't have any custom containers yet", LABEL),
                        0.3f, 0.8f, 0.8f, 0.9f
                );
                addComponent(
                        new DynamicTextComponent("Use the 'Containers' submenu to add custom containers", LABEL),
                        0.3f, 0.7f, 1.0f, 0.8f
                );
                addComponent(
                        new DynamicTextComponent("(Click the 'Containers' button in the menu with all the", LABEL),
                        0.3f, 0.6f, 1.0f, 0.7f
                );
                addComponent(
                        new DynamicTextComponent("Export buttons and the 'Items' and 'Textures' button)", LABEL),
                        0.3f, 0.5f, 0.9f, 0.6f
                );
            }

            HelpButtons.addHelpLink(this, "edit%20menu/items/edit/pocket container selection.html");
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }

        private static class ContainerButton extends DynamicTextButton {

            private final ContainerReference container;
            private final Set<ContainerReference> selectedContainers;

            public ContainerButton(ContainerReference container, Set<ContainerReference> selectedContainers) {
                super(
                        container.get().getName(),
                        TextBuilder.Properties.createButton(new Color(0, true), Color.BLACK),
                        TextBuilder.Properties.createButton(new Color(0, true), Color.BLUE),
                        () -> {
                            if (selectedContainers.contains(container)) {
                                selectedContainers.remove(container);
                            } else {
                                selectedContainers.add(container);
                            }
                        });
                this.container = container;
                this.selectedContainers = selectedContainers;
            }

            @Override
            public void render(GuiRenderer renderer) {
                if (selectedContainers.contains(container)) {
                    renderer.clear(new SimpleGuiColor(50, 150, 50));
                } else {
                    renderer.clear(new SimpleGuiColor(50, 50, 50));
                }
                super.render(renderer);
            }

            @Override
            public void click(float x, float y, int button) {
                super.click(x, y, button);
                state.getWindow().markChange();
            }
        }
    }
}
