package nl.knokko.customitems.editor.menu.edit.container.overlay;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TemplateGenerator {

    private static final Color OUTER_EDGE_COLOR = new Color(60, 0, 85);
    private static final Color INNER_EDGE_COLOR = new Color(120, 0, 175);
    private static final Color SLOT_EDGE_COLOR = new Color(0, 65, 175);

    public static BufferedImage generateTemplate(int numRows) {
        BufferedImage image = new BufferedImage(256, 105, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(41, 4, OUTER_EDGE_COLOR.getRGB());
        image.setRGB(214, 4, OUTER_EDGE_COLOR.getRGB());

        Graphics graphics = image.createGraphics();
        graphics.setColor(OUTER_EDGE_COLOR);
        int maxY = 22 + 18 * numRows;
        graphics.drawLine(42, 3, 213, 3);
        graphics.drawLine(40, 5, 40, maxY);
        graphics.drawLine(40, maxY, 215, maxY);
        graphics.drawLine(215, 5, 215, maxY);

        graphics.setColor(INNER_EDGE_COLOR);
        graphics.drawLine(42, 4, 213, 4);
        graphics.drawLine(41, 5, 214, 5);
        graphics.drawLine(41, 6, 43, 6);
        graphics.drawLine(212, 6, 214, 6);
        graphics.drawLine(41, 7, 41, maxY - 1);
        graphics.drawLine(42, 7, 42, maxY - 1);
        graphics.drawLine(213, 7, 213, maxY - 1);
        graphics.drawLine(214, 7, 214, maxY - 1);

        graphics.setColor(SLOT_EDGE_COLOR);
        for (int currentRow = 0; currentRow < numRows; currentRow++) {
            for (int currentColumn = 0; currentColumn < 9; currentColumn++) {
                graphics.drawRect(47 + 18 * currentColumn, 20 + 18 * currentRow, 17, 17);
            }
        }

        graphics.dispose();
        return image;
    }
}
