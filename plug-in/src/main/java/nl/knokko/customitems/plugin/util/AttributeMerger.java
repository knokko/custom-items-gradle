package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.AttributeModifierValues;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;

import java.util.*;
import java.util.stream.Collectors;

import static nl.knokko.customitems.util.Checks.isClose;

public class AttributeMerger {

    public static RawAttribute convertAttributeModifier(AttributeModifierValues modifier, UUID id) {
        return new RawAttribute(
                id,
                modifier.getAttribute().getName(),
                modifier.getSlot().getSlot(),
                modifier.getOperation().getOperation(),
                modifier.getValue()
        );
    }

    public static RawAttribute[] merge(ItemSetWrapper itemSet, CustomItemValues item, Collection<UUID> upgradeIDs) {
        return merge(item, upgradeIDs.stream().map(
                upgradeID -> itemSet.get().getUpgrade(upgradeID).get()
        ).collect(Collectors.toList()));
    }

    public static RawAttribute[] merge(CustomItemValues item, Collection<UpgradeValues> upgrades) {
        List<AttributeModifierValues> kciAttributes = merge(collectAttributeModifiers(item, upgrades));
        return kciAttributes.stream().map(raw -> convertAttributeModifier(raw, UUID.randomUUID())).toArray(RawAttribute[]::new);
    }

    static List<AttributeModifierValues> collectAttributeModifiers(
            CustomItemValues item, Collection<UpgradeValues> upgrades
    ) {
        List<AttributeModifierValues> allAttributes = new ArrayList<>();
        if (item != null) allAttributes.addAll(item.getAttributeModifiers());
        for (UpgradeValues upgrade : upgrades) allAttributes.addAll(upgrade.getAttributeModifiers());
        return allAttributes;
    }

    private static AttributeCore getCore(AttributeModifierValues modifier) {
        return new AttributeCore(modifier.getAttribute(), modifier.getSlot(), modifier.getOperation());
    }

    private static AttributeModifierValues backToModifier(AttributeCore core, double value) {
        return AttributeModifierValues.createQuick(
                core.attribute, core.slot, core.operation, value
        );
    }

    static List<AttributeModifierValues> merge(List<AttributeModifierValues> original) {
        Map<AttributeCore, Double> map = new HashMap<>();

        for (AttributeModifierValues modifier : original) {
            AttributeCore core = getCore(modifier);
            if (modifier.getOperation() == AttributeModifierValues.Operation.ADD
                    || modifier.getOperation() == AttributeModifierValues.Operation.ADD_FACTOR) {
                double value = map.getOrDefault(core, 0.0);
                map.put(core, value + modifier.getValue());
            } else if (modifier.getOperation() == AttributeModifierValues.Operation.MULTIPLY) {
                double value = map.getOrDefault(core, 1.0);
                map.put(core, value * (1.0 + modifier.getValue()));
            } else {
                throw new IllegalArgumentException("Unknown operation " + modifier.getOperation());
            }
        }

        List<AttributeModifierValues> mergedAttributes = new ArrayList<>(map.size());
        for (Map.Entry<AttributeCore, Double> entry : map.entrySet()) {
            AttributeCore core = entry.getKey();
            double value = entry.getValue();
            if (core.operation == AttributeModifierValues.Operation.MULTIPLY) value -= 1.0;
            if (!isClose(value, 0.0)) mergedAttributes.add(backToModifier(core, value));
        }

        // Attribute modifiers should be sorted by their attribute, slot, and operation in the order of first occurrence
        // in the original list, see https://github.com/knokko/custom-items-gradle/issues/241
        mergedAttributes.sort((a, b) -> {
            for (AttributeModifierValues originalModifier : original) {
                if (a.getAttribute() == originalModifier.getAttribute()
                                && a.getSlot() == originalModifier.getSlot()
                                && a.getOperation() == originalModifier.getOperation()
                ) {
                    return -1;
                }

                if (b.getAttribute() == originalModifier.getAttribute()
                        && b.getSlot() == originalModifier.getSlot()
                        && b.getOperation() == originalModifier.getOperation()
                ) {
                    return 1;
                }
            }

            return 0;
        });

        return mergedAttributes;
    }

    private static class AttributeCore {

        final AttributeModifierValues.Attribute attribute;
        final AttributeModifierValues.Slot slot;
        final AttributeModifierValues.Operation operation;

        AttributeCore(
                AttributeModifierValues.Attribute attribute,
                AttributeModifierValues.Slot slot,
                AttributeModifierValues.Operation operation
        ) {
            this.attribute = attribute;
            this.slot = slot;
            this.operation = operation;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof AttributeCore) {
                AttributeCore otherCore = (AttributeCore) other;
                return this.attribute == otherCore.attribute && this.slot == otherCore.slot
                        && this.operation == otherCore.operation;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return attribute.ordinal() + 10 * slot.ordinal() + 100 * operation.ordinal();
        }
    }
}
