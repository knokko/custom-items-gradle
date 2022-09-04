package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.item.CIMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestBlockProducerValues {

    private static final ProducedBlock SIMPLE_BLOCK = new ProducedBlock(CIMaterial.STONE);

    @Test
    public void testNothingChance() {
        assertEquals(Chance.percentage(100), BlockProducerValues.createQuick().getNothingChance());

        BlockProducerValues partialTable = BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(60))
        );
        assertEquals(Chance.percentage(40), partialTable.getNothingChance());

        BlockProducerValues fullTable = BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(30)),
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(70))
        );
        assertEquals(Chance.percentage(0), fullTable.getNothingChance());

        BlockProducerValues overTable = BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(80)),
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(40))
        );
        assertNull(overTable.getNothingChance());
    }

    @Test(expected = ValidationException.class)
    public void testValidateTooBigTotalChance() throws ValidationException, ProgrammingValidationException {
        BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(80)),
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(40))
        ).validate(new ItemSet(ItemSet.Side.EDITOR));
    }

    @Test
    public void testValidateNotFull() throws ValidationException, ProgrammingValidationException {
        BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(60))
        ).validate(new ItemSet(ItemSet.Side.EDITOR));
    }

    @Test
    public void testValidateFull() throws ValidationException, ProgrammingValidationException {
        BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(30)),
                BlockProducerValues.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(70))
        ).validate(new ItemSet(ItemSet.Side.EDITOR));
    }

    @Test
    public void testProduce() {
        ProducedBlock result1 = new ProducedBlock(CIMaterial.GLASS);
        ProducedBlock result2 = new ProducedBlock(CIMaterial.WOOL);

        BlockProducerValues mixedTable = BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(result1, Chance.percentage(30)),
                BlockProducerValues.Entry.createQuick(result2, Chance.percentage(60))
        );

        assertEquals(result1, mixedTable.produce(Chance.percentage(0)));
        assertEquals(result1, mixedTable.produce(Chance.percentage(1)));
        assertEquals(result1, mixedTable.produce(Chance.percentage(29)));
        assertEquals(result2, mixedTable.produce(Chance.percentage(30)));
        assertEquals(result2, mixedTable.produce(Chance.percentage(31)));
        assertEquals(result2, mixedTable.produce(Chance.percentage(89)));
        assertNull(mixedTable.produce(Chance.percentage(90)));
        assertNull(mixedTable.produce(Chance.percentage(99)));

        BlockProducerValues singletonTable = BlockProducerValues.createQuick(
                BlockProducerValues.Entry.createQuick(result1, Chance.percentage(100))
        );
        assertEquals(result1, singletonTable.produce(Chance.percentage(0)));
        assertEquals(result1, singletonTable.produce(Chance.percentage(99)));
    }
}
