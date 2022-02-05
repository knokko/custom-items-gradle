package nl.knokko.customitems.item.command;

import java.util.*;

import static nl.knokko.customitems.item.command.CommandSubstitution.*;

public enum ItemCommandEvent {

    LEFT_CLICK_GENERAL(
            "Left-click",
                    "This event is fired whenever a player left-clicks while holding this item in their main hand."
    ),
    RIGHT_CLICK_GENERAL(
            "Right-click",
            "This event is fired whenever a player right-clicks while holding this item in their main hand."
    ),
    LEFT_CLICK_BLOCK(
            "Left-click a block",
            "This event is fired whenever a player left-clicks a block while holding this item in their main hand.",
            BLOCK_SUBSTITUTIONS
    ),
    RIGHT_CLICK_BLOCK(
            "Right-click a block",
            "This event is fired whenever a player right-clicks a block while holding this item in their main hand.",
            BLOCK_SUBSTITUTIONS
    ),
    BREAK_BLOCK(
            "Break a block",
            "This event is fired whenever a player breaks a block while holding this item in their main hand",
            BLOCK_SUBSTITUTIONS
    ),
    MELEE_ATTACK_ENTITY(
            "Attack an entity",
            "This event is fired whenever a player performs a melee attack on an entity while holding this item in their main hand",
            ENTITY_SUBSTITUTIONS
    ),
    RIGHT_CLICK_ENTITY(
            "Right-click on an entity",
            "This event is fired whenever a player right-clicks an entity while holding this item in their main hand",
            ENTITY_SUBSTITUTIONS
    ),
    MELEE_ATTACK_PLAYER(
            "Attack a player",
            "This event is fired whenever a player performs a melee attack on another player while holding this item in their main hand",
            ENTITY_SUBSTITUTIONS, PLAYER_SUBSTITUTIONS
    ),
    RIGHT_CLICK_PLAYER(
            "Right-click on a player",
            "This event is fired whenever a player right-clicks another player while holding this item in their main hand",
            ENTITY_SUBSTITUTIONS, PLAYER_SUBSTITUTIONS
    );



    public final String displayName;
    public final String description;
    public final List<CommandSubstitution> substitutions;

    private ItemCommandEvent(String displayName, String description, CommandSubstitution[]... substitutions) {
        this.displayName = displayName;
        this.description = description;

        List<CommandSubstitution> substitutionList = new ArrayList<>();
        for (CommandSubstitution[] substitutionsArray : substitutions) {
            Collections.addAll(substitutionList, substitutionsArray);
        }
        Collections.addAll(substitutionList, IMPLICIT_SUBSTITUTIONS);
        this.substitutions = Collections.unmodifiableList(substitutionList);

        Set<String> substitutionNames = new HashSet<>(substitutionList.size());
        for (CommandSubstitution substitution : substitutionList) {
            if (substitutionNames.contains(substitution.name)) {
                throw new Error("Duplicate substitution name " + substitution.name);
            }
            substitutionNames.add(substitution.name);
        }
    }

    public String performSubstitutions(String rawCommand, Map<CommandSubstitution, String> substitutionMap) {
        String result = rawCommand;
        for (CommandSubstitution substitution : this.substitutions) {
            String substitutionValue = substitutionMap.get(substitution);
            if (substitutionValue == null) {
                throw new IllegalArgumentException("No value for substitution " + substitution.name + " was given");
            }
            result = result.replaceAll(substitution.getTextToSubstitute(), substitutionValue);
        }
        return result;
    }
}
