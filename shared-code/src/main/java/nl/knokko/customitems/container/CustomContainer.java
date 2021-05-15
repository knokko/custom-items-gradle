package nl.knokko.customitems.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.CustomSlot;
import nl.knokko.customitems.container.slot.EmptyCustomSlot;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.item.CustomItem;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.SCIngredient;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ExceptionSupplier;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class CustomContainer {
	
	public static Iterable<CustomSlot> slotIterable(CustomSlot[][] slots) {
		return () -> new Iterator<CustomSlot>() {

			int x = 0;
			int y = 0;

			@Override
			public boolean hasNext() {
				return y < slots[x].length;
			}

			@Override
			public CustomSlot next() {
				CustomSlot result = slots[x][y];
				x++;
				if (x == 9) {
					x = 0;
					y++;
				}
				return result;
			}
		};
	}
	
	public static CustomContainer load(
			BitInput input,
			Function<String, CustomItem> itemByName,
			Function<String, CustomFuelRegistry> fuelRegistryByName,
			ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient,
			ExceptionSupplier<Object, UnknownEncodingException> loadResult
	) throws UnknownEncodingException {
		
		byte encoding = input.readByte();
		switch (encoding) {
		case Encodings.ENCODING1: return load1(input, itemByName, fuelRegistryByName,
				loadIngredient, loadResult);
		default: throw new UnknownEncodingException("Container", encoding);
		}
	}
	
	private static CustomContainer load1(
			BitInput input,
			Function<String, CustomItem> itemByName,
			Function<String, CustomFuelRegistry> fuelRegistryByName,
			ExceptionSupplier<SCIngredient, UnknownEncodingException> loadIngredient,
			ExceptionSupplier<Object, UnknownEncodingException> loadResult
	) throws UnknownEncodingException {
		
		String name = input.readString();
		SlotDisplay selectionIcon = SlotDisplay.load(input, itemByName);
		
		int numRecipes = input.readInt();
		Collection<ContainerRecipe> recipes = new ArrayList<>(numRecipes);
		for (int recipeCounter = 0; recipeCounter < numRecipes; recipeCounter++) {
			recipes.add(ContainerRecipe.load(input, loadIngredient, loadResult));
		}
		
		FuelMode fuelMode;
		if (input.readBoolean())
			fuelMode = FuelMode.ANY;
		else
			fuelMode = FuelMode.ALL;
		
		int width = 9;
		int height = input.readInt();
		CustomSlot[][] slots = new CustomSlot[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				slots[x][y] = CustomSlot.load(input, itemByName, fuelRegistryByName);
			}
		}
		
		VanillaContainerType vanillaType = VanillaContainerType.valueOf(input.readString());
		boolean persistentStorage = input.readBoolean();
		
		return new CustomContainer(name, selectionIcon, recipes, fuelMode,
				slots, vanillaType, persistentStorage);
	}
	
	// A bukkit/minecraft limitation only allows inventories with a width of 9 slots
	private static final int WIDTH = 9;
	
	private final String name;
	private SlotDisplay selectionIcon;
	
	private CustomSlot[][] slots;
	
	// TODO Perhaps allow vanilla registries to be used as well
	private final Collection<ContainerRecipe> recipes;
	
	private FuelMode fuelMode;
	// TODO Perhaps allow overruling a vanilla type completely
	private VanillaContainerType type;
	private boolean persistentStorage;
	
	public CustomContainer(String name) {
		this(name, 
				new SlotDisplay(new SimpleVanillaDisplayItem(CIMaterial.FURNACE), 
						name, new String[0], 1), 
				new ArrayList<>(), FuelMode.ALL, new CustomSlot[9][6], 
				VanillaContainerType.CRAFTING_TABLE, false
		);
	}
	
	public CustomContainer(String name, SlotDisplay selectionIcon, 
			Collection<ContainerRecipe> recipes, FuelMode fuelMode, 
			CustomSlot[][] slots, VanillaContainerType type, boolean persistentStorage) {
		this.name = name;
		this.selectionIcon = selectionIcon;
		this.recipes = recipes;
		this.fuelMode = fuelMode;
		this.slots = slots;
		this.type = type;
		this.persistentStorage = persistentStorage;
		
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < slots[x].length; y++) {
				if (slots[x][y] == null) {
					slots[x][y] = new EmptyCustomSlot();
				}
			}
		}
	}
	
	public void save(
			BitOutput output,
			Consumer<SCIngredient> saveIngredient, Consumer<Object> saveResult
	) {
		save1(output, saveIngredient, saveResult);
	}
	
	private void save1(
			BitOutput output,
			Consumer<SCIngredient> saveIngredient, Consumer<Object> saveResult
	) {
		output.addByte(Encodings.ENCODING1);
		output.addString(name);
		selectionIcon.save(output);
		output.addInt(recipes.size());
		for (ContainerRecipe recipe : recipes) {
			recipe.save(output, saveIngredient, saveResult);
		}
		output.addBoolean(fuelMode == FuelMode.ANY);
		output.addInt(getHeight());
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				slots[x][y].save(output);
			}
		}
		output.addString(type.name());
		output.addBoolean(persistentStorage);
	}
	
	public String getName() {
		return name;
	}
	
	public SlotDisplay getSelectionIcon() {
		return selectionIcon;
	}
	
	public void setSelectionIcon(SlotDisplay newSelectionIcon) {
		selectionIcon = newSelectionIcon;
	}
	
	public VanillaContainerType getVanillaType() {
		return type;
	}
	
	public void setVanillaType(VanillaContainerType newType) {
		type = newType;
	}
	
	public boolean hasPersistentStorage() {
		return persistentStorage;
	}
	
	public void setPersistentStorage(boolean persistent) {
		persistentStorage = persistent;
	}
	
	public void setFuelMode(FuelMode newFuelMode) {
		fuelMode = newFuelMode;
	}
	
	public FuelMode getFuelMode() {
		return fuelMode;
	}
	
	public void resize(int newHeight) {
		
		int oldHeight = getHeight();
		
		CustomSlot[][] newSlots = new CustomSlot[WIDTH][newHeight];
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < oldHeight && y < newHeight; y++) {
				newSlots[x][y] = slots[x][y];
			}
		}
		
		for (int y = oldHeight; y < newHeight; y++) {
			for (int x = 0; x < WIDTH; x++) {
				newSlots[x][y] = new EmptyCustomSlot();
			}
		}
			
		slots = newSlots;
	}
	
	public int getHeight() {
		return slots[0].length;
	}

	public CustomSlot getSlot(int x, int y) {
		return slots[x][y];
	}
	
	public Iterable<CustomSlot> getSlots() {
		return slotIterable(slots);
	}
	
	public void setSlot(CustomSlot newSlot, int x, int y) {
		slots[x][y] = newSlot;
	}
	
	public Collection<ContainerRecipe> getRecipes() {
		return recipes;
	}
	
	private static class Encodings {
		
		static final byte ENCODING1 = 1;
	}
}
