import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class VersionTrimmer {

    private static final String START_MARKER = "// MARKER START ";
    private static final String END_MARKER = "// MARKER END ";

    private static final String MARKER_GENERAL = "GENERAL";
    private static final String MARKER_JAVA8 = "JAVA8";
    private static final String MARKER_JAVA16 = "JAVA16";
    private static final String MARKER_JAVA17 = "JAVA17";
    private static final String MARKER_CE_EVENT_HANDLER = "CE EVENT HANDLER";
    private static final String MARKER_CUSTOM_RECIPES = "CUSTOM RECIPES";
    private static final String MARKER_TEST_CUSTOM_RECIPES = "TEST CUSTOM RECIPES";
    private static final String MARKER_NMS12_DEPENDENCY = "NMS 12 DEPENDENCY";
    private static final String MARKER_NMS_DEPENDENCIES = "NMS DEPENDENCIES";
    private static final String MARKER_NMS_BASE = "NMS BASE";
    private static final String MARKER_NMS13PLUS = "NMS13PLUS";
    private static final String MARKER_NMS13 = "NMS13";
    private static final String MARKER_NMS14 = "NMS14";
    private static final String MARKER_NMS15 = "NMS15";
    private static final String MARKER_NMS16PLUS = "NMS16PLUS";
    private static final String MARKER_NMS16 = "NMS16";
    private static final String MARKER_NMS17PLUS = "NMS17PLUS";
    private static final String MARKER_NMS17 = "NMS17";
    private static final String MARKER_NMS18PLUS = "NMS18PLUS";
    private static final String MARKER_NMS18 = "NMS18";
    private static final String MARKER_NMS19 = "NMS19";
    private static final String MARKER_NMS20 = "NMS20";

    private static final String[] ALL_MARKERS = {
            MARKER_GENERAL, MARKER_JAVA8, MARKER_JAVA16, MARKER_JAVA17, MARKER_CE_EVENT_HANDLER,
            MARKER_TEST_CUSTOM_RECIPES, MARKER_CUSTOM_RECIPES, MARKER_NMS_BASE,
            MARKER_NMS12_DEPENDENCY, MARKER_NMS_DEPENDENCIES, MARKER_NMS13PLUS, MARKER_NMS13, MARKER_NMS14,
            MARKER_NMS15, MARKER_NMS16PLUS, MARKER_NMS16, MARKER_NMS17PLUS, MARKER_NMS17,
            MARKER_NMS18PLUS, MARKER_NMS18, MARKER_NMS19, MARKER_NMS20
    };

    public static void main(String[] args) throws IOException {
        List<Line> lines = new ArrayList<>();
        String currentMarker = null;

        Scanner gradleScanner = new Scanner(new File("build.gradle"));
        while (gradleScanner.hasNextLine()) {

            String originalLine = gradleScanner.nextLine();
            if (originalLine.startsWith(START_MARKER)) {
                String newMarker = originalLine.substring(START_MARKER.length());
                if (currentMarker != null) {
                    throw new IllegalStateException("current marker is " + currentMarker + " and new marker is " + newMarker);
                }
                if (Arrays.stream(ALL_MARKERS).noneMatch(validMarker -> validMarker.equals(newMarker))) {
                    throw new IllegalArgumentException("Invalid marker " + newMarker);
                }
                currentMarker = newMarker;
            }

            else if (originalLine.startsWith(END_MARKER)) {
                String oldMarker = originalLine.substring(END_MARKER.length());
                if (!oldMarker.equals(currentMarker)) {
                    throw new IllegalArgumentException("Ending marker " + oldMarker + ", but current marker is " + currentMarker);
                }
                currentMarker = null;
            }

            else {
                String lineToAdd;
                if (
                        MARKER_JAVA16.equals(currentMarker) || MARKER_NMS17.equals(currentMarker)
                                || MARKER_NMS18.equals(currentMarker) || MARKER_JAVA17.equals(currentMarker)
                                || MARKER_NMS17PLUS.equals(currentMarker) || MARKER_NMS18PLUS.equals(currentMarker)
                                || MARKER_NMS19.equals(currentMarker) || MARKER_NMS20.equals(currentMarker)
                                || MARKER_CE_EVENT_HANDLER.equals(currentMarker)
                                || MARKER_TEST_CUSTOM_RECIPES.equals(currentMarker)
                ) {
                    // The projects for kci-nms 17 and later are commented out, so we should remove the 2 slashes
                    lineToAdd = originalLine.substring(2);
                } else {
                    lineToAdd = originalLine;
                }
                lines.add(new Line(currentMarker, lineToAdd));
            }
        }
        gradleScanner.close();

        String[] allowedMarkers;
        String[] allowedProjects;
        String targetVersion = args[0];
        switch (targetVersion) {
            case "jitpack":
                allowedMarkers = new String[]{MARKER_GENERAL, MARKER_JAVA8, MARKER_CUSTOM_RECIPES, MARKER_NMS_BASE, null};
                allowedProjects = new String[]{
                        "bit-helper", "gui", "custom-recipes", "shared-code", "plug-in", "editor", "kci-nms"
                };
                break;
            case "1.12":
                allowedMarkers = new String[]{
                        MARKER_GENERAL, MARKER_JAVA8, MARKER_NMS12_DEPENDENCY,
                        MARKER_CUSTOM_RECIPES, MARKER_NMS_BASE, null
                };
                allowedProjects = new String[]{
                        "bit-helper", "gui", "custom-recipes", "shared-code", "plug-in", "editor", "kci-nms", "kci-nms12"
                };
                break;
            case "1.13-to-1.16":
                allowedMarkers = new String[]{
                        MARKER_GENERAL, MARKER_NMS_BASE, MARKER_JAVA8, MARKER_NMS13PLUS, MARKER_NMS13,
                        MARKER_NMS14, MARKER_NMS15, MARKER_NMS16PLUS, MARKER_NMS16
                };
                allowedProjects = new String[]{
                        "kci-nms", "kci-nms13plus", "kci-nms13", "kci-nms14", "kci-nms15", "kci-nms16plus", "kci-nms16"
                };
                break;
            case "1.17":
                allowedMarkers = new String[]{
                        MARKER_GENERAL, MARKER_NMS_BASE, MARKER_JAVA16, MARKER_NMS13PLUS, MARKER_NMS17PLUS, MARKER_NMS17
                };
                allowedProjects = new String[]{
                        "kci-nms", "kci-nms13plus", "kci-nms16plus", "kci-nms17"
                };
                break;
            case "1.18-to-1.20":
                allowedMarkers = new String[]{
                        MARKER_GENERAL, MARKER_JAVA17, MARKER_CE_EVENT_HANDLER, MARKER_NMS_BASE,
                        MARKER_NMS13PLUS, MARKER_NMS18PLUS, MARKER_NMS18, MARKER_NMS19, MARKER_NMS20
                };
                allowedProjects = new String[]{
                        "ce-event-handler", "kci-nms", "kci-nms13plus", "kci-nms16plus",
                        "kci-nms18", "kci-nms18plus", "kci-nms19", "kci-nms20"
                };
                break;
            case "test-custom-recipes":
                allowedMarkers = new String[]{ MARKER_GENERAL, MARKER_JAVA17, MARKER_TEST_CUSTOM_RECIPES };
                allowedProjects = new String[]{ "custom-recipes", "test-custom-recipes" };
                break;
            default:
                throw new IllegalArgumentException("Unknown target version " + targetVersion);
        }

        PrintWriter gradleWriter = new PrintWriter("build.gradle");
        for (Line line : lines) {
            if (Arrays.stream(allowedMarkers).anyMatch(marker -> Objects.equals(marker, line.marker))) {
                gradleWriter.println(line.content);
            }
        }
        gradleWriter.flush();
        gradleWriter.close();

        PrintWriter settingsWriter = new PrintWriter("settings.gradle");
        settingsWriter.println("rootProject.name = 'custom-items'");
        settingsWriter.print("include ");
        for (int index = 0; index < allowedProjects.length; index++) {
            settingsWriter.print("'" + allowedProjects[index] + "'");
            if (index != allowedProjects.length - 1) {
                settingsWriter.print(", ");
            }
        }
        settingsWriter.println();
        settingsWriter.flush();
        settingsWriter.close();
    }

    private static class Line {

        final String marker;
        final String content;

        Line(String marker, String content) {
            this.marker = marker;
            this.content = content;
        }
    }
}
