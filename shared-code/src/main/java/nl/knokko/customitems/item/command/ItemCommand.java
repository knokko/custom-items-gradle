package nl.knokko.customitems.item.command;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

public class ItemCommand extends ModelValues {

    public static ItemCommand load(BitInput input) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("ItemCommand", encoding);

        ItemCommand result = new ItemCommand(false);
        result.rawCommand = input.readString();
        result.executor = Executor.valueOf(input.readString());
        result.chance = Chance.load(input);
        result.cooldown = input.readInt();
        result.activateCooldownWhenChanceFails = input.readBoolean();

        return result;
    }

    public static ItemCommand createFromLegacy(String legacyCommand) {
        ItemCommand command = new ItemCommand(true);
        command.setRawCommand(legacyCommand);
        command.setExecutor(Executor.PLAYER);
        command.setChance(Chance.percentage(100));
        command.setCooldown(0);
        return command;
    }

    private String rawCommand;
    private Executor executor;
    private Chance chance;
    private int cooldown;
    private boolean activateCooldownWhenChanceFails;

    public ItemCommand(boolean mutable) {
        super(mutable);

        this.rawCommand = "";
        this.executor = Executor.CONSOLE;
        this.chance = Chance.percentage(100);
        this.cooldown = 0;
        this.activateCooldownWhenChanceFails = true;
    }

    public ItemCommand(ItemCommand toCopy, boolean mutable) {
        super(mutable);

        this.rawCommand = toCopy.getRawCommand();
        this.executor = toCopy.getExecutor();
        this.chance = toCopy.getChance();
        this.cooldown = toCopy.getCooldown();
        this.activateCooldownWhenChanceFails = toCopy.activateCooldownWhenChanceFails();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(this.rawCommand);
        output.addString(this.executor.name());
        this.chance.save(output);
        output.addInt(this.cooldown);
        output.addBoolean(this.activateCooldownWhenChanceFails);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ItemCommand) {
            ItemCommand otherCommand = (ItemCommand) other;
            return this.rawCommand.equals(otherCommand.rawCommand) && this.executor == otherCommand.executor
                    && this.chance.equals(otherCommand.chance) && this.cooldown == otherCommand.cooldown
                    && this.activateCooldownWhenChanceFails == otherCommand.activateCooldownWhenChanceFails;
        } else {
            return false;
        }
    }

    @Override
    public ItemCommand copy(boolean mutable) {
        return new ItemCommand(this, mutable);
    }

    public String getRawCommand() {
        return this.rawCommand;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public Chance getChance() {
        return this.chance;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public boolean activateCooldownWhenChanceFails() {
        return this.activateCooldownWhenChanceFails;
    }

    public void setRawCommand(String newCommand) {
        assertMutable();
        Checks.notNull(newCommand);
        this.rawCommand = newCommand;
    }

    public void setExecutor(Executor newExecutor) {
        assertMutable();
        Checks.notNull(newExecutor);
        this.executor = newExecutor;
    }

    public void setChance(Chance newChance) {
        assertMutable();
        Checks.notNull(newChance);
        this.chance = newChance;
    }

    public void setCooldown(int cooldown) {
        assertMutable();
        this.cooldown = cooldown;
    }

    public void setActivateCooldownWhenChanceFails(boolean newValue) {
        assertMutable();
        this.activateCooldownWhenChanceFails = newValue;
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (this.rawCommand == null) throw new ProgrammingValidationException("No rawCommand");
        if (this.rawCommand.isEmpty()) throw new ValidationException("Command can't be empty");
        if (this.chance == null) throw new ProgrammingValidationException("No chance");
        // The Chance class handles its validation internally
        if (this.cooldown < 0) throw new ValidationException("Cooldown can't be negative");
    }

    public enum Executor {
        PLAYER,
        CONSOLE
    }
}
