package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import nl.knokko.customitems.plugin.CustomItemsApi;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class DropKciItemCommand extends AbstractCommand {

    public DropKciItemCommand() {
        this.setName("kci_drop");
        this.setSyntax("kci_drop ([amount:<int>]) [item_name:<string>] at [location:<location>]");
        this.setRequiredArguments(3, 4);
    }

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        List<Argument> arguments = new ArrayList<>(4);
        for (Argument argument : scriptEntry) {
            arguments.add(argument);
        }

        if (arguments.size() != 3 && arguments.size() != 4) {
            throw new InvalidArgumentsException("Wrong number of arguments: there should be 3 or 4, but found " + arguments.size());
        }

        int amount;
        int itemIndex;
        if (arguments.size() == 3) {
            amount = 1;
            itemIndex = 0;
        } else {
            String rawAmount = arguments.get(0).getValue();
            try {
                amount = parseInt(rawAmount);
            } catch (NumberFormatException invalidAmount) {
                throw new InvalidArgumentsException("Invalid amount: " + rawAmount);
            }
            itemIndex = 1;
        }
        scriptEntry.addObject("amount", amount);

        int atIndex = itemIndex + 1;
        int locationIndex = atIndex + 1;

        String itemName = arguments.get(itemIndex).getValue();
        if (!CustomItemsApi.hasItem(itemName)) {
            throw new InvalidArgumentsException("There is no custom item named '" + itemName + "'");
        }
        scriptEntry.addObject("item_name", itemName);

        if (!arguments.get(atIndex).getValue().equals("at")) {
            throw new InvalidArgumentsException("Expected second-last argument to be 'at', but found '" + arguments.get(atIndex).getValue() + "'");
        }

        scriptEntry.addObject("location", arguments.get(locationIndex).asType(LocationTag.class));
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        int amount = (int) scriptEntry.getObject("amount");
        String itemName = scriptEntry.getObjectTag("item_name");
        Location location = scriptEntry.getObjectTag("location");

        location.getWorld().dropItemNaturally(location, CustomItemsApi.createItemStack(itemName, amount));
    }
}
