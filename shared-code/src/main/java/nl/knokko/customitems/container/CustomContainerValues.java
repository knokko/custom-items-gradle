package nl.knokko.customitems.container;

import nl.knokko.customitems.container.fuel.FuelMode;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.EmptySlotValues;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
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

import java.util.*;

public class CustomContainerValues extends ModelValues {

    private static final int WIDTH = 9;

    public static CustomContainerValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        CustomContainerValues result = new CustomContainerValues(false);

        if (encoding < 1 || encoding > 2) throw new UnknownEncodingException("CustomContainer", encoding);

        result.name = input.readString();
        result.selectionIcon = SlotDisplayValues.load(input, itemSet);
        int numRecipes = input.readInt();
        result.recipes = new ArrayList<>(numRecipes);
        for (int counter = 0; counter < numRecipes; counter++) {
            result.recipes.add(ContainerRecipeValues.load(input, itemSet));
        }
        if (input.readBoolean()) {
            result.fuelMode = FuelMode.ANY;
        } else {
            result.fuelMode = FuelMode.ALL;
        }
        int height = input.readInt();
        result.slots = new ContainerSlotValues[WIDTH][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < WIDTH; x++) {
                result.slots[x][y] = ContainerSlotValues.load(input, itemSet);
            }
        }
        if (encoding == 1) {
            result.host = new CustomContainerHost(VanillaContainerType.valueOf(input.readString()));
        } else {
            result.host = CustomContainerHost.load(input, itemSet);
        }
        result.persistentStorage = input.readBoolean();

        return result;
    }

    private String name;
    private SlotDisplayValues selectionIcon;

    private ContainerSlotValues[][] slots;
    private List<ContainerRecipeValues> recipes;

    private FuelMode fuelMode;
    private CustomContainerHost host;
    private boolean persistentStorage;

    public CustomContainerValues(boolean mutable) {
        super(mutable);
        this.name = "";
        this.selectionIcon = new SlotDisplayValues(false);
        this.slots = new ContainerSlotValues[WIDTH][6];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < this.slots[x].length; y++) {
                this.slots[x][y] = new EmptySlotValues();
            }
        }
        this.recipes = new ArrayList<>(0);
        this.fuelMode = FuelMode.ALL;
        this.host = new CustomContainerHost(VanillaContainerType.CRAFTING_TABLE);
        this.persistentStorage = false;
    }

    public CustomContainerValues(CustomContainerValues toCopy, boolean mutable) {
        super(mutable);
        this.name = toCopy.getName();
        this.selectionIcon = toCopy.getSelectionIcon();
        this.slots = toCopy.getSlots();
        this.recipes = toCopy.getRecipes();
        this.fuelMode = toCopy.getFuelMode();
        this.host = toCopy.getHost();
        this.persistentStorage = toCopy.hasPersistentStorage();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 2);
        output.addString(name);
        selectionIcon.save(output);
        output.addInt(recipes.size());
        for (ContainerRecipeValues recipe : recipes) {
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
        output.addBoolean(persistentStorage);
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == CustomContainerValues.class) {
            CustomContainerValues otherContainer = (CustomContainerValues) other;
            return this.name.equals(otherContainer.name) && this.selectionIcon.equals(otherContainer.selectionIcon)
                    && this.recipes.equals(otherContainer.recipes) && this.fuelMode == otherContainer.fuelMode
                    && Arrays.deepEquals(this.slots, otherContainer.slots) && this.host.equals(otherContainer.host)
                    && this.persistentStorage == otherContainer.persistentStorage;
        } else {
            return false;
        }
    }

    @Override
    public CustomContainerValues copy(boolean mutable) {
        return new CustomContainerValues(this, mutable);
    }

    public String getName() {
        return name;
    }

    public SlotDisplayValues getSelectionIcon() {
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

    public ContainerSlotValues getSlot(int x, int y) throws IllegalArgumentException {
        checkSlotIndices(x, y);
        return slots[x][y];
    }

    public ContainerSlotValues[][] getSlots() {
        ContainerSlotValues[][] shallowSlotsCopy = new ContainerSlotValues[WIDTH][getHeight()];
        for (int x = 0; x < WIDTH; x++) {
            if (getHeight() >= 0) System.arraycopy(this.slots[x], 0, shallowSlotsCopy[x], 0, getHeight());
        }
        return shallowSlotsCopy;
    }

    public Collection<ContainerSlotValues> createSlotList() {
        return createSlotList(this.slots);
    }

    public static Collection<ContainerSlotValues> createSlotList(ContainerSlotValues[][] slots) {
        Collection<ContainerSlotValues> slotList = new ArrayList<>(WIDTH * slots[0].length);
        for (ContainerSlotValues[] slotArray : slots) {
            Collections.addAll(slotList, slotArray);
        }
        return slotList;
    }

    public List<ContainerRecipeValues> getRecipes() {
        return new ArrayList<>(recipes);
    }

    public FuelMode getFuelMode() {
        return fuelMode;
    }

    public CustomContainerHost getHost() {
        return host;
    }

    public boolean hasPersistentStorage() {
        return persistentStorage;
    }

    public void setName(String name) {
        assertMutable();
        Checks.notNull(name);
        this.name = name;
    }

    public void setSelectionIcon(SlotDisplayValues selectionIcon) {
        assertMutable();
        Checks.notNull(selectionIcon);
        this.selectionIcon = selectionIcon;
    }

    public void setSlot(int x, int y, ContainerSlotValues newSlot) throws IllegalArgumentException {
        assertMutable();
        checkSlotIndices(x, y);
        Checks.notNull(newSlot);
        this.slots[x][y] = newSlot.copy(false);
    }

    public void setSlots(ContainerSlotValues[][] newSlots) {
        assertMutable();
        int height = newSlots[0].length;
        this.slots = new ContainerSlotValues[WIDTH][height];
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

        ContainerSlotValues[][] newSlots = new ContainerSlotValues[WIDTH][getHeight() - 1];
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

        ContainerSlotValues[][] newSlots = new ContainerSlotValues[WIDTH][getHeight() + 1];
        for (int x = 0; x < WIDTH; x++) {
            System.arraycopy(this.slots[x], 0, newSlots[x], 0, rowIndex);
            newSlots[x][rowIndex] = new EmptySlotValues();
            System.arraycopy(this.slots[x], rowIndex, newSlots[x], rowIndex + 1, getHeight() - rowIndex);
        }
        this.slots = newSlots;
    }

    public void setRecipes(List<ContainerRecipeValues> recipes) {
        assertMutable();
        Checks.nonNull(recipes);
        this.recipes = Mutability.createDeepCopy(recipes, false);
    }

    public void setFuelMode(FuelMode fuelMode) {
        assertMutable();
        Checks.notNull(fuelMode);
        this.fuelMode = fuelMode;
    }

    public void setHost(CustomContainerHost newHost) {
        assertMutable();
        Checks.notNull(newHost);
        this.host = newHost;
    }

    public void setPersistentStorage(boolean persistentStorage) {
        assertMutable();
        this.persistentStorage = persistentStorage;
    }

    public void validate(ItemSet itemSet, String oldName) throws ValidationException, ProgrammingValidationException {
        if (name == null) throw new ProgrammingValidationException("No name");
        if (name.isEmpty()) throw new ValidationException("Name can't be empty");
        if (!name.equals(oldName) && itemSet.getContainer(name).isPresent()) {
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
                Collection<ContainerSlotValues> otherSlots = new ArrayList<>(WIDTH * height - 1);
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
        for (ContainerRecipeValues recipe : recipes) {
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
        // There are no invalid values for persistentStorage
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
        for (ContainerRecipeValues recipe : recipes) {
            Validation.scope("Recipes", () -> recipe.validateExportVersion(version));
        }
        host.validateExportVersion(version);
    }
}
