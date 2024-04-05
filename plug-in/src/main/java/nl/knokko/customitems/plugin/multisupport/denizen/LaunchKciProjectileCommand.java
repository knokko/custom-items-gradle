package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import nl.knokko.customitems.plugin.CustomItemsApi;

import java.util.ArrayList;
import java.util.List;

public class LaunchKciProjectileCommand extends AbstractCommand {

    public LaunchKciProjectileCommand() {
        this.setName("kci_launch");
        this.setSyntax("kci_launch [projectile_name:<string>] from [shooter:<entity>]");
        this.setRequiredArguments(3, 3);
    }

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        List<Argument> arguments = new ArrayList<>(3);
        for (Argument argument : scriptEntry) {
            arguments.add(argument);
        }

        if (arguments.size() != 3) {
            throw new InvalidArgumentsException("Wrong number of arguments: there should be 2, but found " + arguments.size());
        }

        String projectileName = arguments.get(0).getValue();
        if (!CustomItemsApi.hasProjectile(projectileName)) {
            throw new InvalidArgumentsException("There is no projectile named '" + projectileName + "'");
        }
        scriptEntry.addObject("projectile_name", projectileName);
        if (!arguments.get(1).getValue().equals("from")) {
            throw new InvalidArgumentsException("Expected second argument to be 'from', but found '" + arguments.get(1).getValue() + "'");
        }
        EntityTag shooter = arguments.get(2).asType(EntityTag.class);
        if (!shooter.isLivingEntity()) {
            throw new InvalidArgumentsException("Shooter must be a living entity");
        }
        scriptEntry.addObject("shooter", shooter);
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        String projectileName = scriptEntry.getObjectTag("projectile_name");
        EntityTag shooter = scriptEntry.getObjectTag("shooter");
        if (projectileName != null && shooter != null) {
            CustomItemsApi.launchProjectile(shooter.getLivingEntity(), projectileName);
        }
    }
}
