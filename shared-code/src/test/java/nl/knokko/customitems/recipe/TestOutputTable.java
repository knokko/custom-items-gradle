package nl.knokko.customitems.recipe;

import static org.junit.Assert.*;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.DataVanillaResultValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.SimpleVanillaResultValues;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.Test;

public class TestOutputTable {

	private static final ResultValues SIMPLE_RESULT = SimpleVanillaResultValues.createQuick(CIMaterial.STONE, 1);

	@Test
	public void testNothingChance() {
		assertEquals(100, new OutputTableValues(false).getNothingChance());

		OutputTableValues partialTable = OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 60)
		);
		assertEquals(40, partialTable.getNothingChance());
		
		OutputTableValues fullTable = OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 30),
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 70)
		);
		assertEquals(0, fullTable.getNothingChance());
		
		OutputTableValues overTable = OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 80),
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 40)
		);
		assertEquals(-20, overTable.getNothingChance());
	}

	@Test(expected = ValidationException.class)
	public void testValidateEmpty() throws ValidationException, ProgrammingValidationException {
		new OutputTableValues(false).validate(new ItemSet(ItemSet.Side.EDITOR));
	}

	@Test(expected = ValidationException.class)
	public void testValidateNegativeChance() throws ValidationException, ProgrammingValidationException {
		OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, -10)
		).validate(new ItemSet(ItemSet.Side.EDITOR));
	}

	@Test(expected = ValidationException.class)
	public void testValidateTooBigTotalChance() throws ValidationException, ProgrammingValidationException {
		OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 80),
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 40)
		).validate(new ItemSet(ItemSet.Side.EDITOR));
	}

	@Test
	public void testValidateNotFull() throws ValidationException, ProgrammingValidationException {
		OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 60)
		).validate(new ItemSet(ItemSet.Side.EDITOR));
	}

	@Test
	public void testValidateFull() throws ValidationException, ProgrammingValidationException {
		OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 30),
				OutputTableValues.Entry.createQuick(SIMPLE_RESULT, 70)
		).validate(new ItemSet(ItemSet.Side.EDITOR));
	}

	@Test
	public void testPickResult() {
		ResultValues result1 = SimpleVanillaResultValues.createQuick(CIMaterial.GLASS, 2);
		ResultValues result2 = DataVanillaResultValues.createQuick(CIMaterial.WOOL, 5, 3);

		OutputTableValues mixedTable = OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(result1, 30),
				OutputTableValues.Entry.createQuick(result2, 60)
		);

		assertEquals(result1, mixedTable.pickResult(0));
		assertEquals(result1, mixedTable.pickResult(1));
		assertEquals(result1, mixedTable.pickResult(29));
		assertEquals(result2, mixedTable.pickResult(30));
		assertEquals(result2, mixedTable.pickResult(31));
		assertEquals(result2, mixedTable.pickResult(89));
		assertNull(mixedTable.pickResult(90));
		assertNull(mixedTable.pickResult(99));
		
		OutputTableValues singletonTable = OutputTableValues.createQuick(
				OutputTableValues.Entry.createQuick(result1, 100)
		);
		assertEquals(result1, singletonTable.pickResult(0));
		assertEquals(result1, singletonTable.pickResult(99));
	}
}
