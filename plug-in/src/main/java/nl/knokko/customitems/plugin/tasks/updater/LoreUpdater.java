package nl.knokko.customitems.plugin.tasks.updater;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LoreUpdater {

    private static final String DURABILITY_SPLIT = " / ";

    public static boolean updateBaseLore(ItemMeta meta, List<String> oldBaseLore, List<String> newBaseLore) {
        List<String> oldLore = null;
        if (meta.hasLore()) oldLore = meta.getLore();

        // If the item didn't have any lore, it can just be recreated
        if (oldLore == null) return true;

        Integer fragmentIndex = findLoreFragmentIndex(oldLore, oldBaseLore);
        if (fragmentIndex != null) {
            meta.setLore(replaceLoreFragment(oldLore, fragmentIndex, oldBaseLore.size(), newBaseLore));
        }
        return false;
    }

    static Integer findLoreFragmentIndex(List<String> fullLore, List<String> loreFragment) {
        // Loop from high index to low index because the base lore is preferably at the end.
        // This is important when the old lore is empty, but the new lore is not, since that would cause any
        // position in the full lore to match.
        outerLoop:
        for (int startIndex = fullLore.size() - loreFragment.size(); startIndex >= 0; startIndex--) {
            for (int fragmentIndex = 0; fragmentIndex < loreFragment.size(); fragmentIndex++) {
                if (!fullLore.get(startIndex + fragmentIndex).equals(loreFragment.get(fragmentIndex))) {
                    continue outerLoop;
                }
            }
            return startIndex;
        }

        return null;
    }

    static List<String> replaceLoreFragment(List<String> oldLore, int replaceIndex, int oldFragmentSize, List<String> newFragment) {
        List<String> newLore = new ArrayList<>(oldLore.size() + newFragment.size() - oldFragmentSize);
        for (int index = 0; index < replaceIndex; index++) {
            newLore.add(oldLore.get(index));
        }
        newLore.addAll(newFragment);
        for (int index = replaceIndex + oldFragmentSize; index < oldLore.size(); index++) {
            newLore.add(oldLore.get(index));
        }
        return newLore;
    }

    /**
     * @param meta The `ItemMeta` of the tool whose durability should be updated
     * @param oldDurability The previous durability of the tool
     * @param newDurability The desired/new durability of the tool
     * @param oldMaxDurability The (expected) old maximum durability of the tool. If this value is unknown, you can
     *                         use newMaxDurability, although this makes it slightly harder to find the old lore line.
     * @param newMaxDurability The new maximum durability of the tool. Must be null if and only if newDurability is null
     * @param durabilityPrefix The (new) durability prefix of lang.yml
     * @return True if the caller should recreate the lore; false if not
     */
    public static boolean updateDurability(
            ItemMeta meta, Long oldDurability, Long newDurability,
            Long oldMaxDurability, Long newMaxDurability, String durabilityPrefix
    ) {
        List<String> oldLore = null;
        if (meta.hasLore()) oldLore = meta.getLore();

        // If the tool doesn't have any lore yet, it's easiest and best to just recreate the entire lore
        if (oldLore == null && newDurability != null) return true;

        if ((newDurability == null) != (newMaxDurability == null)) {
            throw new IllegalArgumentException("newMaxDurability must be null if and only if newDurability is null");
        }

        Integer durabilityLineIndex = null;
        if (oldLore != null) {
            durabilityLineIndex = findDurabilityLineIndex(oldLore, durabilityPrefix, oldDurability, oldMaxDurability);
        }

        if (newDurability == null) {
            if (durabilityLineIndex != null) {

                // Copy the original lore, but exclude the durability line and the empty line that follows it
                List<String> newLore = new ArrayList<>(oldLore.size() - 1);
                for (int index = 0; index < durabilityLineIndex; index++) {
                    newLore.add(oldLore.get(index));
                }
                for (int index = durabilityLineIndex + 1; index < oldLore.size(); index++) {

                    // If there is an empty line right after the old durability line, it should be skipped
                    if (index != durabilityLineIndex + 1 || !oldLore.get(index).isEmpty()) {
                        newLore.add(oldLore.get(index));
                    }
                }
                meta.setLore(newLore);
            }
        } else {
            List<String> newLore;
            if (durabilityLineIndex != null) {

                // Simply replace the old durability line with the new durability line
                newLore = new ArrayList<>(oldLore);
                newLore.set(durabilityLineIndex, createDurabilityLine(durabilityPrefix, newDurability, newMaxDurability));
            } else {

                // Simply prepend a new durability line to the lore
                newLore = new ArrayList<>(oldLore.size() + 2);
                newLore.add(createDurabilityLine(durabilityPrefix, newDurability, newMaxDurability));
                newLore.addAll(oldLore);
            }
            meta.setLore(newLore);
        }

        return false;
    }

    public static String createDurabilityLine(String prefix, long current, long max) {
        return prefix + " " + current + DURABILITY_SPLIT + max;
    }

    static Integer findDurabilityLineIndex(
            List<String> oldLore, String durabilityPrefix, Long oldDurability, Long oldMaxDurability
    ) {
        List<DurabilityLine> candidates = new ArrayList<>(1);

        // We expect the previous lore to contain exactly this durability line, but this is not guaranteed because
        // the durability prefix may change or the lore has been changed in the Editor (before being processed by the
        // item updater).
        String expectedLine = null;
        if (oldDurability != null && oldMaxDurability != null) {
            expectedLine = createDurabilityLine(durabilityPrefix, oldDurability, oldMaxDurability);
        }

        for (int index = 0; index < oldLore.size(); index++) {
            String line = oldLore.get(index);

            // If the line matches the expected line exactly, we can return it immediately
            if (line.equals(expectedLine)) return index;

            // Otherwise, check if the line is a candidate
            else {
                DurabilityLine candidate = parseDurabilityLine(index, line);
                if (candidate != null) candidates.add(candidate);
            }
        }

        // When there are 0 or 1 candidates, we can return immediately
        if (candidates.isEmpty()) return null;
        if (candidates.size() == 1) return candidates.get(0).index;

        // If there are multiple, we should try to pick the best candidate.
        // This is just an attempt, and we hopefully pick the right one.
        // Luckily, this should rarely occur in practice.

        boolean hasAnyRightPrefix = candidates.stream().anyMatch(candidate -> candidate.prefix.equals(durabilityPrefix));
        if (hasAnyRightPrefix) {
            candidates.removeIf(candidate -> !candidate.prefix.equals(durabilityPrefix));
            if (candidates.size() == 1) return candidates.get(0).index;
        }

        boolean hasAnyRightMaximum = oldMaxDurability != null && candidates.stream().anyMatch(
                candidate -> candidate.maxDurability == oldMaxDurability
        );
        if (hasAnyRightMaximum) {
            candidates.removeIf(candidate -> candidate.maxDurability != oldMaxDurability);
            if (candidates.size() == 1) return candidates.get(0).index;
        }

        boolean hasAnyRightValue = oldDurability != null && candidates.stream().anyMatch(
                candidate -> candidate.oldDurability == oldDurability
        );
        if (hasAnyRightValue) {
            candidates.removeIf(candidate -> candidate.oldDurability != oldDurability);
        }

        // If there is still more than 1 line left, we will guess the first line
        return candidates.get(0).index;
    }

    static DurabilityLine parseDurabilityLine(int index, String line) {
        if (line.contains(DURABILITY_SPLIT)) {
            int indexSplit = line.lastIndexOf(DURABILITY_SPLIT);
            try {
                int startIndexOldDurability = line.lastIndexOf(' ', indexSplit - 1);
                if (startIndexOldDurability == -1) return null;

                long parsedOldDurability = Long.parseLong(line.substring(startIndexOldDurability + 1, indexSplit));
                long parsedMaxDurability = Long.parseLong(line.substring(indexSplit + DURABILITY_SPLIT.length()));

                return new DurabilityLine(
                        index, line.substring(0, startIndexOldDurability), parsedOldDurability, parsedMaxDurability
                );
            } catch (NumberFormatException invalidFormat) {
                // This block will be reached when the line looks like a valid durability line
                return null;
            }
        } else {
            return null;
        }
    }

    static class DurabilityLine {

        final int index;
        final String prefix;
        final long oldDurability;
        final long maxDurability;

        DurabilityLine(int index, String prefix, long oldDurability, long maxDurability) {
            this.index = index;
            this.prefix = prefix;
            this.oldDurability = oldDurability;
            this.maxDurability = maxDurability;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof DurabilityLine) {
                DurabilityLine otherLine = (DurabilityLine) other;
                return this.index == otherLine.index && this.prefix.equals(otherLine.prefix)
                        && this.oldDurability == otherLine.oldDurability && this.maxDurability == otherLine.maxDurability;
            } else {
                return false;
            }
        }
    }
}
