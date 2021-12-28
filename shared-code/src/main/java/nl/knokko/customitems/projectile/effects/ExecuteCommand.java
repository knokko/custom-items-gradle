package nl.knokko.customitems.projectile.effects;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ExecuteCommand extends ProjectileEffect {
	
	static ExecuteCommand load1(BitInput input) {
		return new ExecuteCommand(input.readString(), EXECUTORS[input.readByte()]);
	}
	
	public String command;
	
	public Executor executor;

	public ExecuteCommand(String command, Executor executor) {
		this.command = command;
		this.executor = executor;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ExecuteCommand) {
			ExecuteCommand exCommand = (ExecuteCommand) other;
			return command.equals(exCommand.command) && executor == exCommand.executor;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "/" + command;
	}

	@Override
	public void toBits(BitOutput output) {
		output.addByte(ENCODING_COMMAND_1);
		output.addString(command);
		output.addByte((byte) executor.ordinal());
	}

	@Override
	public String validate() {
		if (command == null)
			return "The command can't be null";
		if (command.isEmpty())
			return "The command to execute can't be empty";
		if (executor == null)
			return "You must choose a command executor";
		return null;
	}
	
	private static final Executor[] EXECUTORS = Executor.values();
	
	public enum Executor {
		
		SHOOTER,
		CONSOLE;
	}
}
