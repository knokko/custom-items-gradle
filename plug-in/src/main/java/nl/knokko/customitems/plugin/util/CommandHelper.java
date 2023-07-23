package nl.knokko.customitems.plugin.util;

import java.util.ArrayList;
import java.util.List;

public class CommandHelper {

    public static String[] escapeArgs(String... original) {
        List<String> newArgs = new ArrayList<>(original.length);
        StringBuilder currentArg = null;
        for (String newArg : original) {
            int indexQuote = newArg.indexOf("'");
            if (indexQuote == -1) {
                if (currentArg == null) newArgs.add(newArg);
                else currentArg.append(" ").append(newArg);
            } else {
                if (newArg.lastIndexOf("'") != indexQuote) return original; // Invalid syntax, return original
                if (currentArg == null) {
                    if (indexQuote == 0) currentArg = new StringBuilder(newArg.substring(1));
                    else return original; // In case of invalid syntax, return original
                } else {
                    if (indexQuote == newArg.length() - 1) {
                        newArgs.add(currentArg + " " + newArg.substring(0, indexQuote));
                        currentArg = null;
                    } else return original; // Invalid syntax
                }
            }
        }

        return newArgs.toArray(new String[0]);
    }
}
