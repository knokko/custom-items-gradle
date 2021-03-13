package nl.knokko.customitems.container;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import nl.knokko.customitems.container.slot.*;
import org.junit.Test;

import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.display.CustomItemDisplayItem;
import nl.knokko.customitems.container.slot.display.DataVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SimpleVanillaDisplayItem;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.recipe.ContainerRecipe;
import nl.knokko.customitems.recipe.ContainerRecipe.InputEntry;
import nl.knokko.customitems.recipe.ContainerRecipe.OutputEntry;
import nl.knokko.customitems.recipe.OutputTable;
import nl.knokko.customitems.test.DummyIngredient;
import nl.knokko.customitems.test.TestCustomItem;
import nl.knokko.customitems.test.TestHelper;
import nl.knokko.customitems.trouble.UnknownEncodingException;

public class TestCustomContainer {
	
	private static SlotDisplay selectionIcon() {
		return new SlotDisplay(
				new SimpleVanillaDisplayItem(CIMaterial.ANVIL), 
				"Test Container Icon", new String[] {
						"Just some example lore",
						"With 2 lines!"
				}, 5
		);
	}

	@Test
	public void testSaveLoad() {
		TestHelper.testSaveLoad(
				output -> createTestContainer().save(output, 
						ingredient -> output.addInt(((DummyIngredient)ingredient).getId()), 
						result -> output.addInt(((DummyIngredient)result).getId())),
				input -> {
					try {
						CustomContainer container = CustomContainer.load(input,
								TestCustomItem::new,
								name -> new CustomFuelRegistry(name, new ArrayList<>(0)), 
								() -> new DummyIngredient(input.readInt()), 
								() -> new DummyIngredient(input.readInt()));
						
						// General properties
						assertEquals("test_container", container.getName());
						assertEquals(selectionIcon(), container.getSelectionIcon());
						assertEquals(FuelMode.ANY, container.getFuelMode());
						assertEquals(VanillaContainerType.BLAST_FURNACE, container.getVanillaType());
						assertTrue(container.hasPersistentStorage());
						
						// Recipes
						assertEquals(1, container.getRecipes().size());
						{
							ContainerRecipe recipe = container.getRecipes().iterator().next();
							assertEquals(2, recipe.getInputs().size());
							Iterator<InputEntry> inputs = recipe.getInputs().iterator();
							InputEntry input1 = inputs.next();
							assertEquals("ingot", input1.getInputSlotName());
							assertEquals(1, ((DummyIngredient)input1.getIngredient()).getId());
							InputEntry input2 = inputs.next();
							assertEquals("dust", input2.getInputSlotName());
							assertEquals(2, ((DummyIngredient)input2.getIngredient()).getId());
							
							assertEquals(1, recipe.getOutputs().size());
							OutputEntry output = recipe.getOutputs().iterator().next();
							assertEquals("ingot", output.getOutputSlotName());

							OutputTable outputTable = output.getOutputTable();
							assertEquals(20, outputTable.getEntries().get(0).getChance());
							assertEquals(3, ((DummyIngredient)outputTable.getEntries().get(0).getResult()).getId());
							assertEquals(10, outputTable.getEntries().get(1).getChance());
							assertEquals(4, ((DummyIngredient)outputTable.getEntries().get(1).getResult()).getId());
							
							assertEquals(14, recipe.getDuration());
							assertEquals(21, recipe.getExperience());
						}
						
						// Slots
						assertEquals(2, container.getHeight());
						
						assertEquals("ingot", ((OutputCustomSlot)container.getSlot(1, 1)).getName());
						{
							SlotDisplay placeholder = ((OutputCustomSlot) container.getSlot(1, 1)).getPlaceholder();
							assertEquals(5, placeholder.getAmount());
							assertEquals("Output wool", placeholder.getDisplayName());
							assertArrayEquals(new String[] {"Actually metal"}, placeholder.getLore());
						}
						assertEquals("ingot", ((InputCustomSlot)container.getSlot(3, 0)).getName());
						{
							SlotDisplay placeholder = ((InputCustomSlot) container.getSlot(3, 0)).getPlaceholder();
							assertEquals(2, placeholder.getAmount());
							assertEquals("Iron input", placeholder.getDisplayName());
							assertEquals(0, placeholder.getLore().length);
						}
						assertEquals("dust", ((InputCustomSlot)container.getSlot(3, 1)).getName());
						{
							FuelCustomSlot fuelSlot = (FuelCustomSlot) container.getSlot(5, 1);
							assertEquals("theFuel", fuelSlot.getName());
							assertEquals("theRegistry", fuelSlot.getRegistry().getName());
							SlotDisplay placeholder = fuelSlot.getPlaceholder();
							assertEquals(10, placeholder.getAmount());
							assertEquals("Insert fuel here", placeholder.getDisplayName());
							assertArrayEquals(new String[] {"Replace this dummy item with the fuel you want"}, placeholder.getLore());
							assertTrue(placeholder.getItem() instanceof SimpleVanillaDisplayItem);
						}
						{
							FuelIndicatorCustomSlot indicator = (FuelIndicatorCustomSlot) container.getSlot(4, 0);
							assertEquals("theFuel", indicator.getFuelSlotName());
							
							SlotDisplay display = indicator.getDisplay();
							SimpleVanillaDisplayItem displayItem = (SimpleVanillaDisplayItem) display.getItem();
							assertEquals(CIMaterial.LOG, displayItem.getMaterial());
							assertEquals("SomeLog", display.getDisplayName());
							assertArrayEquals(new String[] {"Just some log"}, display.getLore());
							assertEquals(1, display.getAmount());
							
							SlotDisplay placeholder = indicator.getPlaceholder();
							DataVanillaDisplayItem placeholderItem = (DataVanillaDisplayItem) placeholder.getItem();
							assertEquals(CIMaterial.WOOL, placeholderItem.getMaterial());
							assertEquals(2, placeholderItem.getData());
							assertEquals("ColoredWool", placeholder.getDisplayName());
							assertArrayEquals(new String[] {"This wool has some color", "But I don't know which"}, placeholder.getLore());
							assertEquals(3, placeholder.getAmount());
							
							assertEquals(15, indicator.getDomain().getBegin());
							assertEquals(25, indicator.getDomain().getEnd());
						}
						{
							ProgressIndicatorCustomSlot indicator = (ProgressIndicatorCustomSlot) container.getSlot(5, 0);
							assertEquals("test_item", ((CustomItemDisplayItem) indicator.getDisplay().getItem()).getItem().getName());
							assertEquals(5, indicator.getDisplay().getAmount());
							assertEquals("another", ((CustomItemDisplayItem) indicator.getPlaceHolder().getItem()).getItem().getName());
							assertEquals(2, indicator.getPlaceHolder().getAmount());
							assertEquals(1, indicator.getDomain().getBegin());
							assertEquals(2, indicator.getDomain().getEnd());
						}

						assertTrue(container.getSlot(4, 1) instanceof StorageCustomSlot);
					} catch (UnknownEncodingException e) {
						throw new Error(e);
					}
				}
		);
	}
	
