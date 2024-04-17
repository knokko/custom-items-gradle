package nl.knokko.customitems.recipe;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.recipe.result.DataVanillaResult;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestOutputTable {

	private static final KciResult SIMPLE_RESULT = SimpleVanillaResult.createQuick(VMaterial.STONE, 1);

	@Test
	public void testNothingChance() {
		assertEquals(Chance.percentage(100), new OutputTable(false).getNothingChance());

		OutputTable partialTable = OutputTable.createQuick(
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 60)
		);
		assertEquals(Chance.percentage(40), partialTable.getNothingChance());
		
		OutputTable fullTable = OutputTable.createQuick(
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 30),
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 70)
		);
		assertEquals(Chance.percentage(0), fullTable.getNothingChance());
		
		OutputTable overTable = OutputTable.createQuick(
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 80),
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 40)
		);
		assertNull(overTable.getNothingChance());
	}

	@Test
	public void testValidateEmpty() {
		assertThrows(ValidationException.class, () -> {
			new OutputTable(false).validate(new ItemSet(ItemSet.Side.EDITOR));
		});
	}

	@Test
	public void testValidateTooBigTotalChance() {
		assertThrows(ValidationException.class, () -> {
			OutputTable.createQuick(
					OutputTable.Entry.createQuick(SIMPLE_RESULT, 80),
					OutputTable.Entry.createQuick(SIMPLE_RESULT, 40)
			).validate(new ItemSet(ItemSet.Side.EDITOR));
		});
	}

	@Test
	public void testValidateNotFull() throws ValidationException, ProgrammingValidationException {
		OutputTable.createQuick(
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 60)
		).validate(new ItemSet(ItemSet.Side.EDITOR));
	}

	@Test
	public void testValidateFull() throws ValidationException, ProgrammingValidationException {
		OutputTable.createQuick(
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 30),
				OutputTable.Entry.createQuick(SIMPLE_RESULT, 70)
		).validate(new ItemSet(ItemSet.Side.EDITOR));
	}

	@Test
	public void testPickResult() {
		KciResult result1 = SimpleVanillaResult.createQuick(VMaterial.GLASS, 2);
		KciResult result2 = DataVanillaResult.createQuick(VMaterial.WOOL, 5, 3);

		OutputTable mixedTable = OutputTable.createQuick(
				OutputTable.Entry.createQuick(result1, 30),
				OutputTable.Entry.createQuick(result2, 60)
		);

		assertEquals(result1, mixedTable.pickResult(Chance.percentage(0)));
		assertEquals(result1, mixedTable.pickResult(Chance.percentage(1)));
		assertEquals(result1, mixedTable.pickResult(Chance.percentage(29)));
		assertEquals(result2, mixedTable.pickResult(Chance.percentage(30)));
		assertEquals(result2, mixedTable.pickResult(Chance.percentage(31)));
		assertEquals(result2, mixedTable.pickResult(Chance.percentage(89)));
		assertNull(mixedTable.pickResult(Chance.percentage(90)));
		assertNull(mixedTable.pickResult(Chance.percentage(99)));
		
		OutputTable singletonTable = OutputTable.createQuick(
				OutputTable.Entry.createQuick(result1, 100)
		);
		assertEquals(result1, singletonTable.pickResult(Chance.percentage(0)));
		assertEquals(result1, singletonTable.pickResult(Chance.percentage(99)));
	}
}
