package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.AllowedBiomesValues;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class TreeGeneratorValues extends ModelValues {

    public static TreeGeneratorValues load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding != 1) throw new UnknownEncodingException("TreeGenerator", encoding);

        TreeGeneratorValues result = new TreeGeneratorValues(false);
        result.treeType = CITreeType.valueOf(input.readString());
        result.allowedBiomes = AllowedBiomesValues.load(input);
        result.logMaterial = BlockProducerValues.load(input, itemSet);
        result.leavesMaterial = BlockProducerValues.load(input, itemSet);
        result.chance = Chance.load(input);
        result.minNumTrees = input.readInt();
        result.maxNumTrees = input.readInt();
        result.maxNumAttempts = input.readInt();

        return result;
    }

    private CITreeType treeType;
    private AllowedBiomesValues allowedBiomes;

    private BlockProducerValues logMaterial, leavesMaterial;

    private Chance chance;
    private int minNumTrees, maxNumTrees, maxNumAttempts;

    public TreeGeneratorValues(boolean mutable) {
        super(mutable);
        this.treeType = CITreeType.TREE;
        this.allowedBiomes = new AllowedBiomesValues(false);

        this.logMaterial = new BlockProducerValues(false);
        this.leavesMaterial = new BlockProducerValues(false);

        this.chance = Chance.percentage(50);
        this.minNumTrees = 1;
        this.maxNumTrees = 2;
        this.maxNumAttempts = 3;
    }

    public TreeGeneratorValues(TreeGeneratorValues toCopy, boolean mutable) {
        super(mutable);
        this.treeType = toCopy.getTreeType();
        this.allowedBiomes = toCopy.getAllowedBiomes();
        this.logMaterial = toCopy.getLogMaterial();
        this.leavesMaterial = toCopy.getLeavesMaterial();
        this.chance = toCopy.getChance();
        this.minNumTrees = toCopy.getMinNumTrees();
        this.maxNumTrees = toCopy.getMaxNumTrees();
        this.maxNumAttempts = toCopy.getMaxNumAttempts();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(treeType.name());
        allowedBiomes.save(output);
        logMaterial.save(output);
        leavesMaterial.save(output);
        chance.save(output);
        output.addInts(minNumTrees, maxNumTrees, maxNumAttempts);
    }

    @Override
    public TreeGeneratorValues copy(boolean mutable) {
        return new TreeGeneratorValues(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TreeGeneratorValues) {
            TreeGeneratorValues otherTree = (TreeGeneratorValues) other;
            return this.treeType == otherTree.treeType && this.allowedBiomes.equals(otherTree.allowedBiomes)
                    && this.logMaterial.equals(otherTree.logMaterial) && this.leavesMaterial.equals(otherTree.leavesMaterial)
                    && this.chance.equals(otherTree.chance) && this.minNumTrees == otherTree.minNumTrees
                    && this.maxNumTrees == otherTree.maxNumTrees && this.maxNumAttempts == otherTree.maxNumAttempts;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        Optional<BlockProducerValues.Entry> maxEntry = logMaterial.getEntries().stream().max(
                Comparator.comparingInt(entry -> entry.getChance().getRawValue())
        );
        if (maxEntry.isPresent()) {
            return maxEntry.get().getBlock().toString();
        } else {
            return "nothing";
        }
    }

    public CITreeType getTreeType() {
        return treeType;
    }

    public AllowedBiomesValues getAllowedBiomes() {
        return allowedBiomes;
    }

    public BlockProducerValues getLogMaterial() {
        return logMaterial;
    }

    public BlockProducerValues getLeavesMaterial() {
        return leavesMaterial;
    }

    public Chance getChance() {
        return chance;
    }

    public int getMinNumTrees() {
        return minNumTrees;
    }

    public int getMaxNumTrees() {
        return maxNumTrees;
    }

    public int getMaxNumAttempts() {
        return maxNumAttempts;
    }

    public void setTreeType(CITreeType treeType) {
        assertMutable();
        this.treeType = Objects.requireNonNull(treeType);
    }

    public void setAllowedBiomes(AllowedBiomesValues allowedBiomes) {
        assertMutable();
        this.allowedBiomes = allowedBiomes.copy(false);
    }

    public void setLogMaterial(BlockProducerValues logMaterial) {
        assertMutable();
        this.logMaterial = logMaterial.copy(false);
    }

    public void setLeavesMaterial(BlockProducerValues leavesMaterial) {
        assertMutable();
        this.leavesMaterial = leavesMaterial.copy(false);
    }

    public void setChance(Chance chance) {
        assertMutable();
        this.chance = Objects.requireNonNull(chance);
    }

    public void setMinNumTrees(int minNumTrees) {
        assertMutable();
        this.minNumTrees = minNumTrees;
    }

    public void setMaxNumTrees(int maxNumTrees) {
        assertMutable();
        this.maxNumTrees = maxNumTrees;
    }

    public void setMaxNumAttempts(int maxNumAttempts) {
        assertMutable();
        this.maxNumAttempts = maxNumAttempts;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (treeType == null) throw new ProgrammingValidationException("No tree type");
        if (allowedBiomes == null) throw new ProgrammingValidationException("No allowed biomes");
        Validation.scope("Allowed biomes", allowedBiomes::validate);

        if (logMaterial == null) throw new ProgrammingValidationException("No log material");
        Validation.scope("Log material", logMaterial::validate, itemSet);
        if (leavesMaterial == null) throw new ProgrammingValidationException("No leaves material");
        Validation.scope("Leaves material", leavesMaterial::validate, itemSet);

        if (chance == null) throw new ProgrammingValidationException("No chance");
        if (minNumTrees <= 0) throw new ValidationException("Minimum number of trees must be positive");
        if (minNumTrees > maxNumTrees) {
            throw new ValidationException("Minimum number of trees can't be larger than maximum number of trees");
        }
        if (maxNumTrees > maxNumAttempts) {
            throw new ValidationException("Maximum number of trees can't be larger than maximum number of attempts");
        }
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version < treeType.firstVersion) {
            throw new ValidationException(treeType + " doesn't exist yet in MC " + MCVersions.createString(version));
        }
        if (version > treeType.lastVersion) {
            throw new ValidationException(treeType + " doesn't exist anymore in MC " + MCVersions.createString(version));
        }

        Validation.scope("Biomes", allowedBiomes::validateExportVersion, version);
        Validation.scope("Log material", logMaterial::validateExportVersion, version);
        Validation.scope("Leaves material", leavesMaterial::validateExportVersion, version);
    }
}
