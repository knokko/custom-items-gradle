package nl.knokko.customitems.container;

import nl.knokko.customitems.container.slot.ContainerSlotValues;
import nl.knokko.customitems.container.slot.InputSlotValues;
import nl.knokko.customitems.container.slot.OutputSlotValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.OutputTableValues;
import nl.knokko.customitems.recipe.ingredient.IngredientValues;
import nl.knokko.customitems.recipe.ingredient.NoIngredientValues;
import nl.knokko.customitems.recipe.result.ResultValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        } else {
            throw new UnknownEncodingException("ContainerRecipe", encoding);
        }

        return result;
    }

    private Map<String, IngredientValues> inputs;
    private Map<String, OutputTableValues> outputs;

    private int duration;
    private int experience;

    public ContainerRecipeValues(boolean mutable) {
        super(mutable);
        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.duration = 40;
        this.experience = 5;
    }

    public ContainerRecipeValues(ContainerRecipeValues toCopy, boolean mutable) {
        super(mutable);
        this.inputs = toCopy.getInputs();
        this.outputs = toCopy.getOutputs();
        this.duration = toCopy.getDuration();
        this.experience = toCopy.getExperience();
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
    }

    public void save(BitOutput output) {
        output.addByte((byte) 3);

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
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == ContainerRecipeValues.class) {
            ContainerRecipeValues otherRecipe = (ContainerRecipeValues) other;
            return this.inputs.equals(otherRecipe.inputs) && this.outputs.equals(otherRecipe.outputs)
                    && this.duration == otherRecipe.duration && this.experience == otherRecipe.experience;
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

    public int getDuration() {
        return duration;
    }

    public int getExperience() {
        return experience;
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

    public void setDuration(int duration) {
        assertMutable();
        this.duration = duration;
    }

    public void setExperience(int experience) {
        assertMutable();
        this.experience = experience;
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

            if (outputEntry.getValue() == null) throw new ProgrammingValidationException("Missing the result of output " + outputEntry.getKey());
            Validation.scope("Output " + outputEntry.getKey(), outputEntry.getValue()::validate, itemSet);
        }

        if (duration < 0) throw new ValidationException("Duration can't be negative");
        if (experience < 0) throw new ValidationException("Experience can't be negative");
    }

    public boolean conflictsWith(ContainerRecipeValues other) {
        // No conflict if possible if the number of used input slots are different
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
    }
}
