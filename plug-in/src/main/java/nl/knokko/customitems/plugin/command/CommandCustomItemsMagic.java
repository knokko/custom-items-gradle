package nl.knokko.customitems.plugin.command;

import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import nl.knokko.customitems.plugin.multisupport.magic.MagicSupport;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

class CommandCustomItemsMagic {

    void handle(CommandSender sender) {
        if (MagicSupport.MAGIC == null) {
            sender.sendMessage(ChatColor.RED + "Magic integration is disabled (probably because Magic is not installed)");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Magic integration is enabled. The list of spells is:");
        for (SpellTemplate spell : MagicSupport.MAGIC.getSpellTemplates()) {
            sender.sendMessage(ChatColor.AQUA + spell.getKey());
        }
    }
}
