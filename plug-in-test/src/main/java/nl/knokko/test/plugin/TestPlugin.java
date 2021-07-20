package nl.knokko.test.plugin;

import customitems.plugin.set.backward.Backward1;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Backward1.testBackwardCompatibility1();
        System.out.println("All unit tests succeeded");
    }
}
