package nl.knokko.test.plugin;

import customitems.plugin.set.backward.*;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Backward1.testBackwardCompatibility1();
        Backward2.testBackwardCompatibility2();
        Backward3.testBackwardCompatibility3();
        Backward4.testBackwardCompatibility4();
        Backward5.testBackwardCompatibility5();
        System.out.println("All unit tests succeeded");
    }
}
