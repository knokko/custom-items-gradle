package nl.knokko.customitems.plugin.multisupport.skript.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import nl.knokko.customitems.plugin.container.ContainerInstance;

@SuppressWarnings("unused")
public class ClsContainer {

    static {
        Classes.registerClass(new ClassInfo<>(ContainerInstance.class, "kcicontainer")
                .user("kcicontainer")
                .name("KciContainer")
                .description("Custom container of Knokko's Custom Items")
                .defaultExpression(new EventValueExpression<>(ContainerInstance.class))
                .parser(new Parser<ContainerInstance>() {

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public ContainerInstance parse(String input, ParseContext context) {
                        return null;
                    }

                    @Override
                    public String toString(ContainerInstance o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public String toVariableNameString(ContainerInstance container) {
                        return "containerinstance(" + container.getType().getName() + ")";
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "containerinstance(.*)";
                    }
                })
        );
    }
}
