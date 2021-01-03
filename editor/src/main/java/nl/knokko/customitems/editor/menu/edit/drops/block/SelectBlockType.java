package nl.knokko.customitems.editor.menu.edit.drops.block;

import java.util.Locale;

import nl.knokko.customitems.drops.BlockType;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

public class SelectBlockType extends GuiMenu {
	
	private final GuiComponent returnMenu;
	private final Receiver receiver;
	
	private final TextEditField searchField;
	private final BlockList list;
	
	private boolean refreshList;
	
	public SelectBlockType(GuiComponent returnMenu, Receiver receiver) {
		this.returnMenu = returnMenu;
		this.receiver = receiver;
		
		this.list = new BlockList();
		this.searchField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE) {
			
			@Override
			protected void updateTexture() {
				super.updateTexture();
				refreshList = true;
			}
		};
	}

	@Override
	protected void addComponents() {
		addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
			state.getWindow().setMainComponent(returnMenu);
		}), 0.025f, 0.8f, 0.2f, 0.9f);
		
		addComponent(new DynamicTextComponent("Search:", EditProps.LABEL), 0.1f, 0.5f, 0.3f, 0.6f);
		addComponent(searchField, 0.1f, 0.4f, 0.3f, 0.5f);
		
		addComponent(list, 0.4f, 0f, 1f, 1f);
	}
	
	@Override
	public GuiColor getBackgroundColor() {
		return EditProps.BACKGROUND;
	}
	
	public static interface Receiver {
		
		void onSelect(BlockType newBlock);
	}
	
	private class BlockList extends GuiMenu {
		
		@Override
		protected void addComponents() {
			refresh();
		}
		
		@Override
		public GuiColor getBackgroundColor() {
			return EditProps.BACKGROUND;
		}
		
		@Override
		public void update() {
			super.update();
			if (refreshList) {
				refresh();
			}
		}
		
		protected void refresh() {
			refreshList = false;
			clearComponents();
			
			BlockType[] blocks = BlockType.values();
			int heightIndex = 0;
			for (BlockType block : blocks) {
				String blockName = block.toString();
				if (blockName.toLowerCase(Locale.ROOT).contains(searchField.getText().toLowerCase(Locale.ROOT))) {
					addComponent(new DynamicTextButton(block.toString(), EditProps.SELECT_BASE, EditProps.SELECT_HOVER, () -> {
						receiver.onSelect(block);
						state.getWindow().setMainComponent(returnMenu);
					}), 0f, 0.9f - heightIndex * 0.1f, Math.min(1f, blockName.length() * 0.05f), 1f - heightIndex * 0.1f);
					heightIndex++;
				}
			}
		}
	}
}
