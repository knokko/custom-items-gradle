package nl.knokko.customitems.container;

import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.ContainerSlot;
import nl.knokko.customitems.container.slot.EmptySlot;
import nl.knokko.customitems.container.slot.display.SlotDisplay;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Checks;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.awt.image.BufferedImage;
import java.util.*;

import static nl.knokko.customitems.texture.KciTexture.*;

public class KciContainer extends ModelValues {

    private static final int WIDTH = 9;

    public static KciContainer load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        KciContainer result = new KciContainer(false);

        if (encoding < 1 || encoding > 4) throw new UnknownEncodingException("CustomContainer", encoding);

        result.name = input.readString();
        result.selectionIcon = SlotDisplay.load(input, itemSet);
        int numRecipes = input.readInt();
        result.recipes = new ArrayList<>(numRecipes);
        for (int counter = 0; counter < numRecipes; counter++) {
            result.recipes.add(ContainerRecipe.load(input, itemSet));
        }
        if (input.readBoolean()) {
            result.fuelMode = FuelMode.ANY;
        } else {
            result.fuelMode = FuelMode.ALL;
        }
        int height = input.readInt();
        result.slots = new ContainerSlot[WIDTH][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < WIDTH; x++) {
                result.slots[x][y] = ContainerSlot.load(input, itemSet);
            }
        }

        if (encoding == 1) {
            result.host = new ContainerHost(VContainerType.valueOf(input.readString()));
        } else {
            result.host = ContainerHost.load(input, itemSet);
        }

        if (encoding <= 2) {
            result.storageMode = input.readBoolean() ? ContainerStorageMode.PER_LOCATION : ContainerStorageMode.NOT_PERSISTENT;
            result.overlayChar = 0;
            result.overlayTexture = null;
            result.requiresPermission = false;
        } else {
            result.storageMode = ContainerStorageMode.valueOf(input.readString());
            if (input.readBoolean()) {
                result.overlayChar = input.readChar();
                result.overlayTexture = loadImage(input, true);
            } else {
                result.overlayChar = 0;
                result.overlayTexture = null;
            }
            result.requiresPermission = input.readBoolean();
        }

        if (encoding >= 4) {
            result.hidden = input.readBoolean();
        } else {
            result.hidden = false;
        }

        return result;
    }

    private String name;
    private SlotDisplay selectionIcon;

    private ContainerSlot[][] slots;
    private List<ContainerRecipe> recipes;

    private FuelMode fuelMode;
    private ContainerHost host;
    private ContainerStorageMode storageMode;

    private char overlayChar;
    private BufferedImage overlayTexture;
    private boolean requiresPermission;
    private boolean hidden;

    public KciContainer(boolean mutable) {
        super(mutable);
        this.name = "";
        this.selectionIcon = new SlotDisplay(false);
        this.slots = new ContainerSlot[WIDTH][6];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < this.slots[x].length; y++) {
                this.slots[x][y] = new EmptySlot();
            }
        }
        this.recipes = new ArrayList<>(0);
        this.fuelMode = FuelMode.ALL;
        this.host = new ContainerHost(VContainerType.CRAFTING_TABLE);
        this.storageMode = ContainerStorageMode.NOT_PERSISTENT;
        this.overlayChar = 0;
        this.overlayTexture = null;
        this.requiresPermission = false;
        this.hidden = false;
    }

    public KciContainer(KciContainer toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.selectionIcon = toCopy.getSelectionIcon();
        this.slots = toCopy.getSlots();
        this.recipes = toCopy.getRecipes();
        this.fuelMode = toCopy.getFuelMode();
        this.host = toCopy.getHost();
        this.storageMode = toCopy.getStorageMode();
        this.overlayChar = toCopy.getOverlayChar();
        this.overlayTexture = toCopy.getOverlayTexture();
        this.requiresPermission = toCopy.requiresPermission();
        this.hidden = toCopy.isHidden();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 4);
        output.addString(name);
        selectionIcon.save(output);
        output.addInt(recipes.size());
        for (ContainerRecipe recipe : recipes) {
            recipe.save(output);
        }
        output.addBoolean(fuelMode == FuelMode.ANY);
        output.addInt(getHeight());
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < WIDTH; x++) {
                slots[x][y].save(output);
            }
        }
        host.save(output);
        output.addString(storageMode.name());
        output.addBoolean(overlayTexture != null);
        if (overlayTexture != null) {
            output.addChar(overlayChar);
            saveImage(output, overlayTexture);
        }
        output.addBoolean(requiresPermission);
        output.addBoolean(hidden);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == KciContainer.class) {
            KciContainer otherContainer = (KciContainer) other;
            return this.name.equals(otherContainer.name) && this.selectionIcon.equals(otherContainer.selectionIcon)
                    && this.recipes.equals(otherContainer.recipes) && this.fuelMode == otherContainer.fuelMode
                    && Arrays.deepEquals(this.slots, otherContainer.slots) && this.host.equals(otherContainer.host)
                    && this.storageMode == otherContainer.storageMode && areImagesEqual(this.overlayTexture, otherContainer.overlayTexture)
                    && this.overlayChar == otherContainer.overlayChar && this.requiresPermission == otherContainer.requiresPermission
                    && this.hidden == otherContainer.hidden;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Container " + name;
    }

    @Override
    public KciContainer copy(boolean mutable) {
        return new KciContainer(this, mutable);
    }

    public String getName() {
        return name;
    }

    public SlotDisplay getSelectionIcon() {
        return selectionIcon;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return slots[0].length;
    }

    private void checkSlotIndices(int x, int y) throws IllegalArgumentException {
        if (x < 0) throw new IllegalArgumentException("X (" + x + ") can't be negative");
        if (x >= WIDTH) throw new IllegalArgumentException("X (" + x + ") can be at most " + (WIDTH - 1));
        if (y < 0) throw new IllegalArgumentException("Y (" + y + ") can't be negative");
        if (y >= getHeight()) throw new IllegalArgumentException("Y (" + y + ") can be at most " + (getHeight() - 1));
    }

    public ContainerSlot getSlot(int x, int y) throws IllegalArgumentException {
        checkSlotIndices(x, y);
        return slots[x][y];
    }

    public ContainerSlot[][] getSlots() {
        ContainerSlot[][] shallowSlotsCopy = new ContainerSlot[WIDTH][getHeight()];
        for (int x = 0; x < WIDTH; x++) {
            if (getHeight() >= 0) System.arraycopy(this.slots[x], 0, shallowSlotsCopy[x], 0, getHeight());
        }
        return shallowSlotsCopy;
    }

    public Collection<ContainerSlot> createSlotList() {
        return createSlotList(this.slots);
    }

    public static Collection<ContainerSlot> createSlotList(ContainerSlot[][] slots) {
        Collection<ContainerSlot> slotList = new ArrayList<>(WIDTH * slots[0].length);
        for (ContainerSlot[] slotArray : slots) {
            Collections.addAll(slotList, slotArray);
        }
        return slotList;
    }

    public List<ContainerRecipe> getRecipes() {
        return new ArrayList<>(recipes);
    }

    public FuelMode getFuelMode() {
        return fuelMode;
    }

    public ContainerHost getHost() {
        return host;
    }

    public ContainerStorageMode getStorageMode() {
        return storageMode;
    }

    public char getOverlayChar() {
        return overlayChar;
    }

    public BufferedImage getOverlayTexture() {
        return overlayTexture;
    }

    public boolean requiresPermission() {
        return requiresPermission;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setSelectionIcon(SlotDisplay selectionIcon) {
        assertMutable();
        Checks.notNull(selectionIcon);
        this.selectionIcon = selectionIcon;
    }

    public void setSlot(int x, int y, ContainerSlot newSlot) throws IllegalArgumentException {
        assertMutable();
        checkSlotIndices(x, y);
        Checks.notNull(newSlot);
        this.slots[x][y] = newSlot.copy(false);
    }

    public void setSlots(ContainerSlot[][] newSlots) {
        assertMutable();
        int height = newSlots[0].length;
        this.slots = new ContainerSlot[WIDTH][height];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < height; y++) {
                this.slots[x][y] = newSlots[x][y].copy(false);
            }
        }
    }

    public void removeSlotRow(int rowIndex) throws IllegalArgumentException {
        assertMutable();
        if (rowIndex < 0) throw new IllegalArgumentException("Row index (" + rowIndex + ") can't be negative");
        if (rowIndex >= getHeight()) throw new IllegalArgumentException("Row index (" + rowIndex + ") can be at most " + (getHeight() - 1));

        ContainerSlot[][] newSlots = new ContainerSlot[WIDTH][getHeight() - 1];
        for (int x = 0; x < WIDTH; x++) {
            System.arraycopy(this.slots[x], 0, newSlots[x], 0, rowIndex);
            System.arraycopy(this.slots[x], rowIndex + 1, newSlots[x], rowIndex, getHeight() - (rowIndex + 1));
        }
        this.slots = newSlots;
    }

    public void insertSlotRow(int rowIndex) throws IllegalArgumentException {
        assertMutable();
        if (rowIndex < 0) throw new IllegalArgumentException("Row index (" + rowIndex + ") can't be negative");
        if (rowIndex > getHeight()) throw new IllegalArgumentException("Row index (" + rowIndex + ") can be at most " + getHeight());

        ContainerSlot[][] newSlots = new ContainerSlot[WIDTH][getHeight() + 1];
        for (int x = 0; x < WIDTH; x++) {
            System.arraycopy(this.slots[x], 0, newSlots[x], 0, rowIndex);
            newSlots[x][rowIndex] = new EmptySlot();
            System.arraycopy(this.slots[x], rowIndex, newSlots[x], rowIndex + 1, getHeight() - rowIndex);
        }
        this.slots = newSlots;
    }

    public void setRecipes(List<ContainerRecipe> recipes) {
        assertMutable();
        Checks.nonNull(recipes);
        this.recipes = Mutability.createDeepCopy(recipes, false);
    }

    public void setFuelMode(FuelMode fuelMode) {
        assertMutable();
        Checks.notNull(fuelMode);
        this.fuelMode = fuelMode;
    }

    public void setHost(ContainerHost newHost) {
        assertMutable();
        Checks.notNull(newHost);
        this.host = newHost;
    }

    public void setStorageMode(ContainerStorageMode newStorageMode) {
        assertMutable();
        this.storageMode = newStorageMode;
    }

    public void setOverlayChar(char overlayChar) {
        // Leave out the assertMutable() check for resourcepack generation convenience
        this.overlayChar = overlayChar;
    }

    public void setOverlayTexture(BufferedImage newOverlayTexture) {
        assertMutable();
        this.overlayTexture = newOverlayTexture;
    }

    public void setRequiresPermission(boolean requiresPermission) {
        assertMutable();
        this.requiresPermission = requiresPermission;
    }

    public void setHidden(boolean hidden) {
        assertMutable();
        this.hidden = hidden;
    }

    public void validate(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (!name.equals(oldName) && itemSet.containers.get(name).isPresent()) {
            throw new ValidationException("Another container with this name already exists");
        }
        if (selectionIcon == null) throw new ProgrammingValidationException("No selection icon");

        Validation.scope("Selection icon", selectionIcon::validate, itemSet);
        if (slots == null) throw new ProgrammingValidationException("No slots");
        if (slots.length != WIDTH) throw new ProgrammingValidationException("Number of slot columns isn't 9");
        for (int x = 0; x < WIDTH; x++) {
            int height = getHeight();
            if (slots[x].length != height) throw new ProgrammingValidationException("Not all slot columns have equal length");
            for (int y = 0; y < height; y++) {
                if (slots[x][y] == null) throw new ProgrammingValidationException("Missing slot (" + x + ", " + y + ")");
                int rememberX = x;
                int rememberY = y;
                Collection<ContainerSlot> otherSlots = new ArrayList<>(WIDTH * height - 1);
                for (int otherX = 0; otherX < WIDTH; otherX++) {
                    for (int otherY = 0; otherY < height; otherY++) {
                        if (otherX != x || otherY != y) {
                            otherSlots.add(slots[otherX][otherY]);
                        }
                    }
                }
                Validation.scope("Slot (" + x + ", " + y + ")", () -> slots[rememberX][rememberY].validate(itemSet, otherSlots));
            }
        }

        if (recipes == null) throw new ProgrammingValidationException("No recipes");
        for (ContainerRecipe recipe : recipes) {
            if (recipe == null) throw new ProgrammingValidationException("Missing a recipe");
            Validation.scope("Recipe", () -> recipe.validate(itemSet, this));
        }

        for (int index1 = 0; index1 < recipes.size(); index1++) {
            for (int index2 = index1 + 1; index2 < recipes.size(); index2++) {
                if (recipes.get(index1).conflictsWith(recipes.get(index2))) {
                    throw new ValidationException("Recipe " + (index1 + 1) + " conflicts with recipe " + (index2 + 1));
                }
            }
        }

        if (fuelMode == null) throw new ProgrammingValidationException("No fuel mode");
        if (host == null) throw new ProgrammingValidationException("No host");
        Validation.scope("Host", host::validate, itemSet);
        if (storageMode == null) throw new ProgrammingValidationException("No storage mode");
        if (overlayTexture != null) {
            if (overlayTexture.getWidth() != 256) throw new ValidationException("The overlay texture width should be 256 pixels");
            if (overlayTexture.getHeight() != 105) throw new ValidationException("The overlay texture height should be 105 pixels");
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        Validation.scope("Selection icon", () -> selectionIcon.validateExportVersion(version));
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < getHeight(); y++) {
                int finalX = x;
                int finalY = y;
                Validation.scope(
                        "Slot ("+ (x + 1) + "," + (y + 1) + ")",
                        () -> getSlot(finalX, finalY).validateExportVersion(version)
                );
            }
        }
        for (ContainerRecipe recipe : recipes) {
            Validation.scope("Recipes", () -> recipe.validateExportVersion(version));
        }
        host.validateExportVersion(version);
    }
}
