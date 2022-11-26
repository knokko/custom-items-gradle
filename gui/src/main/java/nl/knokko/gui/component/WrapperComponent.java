package nl.knokko.gui.component;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.testing.CheckableComponent;
import nl.knokko.gui.testing.EditableComponent;
import nl.knokko.gui.testing.ImageShowingComponent;
import nl.knokko.gui.testing.TextShowingComponent;

public class WrapperComponent<C extends GuiComponent> extends AbstractGuiComponent 
implements TextShowingComponent, ImageShowingComponent, CheckableComponent, EditableComponent {
    
    protected C component;
    protected GuiColor background;

    private boolean wasActive;
    
    public WrapperComponent(C component){
        this.component = component;
    }
    
    public WrapperComponent(C component, GuiColor background) {
    	this(component);
    	this.background = background;
    }
    
    public void setComponent(C component){
        this.component = component;
        if(component != null && state != null){
            component.setState(state);
            component.init();
        }
    }
    
    public C getComponent(){
        return component;
    }
    
    public boolean isActive(){
        return true;
    }
    
    @Override
    public void setState(GuiComponentState state) {
    	super.setState(state);
    	if(component != null)
    		component.setState(state);
    }

    @Override
    public void init() {
        if(component != null)
            component.init();
    }

    @Override
    public void update() {
        if(component != null && isActive())
            component.update();

        boolean isActive = isActive();
        if (isActive != wasActive) {
            state.getWindow().markChange();
        }

        wasActive = isActive;
    }

    @Override
    public void render(GuiRenderer renderer) {
    	if (background != null)
    		renderer.clear(background);
        if(component != null && isActive())
            component.render(renderer);
    }

    @Override
    public void click(float x, float y, int button) {
        if(component != null && isActive())
            component.click(x, y, button);
    }

    @Override
    public void clickOut(int button) {
        if(component != null && isActive())
            component.clickOut(button);
    }

    @Override
    public boolean scroll(float amount) {
        if(component != null && isActive())
            return component.scroll(amount);
        return false;
    }

    @Override
    public void keyPressed(int keyCode) {
        if(component != null && isActive())
            component.keyPressed(keyCode);
    }

    @Override
    public void keyPressed(char character) {
        if(component != null && isActive())
            component.keyPressed(character);
    }

    @Override
    public void keyReleased(int keyCode) {
        if(component != null && isActive())
            component.keyReleased(keyCode);
    }

	@Override
	public Collection<ImageShowingComponent.Pair> getShowingComponents() {
		if (isActive() && component instanceof ImageShowingComponent) {
			return ((ImageShowingComponent) component).getShowingComponents();
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public Collection<BufferedImage> getShownImages() {
		if (isActive() && component instanceof ImageShowingComponent) {
			return ((ImageShowingComponent) component).getShownImages();
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public TextShowingComponent.Pair getShowingComponent(String text) {
		if (isActive() && component instanceof TextShowingComponent) {
			return ((TextShowingComponent) component).getShowingComponent(text);
		} else {
			return null;
		}
	}

	@Override
	public Collection<TextShowingComponent.Pair> getShowingComponents(String text) {
		if (isActive() && component instanceof TextShowingComponent) {
			return ((TextShowingComponent) component).getShowingComponents(text);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public Collection<CheckableComponent.Pair> getCheckboxCenters() {
		if (isActive() && component instanceof CheckableComponent) {
			return ((CheckableComponent) component).getCheckboxCenters();
		} else {
			return Collections.emptySet();
		}
	}
	
	@Override
	public Collection<EditableComponent.Pair> getEditableLocations() {
		if (isActive() && component instanceof EditableComponent) {
			return ((EditableComponent) component).getEditableLocations();
		} else {
			return Collections.emptySet();
		}
	}
}