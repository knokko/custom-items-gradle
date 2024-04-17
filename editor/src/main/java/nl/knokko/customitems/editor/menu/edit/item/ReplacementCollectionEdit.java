package nl.knokko.customitems.editor.menu.edit.item;

import java.util.Collection;
import java.util.function.Consumer;

import nl.knokko.customitems.editor.menu.edit.*;
import nl.knokko.customitems.editor.menu.edit.collection.InlineCollectionEdit;
import nl.knokko.customitems.item.ReplacementConditionEntry;
import nl.knokko.customitems.itemset.ItemReference;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.ImageButton;
import nl.knokko.gui.component.text.EagerIntEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ReplacementCollectionEdit extends InlineCollectionEdit<ReplacementConditionEntry> {

	private final ReplacementConditionEntry exampleCondition;
	private final Iterable<ItemReference> backingItems;
	private ReplacementConditionEntry.ConditionOperation op;
	private final Consumer<ReplacementConditionEntry.ConditionOperation> changeOperation;
	private final int MAX_DEFAULT_SPACE = 2368; // 37 slots of 64 items at most
	
	public ReplacementCollectionEdit(
			Collection<ReplacementConditionEntry> currentCollection,
			ReplacementConditionEntry.ConditionOperation currentOp,
			Consumer<Collection<ReplacementConditionEntry>> onApply,
			GuiComponent returnMenu,
			ReplacementConditionEntry exampleCondition,
			Iterable<ItemReference> backingItems,
			Consumer<ReplacementConditionEntry.ConditionOperation> changeOperation) {
		super(returnMenu, currentCollection, onApply);
		this.exampleCondition = exampleCondition;
		this.backingItems = backingItems;
		this.changeOperation = changeOperation;
		this.op = currentOp;
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		addComponent(EnumSelect.createSelectButton(ReplacementConditionEntry.ConditionOperation.class, newCondition -> {
			this.op = newCondition;
		}, op), 0.025f, 0.4f, 0.175f, 0.5f);
		
		SubComponent apply = getComponentAt(0.1f, 0.15f);
		removeComponent(apply);
		
		addComponent(new DynamicTextButton("Apply", SAVE_BASE, SAVE_HOVER, () -> {
			//Sanity checking whether every item is replaced with the same item when using the AND or OR operators
			if (op == ReplacementConditionEntry.ConditionOperation.AND || op == ReplacementConditionEntry.ConditionOperation.OR) {
				String replacementItem = null;
				for (ReplacementConditionEntry cond: ownCollection) {
					if (cond.getReplaceItemReference() == null) {
						errorComponent.setText("You must choose a replace item");
						return;
					}
					if (replacementItem == null) {
						replacementItem = cond.getReplaceItem().getName();
					} else if (!replacementItem.equals(cond.getReplaceItem().getName())) {
						errorComponent.setText("With the OR and AND operators, all replacement items must be the same item. Use NONE if you want the item to be "
								+ "replaced by different items based on different conditions");
						return;
					}
				}
			}

			//Sanity checking conditions for always being true or false
			for (ReplacementConditionEntry cond: ownCollection) {
				if (cond.getCondition() == ReplacementConditionEntry.ReplacementCondition.HASITEM) {
					if (cond.getOperation() == ReplacementConditionEntry.ReplacementOperation.ATMOST && cond.getValue() >= MAX_DEFAULT_SPACE) {
						errorComponent.setText("One of your conditions will always be true, check the condition where " +
								"you have as operation ATMOST with value " + cond.getValue());
						return;
					} else if (cond.getOperation() == ReplacementConditionEntry.ReplacementOperation.ATLEAST && cond.getValue() >= MAX_DEFAULT_SPACE) {
						errorComponent.setText("One of your conditions will always be false, check the condition where " +
								"you have as operation ATLEAST with value " + cond.getValue());
						return;
					} else if (cond.getOperation() == ReplacementConditionEntry.ReplacementOperation.EXACTLY && (cond.getValue() >= MAX_DEFAULT_SPACE ||
							cond.getValue() <= 0)) {
						errorComponent.setText("One of your conditions will always be false, check the condition where " +
								"you have as operation EXACTLY with value " + cond.getValue());
						return;
					}
				}
			}

			onApply.accept(ownCollection);
			changeOperation.accept(op);
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.1f, 0.175f, 0.2f);
	}

	@Override
	protected void addRowComponents(int itemIndex, float minY, float maxY) {
		ReplacementConditionEntry replaceCondition = ownCollection.get(itemIndex);
		GuiComponent conditionButton = EnumSelect.createSelectButton(
				ReplacementConditionEntry.ReplacementCondition.class, replaceCondition::setCondition, replaceCondition.getCondition()
		);

		GuiComponent itemButton = CollectionSelect.createButton(
				backingItems, replaceCondition::setItem, item -> item.get().getName(),
				replaceCondition.getItemReference(), false
		);
		GuiComponent operationButton = EnumSelect.createSelectButton(
				ReplacementConditionEntry.ReplacementOperation.class, replaceCondition::setOperation, replaceCondition.getOperation()
		);
		GuiComponent replacingItemButton = CollectionSelect.createButton(
				backingItems, replaceCondition::setReplaceItem,
				item -> item.get().getName(), replaceCondition.getReplaceItemReference(), false
		);
		addComponent(new ImageButton(deleteBase, deleteHover, () -> {
			removeItem(itemIndex);
		}), 0.25f, minY, 0.3f, maxY);
		addComponent(conditionButton, 0.31f, minY, 0.43f, maxY);
		addComponent(itemButton, 0.445f, minY, 0.57f, maxY);
		addComponent(operationButton, 0.58f, minY, 0.7f, maxY);
		addComponent(new DynamicTextComponent("Value: ", EditProps.LABEL), 0.71f, minY, 0.81f, maxY);
		addComponent(
				new EagerIntEditField(replaceCondition.getValue(), (long) -1024.0, EDIT_BASE, EDIT_ACTIVE, replaceCondition::setValue),
				0.82f, minY, 0.905f, maxY
		);
		addComponent(replacingItemButton, 0.91f, minY, 0.99f, maxY);
	}

	@Override
	protected ReplacementConditionEntry addNew() {
		return exampleCondition;
	}

	@Override
	protected String getHelpPage() {
		return "edit%20menu/items/edit/replace.html";
	}
}