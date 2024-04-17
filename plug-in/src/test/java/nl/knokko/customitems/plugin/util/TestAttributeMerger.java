package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.KciAttributeModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAttributeMerger {

    @Test
    public void testMergeAllDistinct() {
        List<KciAttributeModifier> distinctAttributes = new ArrayList<>(5);
        distinctAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ATTACK_DAMAGE, KciAttributeModifier.Slot.OFFHAND,
                KciAttributeModifier.Operation.ADD, 1.0
        ));
        distinctAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ATTACK_DAMAGE, KciAttributeModifier.Slot.HEAD,
                KciAttributeModifier.Operation.ADD, 1.0
        ));
        distinctAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ATTACK_DAMAGE, KciAttributeModifier.Slot.OFFHAND,
                KciAttributeModifier.Operation.ADD_FACTOR, 1.0
        ));
        distinctAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ATTACK_SPEED, KciAttributeModifier.Slot.OFFHAND,
                KciAttributeModifier.Operation.ADD, 1.0
        ));
        distinctAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ATTACK_SPEED, KciAttributeModifier.Slot.MAINHAND,
                KciAttributeModifier.Operation.MULTIPLY, 4.0
        ));

        assertEquals(distinctAttributes, AttributeMerger.merge(distinctAttributes));
    }

    @Test
    public void testMergeEmpty() {
        assertTrue(AttributeMerger.merge(new ArrayList<>()).isEmpty());
    }

    @Test
    public void testMergeSome() {
        List<KciAttributeModifier> originalAttributes = new ArrayList<>(5);
        originalAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ARMOR, KciAttributeModifier.Slot.CHEST,
                KciAttributeModifier.Operation.ADD, 3.0
        ));
        originalAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.MOVEMENT_SPEED, KciAttributeModifier.Slot.OFFHAND,
                KciAttributeModifier.Operation.ADD_FACTOR, 1.4
        ));
        originalAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ARMOR, KciAttributeModifier.Slot.CHEST,
                KciAttributeModifier.Operation.ADD, 2.0
        ));
        originalAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ARMOR_TOUGHNESS, KciAttributeModifier.Slot.CHEST,
                KciAttributeModifier.Operation.MULTIPLY, 2.0
        ));
        originalAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.MOVEMENT_SPEED, KciAttributeModifier.Slot.OFFHAND,
                KciAttributeModifier.Operation.ADD_FACTOR, 1.4
        ));

        List<KciAttributeModifier> mergedAttributes = new ArrayList<>(3);
        mergedAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ARMOR, KciAttributeModifier.Slot.CHEST,
                KciAttributeModifier.Operation.ADD, 5.0
        ));
        mergedAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.MOVEMENT_SPEED, KciAttributeModifier.Slot.OFFHAND,
                KciAttributeModifier.Operation.ADD_FACTOR, 2.8
        ));
        mergedAttributes.add(KciAttributeModifier.createQuick(
                KciAttributeModifier.Attribute.ARMOR_TOUGHNESS, KciAttributeModifier.Slot.CHEST,
                KciAttributeModifier.Operation.MULTIPLY, 2.0
        ));

        assertEquals(mergedAttributes, AttributeMerger.merge(originalAttributes));
    }
}
