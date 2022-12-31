package nl.knokko.customitems.container;

import nl.knokko.customitems.container.energy.RecipeEnergyValues;
import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.InputSlotValues;
import nl.knokko.customitems.container.slot.ManualOutputSlotValues;
import nl.knokko.customitems.container.slot.OutputSlotValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.model.Mutability;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.recipe.result.UpgradeResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.*;

public class ContainerRecipeValues extends ModelValues {

    public static ContainerRecipeValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        ContainerRecipeValues result = new ContainerRecipeValues(false);

        if (encoding == 1) {
            result.load1(input, itemSet);
        } else if (encoding == 2) {
            result.load2(input, itemSet);
        } else if (encoding == 3) {
            result.load3(input, itemSet);
        } else if (encoding == 4) {
            result.load4(input, itemSet);
        } else if (encoding == 5) {
            result.load5(input, itemSet);
        } else {
            throw new UnknownEncodingException("ContainerRecipe", encoding);
        }

        return result;
    }

    private Map<String, IngredientValues> inputs;
    private Map<String, OutputTableValues> outputs;

    private String manualOutputSlotName;
    private ResultValues manualOutput;

    private int duration;
    private int experience;

    private String requiredPermission;
    private Collection<RecipeEnergyValues> energy;

    public ContainerRecipeValues(boolean mutable) {
        super(mutable);
        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.manualOutputSlotName = null;
        this.manualOutput = null;
        this.duration = 40;
        this.experience = 5;
        this.requiredPermission = null;
        this.energy = new ArrayList<>();
    }

    public ContainerRecipeValues(ContainerRecipeValues toCopy, boolean mutable) {
        super(mutable);
        this.inputs = toCopy.getInputs();
        this.outputs = toCopy.getOutputs();
        this.manualOutputSlotName = toCopy.getManualOutputSlotName();
        this.manualOutput = toCopy.getManualOutput();
        this.duration = toCopy.getDuration();
        this.experience = toCopy.getExperience();
        this.requiredPermission = toCopy.getRequiredPermission();
        this.energy = toCopy.getEnergy();
    }

    private void loadInputs(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        int numInputs = input.readInt();
        this.inputs = new HashMap<>(numInputs);
        for (int counter = 0; counter < numInputs; counter++) {
            String inputSlotName = input.readString();
            IngredientValues ingredient = IngredientValues.load(input, itemSet);

            // A bug in the past made it possible that NoIngredient instances are
            // encoded ;( This seems like the easiest way to nullify the damage.
            if (!(ingredient instanceof NoIngredientValues)) {
                this.inputs.put(inputSlotName, ingredient);
            }
        }
    }

    private void load1(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadInputs(input, itemSet);

        int numOutputs = input.readInt();
        this.outputs = new HashMap<>(numOutputs);
        for (int counter = 0; counter < numOutputs; counter++) {
            String outputSlotName = input.readString();
            ResultValues result = ResultValues.load(input, itemSet);

            Collection<OutputTableValues.Entry> singleOutputList = new ArrayList<>(1);
            OutputTableValues.Entry theResultEntry = new OutputTableValues.Entry(true);
            theResultEntry.setChance(Chance.percentage(100));
            theResultEntry.setResult(result);
            singleOutputList.add(theResultEntry);
            OutputTableValues outputTable = new OutputTableValues(true);
            outputTable.setEntries(singleOutputList);

            this.outputs.put(outputSlotName, outputTable.copy(false));
        }

        this.duration = input.readInt();
        this.experience = input.readInt();
        this.requiredPermission = null;
    }

    private void load2(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadInputs(input, itemSet);

        int numOutputs = input.readInt();
        this.outputs = new HashMap<>(numOutputs);
        for (int counter = 0; counter < numOutputs; counter++) {
            String outputSlotName = input.readString();
            OutputTableValues resultTable = OutputTableValues.load1(input, itemSet);
            this.outputs.put(outputSlotName, resultTable);
        }

        this.duration = input.readInt();
        this.experience = input.readInt();
        this.requiredPermission = null;
    }

    private void load3(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        loadInputs(input, itemSet);

        int numOutputs = input.readInt();
        this.outputs = new HashMap<>(numOutputs);
        for (int counter = 0; counter < numOutputs; counter++) {
            String outputSlotName = input.readString();
            OutputTableValues resultTable = OutputTableValues.load(input, itemSet);
            this.outputs.put(outputSlotName, resultTable);
        }

        this.duration = input.readInt();
        this.experience = input.readInt();
        this.requiredPermission = null;
    }

    private void load4(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.load3(input, itemSet);
        this.manualOutputSlotName = input.readString();
        if (this.manualOutputSlotName != null) {
            this.manualOutput = ResultValues.load(input, itemSet);
        }
    }

    private void load5(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        this.load4(input, itemSet);
        this.requiredPermission = input.readString();
        int numEnergyEntries = input.readInt();
        this.energy = new ArrayList<>(numEnergyEntries);
        for (int counter = 0; counter < numEnergyEntries; counter++) {
            this.energy.add(RecipeEnergyValues.load(input, itemSet));
        }
    }

    public void save(BitOutput output) {
        output.addByte((byte) 5);

        output.addInt(inputs.size());
        for (Map.Entry<String, IngredientValues> inputEntry : inputs.entrySet()) {
            output.addString(inputEntry.getKey());
            inputEntry.getValue().save(output);
        }

        output.addInt(outputs.size());
        for (Map.Entry<String, OutputTableValues> outputEntry : outputs.entrySet()) {
            output.addString(outputEntry.getKey());
            outputEntry.getValue().save(output);
        }

        output.addInt(duration);
        output.addInt(experience);

        output.addString(this.manualOutputSlotName);
        if (this.manualOutputSlotName != null) {
            this.manualOutput.save(output);
        }
        output.addString(this.requiredPermission);

        output.addInt(energy.size());
        for (RecipeEnergyValues energyEntry : energy) {
            energyEntry.save(output);
        }
    }

    @Override
    public String toString() {
        return "ContainerRecipe(inputs=" + this.inputs + ",outputs=" + this.outputs + ",manual output=" + this.manualOutput;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == ContainerRecipeValues.class) {
            ContainerRecipeValues otherRecipe = (ContainerRecipeValues) other;
            return this.inputs.equals(otherRecipe.inputs) && this.outputs.equals(otherRecipe.outputs)
                    && this.duration == otherRecipe.duration && this.experience == otherRecipe.experience
                    && Objects.equals(this.manualOutputSlotName, otherRecipe.manualOutputSlotName)
                    && Objects.equals(this.manualOutput, otherRecipe.manualOutput)
                    && Objects.equals(this.requiredPermission, otherRecipe.requiredPermission);
        } else {
            return false;
        }
    }

    @Override
    public ContainerRecipeValues copy(boolean mutable) {
        return new ContainerRecipeValues(this, mutable);
    }

    public Map<String, IngredientValues> getInputs() {
        return new HashMap<>(inputs);
    }

    public IngredientValues getInput(String inputSlotName) {
        return inputs.get(inputSlotName);
    }

    public Map<String, OutputTableValues> getOutputs() {
        return new HashMap<>(outputs);
    }

    public OutputTableValues getOutput(String outputSlotName) {
        return outputs.get(outputSlotName);
    }

    public String getManualOutputSlotName() {
        return this.manualOutputSlotName;
    }

    public ResultValues getManualOutput() {
        return this.manualOutput;
    }

    public int getDuration() {
        return duration;
    }

    public int getExperience() {
        return experience;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public Collection<RecipeEnergyValues> getEnergy() {
        return new ArrayList<>(energy);
    }

    public void setInput(String inputSlotName, IngredientValues input) {
        assertMutable();
        Checks.nonNull(inputSlotName, input);
        this.inputs.put(inputSlotName, input);
    }

    public void clearInput(String inputSlotName) {
        assertMutable();
        Checks.notNull(inputSlotName);
        this.inputs.remove(inputSlotName);
    }

    public void setOutput(String outputSlotName, OutputTableValues output) {
        assertMutable();
        Checks.nonNull(outputSlotName, output);
        this.outputs.put(outputSlotName, output);
    }

    public void clearOutput(String outputSlotName) {
        assertMutable();
        Checks.notNull(outputSlotName);
        this.outputs.remove(outputSlotName);
    }

    public void setManualOutput(String slotName, ResultValues output) {
        assertMutable();
        if (slotName == null) {
            if (output != null) throw new IllegalArgumentException("output must be null if slotName is null");
            this.manualOutputSlotName = null;
            this.manualOutput = null;
        } else {
            Checks.notNull(output);
            this.manualOutputSlotName = slotName;
            this.manualOutput = output;
        }
    }

    public void setDuration(int duration) {
        assertMutable();
        this.duration = duration;
    }

    public void setExperience(int experience) {
        assertMutable();
        this.experience = experience;
    }

    public void setRequiredPermission(String requiredPermission) {
        assertMutable();
        this.requiredPermission = "".equals(requiredPermission) ? null : requiredPermission;
    }

    public void setEnergy(Collection<RecipeEnergyValues> energy) {
        assertMutable();
        Checks.nonNull(energy);
        this.energy = Mutability.createDeepCopy(energy, false);
    }

    public void validate(ItemSet itemSet, CustomContainerValues container) throws ValidationException, ProgrammingValidationException {
        if (inputs == null) throw new ProgrammingValidationException("No inputs");
        Collection<ContainerSlotValues> slots = container.createSlotList();
        for (Map.Entry<String, IngredientValues> inputEntry : inputs.entrySet()) {
            if (inputEntry.getKey() == null) throw new ProgrammingValidationException("Missing the name of an input");
            if (slots.stream().noneMatch(
                    slot -> slot instanceof InputSlotValues && ((InputSlotValues) slot).getName().equals(inputEntry.getKey())
            )) {
                throw new ValidationException("No input slot with name " + inputEntry.getKey() + " exists anymore");
            }

            if (inputEntry.getValue() == null) throw new ProgrammingValidationException("Missing the ingredient of input " + inputEntry.getKey());
            Validation.scope("Input " + inputEntry.getKey(), inputEntry.getValue()::validateComplete, itemSet);
        }

        if (outputs == null) throw new ProgrammingValidationException("No outputs");
        for (Map.Entry<String, OutputTableValues> outputEntry : outputs.entrySet()) {
            if (outputEntry.getKey() == null) throw new ProgrammingValidationException("Missing the name of an output");
            if (slots.stream().noneMatch(
                    slot -> slot instanceof OutputSlotValues && ((OutputSlotValues) slot).getName().equals(outputEntry.getKey())
            )) {
                throw new ValidationException("No output slot with name " + outputEntry.getKey() + " exists anymore");
            }

            for (OutputTableValues outputTable : outputs.values()) {
                for (OutputTableValues.Entry outputTableEntry : outputTable.getEntries()) {
                    if (outputTableEntry.getResult() instanceof UpgradeResultValues) {
                        validateUpgradeResult((UpgradeResultValues) outputTableEntry.getResult());
                    }
                }
            }

            if (outputEntry.getValue() == null) throw new ProgrammingValidationException("Missing the result of output " + outputEntry.getKey());
            Validation.scope("Output " + outputEntry.getKey(), outputEntry.getValue()::validate, itemSet);
        }

        if (manualOutputSlotName != null) {
            if (!outputs.isEmpty()) throw new ValidationException("No regular outputs are allowed if a manual output slot is used");
            if (manualOutput == null) throw new ProgrammingValidationException("No manual output");
            if (slots.stream().noneMatch(
                    slot -> slot instanceof ManualOutputSlotValues && ((ManualOutputSlotValues) slot).getName().equals(manualOutputSlotName)
            )) {
                throw new ValidationException("No manual output slot with name " + manualOutputSlotName + " exists anymore");
            }
            if (duration != 0) {
                throw new ValidationException("Duration must be 0 if a manual output is used");
            }
            if (manualOutput instanceof UpgradeResultValues) {
                validateUpgradeResult((UpgradeResultValues) manualOutput);
            }
        }

        if (duration < 0) throw new ValidationException("Duration can't be negative");
        if (experience < 0) throw new ValidationException("Experience can't be negative");

        if ("".equals(requiredPermission)) throw new ProgrammingValidationException("Required permission can't be empty");

        if (energy == null) throw new ProgrammingValidationException("No energy");
        for (RecipeEnergyValues energyEntry : energy) {
            if (energyEntry == null) throw new ProgrammingValidationException("Missing an energy entry");
            energyEntry.validateComplete(itemSet);
        }
    }

    private void validateUpgradeResult(UpgradeResultValues upgrade) throws ValidationException {
        String inputSlotName = upgrade.getInputSlotName();
        IngredientValues ingredientToUpgrade = this.getInput(inputSlotName);
        if (ingredientToUpgrade == null || ingredientToUpgrade instanceof NoIngredientValues) {
            throw new ValidationException("Missing input " + inputSlotName + " to be upgraded");
        }
    }

    public boolean conflictsWith(ContainerRecipeValues other) {
        // No conflict is possible if the number of used input slots are different
        if (inputs.size() != other.inputs.size()) return false;

        for (Map.Entry<String, IngredientValues> inputEntry : inputs.entrySet()) {
            String inputSlotName = inputEntry.getKey();
            IngredientValues ownIngredient = inputEntry.getValue();
            IngredientValues otherIngredient = other.getInput(inputSlotName);

            // There is only a conflict if ALL ingredients have a conflict, so there is no conflict if at least 1
            // ingredient doesn't have a conflict
            if (!ownIngredient.conflictsWith(otherIngredient)) return false;
        }

        return true;
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        for (Map.Entry<String, IngredientValues> inputEntry : inputs.entrySet()) {
            Validation.scope(
                    "Input " + inputEntry.getKey(),
                    () -> inputEntry.getValue().validateExportVersion(version)
            );
        }

        for (Map.Entry<String, OutputTableValues> outputEntry : outputs.entrySet()) {
            Validation.scope(
                    "Output " + outputEntry.getKey(),
                    () -> outputEntry.getValue().validateExportVersion(version)
            );
        }

        if (manualOutput != null) {
            Validation.scope("Output", () -> manualOutput.validateExportVersion(version));
        }
    }
}
