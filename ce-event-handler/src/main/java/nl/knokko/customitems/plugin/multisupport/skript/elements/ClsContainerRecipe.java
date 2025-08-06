package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import nl.knokko.customitems.container.ContainerRecipe;

@SuppressWarnings("unused")
public class ClsContainerRecipe {

	static {
		Classes.registerClass(new ClassInfo<>(ContainerRecipe.class, "kcicontainerrecipe")
				.user("kcicontainerrecipe")
				.name("KciContainerRecipe")
				.description("Custom container recipe of Knokko's Custom Items")
				.defaultExpression(new EventValueExpression<>(ContainerRecipe.class))
				.parser(new Parser<ContainerRecipe>() {

					@Override
					public boolean canParse(ParseContext context) {
						return false;
					}

					@Override
					public ContainerRecipe parse(String input, ParseContext context) {
						return null;
					}

					@Override
					public String toString(ContainerRecipe o, int flags) {
						return toVariableNameString(o);
					}

					@Override
					public String toVariableNameString(ContainerRecipe recipe) {
						return "containerrecipe()";
					}

					//@Override
					public String getVariableNamePattern() {
						return "containerrecipe(.*)";
					}
				})
		);
	}
}
