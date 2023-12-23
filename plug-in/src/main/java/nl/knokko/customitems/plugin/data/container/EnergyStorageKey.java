package nl.knokko.customitems.plugin.data.container;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.Objects;
import java.util.UUID;

public class EnergyStorageKey {

    public static EnergyStorageKey load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("EnergyStorageKey", encoding);

        UUID energyId = new UUID(input.readLong(), input.readLong());
        ContainerStorageKey containerKey = ContainerStorageKey.load(input);

        return new EnergyStorageKey(energyId, containerKey);
    }

    public final UUID energyId;
    public final ContainerStorageKey containerKey;

    public EnergyStorageKey(UUID energyId, ContainerStorageKey containerKey) {
        this.energyId = Objects.requireNonNull(energyId);
        this.containerKey = Objects.requireNonNull(containerKey);
    }

    @Override
    public int hashCode() {
        return energyId.hashCode() + 13 * containerKey.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnergyStorageKey) {
            EnergyStorageKey otherKey = (EnergyStorageKey) other;
            return this.energyId.equals(otherKey.energyId) && this.containerKey.equals(otherKey.containerKey);
        } else {
            return false;
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addLong(energyId.getMostSignificantBits());
        output.addLong(energyId.getLeastSignificantBits());
        containerKey.save(output);
    }
}
