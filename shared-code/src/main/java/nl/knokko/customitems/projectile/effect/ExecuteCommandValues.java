package nl.knokko.customitems.projectile.effect;

import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

public class ExecuteCommandValues extends ProjectileEffectValues {

    static ExecuteCommandValues load(BitInput input, byte encoding) throws UnknownEncodingException {
        ExecuteCommandValues result = new ExecuteCommandValues(false);

        if (encoding == ENCODING_COMMAND_1) {
            result.load1(input);
        } else {
            throw new UnknownEncodingException("ExecuteCommandProjectileEffect", encoding);
        }

        return result;
    }

    public static ExecuteCommandValues createQuick(String command, Executor executor) {
        ExecuteCommandValues result = new ExecuteCommandValues(true);
        result.setCommand(command);
        result.setExecutor(executor);
        return result;
    }

    private String command;
    private Executor executor;

    public ExecuteCommandValues(boolean mutable) {
        super(mutable);
        this.command = "";
        this.executor = Executor.SHOOTER;
    }

    public ExecuteCommandValues(ExecuteCommandValues toCopy, boolean mutable) {
        super(mutable);
        this.command = toCopy.getCommand();
        this.executor = toCopy.getExecutor();
    }

    @Override
    public String toString() {
        // Don't include the actual command because that could get too long to display nicely
        return "ExecuteCommand";
    }

    private void load1(BitInput input) {
        this.command = input.readString();
        this.executor = Executor.values()[input.readByte()];
    }

    @Override
    public void save(BitOutput output) {
        output.addByte(ENCODING_COMMAND_1);
        output.addString(command);
        output.addByte((byte) executor.ordinal());
    }

    @Override
    public ExecuteCommandValues copy(boolean mutable) {
        return new ExecuteCommandValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == ExecuteCommandValues.class) {
            ExecuteCommandValues otherEffect = (ExecuteCommandValues) other;
            return this.command.equals(otherEffect.command) && this.executor == otherEffect.executor;
        } else {
            return false;
        }
    }

    public String getCommand() {
        return command;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setCommand(String newCommand) {
        assertMutable();
        Checks.notNull(newCommand);
        this.command = newCommand;
    }

    public void setExecutor(Executor newExecutor) {
        assertMutable();
        Checks.notNull(newExecutor);
        this.executor = newExecutor;
    }

    @Override
    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (command == null) throw new ProgrammingValidationException("No command");
        if (command.isEmpty()) throw new ValidationException("Command can't be empty");
        if (executor == null) throw new ProgrammingValidationException("No executor");
    }

    public enum Executor {

        SHOOTER,
        CONSOLE;
    }
}
