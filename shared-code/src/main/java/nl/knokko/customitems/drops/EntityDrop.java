package nl.knokko.customitems.drops;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import nl.knokko.customitems.encoding.DropEncoding;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class EntityDrop {
	
	public static EntityDrop load(
			BitInput input, 
			BiFunction<String, Byte, Object> createCustomItemResultByName,
			ExceptionSupplier<Object, UnknownEncodingException> loadResult,
			Function<String, CustomItem> getCustomItemByName
	) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding == DropEncoding.Entity.ENCODING1)
			return load1(input, createCustomItemResultByName);
		else if (encoding == DropEncoding.Entity.ENCODING2)
			return load2(input, loadResult, getCustomItemByName);
		else
			throw new UnknownEncodingException("MobDrop", encoding);
	}
	
	private static EntityDrop load1(
			BitInput input, 
			BiFunction<String, Byte, Object> createCustomItemResultByName
	) {
		return new EntityDrop(
				CIEntityType.getByOrdinal(input.readInt()), 
				input.readString(), Drop.load1(input, createCustomItemResultByName)
		);
	}
	
	private static EntityDrop load2(
			BitInput input, 
			ExceptionSupplier<Object, UnknownEncodingException> loadResult,
			Function<String, CustomItem> getCustomItemByName
	) throws UnknownEncodingException {
		return new EntityDrop(
				CIEntityType.getByOrdinal(input.readInt()), 
				input.readString(), Drop.load2(input, loadResult, getCustomItemByName)
		);
	}
	
	private CIEntityType entity;
	private String requiredName;
	
	private Drop drop;
	
	public EntityDrop(CIEntityType entity, String requiredName, Drop drop) {
		this.entity = entity;
		this.requiredName = requiredName;
		this.drop = drop;
	}
	
	@Override
	public String toString() {
		return drop + " for " + entity + (requiredName == null ? "" : " named " + requiredName);
	}
	
	public void save(BitOutput output, Consumer<Object> saveResult) {
		save2(output, saveResult);
	}
	
	/*
	protected void save1(BitOutput output) {
		output.addByte(DropEncoding.Entity.ENCODING1);
		output.addInt(entity.ordinal());
		output.addString(requiredName);
		drop.save1(output);
	}*/
	
	protected void save2(BitOutput output, Consumer<Object> saveResult) {
		output.addByte(DropEncoding.Entity.ENCODING2);
		output.addInt(entity.ordinal());
		output.addString(requiredName);
		drop.save2(output, saveResult);
	}
	
	public CIEntityType getEntityType() {
		return entity;
	}
	
	public String getRequiredName() {
		return requiredName;
	}
	
	public Drop getDrop() {
		return drop;
	}
	
	public void setEntityType(CIEntityType newType) {
		entity = newType;
	}
	
	public void setRequiredName(String newReqName) {
		requiredName = newReqName;
	}
	
	public void setDrop(Drop newDrop) {
		drop = newDrop;
	}
}
