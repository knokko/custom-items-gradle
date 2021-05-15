package nl.knokko.customitems.block;

import static nl.knokko.customitems.block.BlockConstants.MAX_BLOCK_ID;
import static nl.knokko.customitems.block.BlockConstants.MIN_BLOCK_ID;

public class BlockTablePrinter {

    public static void main(String[] args) {
        for (int blockID = MIN_BLOCK_ID; blockID <= MAX_BLOCK_ID; blockID++) {
            System.out.println("<tr>");
            System.out.println("    <td>" + blockID + "</td>");
            System.out.println("    <td>" + MushroomBlockMapping.getType(blockID).name().toLowerCase() + "</td>");
            for (boolean side : MushroomBlockMapping.getDirections(blockID)) {
                System.out.println("    <td>" + (side ? "yes" : "no") + "</td>");
            }
            System.out.println("</tr>");
        }
    }
}
