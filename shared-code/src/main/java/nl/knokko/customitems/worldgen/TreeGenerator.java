package nl.knokko.customitems.worldgen;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.drops.AllowedBiomes;
import nl.knokko.customitems.item.VMaterial;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.Chance;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.Validation;
import nl.knokko.customitems.util.ValidationException;

import java.util.*;

import static nl.knokko.customitems.MCVersions.*;

public class TreeGenerator extends ModelValues {

    public static TreeGenerator load(BitInput input, ItemSet itemSet) throws UnknownEncodingException {
        byte encoding = input.readByte();
        if (encoding < 1 || encoding > 3) throw new UnknownEncodingException("TreeGenerator", encoding);

        TreeGenerator result = new TreeGenerator(false);
        result.treeType = VTreeType.valueOf(input.readString());
        result.allowedBiomes = AllowedBiomes.load(input);
        result.allowedTerrain = ReplaceBlocks.load(input, itemSet);
        result.logMaterial = BlockProducer.load(input, itemSet);
        result.leavesMaterial = BlockProducer.load(input, itemSet);
        result.chance = Chance.load(input);
        result.minNumTrees = input.readInt();
        result.maxNumTrees = input.readInt();
        result.maxNumAttempts = input.readInt();

        if (encoding >= 2) {
            int numAllowedWorlds = input.readInt();
            List<String> allowedWorlds = new ArrayList<>(numAllowedWorlds);
            for (int counter = 0; counter < numAllowedWorlds; counter++) allowedWorlds.add(input.readString());
            result.allowedWorlds = Collections.unmodifiableList(allowedWorlds);
        } else result.allowedWorlds = Collections.emptyList();

        if (encoding >= 3) {
            result.minimumDepth = input.readInt();
            result.maximumDepth = input.readInt();
        }

        return result;
    }

    private VTreeType treeType;
    private AllowedBiomes allowedBiomes;
    private List<String> allowedWorlds;
    private ReplaceBlocks allowedTerrain;

    private BlockProducer logMaterial, leavesMaterial;

    private Chance chance;
    private int minNumTrees, maxNumTrees, maxNumAttempts;
    private int minimumDepth, maximumDepth;

    public TreeGenerator(boolean mutable) {
        super(mutable);
        this.treeType = VTreeType.TREE;
        this.allowedBiomes = new AllowedBiomes(false);
        this.allowedWorlds = Collections.emptyList();
        this.allowedTerrain = new ReplaceBlocks(true);
        this.allowedTerrain.setVanillaBlocks(EnumSet.of(VMaterial.DIRT, VMaterial.GRASS_BLOCK));
        this.allowedTerrain = this.allowedTerrain.copy(false);

        this.logMaterial = new BlockProducer(false);
        this.leavesMaterial = new BlockProducer(false);

        this.chance = Chance.percentage(50);
        this.minNumTrees = 1;
        this.maxNumTrees = 2;
        this.maxNumAttempts = 3;
        this.minimumDepth = 0;
        this.maximumDepth = 0;
    }

    public TreeGenerator(TreeGenerator toCopy, boolean mutable) {
        super(mutable);
        this.treeType = toCopy.getTreeType();
        this.allowedBiomes = toCopy.getAllowedBiomes();
        this.allowedWorlds = toCopy.getAllowedWorlds();
        this.allowedTerrain = toCopy.getAllowedTerrain();
        this.logMaterial = toCopy.getLogMaterial();
        this.leavesMaterial = toCopy.getLeavesMaterial();
        this.chance = toCopy.getChance();
        this.minNumTrees = toCopy.getMinNumTrees();
        this.maxNumTrees = toCopy.getMaxNumTrees();
        this.maxNumAttempts = toCopy.getMaxNumAttempts();
        this.minimumDepth = toCopy.getMinimumDepth();
        this.maximumDepth = toCopy.getMaximumDepth();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 3);

