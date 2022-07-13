package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class KciContainerCommand extends AbstractCommand {

    public KciContainerCommand() {
        this.setName("kci_container");
        this.setSyntax("kci_container ([player_name:<player>] opens|destroy) [container_name:<string>] at [host_string:<string>] (and drop items at drop_location:<location>)");
        this.setRequiredArguments(4, 9);
    }

    private void assertIsString(List<Argument> arguments, int index, String expected) throws InvalidArgumentsException {
        if (!arguments.get(index).getValue().equals(expected)) {
            throw new InvalidArgumentsException(
                    "Expected argument " + (index + 1) + " to be '" + expected
                            + "', but found " + arguments.get(index).getValue()
            );
        }
    }

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        List<Argument> arguments = new ArrayList<>(9);
        for (Argument argument : scriptEntry) {
            arguments.add(argument);
        }

        if (arguments.get(0).getValue().equals("destroy")) {
            if (arguments.size() != 4 && arguments.size() != 9) {
                throw new InvalidArgumentsException("Expected 4 or 9 arguments, but found " + arguments.size() + " arguments");
            }
            scriptEntry.addObject("operation", "destroy");
            scriptEntry.addObject("containerName", arguments.get(1).getValue());
            assertIsString(arguments, 2, "at");
            scriptEntry.addObject("hostString", arguments.get(3).getValue());
            if (arguments.size() == 9) {
                assertIsString(arguments, 4, "and");
                assertIsString(arguments, 5, "drop");
                assertIsString(arguments, 6, "items");
                assertIsString(arguments, 7, "at");
                scriptEntry.addObject("dropLocation", arguments.get(8).asType(LocationTag.class));
            }
        } else {
            if (arguments.size() != 5) {
                throw new InvalidArgumentsException("Expected 5 arguments, but found " + arguments.size() + " arguments");
            }
            scriptEntry.addObject("operation", "open");
            scriptEntry.addObject("player", arguments.get(0).asType(PlayerTag.class));
            assertIsString(arguments, 1, "opens");
            scriptEntry.addObject("containerName", arguments.get(2).getValue());
            assertIsString(arguments, 3, "at");
            scriptEntry.addObject("hostString", arguments.get(4).getValue());
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        String containerName = scriptEntry.getObjectTag("containerName");
        String hostString = scriptEntry.getObjectTag("hostString");

        if (scriptEntry.getObjectTag("operation").equals("open")) {
            PlayerTag player = scriptEntry.getObjectTag("player");
            if (!CustomItemsApi.openContainerAtStringHost(player.getPlayerEntity(), containerName, hostString)) {
                Bukkit.getLogger().warning("Attempted to open non-existing container '" + containerName + "' via Denizen");
            }
        } else {
            LocationTag dropLocation = scriptEntry.getObjectTag("dropLocation");
            if (CustomItemsApi.destroyCustomContainersAtStringHost(containerName, hostString, dropLocation) == -1) {
                Bukkit.getLogger().warning("Attempted to destroy instances of non-existing container '" + containerName + "' via Denizen");
            }
        }
    }
}
