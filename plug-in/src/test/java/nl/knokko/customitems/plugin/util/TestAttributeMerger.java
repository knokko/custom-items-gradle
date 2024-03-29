package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.AttributeModifierValues;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAttributeMerger {

    @Test
    public void testMergeAllDistinct() {
        List<AttributeModifierValues> distinctAttributes = new ArrayList<>(5);
        distinctAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ATTACK_DAMAGE, AttributeModifierValues.Slot.OFFHAND,
                AttributeModifierValues.Operation.ADD, 1.0
        ));
        distinctAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ATTACK_DAMAGE, AttributeModifierValues.Slot.HEAD,
                AttributeModifierValues.Operation.ADD, 1.0
        ));
        distinctAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ATTACK_DAMAGE, AttributeModifierValues.Slot.OFFHAND,
                AttributeModifierValues.Operation.ADD_FACTOR, 1.0
        ));
        distinctAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ATTACK_SPEED, AttributeModifierValues.Slot.OFFHAND,
                AttributeModifierValues.Operation.ADD, 1.0
        ));
        distinctAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ATTACK_SPEED, AttributeModifierValues.Slot.MAINHAND,
                AttributeModifierValues.Operation.MULTIPLY, 4.0
        ));

        assertEquals(distinctAttributes, AttributeMerger.merge(distinctAttributes));
    }

    @Test
    public void testMergeEmpty() {
        assertTrue(AttributeMerger.merge(new ArrayList<>()).isEmpty());
    }

    @Test
    public void testMergeSome() {
        List<AttributeModifierValues> originalAttributes = new ArrayList<>(5);
        originalAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ARMOR, AttributeModifierValues.Slot.CHEST,
                AttributeModifierValues.Operation.ADD, 3.0
        ));
        originalAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.MOVEMENT_SPEED, AttributeModifierValues.Slot.OFFHAND,
                AttributeModifierValues.Operation.ADD_FACTOR, 1.4
        ));
        originalAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ARMOR, AttributeModifierValues.Slot.CHEST,
                AttributeModifierValues.Operation.ADD, 2.0
        ));
        originalAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ARMOR_TOUGHNESS, AttributeModifierValues.Slot.CHEST,
                AttributeModifierValues.Operation.MULTIPLY, 2.0
        ));
        originalAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.MOVEMENT_SPEED, AttributeModifierValues.Slot.OFFHAND,
                AttributeModifierValues.Operation.ADD_FACTOR, 1.4
        ));

        List<AttributeModifierValues> mergedAttributes = new ArrayList<>(3);
        mergedAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ARMOR, AttributeModifierValues.Slot.CHEST,
                AttributeModifierValues.Operation.ADD, 5.0
        ));
        mergedAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.MOVEMENT_SPEED, AttributeModifierValues.Slot.OFFHAND,
                AttributeModifierValues.Operation.ADD_FACTOR, 2.8
        ));
        mergedAttributes.add(AttributeModifierValues.createQuick(
                AttributeModifierValues.Attribute.ARMOR_TOUGHNESS, AttributeModifierValues.Slot.CHEST,
                AttributeModifierValues.Operation.MULTIPLY, 2.0
        ));

        assertEquals(mergedAttributes, AttributeMerger.merge(originalAttributes));
    }
}
