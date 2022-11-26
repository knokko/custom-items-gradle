package nl.knokko.gui.component;

import nl.knokko.gui.component.state.GuiComponentState;

public abstract class AbstractGuiComponent implements GuiComponent {
	
	protected GuiComponentState state;
	
	public AbstractGuiComponent(){}
	
	public void setState(GuiComponentState state) {
		if(state == null)
			throw new NullPointerException();
		this.state = state;
	}
	
	public GuiComponentState getState(){
		return state;
	}
}