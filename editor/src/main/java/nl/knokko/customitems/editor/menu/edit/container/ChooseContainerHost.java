package nl.knokko.customitems.editor.menu.edit.container;

import nl.knokko.customitems.container.CustomContainerHost;
import nl.knokko.customitems.container.VanillaContainerType;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ChooseContainerHost extends GuiMenu {

    private final GuiComponent returnMenu;
    private final Consumer<CustomContainerHost> onSelect;
    private final ItemSet itemSet;

    public ChooseContainerHost(
            GuiComponent returnMenu, Consumer<CustomContainerHost> onSelect, ItemSet itemSet
    ) {
        this.returnMenu = returnMenu;
        this.onSelect = onSelect;
        this.itemSet = itemSet;
    }

    @Override
    protected void addComponents() {
        HelpButtons.addHelpLink(this, "edit menu/containers/host.html");
        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.1f, 0.7f, 0.2f, 0.8f);

        addComponent(new DynamicTextButton("Choose vanilla container", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnumSelect<>(
                    VanillaContainerType.class,
                    chosenType -> onSelect.accept(new CustomContainerHost(chosenType)),
                    candidateType -> candidateType != VanillaContainerType.NONE,
                    returnMenu
            ));
        }), 0.3f, 0.8f, 0.5f, 0.9f);
        addComponent(
                new DynamicTextComponent("Players can interact by left-clicking while sneaking", LABEL),
                0.35f, 0.75f, 0.8f, 0.8f
        );

        addComponent(new DynamicTextButton("Choose vanilla block", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnumSelect<>(
                    CIMaterial.class,
                    chosenMaterial -> onSelect.accept(new CustomContainerHost(chosenMaterial)),
                    candidateMaterial -> true,
                    returnMenu
            ));
        }), 0.3f, 0.6f, 0.5f, 0.7f);
        addComponent(
                new DynamicTextComponent("Players can interact by right-clicking", LABEL),
                0.35f, 0.55f, 0.7f, 0.6f
        );

        addComponent(new DynamicTextButton("Choose custom block", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CollectionSelect<>(
                    itemSet.getBlocks().references(),
                    chosenBlock -> onSelect.accept(new CustomContainerHost(chosenBlock)),
                    candidateBlock -> true,
                    candidateBlock -> candidateBlock.get().getName(),
                    returnMenu
            ));
        }), 0.3f, 0.4f, 0.5f, 0.5f);
        addComponent(
                new DynamicTextComponent("Players can interact by right-clicking", LABEL),
                0.35f, 0.35f, 0.7f, 0.4f
        );

        addComponent(new DynamicTextButton("Choose no host", BUTTON, HOVER, () -> {
            onSelect.accept(new CustomContainerHost(VanillaContainerType.NONE));
            state.getWindow().setMainComponent(returnMenu);
        }), 0.3f, 0.2f, 0.5f, 0.3f);
        addComponent(
                new DynamicTextComponent("This container can only be used as pocket container", LABEL),
                0.35f, 0.15f, 0.8f, 0.2f
        );
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
