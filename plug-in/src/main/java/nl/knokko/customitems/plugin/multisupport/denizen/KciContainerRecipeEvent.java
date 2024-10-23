package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import nl.knokko.customitems.plugin.container.ContainerInstance;
import nl.knokko.customitems.plugin.events.CustomContainerRecipeEvent;

abstract class KciContainerRecipeEvent extends KciContainerEvent {

    CustomContainerRecipeEvent event;

    @Override
	ContainerInstance getInstance() {
        return event.container;
    }

    @Override
    public ObjectTag getContext(String name) {
		if (name.equals("recipe_duration")) return new ElementTag(event.recipe.getDuration());
		if (name.equals("recipe_experience")) return new ElementTag(event.recipe.getExperience());
		return super.getContext(name);
    }
}
