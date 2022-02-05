package nl.knokko.customitems.item.command;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.*;

public class ItemCommandSystem extends ModelValues {

    public static ItemCommandSystem load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ItemCommandSystem", encoding);

        ItemCommandSystem result = new ItemCommandSystem(true);
        int numUsedEvents = input.readInt();
        for (int eventCounter = 0; eventCounter < numUsedEvents; eventCounter++) {
            ItemCommandEvent event = ItemCommandEvent.valueOf(input.readString());

            int numCommands = input.readInt();
            List<ItemCommand> commands = new ArrayList<>(numCommands);
            for (int commandCounter = 0; commandCounter < numCommands; commandCounter++) {
                commands.add(ItemCommand.load(input));
            }
            result.setCommandsFor(event, commands);
        }

        return result.copy(false);
    }

    private final Map<ItemCommandEvent, List<ItemCommand>> eventMap;

    public ItemCommandSystem(boolean mutable) {
        super(mutable);

        this.eventMap = new EnumMap<>(ItemCommandEvent.class);
    }

    public ItemCommandSystem(ItemCommandSystem toCopy, boolean mutable) {
        super(mutable);

        this.eventMap = new EnumMap<>(ItemCommandEvent.class);
        for (ItemCommandEvent event : ItemCommandEvent.values()) {
            List<ItemCommand> commands = toCopy.getCommandsFor(event);
            if (!commands.isEmpty()) {
                this.eventMap.put(event, commands);
            }
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addInt(this.eventMap.size());
        for (ItemCommandEvent event : this.eventMap.keySet()) {
            output.addString(event.name());

            List<ItemCommand> commandList = this.eventMap.get(event);
            output.addInt(commandList.size());
            for (ItemCommand command : commandList) {
                command.save(output);
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ItemCommandSystem && this.eventMap.equals(((ItemCommandSystem) other).eventMap);
    }

    @Override
    public ItemCommandSystem copy(boolean mutable) {
        return new ItemCommandSystem(this, mutable);
    }

    public List<ItemCommand> getCommandsFor(ItemCommandEvent event) {
        List<ItemCommand> commands = this.eventMap.get(event);
        return commands != null ? commands : Collections.emptyList();
    }

    public void setCommandsFor(ItemCommandEvent event, List<ItemCommand> commands) {
        assertMutable();
        Checks.notNull(event);
        Checks.nonNull(commands);
        if (commands.isEmpty()) {
            this.eventMap.remove(event);
        } else {
            this.eventMap.put(event, Mutability.createDeepCopy(commands, false));
        }
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (this.eventMap == null) throw new ProgrammingValidationException("No event map");
        if (this.eventMap.containsKey(null)) throw new ProgrammingValidationException("Null event key");
        for (List<ItemCommand> commandList : this.eventMap.values()) {
            if (commandList == null) throw new ProgrammingValidationException("Null command list");
            if (commandList.isEmpty()) throw new ProgrammingValidationException("Empty command list");
            Validation.scope("Command list", () -> {
                for (ItemCommand command : commandList) {
                    command.validate();
                }
            });
        }
    }
}
