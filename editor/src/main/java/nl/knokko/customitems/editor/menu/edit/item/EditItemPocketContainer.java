package nl.knokko.customitems.editor.menu.edit.item;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.item.CustomPocketContainer;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.CustomItemType;
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
import java.util.List;
import java.util.function.Consumer;

public class EditItemPocketContainer extends EditItemBase {

    private static final AttributeModifier EXAMPLE_MODIFIER = new AttributeModifier(
            AttributeModifier.Attribute.ATTACK_DAMAGE,
            AttributeModifier.Slot.MAINHAND,
            AttributeModifier.Operation.ADD, 5.0
    );

    private final CustomPocketContainer toModify;

    private Collection<CustomContainer> containers;

    public EditItemPocketContainer(EditMenu menu, CustomPocketContainer oldValues, CustomPocketContainer toModify) {
        super(menu, oldValues, toModify, CustomItemType.Category.DEFAULT);
        this.toModify = toModify;
        if (oldValues == null) {
            containers = new ArrayList<>(0);
        } else {
            containers = oldValues.getContainers();
        }
    }

    @Override
    protected AttributeModifier getExampleAttributeModifier() {
        return EXAMPLE_MODIFIER;
    }

    @Override
    protected void addComponents() {
        super.addComponents();
        addComponent(new DynamicTextComponent("Containers:", EditProps.LABEL), 0.71f, 0.35f, 0.895f, 0.45f);
        addComponent(new DynamicTextButton("Choose...", EditProps.BUTTON, EditProps.HOVER, () -> {
            state.getWindow().setMainComponent(new SelectContainers(
                    this, menu.getSet().getBackingContainers(),
                    containers, newContainers -> containers = newContainers
            ));
        }), 0.9f, 0.35f, 0.975f, 0.45f);

        HelpButtons.addHelpLink(this, "edit%20menu/items/edit/pocket container.html");
    }

    @Override
    protected String create(float attackRange) {
        return menu.getSet().addPocketContainer(new CustomPocketContainer(
                internalType, nameField.getText(), aliasField.getText(),
                getDisplayName(), lore, attributes, enchantments,
                textureSelect.getSelected(), itemFlags, customModel,
                playerEffects, targetEffects, equippedEffects, commands,
                conditions, op, extraNbt, attackRange, null, containers
        ));
    }

    @Override
    protected String apply(float attackRange) {
        return menu.getSet().changePocketContainer(
                toModify, internalType, aliasField.getText(), getDisplayName(), lore,
                attributes, enchantments, textureSelect.getSelected(),
                itemFlags, customModel, playerEffects,
                targetEffects, equippedEffects, commands, conditions, op,
                extraNbt, attackRange, containers
        );
    }

    @Override
    protected CustomItemType.Category getCategory() {
        return CustomItemType.Category.DEFAULT;
    }

    private static class SelectContainers extends GuiMenu {

        private final GuiComponent returnMenu;
        private final Iterable<CustomContainer> allContainers;
        private final Consumer<Collection<CustomContainer>> applySelection;

        private final Set<CustomContainer> selectedContainers;

        private SelectContainers(
                GuiComponent returnMenu, Iterable<CustomContainer> allContainers,
                Collection<CustomContainer> currentSelection,
                Consumer<Collection<CustomContainer>> applySelection
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
            for (CustomContainer container : allContainers) {
                addComponent(new ContainerButton(container, selectedContainers),
                        0.5f, 0.9f - 0.12f * index, 0.8f, 1.0f - 0.12f * index);
                index++;
            }

            if (!allContainers.iterator().hasNext()) {
                addComponent(
                        new DynamicTextComponent("You don't have any custom containers yet", EditProps.LABEL),
                        0.3f, 0.8f, 0.8f, 0.9f
                );
                addComponent(
                        new DynamicTextComponent("Use the 'Containers' submenu to add custom containers", EditProps.LABEL),
                        0.3f, 0.7f, 1.0f, 0.8f
                );
                addComponent(
                        new DynamicTextComponent("(Click the 'Containers' button in the menu with all the", EditProps.LABEL),
                        0.3f, 0.6f, 1.0f, 0.7f
                );
                addComponent(
                        new DynamicTextComponent("Export buttons and the 'Items' and 'Textures' button)", EditProps.LABEL),
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

            private final CustomContainer container;
            private final Set<CustomContainer> selectedContainers;

            public ContainerButton(CustomContainer container, Set<CustomContainer> selectedContainers) {
                super(
                        container.getName(),
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
