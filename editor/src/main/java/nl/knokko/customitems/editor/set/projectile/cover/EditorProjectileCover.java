package nl.knokko.customitems.editor.set.projectile.cover;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.item.CustomItemType;
import nl.knokko.customitems.projectile.ProjectileCover;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class EditorProjectileCover extends ProjectileCover {
	
	static final byte ID_SPHERE = 0;
	static final byte ID_CUSTOM = 1;
	
	public static EditorProjectileCover fromBits(BitInput input, ItemSet set) {
		byte id = input.readByte();
		
		if (id == ID_SPHERE) {
			return new SphereProjectileCover(input, set);
		} else if (id == ID_CUSTOM) {
			return new CustomProjectileCover(input);
		} else {
			throw new IllegalArgumentException("Unknown projectile cover id " + id);
		}
	}
    
    EditorProjectileCover(CustomItemType type,  String name){
    	// The internal item damage will be picked right before exporting
    	super(type, (short) -1, name);
    }
    
    EditorProjectileCover(BitInput input){
    	super(input);
    }
    
    @Override
    public abstract String toString();
    
    public String getResourcePath() {
    	return "customprojectiles/" + name;
    }
	
	public final void toBits(BitOutput output) {
		output.addByte(getID());
		
		output.addString(itemType.name());
		output.addShort(itemDamage);
		
		output.addString(name);
		
		saveData(output);
	}
	
	public final void export(BitOutput output) {
		output.addString(itemType.name());
		output.addShort(itemDamage);
		output.addString(name);
	}
	
	protected abstract byte getID();
	
	protected abstract void saveData(BitOutput output);
	
	public abstract void writeModel(ZipOutputStream output) throws IOException;
}
