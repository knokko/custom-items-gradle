package nl.knokko.customitems.item.command;

public enum CommandSubstitution {

    PLAYER_NAME(
            "player_name", "The name of the player that is holding this item"
    ),
    WORLD_NAME(
            "world_name", "The name of the world in which the event takes place"
    ),
    PLAYER_X(
            "player_x", "The x-coordinate of the player that is holding this item"
    ),
    PLAYER_Y(
            "player_y", "The y-coordinate of the player that is holding this item"
    ),
    PLAYER_Z(
            "player_z", "The z-coordinate of the player that is holding this item"
    ),
    PLAYER_BLOCK_X(
            "player_block_x",
            "The x-coordinate of the block on which the player that is holding this item is standing"
    ),
    PLAYER_BLOCK_Y(
            "player_block_y",
            "The y-coordinate of the block on which the player that is holding this item is standing"
    ),
    PLAYER_BLOCK_Z(
            "player_block_z",
            "The z-coordinate of the block on which the player that is holding this item is standing"
    ),
    BLOCK_X(
            "block_x", "The x-coordinate of the target block"
    ),
    BLOCK_Y(
            "block_y", "The y-coordinate of the target block"
    ),
    BLOCK_Z(
            "block_z", "The z-coordinate of the target block"
    ),
    TARGET_NAME(
            "target_name", "The name of the target player"
    ),
    TARGET_X(
            "target_x", "The x-coordinate of the target entity"
    ),
    TARGET_Y(
            "target_y", "The y-coordinate of the target entity"
    ),
    TARGET_Z(
            "target_z", "The z-coordinate of the target entity"
    ),
    TARGET_BLOCK_X(
            "target_block_x",
            "The x-coordinate of the block on which the target entity is standing"
    ),
    TARGET_BLOCK_Y(
            "target_block_y",
            "The y-coordinate of the block on which the target entity is standing"
    ),
    TARGET_BLOCK_Z(
            "target_block_z",
            "The z-coordinate of the block on which the target entity is standing"
    );

    static final CommandSubstitution[] IMPLICIT_SUBSTITUTIONS = {
            CommandSubstitution.PLAYER_NAME, CommandSubstitution.WORLD_NAME,
            CommandSubstitution.PLAYER_X, CommandSubstitution.PLAYER_Y, CommandSubstitution.PLAYER_Z,
            CommandSubstitution.PLAYER_BLOCK_X, CommandSubstitution.PLAYER_BLOCK_Y, CommandSubstitution.PLAYER_BLOCK_Z
    };
    static final CommandSubstitution[] BLOCK_SUBSTITUTIONS = {
            CommandSubstitution.BLOCK_X, CommandSubstitution.BLOCK_Y, CommandSubstitution.BLOCK_Z
    };
    static final CommandSubstitution[] ENTITY_SUBSTITUTIONS = {
            CommandSubstitution.TARGET_X, CommandSubstitution.TARGET_Y, CommandSubstitution.TARGET_Z,
            CommandSubstitution.TARGET_BLOCK_X, CommandSubstitution.TARGET_BLOCK_Y, CommandSubstitution.TARGET_BLOCK_Z
    };
    static final CommandSubstitution[] PLAYER_SUBSTITUTIONS = {
            CommandSubstitution.TARGET_NAME,
    };

    public final String name;
    public final String description;

    CommandSubstitution(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getTextToSubstitute() {
        return "%" + name + "%";
    }


}
