package nl.knokko.customitems.editor.set.recipe.result;

import nl.knokko.customitems.encoding.RecipeEncoding;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CopiedResult extends Result {
	
	private final String encoded;

	public CopiedResult(BitInput input) {
		super(input);
		this.encoded = input.readString();
		this.initInfo();
	}
	
	public CopiedResult(String encoded) {
		// This class doesn't use amount
		super((byte) -1);
		this.encoded = encoded;
		this.initInfo();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof CopiedResult) {
			CopiedResult result = (CopiedResult) other;
			return encoded.equals(result.encoded);
		} else {
			return false;
		}
	}

	@Override
	protected void saveOwn(BitOutput output) {
		output.addString(encoded);
	}

	@Override
	public byte getID() {
		return RecipeEncoding.Result.COPIED;
	}

	@Override
	protected String[] createInfo() {
		return new String[] {getString()};
	}

	@Override
	public String getString() {
		return "Copied from a server item";
	}

	@Override
	public Result amountClone(byte amount) {
		// This class doesn't use the amount, because that is also copied
		return this;
	}

	@Override
	public String toString() {
		return getString();
	}
}
