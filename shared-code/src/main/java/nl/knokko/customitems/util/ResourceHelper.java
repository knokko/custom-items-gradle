package nl.knokko.customitems.util;

public class ResourceHelper {

    public static String[] chain(String[]...arrays) {
        int length = 0;
        for (String[] array : arrays) {
            length += array.length;
        }
        String[] result = new String[length];
        int index = 0;
        for (String[] array : arrays) {
            System.arraycopy(array, 0, result, index, array.length);
            index += array.length;
        }
        return result;
    }
}
