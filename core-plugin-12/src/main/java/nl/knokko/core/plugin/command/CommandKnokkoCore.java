/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.core.plugin.command;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandKnokkoCore implements CommandExecutor {
	
	private CommandItemAttribute itemAttribute = new CommandItemAttribute();
	private CommandItemName itemName = new CommandItemName();
	private CommandTest test = new CommandTest();
	private CommandItemTag tag = new CommandItemTag();
	
	private void sendUseage(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "You should use /knokkocore itemattribute/itemname/test");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.isOp()) {
			if (args.length > 0) {
				String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
				if (args[0].equals("itemattribute"))
					itemAttribute.onCommand(sender, command, args[0], subArgs);
				else if (args[0].equals("itemname"))
					itemName.onCommand(sender, command, args[0], subArgs);
				else if (args[0].equals("test"))
					test.onCommand(sender, command, args[0], subArgs);
				else if (args[0].equals("tag"))
					tag.onCommand(sender, command, label, subArgs);
				else
					sendUseage(sender);
			} else {
				sendUseage(sender);
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "You do not have access to this command.");
		}
		return false;
	}
}