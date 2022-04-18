package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

public class IOHelper {

    public static BitInput getResourceBitInput(String resourcePath, int resourceSize) {
        try {
            DataInputStream dataInput = new DataInputStream(Objects.requireNonNull(
                    TestPlayerCommandCooldowns.class.getClassLoader().getResourceAsStream(resourcePath)
            ));
            byte[] dataBytes = new byte[resourceSize];
            dataInput.readFully(dataBytes);
            dataInput.close();

            return new ByteArrayBitInput(dataBytes);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }
}
