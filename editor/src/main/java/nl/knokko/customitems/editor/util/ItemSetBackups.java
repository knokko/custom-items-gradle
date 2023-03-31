package nl.knokko.customitems.editor.util;

import java.util.*;
import java.util.stream.Collectors;

public class ItemSetBackups {

    public static Collection<ItemSetBackups> getAll(String[] fileNames) {
        Map<String, List<Long>> backupMap = new HashMap<>();
        for (String fileName : fileNames) {
            if (fileName.endsWith(".cisb")) {
                int indexSpace = fileName.lastIndexOf(' ');
                if (indexSpace != -1) {
                    String itemSetName = fileName.substring(0, indexSpace);
                    try {
                        long saveTime = Long.parseLong(fileName.substring(indexSpace + 1, fileName.length() - ".cisb".length()));
                        if (!backupMap.containsKey(itemSetName)) {
                            backupMap.put(itemSetName, new ArrayList<>());
                        }
                        backupMap.get(itemSetName).add(saveTime);
                    } catch (NumberFormatException invalid) {
                        // I guess we should just skip this file since it's not a valid back-up
                    }
                }
            }
        }

        return backupMap.entrySet().stream().map(entry -> new ItemSetBackups(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    public final String name;
    private final List<Long> saveTimes;

    public ItemSetBackups(String name, List<Long> saveTimes) {
        this.name = Objects.requireNonNull(name);
        this.saveTimes = new ArrayList<>(saveTimes);
        Collections.sort(this.saveTimes);
        Collections.reverse(this.saveTimes);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ItemSetBackups) {
            ItemSetBackups otherBackups = (ItemSetBackups) other;
            return this.name.equals(otherBackups.name) && this.saveTimes.equals(otherBackups.saveTimes);
        } else return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + 31 * this.saveTimes.hashCode();
    }

    @Override
    public String toString() {
        return "BackUp(" + name + ": " + saveTimes + ")";
    }

    public List<Long> getSaveTimes() {
        return Collections.unmodifiableList(saveTimes);
    }

    public Collection<Long> cleanOldBackups(long currentTime) {
        Collection<Long> savesToRemove = new ArrayList<>();
        int numSaves = this.saveTimes.size();

        // 1 day has 24 hours, 1 hour has 3600 seconds, and 1 second has 1000 milliseconds
        long dayDuration = 1000 * 3600 * 24;

        // Never clean up back-ups that are less than 24 hours old
        int saveIndex = 0;
        while (saveIndex < numSaves && this.saveTimes.get(saveIndex) > currentTime - dayDuration) saveIndex++;

        // For back-ups that are older than 24 hours, but not older than 30 days, there should be at most 1 back-up per day
        for (int dayCounter = 1; dayCounter < 30; dayCounter++) {
            long endDayTime = currentTime - dayCounter * dayDuration;
            long startDayTime = endDayTime - dayDuration;

            int numSavesDuringThisDay = 0;
            while (saveIndex < numSaves && this.saveTimes.get(saveIndex) > startDayTime) {

                numSavesDuringThisDay++;
                if (numSavesDuringThisDay > 1) savesToRemove.add(this.saveTimes.get(saveIndex));

                saveIndex++;
            }
        }

        long weekDuration = 7 * dayDuration;

        // For back-ups older than 30 days, there should be a most 1 back-up per week
        int weekCounter = 0;
        while (saveIndex < numSaves) {
            long endWeekTime = currentTime - 30 * dayDuration - weekCounter * weekDuration;
            long startWeekTime = endWeekTime - weekDuration;

            int numSavesDuringThisWeek = 0;
            while (saveIndex < numSaves && this.saveTimes.get(saveIndex) > startWeekTime) {

                numSavesDuringThisWeek++;
                if (numSavesDuringThisWeek > 1) savesToRemove.add(this.saveTimes.get(saveIndex));

                saveIndex++;
            }

            weekCounter++;
        }

        return savesToRemove;
    }
}
