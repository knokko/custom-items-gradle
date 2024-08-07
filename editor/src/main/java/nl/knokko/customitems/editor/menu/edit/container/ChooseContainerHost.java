package nl.knokko.customitems.editor.menu.edit.container;

import nl.knokko.customitems.container.ContainerHost;
import nl.knokko.customitems.container.VContainerType;
import nl.knokko.customitems.drops.VEntityType;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.item.VMaterial;
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
    private final Consumer<ContainerHost> onSelect;
    private final ItemSet itemSet;

    public ChooseContainerHost(
            GuiComponent returnMenu, Consumer<ContainerHost> onSelect, ItemSet itemSet
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
                    VContainerType.class,
                    chosenType -> onSelect.accept(new ContainerHost(chosenType)),
                    candidateType -> candidateType != VContainerType.NONE,
                    returnMenu
            ));
        }), 0.3f, 0.85f, 0.5f, 0.95f);
        addComponent(
                new DynamicTextComponent("Players can interact by left-clicking while sneaking", LABEL),
                0.35f, 0.8f, 0.8f, 0.85f
        );

        addComponent(new DynamicTextButton("Choose vanilla block", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnumSelect<>(
                    VMaterial.class,
                    chosenMaterial -> onSelect.accept(new ContainerHost(chosenMaterial)),
                    candidateMaterial -> true,
                    returnMenu
            ));
        }), 0.3f, 0.65f, 0.5f, 0.75f);
        addComponent(
                new DynamicTextComponent("Players can interact by right-clicking", LABEL),
                0.35f, 0.6f, 0.7f, 0.65f
        );

        addComponent(new DynamicTextButton("Choose vanilla entity [1.14+]", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new EnumSelect<>(
                    VEntityType.class,
                    chosenEntity -> onSelect.accept(new ContainerHost(chosenEntity)),
                    candidateEntity -> true,
                    returnMenu
            ));
        }), 0.3f, 0.45f, 0.6f, 0.55f);
        addComponent(new DynamicTextComponent(
                "Players can interact by right-clicking", LABEL
        ), 0.35f, 0.4f, 0.7f, 0.45f);

        addComponent(new DynamicTextButton("Choose custom block", BUTTON, HOVER, () -> {
            state.getWindow().setMainComponent(new CollectionSelect<>(
                    itemSet.blocks.references(),
                    chosenBlock -> onSelect.accept(new ContainerHost(chosenBlock)),
                    candidateBlock -> true,
                    candidateBlock -> candidateBlock.get().getName(),
                    returnMenu, false
            ));
        }), 0.3f, 0.25f, 0.5f, 0.35f);
        addComponent(
                new DynamicTextComponent("Players can interact by right-clicking", LABEL),
                0.35f, 0.2f, 0.7f, 0.25f
        );

        addComponent(new DynamicTextButton("Choose no host", BUTTON, HOVER, () -> {
            onSelect.accept(new ContainerHost(VContainerType.NONE));
            state.getWindow().setMainComponent(returnMenu);
        }), 0.3f, 0.05f, 0.5f, 0.15f);
        addComponent(
                new DynamicTextComponent("This container can only be used as pocket container", LABEL),
                0.35f, 0f, 0.8f, 0.05f
        );
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }
}
