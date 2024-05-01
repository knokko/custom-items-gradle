package nl.knokko.customitems.recipe;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.recipe.ingredient.KciIngredient;
import nl.knokko.customitems.recipe.ingredient.SimpleVanillaIngredient;
import nl.knokko.customitems.recipe.result.KciResult;
import nl.knokko.customitems.recipe.result.SimpleVanillaResult;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Objects;
import java.util.UUID;

import static nl.knokko.customitems.MCVersions.VERSION1_20;

public class KciSmithingRecipe extends ModelValues {

    public static KciSmithingRecipe load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("SmithingRecipe", encoding);

        KciSmithingRecipe recipe = new KciSmithingRecipe(false);
        recipe.id = new UUID(input.readLong(), input.readLong());
        recipe.template = KciIngredient.load(input, itemSet);
        recipe.tool = KciIngredient.load(input, itemSet);
        recipe.material = KciIngredient.load(input, itemSet);
        recipe.result = KciResult.load(input, itemSet);
        recipe.requiredPermission = input.readString();
        return recipe;
    }

    private UUID id;
    private KciIngredient template;
    private KciIngredient tool;
    private KciIngredient material;
    private KciResult result;
    private String requiredPermission;

    public KciSmithingRecipe(boolean mutable) {
        super(mutable);
        this.id = UUID.randomUUID();
        this.template = new SimpleVanillaIngredient(false);
        this.tool = new SimpleVanillaIngredient(false);
        this.material = new SimpleVanillaIngredient(false);
        this.result = new SimpleVanillaResult(false);
        this.requiredPermission = null;
    }

    public KciSmithingRecipe(KciSmithingRecipe toCopy, boolean mutable) {
        super(mutable);
        this.id = toCopy.getId();
        this.template = toCopy.getTemplate();
        this.tool = toCopy.getTool();
        this.material = toCopy.getMaterial();
        this.result = toCopy.getResult();
        this.requiredPermission = toCopy.getRequiredPermission();
    }

    @Override
    public KciSmithingRecipe copy(boolean mutable) {
        return new KciSmithingRecipe(this, mutable);
    }

    @Override
    public String toString() {
        return "smith " + result.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof KciSmithingRecipe) {
            KciSmithingRecipe recipe = (KciSmithingRecipe) other;
            return this.id.equals(recipe.id) && this.template.equals(recipe.template) &&
                    this.tool.equals(recipe.tool) && this.material.equals(recipe.material) &&
                    this.result.equals(recipe.result) && Objects.equals(this.requiredPermission, recipe.requiredPermission);
        } else return false;
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addLong(id.getMostSignificantBits());
        output.addLong(id.getLeastSignificantBits());
        template.save(output);
        tool.save(output);
        material.save(output);
        result.save(output);
        output.addString(requiredPermission);
    }

    public UUID getId() {
        return id;
    }

    public KciIngredient getTemplate() {
        return template;
    }

    public KciIngredient getTool() {
        return tool;
    }

    public KciIngredient getMaterial() {
        return material;
    }

    public KciResult getResult() {
        return result;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void changeId() {
        assertMutable();
        this.id = UUID.randomUUID();
    }

    public void setTemplate(KciIngredient template) {
        assertMutable();
        this.template = template.copy(false);
    }

    public void setTool(KciIngredient tool) {
        assertMutable();
        this.tool = tool.copy(false);
    }

    public void setMaterial(KciIngredient material) {
        assertMutable();
        this.material = material.copy(false);
    }

    public void setResult(KciResult result) {
        assertMutable();
        this.result = result.copy(false);
    }

    public void setRequiredPermission(String requiredPermission) {
        assertMutable();
        this.requiredPermission = requiredPermission;
    }

    public void validate(ItemSet itemSet, UUID oldID) throws ValidationException, ProgrammingValidationException {
        if (id == null) throw new ProgrammingValidationException("No ID");
        if (oldID != null && !oldID.equals(id)) throw new ValidationException("ID can't be changed");
        if (oldID == null && itemSet.smithingRecipes.get(id).isPresent()) {
            throw new ValidationException("ID is already taken");
        }

        if (template == null) throw new ProgrammingValidationException("No template");
        Validation.scope("Template", template::validateComplete, itemSet);
        if (tool == null) throw new ProgrammingValidationException("No tool");
        Validation.scope("Tool", tool::validateComplete, itemSet);
        if (material == null) throw new ProgrammingValidationException("No material");
        Validation.scope("Material", material::validateComplete, itemSet);
        if (result == null) throw new ProgrammingValidationException("No result");
        Validation.scope("Result", result::validateComplete, itemSet);

        if ("".equals(requiredPermission)) {
            throw new ProgrammingValidationException("Required permission must be null or non-empty");
        }

        for (KciSmithingRecipe recipe : itemSet.smithingRecipes) {
            if (recipe.id.equals(id)) continue;

            if (!recipe.template.conflictsWith(template)) continue;
            if (!recipe.tool.conflictsWith(tool)) continue;
            if (!recipe.material.conflictsWith(material)) continue;

            throw new ValidationException("Conflicts with " + recipe);
        }
    }

    public void validateExportVersion(int mcVersion) throws ValidationException, ProgrammingValidationException {
        if (mcVersion < VERSION1_20) throw new ValidationException("Smithing recipes require MC 1.20 or later");
        Validation.scope("Template", template::validateExportVersion, mcVersion);
        Validation.scope("Tool", tool::validateExportVersion, mcVersion);
        Validation.scope("Material", material::validateExportVersion, mcVersion);
        Validation.scope("Result", result::validateExportVersion, mcVersion);
    }
}
