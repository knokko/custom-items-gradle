package nl.knokko.customitems.plugin.util;

import nl.knokko.customitems.item.KciAttributeModifier;
import nl.knokko.customitems.item.KciItem;
import nl.knokko.customitems.nms.RawAttribute;
import nl.knokko.customitems.plugin.set.ItemSetWrapper;
import nl.knokko.customitems.recipe.upgrade.Upgrade;

import java.util.*;
import java.util.stream.Collectors;

import static nl.knokko.customitems.util.Checks.isClose;

public class AttributeMerger {

    public static RawAttribute convertAttributeModifier(KciAttributeModifier modifier, UUID id) {
        return new RawAttribute(
                id,
                modifier.getAttribute().getName(),
                modifier.getSlot().getSlot(),
                modifier.getOperation().getOperation(),
                modifier.getValue()
        );
    }

    public static RawAttribute[] merge(ItemSetWrapper itemSet, KciItem item, Collection<UUID> upgradeIDs) {
        return merge(item, upgradeIDs.stream().map(
                upgradeID -> itemSet.get().upgrades.get(upgradeID).get()
        ).collect(Collectors.toList()));
    }

    public static RawAttribute[] merge(KciItem item, Collection<Upgrade> upgrades) {
        List<KciAttributeModifier> kciAttributes = merge(collectAttributeModifiers(item, upgrades));
        return kciAttributes.stream().map(raw -> convertAttributeModifier(raw, UUID.randomUUID())).toArray(RawAttribute[]::new);
    }

    static List<KciAttributeModifier> collectAttributeModifiers(
            KciItem item, Collection<Upgrade> upgrades
    ) {
        List<KciAttributeModifier> allAttributes = new ArrayList<>();
        if (item != null) allAttributes.addAll(item.getAttributeModifiers());
        for (Upgrade upgrade : upgrades) allAttributes.addAll(upgrade.getAttributeModifiers());
        return allAttributes;
    }

    private static AttributeCore getCore(KciAttributeModifier modifier) {
        return new AttributeCore(modifier.getAttribute(), modifier.getSlot(), modifier.getOperation());
    }

    private static KciAttributeModifier backToModifier(AttributeCore core, double value) {
        return KciAttributeModifier.createQuick(
                core.attribute, core.slot, core.operation, value
        );
    }

    static List<KciAttributeModifier> merge(List<KciAttributeModifier> original) {
        Map<AttributeCore, Double> map = new HashMap<>();

        for (KciAttributeModifier modifier : original) {
            AttributeCore core = getCore(modifier);
            if (modifier.getOperation() == KciAttributeModifier.Operation.ADD
                    || modifier.getOperation() == KciAttributeModifier.Operation.ADD_FACTOR) {
                double value = map.getOrDefault(core, 0.0);
                map.put(core, value + modifier.getValue());
            } else if (modifier.getOperation() == KciAttributeModifier.Operation.MULTIPLY) {
                double value = map.getOrDefault(core, 1.0);
                map.put(core, value * (1.0 + modifier.getValue()));
            } else {
                throw new IllegalArgumentException("Unknown operation " + modifier.getOperation());
            }
        }

        List<KciAttributeModifier> mergedAttributes = new ArrayList<>(map.size());
        for (Map.Entry<AttributeCore, Double> entry : map.entrySet()) {
            AttributeCore core = entry.getKey();
            double value = entry.getValue();
            if (core.operation == KciAttributeModifier.Operation.MULTIPLY) value -= 1.0;
            if (!isClose(value, 0.0)) mergedAttributes.add(backToModifier(core, value));
        }

        // Attribute modifiers should be sorted by their attribute, slot, and operation in the order of first occurrence
        // in the original list, see https://github.com/knokko/custom-items-gradle/issues/241
        mergedAttributes.sort((a, b) -> {
            for (KciAttributeModifier originalModifier : original) {
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

        final KciAttributeModifier.Attribute attribute;
        final KciAttributeModifier.Slot slot;
        final KciAttributeModifier.Operation operation;

        AttributeCore(
                KciAttributeModifier.Attribute attribute,
                KciAttributeModifier.Slot slot,
                KciAttributeModifier.Operation operation
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
