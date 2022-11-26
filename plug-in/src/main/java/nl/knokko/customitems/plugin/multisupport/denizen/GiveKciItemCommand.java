package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.InventoryTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import nl.knokko.customitems.plugin.CustomItemsApi;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.command.CommandCustomItemsGive;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class GiveKciItemCommand extends AbstractCommand {

    public GiveKciItemCommand() {
        this.setName("kci_give");
        this.setSyntax("kci_give ([amount:<int>]) [item_name:<string>] to [player:<player>]|[inventory:<inventory>]");
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

        int toIndex = itemIndex + 1;
        int inventoryIndex = toIndex + 1;

        String itemName = arguments.get(itemIndex).getValue();
        if (!CustomItemsApi.hasItem(itemName)) {
            throw new InvalidArgumentsException("There is no custom item named '" + itemName + "'");
        }
        scriptEntry.addObject("item_name", itemName);

        if (!arguments.get(toIndex).getValue().equals("to")) {
            throw new InvalidArgumentsException("Expected second-last argument to be 'to', but found '" + arguments.get(toIndex).getValue() + "'");
        }

        if (arguments.get(inventoryIndex).matchesArgumentType(InventoryTag.class)) {
            scriptEntry.addObject("inventory", arguments.get(inventoryIndex).asType(InventoryTag.class));
        } else {
            scriptEntry.addObject("player", arguments.get(inventoryIndex).asType(PlayerTag.class));
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        int amount = (int) scriptEntry.getObject("amount");
        String itemName = scriptEntry.getObjectTag("item_name");
        InventoryTag inventory = scriptEntry.getObjectTag("inventory");
        PlayerTag player = scriptEntry.getObjectTag("player");

        if (player != null) {
            inventory = player.getInventory();
        }

        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();

        CommandCustomItemsGive.giveCustomItemToInventory(
                itemSet, inventory.getInventory(), itemSet.getItem(itemName), amount
        );
    }
}
