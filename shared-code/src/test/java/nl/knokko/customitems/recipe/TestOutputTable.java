package nl.knokko.customitems.recipe;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.knokko.customitems.recipe.OutputTable.Entry;

public class TestOutputTable {

	@Test
	public void testNothingChance() {
		assertEquals(100, new OutputTable().getNothingChance());
		
		OutputTable partialTable = new OutputTable();
		partialTable.getEntries().add(new Entry("test", 60));
		assertEquals(40, partialTable.getNothingChance());
		
		OutputTable fullTable = new OutputTable();
		fullTable.getEntries().add(new Entry("test", 30));
		fullTable.getEntries().add(new Entry("test", 70));
		assertEquals(0, fullTable.getNothingChance());
		
		OutputTable overTable = new OutputTable();
		overTable.getEntries().add(new Entry("test", 80));
		overTable.getEntries().add(new Entry("test", 40));
		assertEquals(-20, overTable.getNothingChance());
	}
	
	@Test
	public void testValidate() {
		
		// Output tables aren't allowed to be empty
		assertEquals("This output table is empty", new OutputTable().validate());
		
		// Negative chances are forbidden
		OutputTable negativeTable = new OutputTable();
		negativeTable.getEntries().add(new Entry("ok", 40));
		negativeTable.getEntries().add(new Entry("bad", -20));
		assertEquals("All chances to drop must be positive", negativeTable.validate());
		
		// The total chance most not be above 100%
		OutputTable overTable = new OutputTable();
		overTable.getEntries().add(new Entry("test", 80));
		overTable.getEntries().add(new Entry("test", 40));
		assertEquals("The sum of the chances can be at most 100%, but is 120%", overTable.validate());
		
		// Output tables don't have to be full
		OutputTable partialTable = new OutputTable();
		partialTable.getEntries().add(new Entry("test", 60));
		assertNull(partialTable.validate());
		
		// Output tables are allowed to be full
		OutputTable fullTable = new OutputTable();
		fullTable.getEntries().add(new Entry("test", 30));
		fullTable.getEntries().add(new Entry("test", 70));
		assertNull(fullTable.validate());
	}
	
	@Test
	public void testPickResult() {
		OutputTable emptyTable = new OutputTable();
		assertNull(emptyTable.pickResult(0));
		assertNull(emptyTable.pickResult(99));
		
		OutputTable mixedTable = new OutputTable();
		mixedTable.getEntries().add(new Entry("a", 30));
		mixedTable.getEntries().add(new Entry("b", 60));
		
		assertEquals("a", mixedTable.pickResult(0));
		assertEquals("a", mixedTable.pickResult(1));
		assertEquals("a", mixedTable.pickResult(29));
		assertEquals("b", mixedTable.pickResult(30));
		assertEquals("b", mixedTable.pickResult(31));
		assertEquals("b", mixedTable.pickResult(89));
		assertNull(mixedTable.pickResult(90));
		assertNull(mixedTable.pickResult(99));
		
		OutputTable singletonTable = new OutputTable();
		singletonTable.getEntries().add(new Entry("c", 100));
		assertEquals("c", singletonTable.pickResult(0));
		assertEquals("c", singletonTable.pickResult(99));
	}
}