	private static CustomContainer createTestContainer() {
		Collection<ContainerRecipe> recipes = new ArrayList<>(1);
		{
			Collection<InputEntry> inputs = new ArrayList<>(2);
			inputs.add(new InputEntry("ingot", new DummyIngredient(1)));
			inputs.add(new InputEntry("dust", new DummyIngredient(2)));
			
			Collection<OutputEntry> outputs = new ArrayList<>(1);
			OutputTable outputTable = new OutputTable();
			outputTable.getEntries().add(new OutputTable.Entry(new DummyIngredient(3), 20));
			outputTable.getEntries().add(new OutputTable.Entry(new DummyIngredient(4), 10));
			outputs.add(new OutputEntry("ingot", outputTable));
			recipes.add(new ContainerRecipe(inputs, outputs, 14, 21));
		}
		
		CustomSlot[][] slots = new CustomSlot[9][2];
		
		CustomFuelRegistry fuelRegistry = new CustomFuelRegistry("theRegistry", new ArrayList<>(0));
		
		slots[1][1] = new OutputCustomSlot("ingot", new SlotDisplay(
				new SimpleVanillaDisplayItem(CIMaterial.WOOL),
				"Output wool", new String[] {"Actually metal"}, 5
		));
		slots[3][0] = new InputCustomSlot("ingot", new SlotDisplay(
				new SimpleVanillaDisplayItem(CIMaterial.IRON_AXE),
				"Iron input", new String[0], 2
		));
		slots[3][1] = new InputCustomSlot("dust", null);
		slots[5][1] = new FuelCustomSlot("theFuel", fuelRegistry, new SlotDisplay(
				new SimpleVanillaDisplayItem(CIMaterial.CHARCOAL),
				"Insert fuel here", new String[] {
						"Replace this dummy item with the fuel you want"
				}, 10
		));
		slots[4][0] = new FuelIndicatorCustomSlot("theFuel", 
				new SlotDisplay(new SimpleVanillaDisplayItem(CIMaterial.LOG), 
						"SomeLog", new String[] { "Just some log" }, 1), 
				new SlotDisplay(new DataVanillaDisplayItem(CIMaterial.WOOL, (byte) 2), 
						"ColoredWool", new String[] {
								"This wool has some color", "But I don't know which"
				}, 3), new IndicatorDomain(15, 25));
		slots[4][1] = new StorageCustomSlot();
		slots[5][0] = new ProgressIndicatorCustomSlot(
				new SlotDisplay(new CustomItemDisplayItem(new TestCustomItem("test_item")), "", new String[0], 5), 
				new SlotDisplay(new CustomItemDisplayItem(new TestCustomItem("another")), "", new String[0], 2),
				new IndicatorDomain(1, 2));
		return new CustomContainer(
				"test_container", selectionIcon(), 
				recipes, FuelMode.ANY, slots, 
				VanillaContainerType.BLAST_FURNACE, true
		);
	}
}
