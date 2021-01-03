package nl.knokko.customitems.editor.set.projectile.cover;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomProjectileCover extends EditorProjectileCover {
	
	public byte[] model;

	public CustomProjectileCover(CustomItemType type, String name, byte[] model) {
		super(type, name);
		this.model = model;
	}
	
	CustomProjectileCover(BitInput input){
		super(input);
		model = input.readByteArray();
	}

	@Override
	protected byte getID() {
		return ID_CUSTOM;
	}

	@Override
	protected void saveData(BitOutput output) {
		output.addByteArray(model);
	}

	@Override
	public void writeModel(ZipOutputStream output) throws IOException {
		output.write(model);
	}

	@Override
	public String toString() {
		return "Custom";
	}
}
