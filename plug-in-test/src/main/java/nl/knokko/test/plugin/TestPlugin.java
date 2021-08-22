package nl.knokko.test.plugin;

import customitems.plugin.set.backward.*;
import nl.knokko.customitems.util.ValidationException;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            Backward1.testBackwardCompatibility1();
            Backward2.testBackwardCompatibility2();
            Backward3.testBackwardCompatibility3();
            Backward4.testBackwardCompatibility4();
            Backward5.testBackwardCompatibility5();
            Backward6.testBackwardCompatibility6();
            Backward7.testBackwardCompatibility7();
            Backward8.testBackwardCompatibility8();
            Backward9.testBackwardCompatibility9();
            System.out.println("All 9 unit tests succeeded");
        } catch (ValidationException failed) {
            throw new RuntimeException(failed);
        }
    }
}