        output.addString(treeType.name());
        allowedBiomes.save(output);
        allowedTerrain.save(output);
        logMaterial.save(output);
        leavesMaterial.save(output);
        chance.save(output);
        output.addInts(minNumTrees, maxNumTrees, maxNumAttempts);
        output.addInt(allowedWorlds.size());
        for (String allowedWorld : allowedWorlds) output.addString(allowedWorld);
        output.addInts(minimumDepth, maximumDepth);
    }

    @Override
    public TreeGenerator copy(boolean mutable) {
        return new TreeGenerator(this, mutable);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TreeGenerator) {
            TreeGenerator otherTree = (TreeGenerator) other;
            return this.treeType == otherTree.treeType && this.allowedBiomes.equals(otherTree.allowedBiomes)
                    && this.allowedTerrain.equals(otherTree.allowedTerrain) && this.allowedWorlds.equals(otherTree.allowedWorlds)
                    && this.logMaterial.equals(otherTree.logMaterial) && this.leavesMaterial.equals(otherTree.leavesMaterial)
                    && this.chance.equals(otherTree.chance) && this.minNumTrees == otherTree.minNumTrees
                    && this.maxNumTrees == otherTree.maxNumTrees && this.maxNumAttempts == otherTree.maxNumAttempts
                    && this.minimumDepth == otherTree.minimumDepth && this.maximumDepth == otherTree.maximumDepth;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        Optional<BlockProducer.Entry> maxEntry = logMaterial.getEntries().stream().max(
                Comparator.comparingInt(entry -> entry.getChance().getRawValue())
        );
        if (maxEntry.isPresent()) {
            return maxEntry.get().getBlock().toString();
        } else {
            return "nothing";
        }
    }

    public VTreeType getTreeType() {
        return treeType;
    }

    public AllowedBiomes getAllowedBiomes() {
        return allowedBiomes;
    }

    public List<String> getAllowedWorlds() {
        return allowedWorlds;
    }

    public ReplaceBlocks getAllowedTerrain() {
        return allowedTerrain;
    }

    public BlockProducer getLogMaterial() {
        return logMaterial;
    }

    public BlockProducer getLeavesMaterial() {
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

    public int getMinimumDepth() {
        return minimumDepth;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }

    public void setTreeType(VTreeType treeType) {
        assertMutable();
        this.treeType = Objects.requireNonNull(treeType);
    }

    public void setAllowedBiomes(AllowedBiomes allowedBiomes) {
        assertMutable();
        this.allowedBiomes = allowedBiomes.copy(false);
    }

    public void setAllowedWorlds(List<String> allowedWorlds) {
        assertMutable();
        this.allowedWorlds = Collections.unmodifiableList(allowedWorlds);
    }

    public void setAllowedTerrain(ReplaceBlocks allowedTerrain) {
        assertMutable();
        this.allowedTerrain = allowedTerrain.copy(false);
    }

    public void setLogMaterial(BlockProducer logMaterial) {
        assertMutable();
        this.logMaterial = logMaterial.copy(false);
    }

    public void setLeavesMaterial(BlockProducer leavesMaterial) {
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

    public void setMinimumDepth(int minimumDepth) {
        assertMutable();
        this.minimumDepth = minimumDepth;
    }

    public void setMaximumDepth(int maximumDepth) {
        assertMutable();
        this.maximumDepth = maximumDepth;
    }

    public void validate(ItemSet itemSet) throws ValidationException, ProgrammingValidationException {
        if (treeType == null) throw new ProgrammingValidationException("No tree type");
        if (allowedBiomes == null) throw new ProgrammingValidationException("No allowed biomes");
        Validation.scope("Allowed biomes", allowedBiomes::validate);
        if (allowedWorlds == null) throw new ProgrammingValidationException("No allowed worlds");
        if (allowedWorlds.contains(null)) throw new ProgrammingValidationException("Missing an allowed world");
        if (allowedTerrain == null) throw new ProgrammingValidationException("No allowed terrain");
        Validation.scope("Allowed terrain", allowedTerrain::validate, itemSet);

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
        if (minimumDepth > maximumDepth) {
            throw new ValidationException("Minimum depth can't be larger than maximum depth");
        }
        if (minimumDepth < 0) throw new ValidationException("Minimum depth can't be negative");
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        if (version < VERSION1_13) throw new ValidationException("Custom trees are only supported in MC 1.13 and later");
        if (version == VERSION1_16) throw new ValidationException("Custom trees are not supported in MC 1.16");

        if (version < treeType.firstVersion) {
            throw new ValidationException(treeType + " doesn't exist yet in MC " + MCVersions.createString(version));
        }
        if (version > treeType.lastVersion) {
            throw new ValidationException(treeType + " doesn't exist anymore in MC " + MCVersions.createString(version));
        }

        if (maximumDepth > 0 && version < VERSION1_17) {
            throw new ValidationException("Maximum depth must be 0 before MC 1.17");
        }

        Validation.scope("Biomes", allowedBiomes::validateExportVersion, version);
        Validation.scope("Allowed terrain", allowedTerrain::validateExportVersion, version);
        Validation.scope("Log material", logMaterial::validateExportVersion, version);
        Validation.scope("Leaves material", leavesMaterial::validateExportVersion, version);
    }
}
