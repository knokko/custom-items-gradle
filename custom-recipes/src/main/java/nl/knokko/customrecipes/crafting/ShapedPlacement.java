package nl.knokko.customrecipes.crafting;

class ShapedPlacement {

    final int offsetX, offsetY;
    final int sizeX, sizeY;
    final int gridSize;

    ShapedPlacement(int offsetX, int offsetY, int sizeX, int sizeY, int gridSize) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.gridSize = gridSize;
    }
}
