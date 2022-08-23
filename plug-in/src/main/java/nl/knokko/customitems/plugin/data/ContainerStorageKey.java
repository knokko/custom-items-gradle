package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.Objects;
import java.util.UUID;

public class ContainerStorageKey {

    public static ContainerStorageKey load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ContainerStorageKey", encoding);

        String name = input.readString();

        PassiveLocation location = null;
        if (input.readBoolean()) {
            UUID worldId = new UUID(input.readLong(), input.readLong());
            int x = input.readInt();
            int y = input.readInt();
            int z = input.readInt();
            location = new PassiveLocation(worldId, x, y, z);
        }

        String stringHost = input.readString();

        UUID playerId = input.readBoolean() ? new UUID(input.readLong(), input.readLong()) : null;

        return new ContainerStorageKey(name, location, stringHost, playerId);
    }

    /**
     * The name of the container type to which the container instance belongs
     */
    public final String containerName;

    /**
     * The location to which the container instance is bound, or null if the container is not bound to a location
     */
    public final PassiveLocation location;

    /**
     * The string host to which the container instance is bound, or null if the container is not bound to a string host
     */
    public final String stringHost;

    /**
     * The UUID of the player that can access the container instance, or null if it is shared between all players
     */
    public final UUID playerID;

    public ContainerStorageKey(String containerName, PassiveLocation location, String stringHost, UUID playerID) {
        this.containerName = containerName;
        this.location = location;
        this.stringHost = stringHost;
        this.playerID = playerID;
    }

    public ContainerStorageKey(ContainerStorageKey toCopy) {
        this(toCopy.containerName, toCopy.location, toCopy.stringHost, toCopy.playerID);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ContainerStorageKey) {
            ContainerStorageKey otherKey = (ContainerStorageKey) other;
            return Objects.equals(this.containerName, otherKey.containerName) && Objects.equals(this.location, otherKey.location)
                    && Objects.equals(this.stringHost, otherKey.stringHost) && Objects.equals(this.playerID, otherKey.playerID);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(containerName) + 71 * Objects.hashCode(location) + 791 * Objects.hashCode(stringHost)
                + 3421 * Objects.hashCode(playerID);
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        // Note that the addString method supports null
        output.addString(containerName);

        output.addBoolean(location != null);
        if (location != null) {
            output.addLong(location.getWorldId().getMostSignificantBits());
            output.addLong(location.getWorldId().getLeastSignificantBits());
            output.addInt(location.getX());
            output.addInt(location.getY());
            output.addInt(location.getZ());
        }

        output.addString(stringHost);

        output.addBoolean(playerID != null);
        if (playerID != null) {
            output.addLong(playerID.getMostSignificantBits());
            output.addLong(playerID.getLeastSignificantBits());
        }
    }
}
