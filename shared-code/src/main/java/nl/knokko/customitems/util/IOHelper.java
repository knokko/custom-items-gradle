package nl.knokko.customitems.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IOHelper {

    public static byte[] readAllBytes(InputStream input) throws IOException {
        byte[] rawContent = new byte[1000];
        int numReadBytes = 0;
        while (true) {
            int nextReadBytes = input.read(rawContent, numReadBytes, rawContent.length - numReadBytes);
            if (nextReadBytes == -1) break;

            numReadBytes += nextReadBytes;
            if (numReadBytes == rawContent.length) rawContent = Arrays.copyOf(rawContent, 2 * numReadBytes);
        }

        return Arrays.copyOf(rawContent, numReadBytes);
    }
}
