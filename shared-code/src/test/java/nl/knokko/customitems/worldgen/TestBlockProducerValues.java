package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBlockProducerValues {

    private static final ProducedBlock SIMPLE_BLOCK = new ProducedBlock(VMaterial.STONE);

    @Test
    public void testNothingChance() {
        assertEquals(Chance.percentage(100), BlockProducer.createQuick().getNothingChance());

        BlockProducer partialTable = BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(60))
        );
        assertEquals(Chance.percentage(40), partialTable.getNothingChance());

        BlockProducer fullTable = BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(30)),
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(70))
        );
        assertEquals(Chance.percentage(0), fullTable.getNothingChance());

        BlockProducer overTable = BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(80)),
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(40))
        );
        assertNull(overTable.getNothingChance());
    }

    @Test
    public void testValidateTooBigTotalChance() {
        assertThrows(ValidationException.class, () -> {
            BlockProducer.createQuick(
                    BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(80)),
                    BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(40))
            ).validate(new ItemSet(ItemSet.Side.EDITOR));
        });
    }

    @Test
    public void testValidateNotFull() throws ValidationException, ProgrammingValidationException {
        BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(60))
        ).validate(new ItemSet(ItemSet.Side.EDITOR));
    }

    @Test
    public void testValidateFull() throws ValidationException, ProgrammingValidationException {
        BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(30)),
                BlockProducer.Entry.createQuick(SIMPLE_BLOCK, Chance.percentage(70))
        ).validate(new ItemSet(ItemSet.Side.EDITOR));
    }

    @Test
    public void testProduce() {
        ProducedBlock result1 = new ProducedBlock(VMaterial.GLASS);
        ProducedBlock result2 = new ProducedBlock(VMaterial.WOOL);

        BlockProducer mixedTable = BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(result1, Chance.percentage(30)),
                BlockProducer.Entry.createQuick(result2, Chance.percentage(60))
        );

        assertEquals(result1, mixedTable.produce(Chance.percentage(0)));
        assertEquals(result1, mixedTable.produce(Chance.percentage(1)));
        assertEquals(result1, mixedTable.produce(Chance.percentage(29)));
        assertEquals(result2, mixedTable.produce(Chance.percentage(30)));
        assertEquals(result2, mixedTable.produce(Chance.percentage(31)));
        assertEquals(result2, mixedTable.produce(Chance.percentage(89)));
        assertNull(mixedTable.produce(Chance.percentage(90)));
        assertNull(mixedTable.produce(Chance.percentage(99)));

        BlockProducer singletonTable = BlockProducer.createQuick(
                BlockProducer.Entry.createQuick(result1, Chance.percentage(100))
        );
        assertEquals(result1, singletonTable.produce(Chance.percentage(0)));
        assertEquals(result1, singletonTable.produce(Chance.percentage(99)));
    }
}
