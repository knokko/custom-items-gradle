package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.menu.edit.QuickCollectionEdit;
import nl.knokko.customitems.editor.menu.edit.ReplacementCollectionSelect;
import nl.knokko.customitems.editor.set.item.CustomItem;
import nl.knokko.customitems.item.ReplaceCondition;
import nl.knokko.customitems.item.ReplaceCondition.ConditionOperation;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementCondition;
import nl.knokko.customitems.item.ReplaceCondition.ReplacementOperation;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class ReplacementCollectionEdit extends QuickCollectionEdit<ReplaceCondition> {
	private final ReplaceCondition exampleCondition;
	private final Collection<CustomItem> backingItems;
	private ConditionOperation op;
	private Consumer<ConditionOperation> operation;
	private final int MAX_DEFAULT_SPACE = 2368; // 37 slots of 64 items at most
	
	public ReplacementCollectionEdit(Collection<ReplaceCondition> currentCollection, Consumer<Collection<ReplaceCondition>> onApply, GuiComponent returnMenu, 
			ReplaceCondition exampleCondition, Collection<CustomItem> backingItems, Consumer<ConditionOperation> operation) {
		super(currentCollection, onApply, returnMenu);
		this.exampleCondition = exampleCondition;
		this.backingItems = backingItems;
		this.operation = operation;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(EnumSelect.createSelectButton(ConditionOperation.class, newCondition -> {
			this.op = newCondition;
		}, op), 0.025f, 0.4f, 0.175f, 0.5f);
		
		SubComponent apply = getComponentAt(0.1f, 0.15f);
		removeComponent(apply);
		
		addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
			//Sanity checking whether every item is replaced with the same item when using the AND or OR operators
			if (op == ConditionOperation.AND || op == ConditionOperation.OR) {
				String replacementItem = null;
				for (ReplaceCondition cond: ownCollection) {
					if (replacementItem == null) {
						replacementItem = cond.getReplacingItemName();
					} else if (!replacementItem.equals(cond.getReplacingItemName())) {
						errorComponent.setText("With the OR and AND operators, all replacement items must be the same item. Use NONE if you want the item to be "
								+ "replaced by different items based on different conditions");
					}
				}
			}

			//Sanity checking conditions for always being true or false
			for (ReplaceCondition cond: ownCollection) {
				if (cond.getCondition() == ReplacementCondition.HASITEM) {
					if (cond.getOp() == ReplacementOperation.ATMOST	&& cond.getValue() >= MAX_DEFAULT_SPACE) {
						errorComponent.setText("One of your conditions will always be true, check the condition where " +
								"you have as operation ATMOST with value " + cond.getValue());
					} else if (cond.getOp() == ReplacementOperation.ATLEAST && cond.getValue() >= MAX_DEFAULT_SPACE) {
						errorComponent.setText("One of your conditions will always be false, check the condition where " +
								"you have as operation ATLEAST with value " + cond.getValue());
					} else if (cond.getOp() == ReplacementOperation.EXACTLY && (cond.getValue() >= MAX_DEFAULT_SPACE ||
							cond.getValue() <= 0)) {
						errorComponent.setText("One of your conditions will always be false, check the condition where " +
								"you have as operation EXACTLY with value " + cond.getValue());
					}
				}
			}

			onApply.accept(ownCollection);
			operation.accept(op);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.1f, 0.175f, 0.2f);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		ReplaceCondition original = ownCollection.get(itemIndex);
		GuiComponent conditionButton = EnumSelect.createSelectButton(ReplacementCondition.class, newCondition -> {
			ReplaceCondition previous = ownCollection.get(itemIndex);
			if (newCondition == ReplacementCondition.ISBROKEN) {
				ownCollection.set(itemIndex, new ReplaceCondition(newCondition, null, ReplacementOperation.NONE, 0, previous.getReplacingItemName()));
			} else {
				ownCollection.set(itemIndex, new ReplaceCondition(
						newCondition, previous.getItemName(), previous.getOp(), previous.getValue(), previous.getReplacingItemName()));
			}	
		}, original.getCondition());
		GuiComponent itemButton = ReplacementCollectionSelect.createButton(backingItems, (CustomItem newItem) -> {
			ReplaceCondition previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new ReplaceCondition(
					previous.getCondition(), newItem.getName(), previous.getOp(), previous.getValue(), previous.getReplacingItemName()));
		}, (CustomItem item) -> { return item.getName(); }, ownCollection.get(itemIndex).getItemName());
		GuiComponent operationButton = EnumSelect.createSelectButton(ReplacementOperation.class, newOperation -> {
			ReplaceCondition previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new ReplaceCondition(
					previous.getCondition(), previous.getItemName(), newOperation, previous.getValue(), previous.getReplacingItemName()));
		}, original.getOp());
		GuiComponent replacingItemButton = ReplacementCollectionSelect.createButton(backingItems, (CustomItem newItem) -> {
			ReplaceCondition previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new ReplaceCondition(
					previous.getCondition(), previous.getItemName(), previous.getOp(), previous.getValue(), newItem.getName()));
		}, (CustomItem item) -> { return item.getName(); }, ownCollection.get(itemIndex).getReplacingItemName());
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.25f, minY, 0.3f, maxY);
		addComponent(conditionButton, 0.31f, minY, 0.43f, maxY);
		addComponent(itemButton, 0.445f, minY, 0.57f, maxY);
		addComponent(operationButton, 0.58f, minY, 0.7f, maxY);
		addComponent(new DynamicTextComponent("Value: ", EditProps.LABEL), 0.71f, minY, 0.81f, maxY);
		addComponent(new EagerIntEditField(original.getValue(), (long) -1024.0, 
				EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE, newValue -> {
			ReplaceCondition previous = ownCollection.get(itemIndex);
			ownCollection.set(itemIndex, new ReplaceCondition(
					previous.getCondition(), previous.getItemName(), previous.getOp(), newValue, previous.getReplacingItemName()));
		}), 0.82f, minY, 0.905f, maxY);
		addComponent(replacingItemButton, 0.91f, minY, 0.99f, maxY);
		
	}

	@Override
	protected ReplaceCondition addNew() {
		return exampleCondition;
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/replace.html";
	}
}