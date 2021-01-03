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

public class BlockDrop {
	
	public static BlockDrop load(
			BitInput input, 
			BiFunction<String, Byte, Object> createCustomItemResultByName,
			ExceptionSupplier<Object, UnknownEncodingException> loadResult,
			Function<String, CustomItem> getCustomItemByName
	) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding == DropEncoding.Block.ENCODING1)
			return load1(input, createCustomItemResultByName);
		else if (encoding == DropEncoding.Block.ENCODING2)
			return load2(input, loadResult, getCustomItemByName);
		else
			throw new UnknownEncodingException("BlockDrop", encoding);
	}
	
	private static BlockDrop load1(
			BitInput input,
			BiFunction<String, Byte, Object> createCustomItemResultByName) {
		return new BlockDrop(
				BlockType.getByOrdinal(input.readInt()), 
				false, Drop.load1(input, createCustomItemResultByName)
		);
	}
	
	private static BlockDrop load2(
			BitInput input, 
			ExceptionSupplier<Object, UnknownEncodingException> loadResult,
			Function<String, CustomItem> getCustomItemByName
	) throws UnknownEncodingException {
		return new BlockDrop(
				BlockType.getByOrdinal(input.readInt()), 
				input.readBoolean(), Drop.load2(input, loadResult, getCustomItemByName)
		);
	}
	
	private BlockType block;
	
	private boolean allowSilkTouch;
	
	private Drop drop;
	
	public BlockDrop(BlockType block, boolean allowSilkTouch, Drop drop) {
		this.block = block;
		this.drop = drop;
		this.allowSilkTouch = allowSilkTouch;
	}
	
	@Override
	public String toString() {
		return drop + " for block " + block;
	}
	
	public void save(BitOutput output, Consumer<Object> saveResult) {
		save2(output, saveResult);
	}
	
	/*
	protected void save1(BitOutput output) {
		output.addByte(DropEncoding.Block.ENCODING1);
		output.addInt(block.ordinal());
		drop.save1(output);
	}*/
	
	protected void save2(BitOutput output, Consumer<Object> saveResult) {
		output.addByte(DropEncoding.Block.ENCODING2);
		output.addInt(block.ordinal());
		output.addBoolean(allowSilkTouch);
		drop.save2(output, saveResult);
	}
	
	public BlockType getBlock() {
		return block;
	}
	
	public boolean allowSilkTouch() {
		return allowSilkTouch;
	}
	
	public Drop getDrop() {
		return drop;
	}
	
	public void setBlock(BlockType newBlock) {
		block = newBlock;
	}
	
	public void setDrop(Drop newDrop) {
		drop = newDrop;
	}
}
