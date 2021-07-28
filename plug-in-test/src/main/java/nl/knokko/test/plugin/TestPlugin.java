package nl.knokko.test.plugin;

import customitems.plugin.set.backward.Backward1;
import customitems.plugin.set.backward.Backward2;
import customitems.plugin.set.backward.Backward3;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Backward1.testBackwardCompatibility1();
        Backward2.testBackwardCompatibility2();
        Backward3.testBackwardCompatibility3();
        System.out.println("All unit tests succeeded");
    }
}
