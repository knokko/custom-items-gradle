package nl.knokko.customitems.editor.menu.edit.container.recipe;

import nl.knokko.customitems.container.energy.RecipeEnergyOperation;
import nl.knokko.customitems.container.energy.RecipeEnergy;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Collection;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ContainerRecipeEnergyCollectionEdit extends InlineCollectionEdit<RecipeEnergy> {

    private final ItemSet itemSet;

    public ContainerRecipeEnergyCollectionEdit(
            GuiComponent returnMenu, ItemSet itemSet,
            Collection<RecipeEnergy> currentCollection,
            Consumer<Collection<RecipeEnergy>> onApply
    ) {
        super(returnMenu, currentCollection, onApply);
        this.itemSet = itemSet;
    }

    @Override
    protected void addRowComponents(int itemIndex, float minY, float maxY) {
        RecipeEnergy item = ownCollection.get(itemIndex);

        addComponent(new DynamicTextComponent("Energy type:", LABEL), 0.21f, minY, 0.32f, maxY);
        addComponent(CollectionSelect.createButton(
                itemSet.energyTypes.references(),
                item::setEnergyType,
                energyTypeReference -> energyTypeReference.get().getName(),
                item.getEnergyTypeReference(), false
        ), 0.33f, minY, 0.45f, maxY);

        addComponent(EnumSelect.createSelectButton(
                RecipeEnergyOperation.class,
                item::setOperation,
                item.getOperation()
        ), 0.5f, minY, 0.7f, maxY);

        addComponent(new EagerIntEditField(
                item.getAmount(), -1_000_000, 1_000_000, EDIT_BASE, EDIT_ACTIVE, item::setAmount
        ), 0.71f, minY, 0.81f, maxY);

        addComponent(new ImageButton(deleteBase, deleteHover, () -> {
            removeItem(itemIndex);
        }), 0.85f, minY, 0.95f, maxY);
    }

    @Override
    protected RecipeEnergy addNew() {
        return new RecipeEnergy(true);
    }

    @Override
    protected String getHelpPage() {
        return "edit menu/containers/recipes/energy.html";
    }
}
