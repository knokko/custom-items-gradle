package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.QuickCollectionEdit;
import nl.knokko.customitems.item.AttributeModifier;
import nl.knokko.customitems.item.AttributeModifier.Attribute;
import nl.knokko.customitems.item.AttributeModifier.Operation;
import nl.knokko.customitems.item.AttributeModifier.Slot;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class AttributeCollectionEdit extends QuickCollectionEdit<AttributeModifier> {

	private final AttributeModifier exampleModifier;
	
	public AttributeCollectionEdit(Collection<AttributeModifier> currentCollection,
			Consumer<Collection<AttributeModifier>> onApply, 
			GuiComponent returnMenu, AttributeModifier exampleModifier) {
		super(currentCollection, onApply, returnMenu);
		this.exampleModifier = exampleModifier;
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		AttributeModifier original = ownCollection.get(itemIndex);
		GuiComponent attributeButton = EnumSelect.createSelectButton(Attribute.class, newAttribute -> {
			AttributeModifier previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new AttributeModifier(
					newAttribute, previous.getSlot(), 
					previous.getOperation(), previous.getValue()));
		}, original.getAttribute());
		GuiComponent slotButton = EnumSelect.createSelectButton(Slot.class, newSlot -> {
			AttributeModifier previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new AttributeModifier(
					previous.getAttribute(), newSlot, previous.getOperation(), 
					previous.getValue()));
		}, original.getSlot());
		GuiComponent operationButton = EnumSelect.createSelectButton(Operation.class, newOperation -> {
			AttributeModifier previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new AttributeModifier(
					previous.getAttribute(), previous.getSlot(), 
					newOperation, previous.getValue()));
		}, original.getOperation());
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.275f, minY, 0.325f, maxY);
		addComponent(attributeButton, 0.33f, minY, 0.51f, maxY);
		addComponent(slotButton, 0.525f, minY, 0.65f, maxY);
		addComponent(operationButton, 0.66f, minY, 0.78f, maxY);
		addComponent(new DynamicTextComponent("Value: ", EditProps.LABEL), 0.79f, minY, 0.89f, maxY);
		addComponent(new EagerFloatEditField(original.getValue(), -1024.0, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newValue -> {
			AttributeModifier previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new AttributeModifier(
					previous.getAttribute(), previous.getSlot(), 
					previous.getOperation(), newValue));
		}), 0.9f, minY, 0.995f, maxY);
	}

	@Override
	protected AttributeModifier addNew() {
		return exampleModifier;
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/attributes.html";
	}
}
