package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class AttributeCollectionEdit extends InlineCollectionEdit<KciAttributeModifier> {

	private final KciAttributeModifier exampleModifier;
	private final boolean showSlot;
	
	public AttributeCollectionEdit(Collection<KciAttributeModifier> currentCollection,
                                   Consumer<Collection<KciAttributeModifier>> onApply,
                                   GuiComponent returnMenu, KciAttributeModifier exampleModifier, boolean showSlot) {
		super(returnMenu, currentCollection, onApply);
		this.exampleModifier = exampleModifier;
		this.showSlot = showSlot;
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		KciAttributeModifier original = ownCollection.get(itemIndex);
		GuiComponent attributeButton = EnumSelect.createSelectButton(KciAttributeModifier.Attribute.class, newAttribute -> {
			ownCollection.get(itemIndex).setAttribute(newAttribute);
		}, original.getAttribute());
		GuiComponent slotButton = EnumSelect.createSelectButton(KciAttributeModifier.Slot.class, newSlot -> {
			ownCollection.get(itemIndex).setSlot(newSlot);
		}, original.getSlot());
		GuiComponent operationButton = EnumSelect.createSelectButton(KciAttributeModifier.Operation.class, newOperation -> {
			ownCollection.get(itemIndex).setOperation(newOperation);
		}, original.getOperation());
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.275f, minY, 0.325f, maxY);
		addComponent(attributeButton, 0.33f, minY, 0.51f, maxY);
		if (showSlot) addComponent(slotButton, 0.525f, minY, 0.65f, maxY);
		addComponent(operationButton, 0.66f, minY, 0.78f, maxY);
		addComponent(new DynamicTextComponent("Value: ", EditProps.LABEL), 0.79f, minY, 0.89f, maxY);
		addComponent(new EagerFloatEditField(original.getValue(), -1024.0, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newValue -> {
			ownCollection.get(itemIndex).setValue(newValue);
		}), 0.9f, minY, 0.995f, maxY);
	}

	@Override
	protected KciAttributeModifier addNew() {
		return exampleModifier.copy(true);
	}

	@Override
	protected String getHelpPage() {
		return "edit menu/items/edit/attributes.html";
	}
}
