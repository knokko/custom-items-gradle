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
package nl.knokko.core.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.core.plugin.command.*;
import nl.knokko.core.plugin.menu.MenuEventHandler;

public class CorePlugin extends JavaPlugin {
	
	private static CorePlugin instance;
	
	public static CorePlugin getInstance() {
		return instance;
	}
	
	/**
	 * The command usage of minecraft changed drastically when the updated from 1.12 to 1.13.
	 * This method can be used to determine whether the current server version uses the old command
	 * system (1.12 and earlier) or the new command system (1.13 and later).
	 * 
	 * Since a separate version of Knokko Core is needed for every minecraft version, the result of
	 * this method is simply hardcoded in the Knokko Core version.
	 * @return
	 */
	public static boolean useNewCommands() {
		return false;
	}
	
	private MenuEventHandler menuHandler;
	
	public MenuEventHandler getMenuHandler() {
		return menuHandler;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		menuHandler = new MenuEventHandler();
		Bukkit.getPluginManager().registerEvents(menuHandler, this);
		getCommand("knokkocore").setExecutor(new CommandKnokkoCore());
	}
}