package nl.knokko.customitems.plugin.multisupport.denizen;

import com.denizenscript.denizen.objects.InventoryTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.plugin.CustomItemsPlugin;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class KciRemoveItemCommand extends AbstractCommand {

    public KciRemoveItemCommand() {
        this.setName("kci_remove");
        this.setSyntax("kci_remove [amount:<int>] [item_name:<string>] from [player:<player>]|[inventory:<inventory>]");
        this.setRequiredArguments(4, 4);
    }

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        List<Argument> arguments = new ArrayList<>(4);
        for (Argument argument : scriptEntry) {
            arguments.add(argument);
        }

        if (arguments.size() != 4) {
            throw new InvalidArgumentsException("Wrong number of arguments: there should be 4, but found " + arguments.size());
        }

        try {
            int amount = parseInt(arguments.get(0).getValue());
            scriptEntry.addObject("amount", amount);
        } catch (NumberFormatException invalidAmount) {
            throw new InvalidArgumentsException("Invalid amount: " + arguments.get(0));
        }

        String itemName = arguments.get(1).getValue();
        scriptEntry.addObject("item_name", itemName);

        if (!arguments.get(2).getValue().equals("from")) {
            throw new InvalidArgumentsException("Third argument (" + arguments.get(2) + ") should be 'from'");
        }

        InventoryTag inventory;
        if (arguments.get(3).matchesArgumentType(InventoryTag.class)) {
            inventory = arguments.get(3).asType(InventoryTag.class);
        } else {
            inventory = arguments.get(3).asType(PlayerTag.class).getInventory();
        }
        scriptEntry.addObject("inventory", inventory);
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        int amount = (int) scriptEntry.getObject("amount");
        String itemName = scriptEntry.getObjectTag("item_name");
        InventoryTag inventory = scriptEntry.getObjectTag("inventory");
        ItemStack[] contents = inventory.getContents();

        ItemSetWrapper itemSet = CustomItemsPlugin.getInstance().getSet();

        int itemIndex = 0;
        while (amount > 0 && itemIndex < contents.length) {
            ItemStack item = contents[itemIndex];
            CustomItemValues customItem = itemSet.getItem(item);
            if (customItem != null && customItem.getName().equals(itemName)) {
                if (amount >= item.getAmount()) {
                    amount -= item.getAmount();
                    contents[itemIndex] = null;
                } else {
                    item.setAmount(item.getAmount() - amount);
                    amount = 0;
                }
            }
            itemIndex++;
        }

        inventory.setContents(contents);
    }
}
