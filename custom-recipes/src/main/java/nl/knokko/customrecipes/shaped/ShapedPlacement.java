package nl.knokko.customrecipes.shaped;

public class ShapedPlacement {

    public final int offsetX, offsetY;
    public final int sizeX, sizeY;
    public final int gridSize;

    public ShapedPlacement(int offsetX, int offsetY, int sizeX, int sizeY, int gridSize) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.gridSize = gridSize;
    }
}
