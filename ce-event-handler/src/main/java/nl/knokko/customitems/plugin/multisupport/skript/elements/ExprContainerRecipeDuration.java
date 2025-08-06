package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import nl.knokko.customitems.container.ContainerRecipe;
import org.bukkit.event.Event;

@SuppressWarnings("unused")
public class ExprContainerRecipeDuration extends SimpleExpression<Integer> {

	static {
		Skript.registerExpression(
				ExprContainerRecipeDuration.class, Integer.class, ExpressionType.PROPERTY,
				"duration of %kcicontainerrecipe%"
		);
	}

	private Expression<ContainerRecipe> recipe;

	@Override
	protected Integer[] get(Event event) {
		ContainerRecipe recipe = this.recipe.getSingle(event);
		if (recipe == null) return new Integer[] { null };
		return new Integer[] { recipe.getDuration() };
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public String toString(Event event, boolean debug) {
		return "get duration of kci container recipe";
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		this.recipe = (Expression<ContainerRecipe>) expressions[0];
		return true;
	}
}
