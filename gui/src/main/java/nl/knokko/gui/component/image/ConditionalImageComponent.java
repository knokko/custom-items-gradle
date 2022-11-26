package nl.knokko.gui.component.image;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;

import nl.knokko.gui.render.GuiRenderer;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.util.Condition;

public class ConditionalImageComponent extends SimpleImageComponent {
    
    protected Condition visible;
    
    public ConditionalImageComponent(GuiTexture texture, Condition isVisible) {
        super(texture);
        visible = isVisible;
    }
    
    @Override
    public void render(GuiRenderer renderer){
        if(visible.isTrue())
            super.render(renderer);
    }
    
    @Override
	public Collection<Pair> getShowingComponents(){
		if (visible.isTrue()) {
			return super.getShowingComponents();
		} else {
			return Collections.emptyList();
		}
	}
    
    @Override
	public Collection<BufferedImage> getShownImages() {
		if (visible.isTrue()) {
			return super.getShownImages();
		} else {
			return Collections.emptyList();
		}
	}
}