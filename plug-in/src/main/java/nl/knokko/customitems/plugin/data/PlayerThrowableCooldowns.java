package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.KciThrowable;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.HashMap;
import java.util.Map;

public class PlayerThrowableCooldowns {

    public void load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("PlayerThrowableCooldowns", encoding);
        if (!cooldownExpireTimes.isEmpty()) throw new IllegalStateException("Loading non-empty throwable cooldowns");

        int numEntries = input.readInt();
        for (int counter = 0; counter < numEntries; counter++) {
            cooldownExpireTimes.put(input.readString(), input.readLong());
        }
    }

    private final Map<String, Long> cooldownExpireTimes = new HashMap<>();

    public void save(BitOutput output) {
        output.addByte((byte) 1);
        output.addInt(cooldownExpireTimes.size());
        cooldownExpireTimes.forEach((throwableName, expiresAt) -> {
            output.addString(throwableName);
            output.addLong(expiresAt);
        });
    }

    public boolean isOnCooldown(KciThrowable throwable, long currentTick) {
        Long cooldownExpiresAt = cooldownExpireTimes.get(throwable.getName());
        return cooldownExpiresAt != null && cooldownExpiresAt > currentTick;
    }

    public void setOnCooldown(KciThrowable throwable, long currentTick) {
        cooldownExpireTimes.put(throwable.getName(), currentTick + throwable.getCooldown());
    }

    public boolean clean(long currentTick) {
        cooldownExpireTimes.values().removeIf(expireTime -> expireTime <= currentTick);
        return cooldownExpireTimes.isEmpty();
    }
}
