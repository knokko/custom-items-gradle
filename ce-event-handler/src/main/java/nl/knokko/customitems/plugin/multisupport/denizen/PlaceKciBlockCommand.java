package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PlaceKciBlockCommand extends AbstractCommand {

    public PlaceKciBlockCommand() {
        this.setName("kci");
        this.setSyntax("kci place [block_name:<string>] (at destination:<location>)");
        this.setRequiredArguments(2, 4);
    }

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        List<Argument> arguments = new ArrayList<>(4);
        for (Argument argument : scriptEntry) {
            arguments.add(argument);
        }

        if (arguments.size() != 2 && arguments.size() != 4) {
            throw new InvalidArgumentsException("Wrong number of arguments: there should be 2 or 4, but found " + arguments.size());
        }

        if (!arguments.get(0).getValue().equals("place")) {
            throw new InvalidArgumentsException("Expected first argument to be 'place', but found '" + arguments.get(0).getValue() + "'");
        }
        String blockName = arguments.get(1).getValue();
        if (!CustomItemsApi.hasBlock(blockName)) {
            throw new InvalidArgumentsException("There is no custom block named '" + blockName + "'");
        }
        scriptEntry.addObject("block_name", blockName);

        if (arguments.size() > 2) {
            if (!arguments.get(2).getValue().equals("at")) {
                throw new InvalidArgumentsException("Expected third argument to be 'at', but found '" + arguments.get(2).getValue() + "'");
            }
            scriptEntry.addObject("destination", arguments.get(3).asType(LocationTag.class));
        } else {
            LocationTag destination = Utilities.entryDefaultLocation(scriptEntry, false);
            if (destination != null) {
                scriptEntry.addObject("destination", destination);
            } else {
                throw new InvalidArgumentsException("You must specify a location");
            }
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        String blockName = scriptEntry.getObjectTag("block_name");
        Location destination = scriptEntry.getObjectTag("destination");
        if (blockName != null && destination != null) {
            CustomItemsApi.placeBlock(destination.getBlock(), blockName);
        }
    }
}
