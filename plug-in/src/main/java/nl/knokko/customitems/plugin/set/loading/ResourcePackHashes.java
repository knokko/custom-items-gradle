package nl.knokko.customitems.plugin.set.loading;

import java.util.Locale;

class ResourcePackHashes {

    final byte[] sha1;
    final byte[] sha256;

    ResourcePackHashes(byte[] sha1, byte[] sha256) {
        this.sha1 = sha1;
        this.sha256 = sha256;
    }

    String getSha256Hex() {
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : sha256) {
            int leftValue = (b & 0xFF) / 16;
            int rightValue = (b & 0xFF) % 16;
            hexBuilder.append(Integer.toHexString(leftValue)).append(Integer.toHexString(rightValue));
        }
        return hexBuilder.toString().toUpperCase(Locale.ROOT);
    }
}
