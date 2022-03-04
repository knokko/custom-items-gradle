package nl.knokko.customitems.plugin.data;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.command.ItemCommand;
import nl.knokko.customitems.item.command.ItemCommandEvent;
import nl.knokko.customitems.item.command.ItemCommandSystem;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.trouble.UnknownEncodingException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCommandCooldowns {

    final Map<String, ItemEntry> itemMap = new HashMap<>();

    @Override
    public boolean equals(Object other) {
        return other instanceof PlayerCommandCooldowns && this.itemMap.equals(((PlayerCommandCooldowns) other).itemMap);
    }

    @Override
    public int hashCode() {
        return this.itemMap.hashCode();
    }

    public void load(BitInput input, ItemSetWrapper itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("PlayerCommandCooldowns", encoding);

        int numEntries = input.readInt();
        for (int counter = 0; counter < numEntries; counter++) {
            String itemName = input.readString();
            CustomItemValues item = itemSet.getItem(itemName);
            ItemEntry entry = new ItemEntry();
            if (item != null) {
                entry.load(input, item.getCommandSystem());
                this.itemMap.put(itemName, entry);
            } else {
                entry.discard(input);
            }
        }
    }

    public void discard(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("PlayerCommandCooldowns", encoding);

        int numEntries = input.readInt();
        for (int counter = 0; counter < numEntries; counter++) {
            input.readString();
            new ItemEntry().discard(input);
        }
    }

    public void save(BitOutput output, ItemSetWrapper itemSet) {
        output.addByte((byte) 1);

        output.addInt(this.itemMap.size());
        for (String itemName : this.itemMap.keySet()) {
            output.addString(itemName);
            this.itemMap.get(itemName).save(output, itemSet.getItem(itemName).getCommandSystem());
        }
    }

    public void setOnCooldown(CustomItemValues item, ItemCommandEvent event, int commandIndex, long currentTick) {
        if (!this.itemMap.containsKey(item.getName())) {
            this.itemMap.put(item.getName(), new ItemEntry());
        }
        this.itemMap.get(item.getName()).setOnCooldown(item.getCommandSystem(), event, commandIndex, currentTick);
    }

    public boolean clean(long currentTick) {
        this.itemMap.values().removeIf(entry -> entry.clean(currentTick));
        return this.itemMap.isEmpty();
    }

    public boolean isOnCooldown(CustomItemValues item, ItemCommandEvent event, int commandIndex, long currentTick) {
        ItemEntry entry = this.itemMap.get(item.getName());
        return entry != null && entry.isOnCooldown(event, commandIndex, currentTick);
    }

    static class ItemEntry {

        final Map<ItemCommandEvent, ItemEventEntry> commandMap = new EnumMap<>(ItemCommandEvent.class);

        @Override
        public boolean equals(Object other) {
            return other instanceof ItemEntry && this.commandMap.equals(((ItemEntry) other).commandMap);
        }

        @Override
        public int hashCode() {
            return this.commandMap.hashCode();
        }

        void load(BitInput input, ItemCommandSystem commandSystem) throws UnknownEncodingException {
            byte encoding = input.readByte();
            if (encoding != 1) throw new UnknownEncodingException("ItemEntry", encoding);

            int numEntries = input.readInt();
            for (int counter = 0; counter < numEntries; counter++) {
                ItemCommandEvent event = ItemCommandEvent.valueOf(input.readString());
                ItemEventEntry eventEntry = new ItemEventEntry();
                eventEntry.load(input, commandSystem.getCommandsFor(event));
                this.commandMap.put(event, eventEntry);
            }
        }

        void discard(BitInput input) throws UnknownEncodingException {
            byte encoding = input.readByte();
            if (encoding != 1) throw new UnknownEncodingException("ItemEntry", encoding);

            int numEntries = input.readInt();
            for (int counter = 0; counter < numEntries; counter++) {
                input.readString();
                ItemEventEntry eventEntry = new ItemEventEntry();
                eventEntry.discard(input);
            }
        }

        void save(BitOutput output, ItemCommandSystem commandSystem) {
            output.addByte((byte) 1);

            output.addInt(this.commandMap.size());
            for (ItemCommandEvent event : this.commandMap.keySet()) {
                output.addString(event.name());
                this.commandMap.get(event).save(output, commandSystem.getCommandsFor(event));
            }
        }

        void setOnCooldown(ItemCommandSystem commandSystem, ItemCommandEvent event, int commandIndex, long currentTick) {
            if (!this.commandMap.containsKey(event)) {
                this.commandMap.put(event, new ItemEventEntry());
            }
            this.commandMap.get(event).setOnCooldown(commandSystem.getCommandsFor(event), commandIndex, currentTick);
        }

        boolean clean(long currentTick) {
            this.commandMap.values().removeIf(entry -> entry.clean(currentTick));
            return this.commandMap.isEmpty();
        }

        boolean isOnCooldown(ItemCommandEvent event, int commandIndex, long currentTick) {
            ItemEventEntry entry = this.commandMap.get(event);
            return entry != null && entry.isOnCooldown(commandIndex, currentTick);
        }
    }

    static class ItemEventEntry {

        final Map<Integer, Long> cooldownsPerCommandIndex = new HashMap<>();

        @Override
        public boolean equals(Object other) {
            return other instanceof ItemEventEntry && this.cooldownsPerCommandIndex.equals(((ItemEventEntry) other).cooldownsPerCommandIndex);
        }

        @Override
        public int hashCode() {
            return this.cooldownsPerCommandIndex.hashCode();
        }

        void load(BitInput input, List<ItemCommand> commands) throws UnknownEncodingException {
            byte encoding = input.readByte();
            if (encoding != 1) throw new UnknownEncodingException("ItemEventEntry", encoding);

            int numEntries = input.readInt();
            for (int counter = 0; counter < numEntries; counter++) {

                int originalCommandIndex = input.readInt();
                String originalCommand = input.readString();
                long cooldownExpireTime = input.readLong();

                int newCommandIndex = -1;

                /*
                 * We need to find out to which command the cooldown belongs. This can be tricky since the user might
                 * have added, removed, and/or moved commands in the Editor.
                 */
                if (commands.size() > originalCommandIndex && commands.get(originalCommandIndex).getRawCommand().equals(originalCommand)) {
                    newCommandIndex = originalCommandIndex;
                } else {
                    for (int candidateIndex = 0; candidateIndex < commands.size(); candidateIndex++) {
                        if (commands.get(candidateIndex).getRawCommand().equals(originalCommand)) {
                            newCommandIndex = candidateIndex;
                            break;
                        }
                    }
                }

                if (newCommandIndex != -1) {
                    this.cooldownsPerCommandIndex.put(newCommandIndex, cooldownExpireTime);
                }
            }
        }

        void discard(BitInput input) throws UnknownEncodingException {
            byte encoding = input.readByte();
            if (encoding != 1) throw new UnknownEncodingException("ItemEventEntry", encoding);

            int numEntries = input.readInt();
            for (int counter = 0; counter < numEntries; counter++) {
                input.readInt();
                input.readString();
                input.readLong();
            }
        }

        void save(BitOutput output, List<ItemCommand> itemCommands) {
            output.addByte((byte) 1);

            output.addInt(this.cooldownsPerCommandIndex.size());
            for (int commandIndex : this.cooldownsPerCommandIndex.keySet()) {
                long cooldownExpireTime = this.cooldownsPerCommandIndex.get(commandIndex);
                ItemCommand command = itemCommands.get(commandIndex);

                output.addInt(commandIndex);
                output.addString(command.getRawCommand());
                output.addLong(cooldownExpireTime);
            }
        }

        void setOnCooldown(List<ItemCommand> commands, int commandIndex, long currentTick) {
            this.cooldownsPerCommandIndex.put(commandIndex, currentTick + commands.get(commandIndex).getCooldown());
        }

        boolean clean(long currentTick) {
            this.cooldownsPerCommandIndex.values().removeIf(expireTick -> expireTick <= currentTick);
            return this.cooldownsPerCommandIndex.isEmpty();
        }

        boolean isOnCooldown(int commandIndex, long currentTick) {
            Long expireTick = this.cooldownsPerCommandIndex.get(commandIndex);
            return expireTick != null && expireTick > currentTick;
        }
    }
}
