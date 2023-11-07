package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.*;
import nl.knokko.customitems.block.*;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.energy.EnergyType;
import nl.knokko.customitems.container.energy.EnergyTypeValues;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.damage.CustomDamageSource;
import nl.knokko.customitems.damage.CustomDamageSourceValues;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.durability.ItemDurabilityAssignments;
import nl.knokko.customitems.item.durability.ItemDurabilityClaim;
import nl.knokko.customitems.item.equipment.EquipmentSet;
import nl.knokko.customitems.item.equipment.EquipmentSetValues;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.misc.CombinedResourcepack;
import nl.knokko.customitems.misc.CombinedResourcepackValues;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.CustomProjectile;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.CustomCraftingRecipe;
import nl.knokko.customitems.recipe.upgrade.Upgrade;
import nl.knokko.customitems.recipe.upgrade.UpgradeValues;
import nl.knokko.customitems.settings.ExportSettingsValues;
import nl.knokko.customitems.sound.CustomSoundType;
import nl.knokko.customitems.sound.CustomSoundTypeValues;
import nl.knokko.customitems.texture.*;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.OutdatedItemSetException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.worldgen.OreVeinGenerator;
import nl.knokko.customitems.worldgen.OreVeinGeneratorValues;
import nl.knokko.customitems.worldgen.TreeGenerator;
import nl.knokko.customitems.worldgen.TreeGeneratorValues;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemSet {

    private static long generateFakeExportTime() {
        /*
         * Unfortunately, we don't know the real export time (when an older version
         * of the editor was used). Luckily, the export time is mostly just a
         * unique identifier for the version of an item set.
         *
         * When using this method, the chance of generating the same 'id' more than
         * once is very small.
         *
         * The primary drawback is that this will also differ each time the server
         * is restarted, so it will cause some unnecessary work. Anyway, we only
         * need this to support old versions of the editor, so users can just
         * use a newer editor to avoid this.
         */
        return (long) (-1_000_000_000_000_000L * Math.random());
    }

    public static ItemSet combine(ItemSet primary, ItemSet secondary) throws ValidationException {

        ItemSet result = new ItemSet(Side.EDITOR);

        result.exportSettings = primary.exportSettings;
        result.combinedResourcepacks.addAll(primary.combinedResourcepacks);
        result.combinedResourcepacks.addAll(secondary.combinedResourcepacks);
        result.textures.addAll(primary.textures);
        result.textures.addAll(secondary.textures);
        result.armorTextures.addAll(primary.armorTextures);
        result.armorTextures.addAll(secondary.armorTextures);
        result.fancyPantsArmorTextures.addAll(primary.fancyPantsArmorTextures);
        result.fancyPantsArmorTextures.addAll(secondary.fancyPantsArmorTextures);

        result.items.addAll(primary.items);
        result.items.addAll(secondary.items);

        result.equipmentSets.addAll(primary.equipmentSets);
        result.equipmentSets.addAll(secondary.equipmentSets);

        result.damageSources.addAll(primary.damageSources);
        result.damageSources.addAll(secondary.damageSources);

        result.craftingRecipes.addAll(primary.craftingRecipes);
        result.craftingRecipes.addAll(secondary.craftingRecipes);

        result.upgrades.addAll(primary.upgrades);
        result.upgrades.addAll(secondary.upgrades);

        result.blockDrops.addAll(primary.blockDrops);
        result.blockDrops.addAll(secondary.blockDrops);
        result.mobDrops.addAll(primary.mobDrops);
        result.mobDrops.addAll(secondary.mobDrops);

        result.containers.addAll(primary.containers);
        result.containers.addAll(secondary.containers);
        result.fuelRegistries.addAll(primary.fuelRegistries);
        result.fuelRegistries.addAll(secondary.fuelRegistries);
        result.energyTypes.addAll(primary.energyTypes);
        result.energyTypes.addAll(secondary.energyTypes);

        result.soundTypes.addAll(primary.soundTypes);
        result.soundTypes.addAll(secondary.soundTypes);

        result.projectiles.addAll(primary.projectiles);
        result.projectiles.addAll(secondary.projectiles);
        result.projectileCovers.addAll(primary.projectileCovers);
        result.projectileCovers.addAll(secondary.projectileCovers);

        result.blocks.addAll(primary.blocks);
        if (!secondary.blocks.isEmpty()) throw new ValidationException("The secondary item set can't have blocks");

        result.oreVeinGenerators.addAll(primary.oreVeinGenerators);
        result.oreVeinGenerators.addAll(secondary.oreVeinGenerators);
        result.treeGenerators.addAll(primary.treeGenerators);
        result.treeGenerators.addAll(secondary.treeGenerators);

        for (String deletedItem : primary.removedItemNames) {
            if (secondary.getItem(deletedItem).isPresent()) {
                throw new ValidationException("The secondary set has item " + deletedItem + ", which is removed in the primary set");
            }
        }
        for (String deletedItem : secondary.removedItemNames) {
            if (primary.getItem(deletedItem).isPresent()) {
                throw new ValidationException("The primary set has item " + deletedItem + ", which is removed in the secondary set");
            }
        }
        result.removedItemNames.addAll(primary.removedItemNames);
        result.removedItemNames.addAll(secondary.removedItemNames);

        try {
            result.validate();
        } catch (ProgrammingValidationException ex) {
            throw new ValidationException(ex.getMessage());
        }

        return result;
    }

    // <---- INTERNAL USE ONLY ---->
    Collection<IntBasedReference<?, ?>> intReferences;
    Collection<StringBasedReference<?, ?>> stringReferences;
    Collection<UUIDBasedReference<?, ?>> uuidReferences;
    boolean finishedLoading;
    // <---- END OF INTERNAL USE ---->

    private long exportTime;

    private ExportSettingsValues exportSettings;
    Collection<CombinedResourcepack> combinedResourcepacks;
    Collection<CustomTexture> textures;
    Collection<ArmorTexture> armorTextures;
    Collection<FancyPantsArmorTexture> fancyPantsArmorTextures;
    Collection<CustomItem> items;
    Collection<EquipmentSet> equipmentSets;
    Collection<CustomDamageSource> damageSources;
    Collection<CustomCraftingRecipe> craftingRecipes;
    Collection<Upgrade> upgrades;
    Collection<BlockDrop> blockDrops;
    Collection<MobDrop> mobDrops;
    Collection<CustomContainer> containers;
    Collection<CustomFuelRegistry> fuelRegistries;
    Collection<EnergyType> energyTypes;
    Collection<CustomSoundType> soundTypes;
    Collection<CustomProjectile> projectiles;
    Collection<ProjectileCover> projectileCovers;
    Collection<CustomBlock> blocks;
    Collection<OreVeinGenerator> oreVeinGenerators;
    Collection<TreeGenerator> treeGenerators;

    Collection<String> removedItemNames;

    // When non-null, this function should be called occasionally
    public Consumer<ItemSet> createBackup;

    final Side side;

    public ItemSet(Side side) {
        Checks.notNull(side);
        this.side = side;
        initialize();
    }

    public ItemSet(
            BitInput input, Side side, boolean allowOutdated
    ) throws IntegrityException, UnknownEncodingException, OutdatedItemSetException {
        Checks.notNull(side);
        this.side = side;
        load(input, allowOutdated);
    }

    private void initialize() {
        exportSettings = new ExportSettingsValues(false);
        combinedResourcepacks = new ArrayList<>();
        textures = new ArrayList<>();
        armorTextures = new ArrayList<>();
        fancyPantsArmorTextures = new ArrayList<>();
        items = new ArrayList<>();
        equipmentSets = new ArrayList<>();
        damageSources = new ArrayList<>();
        craftingRecipes = new ArrayList<>();
        upgrades = new ArrayList<>();
        blockDrops = new ArrayList<>();
        mobDrops = new ArrayList<>();
        blocks = new ArrayList<>();
        oreVeinGenerators = new ArrayList<>();
        treeGenerators = new ArrayList<>();
        containers = new ArrayList<>();
        fuelRegistries = new ArrayList<>();
        energyTypes = new ArrayList<>();
        soundTypes = new ArrayList<>();
        projectiles = new ArrayList<>();
        projectileCovers = new ArrayList<>();

        removedItemNames = new ArrayList<>();

        finishedLoading = true;
    }

    public boolean isEmpty() {
        for (Collection<?> relevantCollection : new Collection<?>[]{
                this.textures, this.armorTextures, this.fancyPantsArmorTextures, this.items, this.equipmentSets,
                this.damageSources, this.craftingRecipes, this.upgrades, this.blockDrops, this.mobDrops, this.blocks,
                this.oreVeinGenerators, this.treeGenerators, this.containers, this.fuelRegistries, this.energyTypes,
                this.soundTypes, this.projectiles, this.projectileCovers, this.removedItemNames,
                this.combinedResourcepacks
        }) {
            if (!relevantCollection.isEmpty()) return false;
        }

        return true;
    }

    private void maybeCreateBackup() {
        Consumer<ItemSet> createBackup = this.createBackup;
        if (createBackup != null && Math.random() < 0.2) {
            createBackup.accept(this);
        }
    }

    public Map<CustomItemType, ItemDurabilityAssignments> assignInternalItemDamages() throws ValidationException {
        Map<CustomItemType, ItemDurabilityAssignments> assignmentMap = new EnumMap<>(CustomItemType.class);

        Map<CustomItemType, Set<Short>> lockedDamageAssignments = new EnumMap<>(CustomItemType.class);
        for (CustomItemValues item : getItems()) {
            if (!item.shouldUpdateAutomatically() && item.getItemDamage() > 0) {
                Set<Short> lockedAssignments = lockedDamageAssignments.computeIfAbsent(item.getItemType(), k -> new HashSet<>());
                if (!lockedAssignments.contains(item.getItemDamage())) {
                    ItemDurabilityAssignments assignments = assignmentMap.computeIfAbsent(item.getItemType(), k -> new ItemDurabilityAssignments());

                    List<BowTextureEntry> pullTextures = null;
                    if (item.getTexture() instanceof BowTextureValues) {
                        pullTextures = ((BowTextureValues) item.getTexture()).getPullTextures();
                    } else if (item.getTexture() instanceof CrossbowTextureValues) {
                        pullTextures = ((CrossbowTextureValues) item.getTexture()).getPullTextures();
                    }

                    ItemDurabilityClaim lockedClaim = new ItemDurabilityClaim(
                            "customitems/" + item.getName(), item.getItemDamage(), pullTextures
                    );
                    assignments.claimList.add(lockedClaim);
                    lockedAssignments.add(item.getItemDamage());
                }
            }
        }

        for (CustomItem itemModel : items) {
            CustomItemValues item = itemModel.cloneValues();
            if (item.shouldUpdateAutomatically() || item.getItemDamage() <= 0) {
                CustomItemType itemType = item.getItemType();
                ItemDurabilityAssignments assignments = assignmentMap.get(itemType);
                if (assignments == null) {
                    assignments = new ItemDurabilityAssignments();
                    assignmentMap.put(itemType, assignments);
                }

                boolean canReuseModel = false;
                DefaultModelType defaultModelType = item.getDefaultModelType();
                if (itemType.hasSimpleModel && defaultModelType == DefaultModelType.BASIC) {

                    ItemModel model = item.getModel();
                    if (model instanceof DefaultItemModel && ((DefaultItemModel) model).getParent().equals(defaultModelType.recommendedParents.get(0))) {
                        canReuseModel = true;
                    }
                }

                boolean reuseExistingModel = false;
                if (canReuseModel) {
                    Short existingItemDamage = assignments.textureReuseMap.get(item.getTexture().getName());
                    if (existingItemDamage != null) {
                        item.setItemDamage(existingItemDamage);
                        reuseExistingModel = true;
                    }
                }

                if (!reuseExistingModel) {
                    short nextItemDamage = assignments.getNextItemDamage(itemType, exportSettings.getMcVersion());
                    Set<Short> lockedDamages = lockedDamageAssignments.get(item.getItemType());
                    if (lockedDamages != null) {
                        while (lockedDamages.contains(nextItemDamage)) {
                            nextItemDamage = assignments.getNextItemDamage(itemType, exportSettings.getMcVersion());
                        }
                    }

                    item.setItemDamage(nextItemDamage);

                    String resourcePath = "customitems/" + item.getName();

                    List<BowTextureEntry> pullTextures = null;
                    if (itemType == CustomItemType.BOW) {
                        pullTextures = ((BowTextureValues) item.getTexture()).getPullTextures();
                    } else if (itemType == CustomItemType.CROSSBOW) {
                        pullTextures = ((CrossbowTextureValues) item.getTexture()).getPullTextures();
                    }

                    assignments.claimList.add(new ItemDurabilityClaim(resourcePath, nextItemDamage, pullTextures));

                    if (canReuseModel) {
                        assignments.textureReuseMap.put(item.getTexture().getName(), nextItemDamage);
                    }
                }

                itemModel.setValues(item);
            }
        }

        for (ProjectileCover coverModel : projectileCovers) {
            ProjectileCoverValues cover = coverModel.cloneValues();
            CustomItemType itemType = cover.getItemType();

            ItemDurabilityAssignments assignments = assignmentMap.get(itemType);
            if (assignments == null) {
                assignments = new ItemDurabilityAssignments();
                assignmentMap.put(itemType, assignments);
            }

            short itemDamage = assignments.getNextItemDamage(itemType, exportSettings.getMcVersion());
            cover.setItemDamage(itemDamage);
            String resourcePath = "customprojectiles/" + cover.getName();
            assignments.claimList.add(new ItemDurabilityClaim(resourcePath, itemDamage, null));

            coverModel.setValues(cover);
        }

        for (ItemDurabilityAssignments assignments : assignmentMap.values()) {
            assignments.claimList.sort(Comparator.comparingInt(a -> a.itemDamage));
        }

        return assignmentMap;
    }

    public void save(BitOutput output, Side targetSide) {
        output.addByte(SetEncoding.ENCODING_11);

        ByteArrayBitOutput checkedOutput = new ByteArrayBitOutput();
        saveContent(checkedOutput, targetSide);
        byte[] contentArray = checkedOutput.getBytes();
        long hash = computeHash(contentArray);

        output.addLong(hash);
        output.addByteArray(contentArray);
    }

    private void saveContent(BitOutput output, Side targetSide) {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);

        exportSettings.save(output);

        if (targetSide == Side.EDITOR) {
            saveParallelCollection(output, threadPool, combinedResourcepacks, (pack, packData) -> pack.getValues().save(packData));
            saveParallelCollection(output, threadPool, textures, (texture, textureData) -> texture.getValues().save(textureData));
            saveParallelCollection(output, threadPool, armorTextures, (texture, textureData) -> texture.getValues().save(textureData));
        } else {
            output.addLong(System.currentTimeMillis());
        }

        saveParallelCollection(
                output, threadPool, fancyPantsArmorTextures,
                (texture, textureData) -> texture.getValues().save(textureData, targetSide)
        );
        saveParallelCollection(output, threadPool, projectileCovers, (cover, coverData) -> cover.getValues().save(coverData, targetSide));
        saveParallelCollection(output, threadPool, projectiles, (projectile, projectileData) -> projectile.getValues().save(projectileData));
        saveParallelCollection(output, threadPool, items, (item, itemData) -> item.getValues().save(itemData, targetSide));
        saveParallelCollection(output, threadPool, equipmentSets, (set, setData) -> set.getValues().save(setData));
        saveParallelCollection(output, threadPool, damageSources, (source, sourceData) -> source.getValues().save(sourceData));
        saveParallelCollection(
                output, threadPool, blocks, (block, blockData) -> {
                    blockData.addInt(block.getValues().getInternalID());
                    block.getValues().save(blockData, targetSide);
                }
        );
        saveParallelCollection(output, threadPool, oreVeinGenerators, (ores, oreData) -> ores.getValues().save(oreData));
        saveParallelCollection(output, threadPool, treeGenerators, (trees, treeData) -> trees.getValues().save(treeData));
        saveParallelCollection(output, threadPool, craftingRecipes, (recipe, recipeData) -> recipe.getValues().save(recipeData));
        saveParallelCollection(output, threadPool, upgrades, (upgrade, upgradeData) -> upgrade.getValues().save(upgradeData));
        saveParallelCollection(output, threadPool, blockDrops, (drop, dropData) -> drop.getValues().save(dropData));
        saveParallelCollection(output, threadPool, mobDrops, (drop, dropData) -> drop.getValues().save(dropData));
        saveParallelCollection(output, threadPool, fuelRegistries, (registry, registryData) -> registry.getValues().save(registryData));
        saveParallelCollection(output, threadPool, energyTypes, (energy, energyData) -> energy.getValues().save(energyData));
        saveParallelCollection(output, threadPool, soundTypes, (sound, soundData) -> sound.getValues().save(soundData, targetSide));
        saveParallelCollection(output, threadPool, containers, (container, containerData) -> container.getValues().save(containerData));
        saveParallelCollection(output, threadPool, removedItemNames, (itemName, itemData) -> itemData.addString(itemName));

        threadPool.shutdown();
    }

    private <T> void saveParallelCollection(
            BitOutput output, ExecutorService threadPool,
            Collection<T> elements, BiConsumer<T, ByteArrayBitOutput> saveElement
    ) {
        output.addInt(elements.size());
        List<Future<ByteArrayBitOutput>> allElementsData = new ArrayList<>();
        for (T element : elements) {
            allElementsData.add(threadPool.submit(() -> {
                ByteArrayBitOutput elementData = new ByteArrayBitOutput(10000);
                saveElement.accept(element, elementData);
                return elementData;
            }));
        }
        allElementsData.forEach(futureData -> {
            try {
                ByteArrayBitOutput elementData = futureData.get();
                output.addBytes(Arrays.copyOf(elementData.getBackingArray(), elementData.getByteIndex()));
                if (elementData.getBoolIndex() > 0) {
                    boolean[] last = BitHelper.byteToBinary(elementData.getBackingArray()[elementData.getByteIndex()]);
                    for (int index = 0; index < elementData.getBoolIndex(); index++)
                        output.addBoolean(last[index]);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void load(
            BitInput input, boolean allowOutdated
    ) throws IntegrityException, UnknownEncodingException, OutdatedItemSetException {
        this.intReferences = new ArrayList<>();
        this.stringReferences = new ArrayList<>();
        this.uuidReferences = new ArrayList<>();

        byte encoding = input.readByte();
        if (!allowOutdated && encoding < SetEncoding.ENCODING_11) throw new OutdatedItemSetException();
        if (encoding == SetEncoding.ENCODING_1) {
            load1(input);
            initDefaults1();
        } else if (encoding == SetEncoding.ENCODING_2) {
            load2(input);
            initDefaults2();
        } else if (encoding == SetEncoding.ENCODING_3) {
            load3(input);
            initDefaults3();
        } else if (encoding == SetEncoding.ENCODING_4) {
            load4(input);
            initDefaults4();
        } else if (encoding == SetEncoding.ENCODING_5) {
            load5(input);
            initDefaults5();
        } else if (encoding == SetEncoding.ENCODING_6) {
            load6(input);
            initDefaults6();
        } else if (encoding == SetEncoding.ENCODING_7) {
            load7(input);
            initDefaults7();
        } else if (encoding == SetEncoding.ENCODING_8) {
            load8(input);
            initDefaults8();
        } else if (encoding == SetEncoding.ENCODING_9) {
            load9(input);
            initDefaults9();
        } else if (encoding == SetEncoding.ENCODING_10) {
            load10(input);
            initDefaults10();
        } else if (encoding == SetEncoding.ENCODING_11) {
            load11(input);
            initDefaults11();
        } else {
            throw new UnknownEncodingException("ItemSet", encoding);
        }
        finishedLoading = true;

        // Ensure that all references find their model (this must happen before the user can rename models)
        for (IntBasedReference<?, ?> intReference : intReferences) {
            intReference.get();
        }
        for (StringBasedReference<?, ?> stringReference : stringReferences) {
            stringReference.get();
        }
        for (UUIDBasedReference<?, ?> uuidReference : uuidReferences) {
            uuidReference.get();
        }
        intReferences = null;
        stringReferences = null;
        uuidReferences = null;
    }

    private void initDefaults1() {
        initDefaults2();
    }

    private void initDefaults2() {
        this.blockDrops = new ArrayList<>();
        this.mobDrops = new ArrayList<>();
        initDefaults3();
    }

    private void initDefaults3() {
        initDefaults4();
    }

    private void initDefaults4() {
        this.projectileCovers = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        initDefaults5();
    }

    private void initDefaults5() {
        this.exportTime = generateFakeExportTime();
        initDefaults6();
    }

    private void initDefaults6() {
        this.fuelRegistries = new ArrayList<>();
        this.containers = new ArrayList<>();
        this.initDefaults7();
    }

    private void initDefaults7() {
        this.armorTextures = new ArrayList<>();
        this.removedItemNames = new ArrayList<>();
        initDefaults8();
    }

    private void initDefaults8() {
        this.blocks = new ArrayList<>();
        initDefaults9();
    }

    private void initDefaults9() {
        this.equipmentSets = new ArrayList<>();
        this.energyTypes = new ArrayList<>();
        this.soundTypes = new ArrayList<>();
        this.oreVeinGenerators = new ArrayList<>();
        this.treeGenerators = new ArrayList<>();
        initDefaults10();
    }

    private void initDefaults10() {
        this.exportSettings = new ExportSettingsValues(false);
        this.fancyPantsArmorTextures = new ArrayList<>();
        this.combinedResourcepacks = new ArrayList<>();
        this.damageSources = new ArrayList<>();
        this.upgrades = new ArrayList<>();
        initDefaults11();
    }

    private void initDefaults11() {
        // Nothing to be done until encoding 12 has been made
    }

    private void loadExportTime(BitInput input) {
        if (side == Side.PLUGIN) {
            this.exportTime = input.readLong();
        }
    }

    private void loadCombinedResourcepacks(BitInput input) throws UnknownEncodingException {
        if (this.side == Side.EDITOR) {
            int numPacks = input.readInt();
            this.combinedResourcepacks = new ArrayList<>(numPacks);
            for (int counter = 0; counter < numPacks; counter++) {
                this.combinedResourcepacks.add(new CombinedResourcepack(CombinedResourcepackValues.load(input)));
            }
        } else this.combinedResourcepacks = new ArrayList<>(0);
    }

    private void loadTextures(BitInput input, boolean readEncoding, boolean expectCompressed) throws UnknownEncodingException {
        if (side == Side.EDITOR) {
            int numTextures = input.readInt();
            this.textures = new ArrayList<>(numTextures);
            for (int counter = 0; counter < numTextures; counter++) {
                if (readEncoding) {
                    this.textures.add(new CustomTexture(BaseTextureValues.load(input, expectCompressed)));
                } else {
                    this.textures.add(new CustomTexture(BaseTextureValues.load(input, BaseTextureValues.ENCODING_SIMPLE_1, expectCompressed)));
                }
            }
        } else {
            this.textures = new ArrayList<>(0);
        }
    }

    private void loadArmorTextures(BitInput input) throws UnknownEncodingException {
        if (side == Side.EDITOR) {
            int numArmorTextures = input.readInt();
            this.armorTextures = new ArrayList<>(numArmorTextures);
            for (int counter = 0; counter < numArmorTextures; counter++) {
                this.armorTextures.add(new ArmorTexture(ArmorTextureValues.load(input)));
            }
        } else {
            this.armorTextures = new ArrayList<>(0);
        }
    }

    private void loadFancyPantsArmorTextures(BitInput input) throws UnknownEncodingException {
        int numFpTextures = input.readInt();
        this.fancyPantsArmorTextures = new ArrayList<>(numFpTextures);
        for (int counter = 0; counter < numFpTextures; counter++) {
            this.fancyPantsArmorTextures.add(new FancyPantsArmorTexture(FancyPantsArmorTextureValues.load(input, side)));
        }
    }

    private void loadItems(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
        int numItems = input.readInt();
        this.items = new ArrayList<>(numItems);
        for (int counter = 0; counter < numItems; counter++) {
            this.items.add(new CustomItem(CustomItemValues.load(input, this, checkCustomModel)));
        }
    }

    private void loadEquipmentSets(BitInput input) throws UnknownEncodingException {
        int numEquipmentSets = input.readInt();
        this.equipmentSets = new ArrayList<>(numEquipmentSets);
        for (int counter = 0; counter < numEquipmentSets; counter++) {
            this.equipmentSets.add(new EquipmentSet(EquipmentSetValues.load(input, this)));
        }
    }

    private void loadDamageSources(BitInput input) throws UnknownEncodingException {
        int numDamageSources = input.readInt();
        this.damageSources = new ArrayList<>(numDamageSources);
        for (int counter = 0; counter < numDamageSources; counter++) {
            this.damageSources.add(new CustomDamageSource(CustomDamageSourceValues.load(input)));
        }
    }

    private void loadBlocks(BitInput input) throws UnknownEncodingException {
        int numBlocks = input.readInt();
        this.blocks = new ArrayList<>(numBlocks);
        for (int counter = 0; counter < numBlocks; counter++) {
            int blockID = input.readInt();
            this.blocks.add(new CustomBlock(CustomBlockValues.load(input, this, blockID)));
        }
    }

    private void loadOreVeinGenerators(BitInput input) throws UnknownEncodingException {
        int numGenerators = input.readInt();
        this.oreVeinGenerators = new ArrayList<>(numGenerators);
        for (int counter = 0; counter < numGenerators; counter++) {
            this.oreVeinGenerators.add(new OreVeinGenerator(OreVeinGeneratorValues.load(input, this)));
        }
    }

    private void loadTreeGenerators(BitInput input) throws UnknownEncodingException {
        int numGenerators = input.readInt();
        this.treeGenerators = new ArrayList<>(numGenerators);
        for (int counter = 0; counter < numGenerators; counter++) {
            this.treeGenerators.add(new TreeGenerator(TreeGeneratorValues.load(input, this)));
        }
    }

    private void loadCraftingRecipes(BitInput input) throws UnknownEncodingException {
        int numRecipes = input.readInt();
        this.craftingRecipes = new ArrayList<>(numRecipes);
        for (int counter = 0; counter < numRecipes; counter++) {
            this.craftingRecipes.add(new CustomCraftingRecipe(CraftingRecipeValues.load(input, this)));
        }
    }

    private void loadBlockDrops(BitInput input) throws UnknownEncodingException {
        int numBlockDrops = input.readInt();
        this.blockDrops = new ArrayList<>(numBlockDrops);
        for (int counter = 0; counter < numBlockDrops; counter++) {
            this.blockDrops.add(new BlockDrop(BlockDropValues.load(input, this)));
        }
    }

    private void loadMobDrops(BitInput input) throws UnknownEncodingException {
        int numMobDrops = input.readInt();
        this.mobDrops = new ArrayList<>(numMobDrops);
        for (int counter = 0; counter < numMobDrops; counter++) {
            this.mobDrops.add(new MobDrop(MobDropValues.load(input, this)));
        }
    }

    private void loadProjectileCovers(BitInput input) throws UnknownEncodingException {
        int numProjectileCovers = input.readInt();
        this.projectileCovers = new ArrayList<>(numProjectileCovers);
        for (int counter = 0; counter < numProjectileCovers; counter++) {
            this.projectileCovers.add(new ProjectileCover(ProjectileCoverValues.load(input, this)));
        }
    }

    private void loadProjectiles(BitInput input) throws UnknownEncodingException {
        int numProjectiles = input.readInt();
        this.projectiles = new ArrayList<>(numProjectiles);
        for (int counter = 0; counter < numProjectiles; counter++) {
            this.projectiles.add(new CustomProjectile(CustomProjectileValues.load(input, this)));
        }
    }

    private void loadFuelRegistries(BitInput input) throws UnknownEncodingException {
        int numFuelRegistries = input.readInt();
        this.fuelRegistries = new ArrayList<>(numFuelRegistries);
        for (int counter = 0; counter < numFuelRegistries; counter++) {
            this.fuelRegistries.add(new CustomFuelRegistry(FuelRegistryValues.load(input, this)));
        }
    }

    private void loadEnergyTypes(BitInput input) throws UnknownEncodingException {
        int numEnergyTypes = input.readInt();
        this.energyTypes = new ArrayList<>(numEnergyTypes);
        for (int counter = 0; counter < numEnergyTypes; counter++) {
            this.energyTypes.add(new EnergyType(EnergyTypeValues.load(input)));
        }
    }

    private void loadSoundTypes(BitInput input) throws UnknownEncodingException {
        int numSoundTypes = input.readInt();
        this.soundTypes = new ArrayList<>(numSoundTypes);
        for (int counter = 0; counter < numSoundTypes; counter++) {
            this.soundTypes.add(new CustomSoundType(CustomSoundTypeValues.load(input, this)));
        }
    }

    private void loadContainers(BitInput input) throws UnknownEncodingException {
        int numContainers = input.readInt();
        this.containers = new ArrayList<>(numContainers);
        for (int counter = 0; counter < numContainers; counter++) {
            this.containers.add(new CustomContainer(CustomContainerValues.load(input, this)));
        }
    }

    private void loadDeletedItemNames(BitInput input) {
        int numDeletedItems = input.readInt();
        this.removedItemNames = new ArrayList<>(numDeletedItems);
        for (int counter = 0; counter < numDeletedItems; counter++) {
            this.removedItemNames.add(input.readString());
        }
    }

    // Note: this hash is only made to check for accidental corruption; NOT malicious inputs
    // Therefore it doesn't have to be cryptographically strong.
    private long computeHash(byte[] content) {
        long result = 0;
        for (byte b : content) {
            int i = b + 129;
            result += i;
            result += i * b;
        }
        return result;
    }

    private void load1(BitInput input) throws UnknownEncodingException {
        loadTextures(input, false, false);
        loadItems(input, false);
        loadCraftingRecipes(input);
    }

    private void load2(BitInput input) throws UnknownEncodingException {
        loadTextures(input, true, false);
        loadItems(input, false);
        loadCraftingRecipes(input);
    }

    private void load3(BitInput input) throws UnknownEncodingException {
        load2(input);
        loadBlockDrops(input);
        loadMobDrops(input);
    }

    private void load4(BitInput input) throws UnknownEncodingException {
        loadTextures(input, true, false);
        loadItems(input, true);
        loadCraftingRecipes(input);
        loadBlockDrops(input);
        loadMobDrops(input);
    }

    private void load5(BitInput input) throws UnknownEncodingException {
        loadTextures(input, true, false);
        loadProjectileCovers(input);
        loadProjectiles(input);
        loadItems(input, true);
        loadCraftingRecipes(input);
        loadBlockDrops(input);
        loadMobDrops(input);
    }

    private interface LoadFunction {
        void load(BitInput input, long hash) throws UnknownEncodingException;
    }

    private void loadWithIntegrityCheck(BitInput input, LoadFunction loadContent) throws IntegrityException, UnknownEncodingException {
        long expectedHash = input.readLong();

        // When the input is corrupted, reading a byte array can load to all kinds of exceptions and errors.
        // An attempt is made to catch any such error and wrap it in an IntegrityException, which should be more clear
        // than a weird error.
        byte[] remaining;
        try {
            remaining = input.readByteArray();
        } catch (Throwable corrupted) {
            throw new IntegrityException(corrupted);
        }

        long actualHash = computeHash(remaining);
        if (expectedHash != actualHash) {
            throw new IntegrityException(expectedHash, actualHash);
        }

        BitInput actualInput = new ByteArrayBitInput(remaining);
        loadContent.load(actualInput, actualHash);
    }

    private void load6(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            this.exportTime = -Math.abs(hash);
            load5(input);
        });
    }

    private void load7(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            this.exportTime = -Math.abs(hash);
            load5(input);
            loadFuelRegistries(input);
            loadContainers(input);
        });
    }

    private void load8(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadExportTime(input);
            loadTextures(input, true, true);
            loadArmorTextures(input);
            loadProjectileCovers(input);
            loadProjectiles(input);
            loadItems(input, true);
            loadCraftingRecipes(input);
            loadBlockDrops(input);
            loadMobDrops(input);
            loadFuelRegistries(input);
            loadContainers(input);
            loadDeletedItemNames(input);
        });
    }

    private void load9(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadExportTime(input);
            loadTextures(input, true, true);
            loadArmorTextures(input);
            loadProjectileCovers(input);
            loadProjectiles(input);
            loadItems(input, true);
            loadBlocks(input);
            loadCraftingRecipes(input);
            loadBlockDrops(input);
            loadMobDrops(input);
            loadFuelRegistries(input);
            loadContainers(input);
            loadDeletedItemNames(input);
        });
    }

    private void load10(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadExportTime(input);
            loadTextures(input, true, true);
            loadArmorTextures(input);
            loadProjectileCovers(input);
            loadProjectiles(input);
            loadItems(input, true);
            loadEquipmentSets(input);
            loadBlocks(input);
            loadOreVeinGenerators(input);
            loadTreeGenerators(input);
            loadCraftingRecipes(input);
            loadBlockDrops(input);
            loadMobDrops(input);
            loadFuelRegistries(input);
            loadEnergyTypes(input);
            loadSoundTypes(input);
            loadContainers(input);
            loadDeletedItemNames(input);
        });
    }

    private void load11(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            this.exportSettings = ExportSettingsValues.load(input);
            loadExportTime(input);
            loadCombinedResourcepacks(input);
            loadTextures(input, true, true);
            loadArmorTextures(input);
            loadFancyPantsArmorTextures(input);
            loadProjectileCovers(input);
            loadProjectiles(input);
            loadItems(input, true);
            loadEquipmentSets(input);
            loadDamageSources(input);
            loadBlocks(input);
            loadOreVeinGenerators(input);
            loadTreeGenerators(input);
            loadCraftingRecipes(input);
            this.upgrades = CollectionHelper.load(input, input1 -> new Upgrade(UpgradeValues.load(input1, this)));
            loadBlockDrops(input);
            loadMobDrops(input);
            loadFuelRegistries(input);
            loadEnergyTypes(input);
            loadSoundTypes(input);
            loadContainers(input);
            loadDeletedItemNames(input);
        });
    }

    public Side getSide() {
        return side;
    }

    public ExportSettingsValues getExportSettings() {
        return exportSettings;
    }

    public long getExportTime() {
        return exportTime;
    }

    public CombinedResourcepacksView getCombinedResourcepacks() {
        return new CombinedResourcepacksView(combinedResourcepacks);
    }

    public CustomTexturesView getTextures() {
        return new CustomTexturesView(textures);
    }

    public ArmorTexturesView getArmorTextures() {
        return new ArmorTexturesView(armorTextures);
    }

    public FancyPantsArmorTexturesView getFancyPantsArmorTextures() {
        return new FancyPantsArmorTexturesView(fancyPantsArmorTextures);
    }

    public CustomItemsView getItems() {
        return new CustomItemsView(items);
    }

    public Set<String> getRemovedItemNames() {
        return new HashSet<>(removedItemNames);
    }

    public EquipmentSetsView getEquipmentSets() {
        return new EquipmentSetsView(equipmentSets);
    }

    public CustomDamageSourcesView getDamageSources() {
        return new CustomDamageSourcesView(damageSources);
    }

    public CustomRecipesView getCraftingRecipes() {
        return new CustomRecipesView(craftingRecipes);
    }

    public UpgradesView getUpgrades() {
        return new UpgradesView(upgrades);
    }

    public BlockDropsView getBlockDrops() {
        return new BlockDropsView(blockDrops);
    }

    public MobDropsView getMobDrops() {
        return new MobDropsView(mobDrops);
    }

    public CustomProjectilesView getProjectiles() {
        return new CustomProjectilesView(projectiles);
    }

    public ProjectileCoversView getProjectileCovers() {
        return new ProjectileCoversView(projectileCovers);
    }

    public CustomContainerView getContainers() {
        return new CustomContainerView(containers);
    }

    public FuelRegistriesView getFuelRegistries() {
        return new FuelRegistriesView(fuelRegistries);
    }

    public EnergyTypesView getEnergyTypes() {
        return new EnergyTypesView(energyTypes);
    }

    public CustomSoundTypesView getSoundTypes() {
        return new CustomSoundTypesView(soundTypes);
    }

    public CustomBlocksView getBlocks() {
        return new CustomBlocksView(blocks);
    }

    public OreVeinGeneratorsView getOreVeinGenerators() {
        return new OreVeinGeneratorsView(oreVeinGenerators);
    }

    public TreeGeneratorsView getTreeGenerators() {
        return new TreeGeneratorsView(treeGenerators);
    }

    public TextureReference getTextureReference(String textureName) throws NoSuchElementException {
        if (finishedLoading) {
            return new TextureReference(CollectionHelper.find(textures, texture -> texture.getValues().getName(), textureName).get());
        } else {
            return new TextureReference(textureName, this);
        }
    }

    public ArmorTextureReference getArmorTextureReference(String textureName) throws NoSuchElementException {
        if (finishedLoading) {
            return new ArmorTextureReference(CollectionHelper.find(armorTextures, texture -> texture.getValues().getName(), textureName).get());
        } else {
            return new ArmorTextureReference(textureName, this);
        }
    }

    public FancyPantsArmorTextureReference getFancyPantsArmorTextureReference(UUID id) throws NoSuchElementException {
        if (finishedLoading) {
            return new FancyPantsArmorTextureReference(CollectionHelper.find(
                    fancyPantsArmorTextures, fpTexture -> fpTexture.getValues().getId(), id
            ).get());
        } else {
            return new FancyPantsArmorTextureReference(id, this);
        }
    }

    public ItemReference getItemReference(String itemName) throws NoSuchElementException {
        if (finishedLoading) {
            return new ItemReference(CollectionHelper.find(items, item -> item.getValues().getName(), itemName).get());
        } else {
            return new ItemReference(itemName, this);
        }
    }

    public CustomDamageSourceReference getDamageSourceReference(UUID damageSourceID) throws NoSuchElementException {
        if (finishedLoading) {
            return new CustomDamageSourceReference(CollectionHelper.find(damageSources, damageSource -> damageSource.getValues().getId(), damageSourceID).get());
        } else {
            return new CustomDamageSourceReference(damageSourceID, this);
        }
    }

    public UpgradeReference getUpgradeReference(UUID upgradeID) throws NoSuchElementException {
        if (finishedLoading) {
            return new UpgradeReference(CollectionHelper.find(upgrades, upgrade -> upgrade.getValues().getId(), upgradeID).get());
        } else {
            return new UpgradeReference(upgradeID, this);
        }
    }

    public BlockReference getBlockReference(int blockID) throws NoSuchElementException {
        if (finishedLoading) {
            return new BlockReference(CollectionHelper.find(blocks, block -> block.getValues().getInternalID(), blockID).get());
        } else {
            return new BlockReference(blockID, this);
        }
    }

    public ContainerReference getContainerReference(String containerName) throws NoSuchElementException {
        if (finishedLoading) {
            return new ContainerReference(CollectionHelper.find(containers, container -> container.getValues().getName(), containerName).get());
        } else {
            return new ContainerReference(containerName, this);
        }
    }

    public FuelRegistryReference getFuelRegistryReference(String registryName) throws NoSuchElementException {
        if (finishedLoading) {
            return new FuelRegistryReference(CollectionHelper.find(fuelRegistries, registry -> registry.getValues().getName(), registryName).get());
        } else {
            return new FuelRegistryReference(registryName, this);
        }
    }

    public EnergyTypeReference getEnergyTypeReference(UUID typeID) throws NoSuchElementException {
        if (finishedLoading) {
            return new EnergyTypeReference(CollectionHelper.find(energyTypes, energyType -> energyType.getValues().getId(), typeID).get());
        } else {
            return new EnergyTypeReference(typeID, this);
        }
    }

    public SoundTypeReference getSoundTypeReference(UUID soundID) throws NoSuchElementException {
        if (finishedLoading) {
            return new SoundTypeReference(CollectionHelper.find(soundTypes, soundType -> soundType.getValues().getId(), soundID).get());
        } else {
            return new SoundTypeReference(soundID, this);
        }
    }

    public ProjectileReference getProjectileReference(String projectileName) throws NoSuchElementException {
        if (finishedLoading) {
            return new ProjectileReference(CollectionHelper.find(projectiles, projectile -> projectile.getValues().getName(), projectileName).get());
        } else {
            return new ProjectileReference(projectileName, this);
        }
    }

    public ProjectileCoverReference getProjectileCoverReference(String coverName) throws NoSuchElementException {
        if (finishedLoading) {
            return new ProjectileCoverReference(CollectionHelper.find(projectileCovers, cover -> cover.getValues().getName(), coverName).get());
        } else {
            return new ProjectileCoverReference(coverName, this);
        }
    }

    public Optional<CombinedResourcepackValues> getCombinedResourcepack(String packName) {
        return CollectionHelper.find(combinedResourcepacks, pack -> pack.getValues().getName(), packName).map(CombinedResourcepack::getValues);
    }

    public Optional<BaseTextureValues> getTexture(String textureName) {
        return CollectionHelper.find(textures, texture -> texture.getValues().getName(), textureName).map(CustomTexture::getValues);
    }

    public Optional<ArmorTextureValues> getArmorTexture(String textureName) {
        return CollectionHelper.find(armorTextures, texture -> texture.getValues().getName(), textureName).map(ArmorTexture::getValues);
    }

    public Optional<FancyPantsArmorTextureValues> getFancyPantsArmorTexture(UUID id) {
        return CollectionHelper.find(fancyPantsArmorTextures, fpTexture -> fpTexture.getValues().getId(), id).map(FancyPantsArmorTexture::getValues);
    }

    public Optional<CustomItemValues> getItem(String itemName) {
        return CollectionHelper.find(items, item -> item.getValues().getName(), itemName).map(CustomItem::getValues);
    }

    public Optional<CustomDamageSourceValues> getDamageSource(UUID damageSourceID) {
        return CollectionHelper.find(damageSources, damageSource -> damageSource.getValues().getId(), damageSourceID).map(CustomDamageSource::getValues);
    }

    public Optional<UpgradeValues> getUpgrade(UUID upgradeID) {
        return CollectionHelper.find(upgrades, upgrade -> upgrade.getValues().getId(), upgradeID).map(Upgrade::getValues);
    }

    public Optional<CustomBlockValues> getBlock(int blockInternalId) {
        return CollectionHelper.find(blocks, block -> block.getValues().getInternalID(), blockInternalId).map(CustomBlock::getValues);
    }

    public Optional<CustomBlockValues> getBlock(String blockName) {
        return CollectionHelper.find(blocks, block -> block.getValues().getName(), blockName).map(CustomBlock::getValues);
    }

    public Optional<CustomContainerValues> getContainer(String containerName) {
        return CollectionHelper.find(containers, container -> container.getValues().getName(), containerName).map(CustomContainer::getValues);
    }

    public Optional<FuelRegistryValues> getFuelRegistry(String registryName) {
        return CollectionHelper.find(fuelRegistries, registry -> registry.getValues().getName(), registryName).map(CustomFuelRegistry::getValues);
    }

    public Optional<EnergyTypeValues> getEnergyType(UUID id) {
        return CollectionHelper.find(energyTypes, energyType -> energyType.getValues().getId(), id).map(EnergyType::getValues);
    }

    public Optional<CustomSoundTypeValues> getSoundType(UUID id) {
        return CollectionHelper.find(soundTypes, soundType -> soundType.getValues().getId(), id).map(CustomSoundType::getValues);
    }

    public Optional<CustomProjectileValues> getProjectile(String projectileName) {
        return CollectionHelper.find(projectiles, projectile -> projectile.getValues().getName(), projectileName).map(CustomProjectile::getValues);
    }

    public Optional<ProjectileCoverValues> getProjectileCover(String coverName) {
        return CollectionHelper.find(projectileCovers, cover -> cover.getValues().getName(), coverName).map(ProjectileCover::getValues);
    }

    private <T> boolean isReferenceValid(Collection<T> collection, T model) {
        if (model == null) return false;
        return collection.contains(model);
    }

    public boolean isReferenceValid(CombinedResourcepackReference reference) {
        return isReferenceValid(combinedResourcepacks, reference.getModel());
    }

    public boolean isReferenceValid(TextureReference reference) {
        return isReferenceValid(textures, reference.getModel());
    }

    public boolean isReferenceValid(ArmorTextureReference reference) {
        return isReferenceValid(armorTextures, reference.getModel());
    }

    public boolean isReferenceValid(FancyPantsArmorTextureReference reference) {
        return isReferenceValid(fancyPantsArmorTextures, reference.getModel());
    }

    public boolean isReferenceValid(ItemReference reference) {
        return isReferenceValid(items, reference.getModel());
    }

    public boolean isReferenceValid(EquipmentSetReference reference) {
        return isReferenceValid(equipmentSets, reference.getModel());
    }

    public boolean isReferenceValid(CustomDamageSourceReference reference) {
        return isReferenceValid(damageSources, reference.getModel());
    }

    public boolean isReferenceValid(CraftingRecipeReference reference) {
        return isReferenceValid(craftingRecipes, reference.getModel());
    }

    public boolean isReferenceValid(UpgradeReference reference) {
        return isReferenceValid(upgrades, reference.getModel());
    }

    public boolean isReferenceValid(BlockDropReference reference) {
        return isReferenceValid(blockDrops, reference.getModel());
    }

    public boolean isReferenceValid(MobDropReference reference) {
        return isReferenceValid(mobDrops, reference.getModel());
    }

    public boolean isReferenceValid(BlockReference reference) {
        return isReferenceValid(blocks, reference.getModel());
    }

    public boolean isReferenceValid(OreVeinGeneratorReference reference) {
        return isReferenceValid(oreVeinGenerators, reference.getModel());
    }

    public boolean isReferenceValid(TreeGeneratorReference reference) {
        return isReferenceValid(treeGenerators, reference.getModel());
    }

    public boolean isReferenceValid(ContainerReference reference) {
        return isReferenceValid(containers, reference.getModel());
    }

    public boolean isReferenceValid(FuelRegistryReference reference) {
        return isReferenceValid(fuelRegistries, reference.getModel());
    }

    public boolean isReferenceValid(EnergyTypeReference reference) {
        return isReferenceValid(energyTypes, reference.getModel());
    }

    public boolean isReferenceValid(SoundTypeReference reference) {
        return isReferenceValid(soundTypes, reference.getModel());
    }

    public boolean isReferenceValid(ProjectileReference reference) {
        return isReferenceValid(projectiles, reference.getModel());
    }

    public boolean isReferenceValid(ProjectileCoverReference reference) {
        return isReferenceValid(projectileCovers, reference.getModel());
    }

    public boolean hasItemBeenDeleted(String itemName) {
        return removedItemNames.contains(itemName);
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        // Avoid annoying NullPointerException's by first doing a general validation check
        validate();

        for (BaseTextureValues texture : getTextures()) {
            Validation.scope(
                    "Texture " + texture.getName(),
                    () -> texture.validateExportVersion(version)
            );
        }

        for (ArmorTextureValues armorTexture : getArmorTextures()) {
            Validation.scope(
                    "Armor texture " + armorTexture.getName(),
                    () -> armorTexture.validateExportVersion(version)
            );
        }

        for (FancyPantsArmorTextureValues fpTexture : getFancyPantsArmorTextures()) {
            Validation.scope("FP texture " + fpTexture.getName(), fpTexture::validateExportVersion, version);
        }

        for (CustomItemValues item : getItems()) {
            Validation.scope(
                    "Item " + item.getName(),
                    () -> item.validateExportVersion(version)
            );
        }

        for (EquipmentSetValues equipmentSet : getEquipmentSets()) {
            Validation.scope(
                    "Equipment set " + equipmentSet,
                    () -> equipmentSet.validateExportVersion(version)
            );
        }

        for (CraftingRecipeValues recipe : getCraftingRecipes()) {
            Validation.scope(
                    "Recipe for " + recipe.getResult(),
                    () -> recipe.validateExportVersion(version)
            );
        }

        for (UpgradeValues upgrade : getUpgrades()) {
            Validation.scope("Upgrade " + upgrade.getName(), upgrade::validateExportVersion, version);
        }

        for (BlockDropValues blockDrop : getBlockDrops()) {
            Validation.scope(
                    "Block drop for " + blockDrop.getBlockType(),
                    () -> blockDrop.validateExportVersion(version)
            );
        }

        for (MobDropValues mobDrop : getMobDrops()) {
            Validation.scope(
                    "Mob drop for " + mobDrop.getEntityType(),
                    () -> mobDrop.validateExportVersion(version)
            );
        }

        for (CustomProjectileValues projectile : getProjectiles()) {
            Validation.scope(
                    "Projectile " + projectile.getName(),
                    () -> projectile.validateExportVersion(version)
            );
        }

        for (ProjectileCoverValues projectileCover : getProjectileCovers()) {
            Validation.scope(
                    "Projectile cover " + projectileCover.getName(),
                    () -> projectileCover.validateExportVersion(version)
            );
        }

        for (CustomContainerValues container : getContainers()) {
            Validation.scope(
                    "Container " + container.getName(),
                    () -> container.validateExportVersion(version)
            );
        }

        for (FuelRegistryValues fuelRegistry : getFuelRegistries()) {
            Validation.scope(
                    "Fuel registry " + fuelRegistry.getName(),
                    () -> fuelRegistry.validateExportVersion(version)
            );
        }

        for (CustomBlockValues block : getBlocks()) {
            Validation.scope(
                    "Block " + block.getName(),
                    () -> block.validateExportVersion(version)
            );
        }

        for (OreVeinGeneratorValues oreVein : getOreVeinGenerators()) {
            Validation.scope("Ore vein generator " + oreVein, oreVein::validateExportVersion, version);
        }

        for (TreeGeneratorValues treeGenerator : getTreeGenerators()) {
            Validation.scope("Tree generator " + treeGenerator, treeGenerator::validateExportVersion, version);
        }
    }

    private <M, I> void validateUniqueIDs(
            String description, Collection<M> collection, Function<M, I> getID
    ) throws ProgrammingValidationException {
        Set<I> foundIDs = new HashSet<>(collection.size());
        for (M element : collection) {
            I id = getID.apply(element);
            if (foundIDs.contains(id)) throw new ProgrammingValidationException("Duplicate " + description + " " + id);
            foundIDs.add(id);
        }
    }

    private void validate() throws ValidationException, ProgrammingValidationException {
        for (CombinedResourcepackValues combinedPack : getCombinedResourcepacks()) {
            Validation.scope(
                    "Combined resourcepack " + combinedPack.getName(),
                    () -> combinedPack.validate(this, combinedPack.getName(), combinedPack.getPriority())
            );
        }
        validateUniqueIDs("Combined resourcepack name", combinedResourcepacks, pack -> pack.getValues().getName());
        validateUniqueIDs("Combined resourcepack priority", combinedResourcepacks, pack -> pack.getValues().getPriority());

        for (CustomTexture texture : textures) {
            Validation.scope(
                    "Texture " + texture.getValues().getName(),
                    () -> texture.getValues().validateComplete(this, texture.getValues().getName())
            );
        }
        validateUniqueIDs("texture name", textures, texture -> texture.getValues().getName());

        for (ArmorTexture texture : armorTextures) {
            Validation.scope(
                    "Armor texture " + texture.getValues().getName(),
                    () -> texture.getValues().validate(this, texture.getValues().getName())
            );
        }
        validateUniqueIDs("armor texture name", armorTextures, armorTexture -> armorTexture.getValues().getName());

        for (FancyPantsArmorTextureValues fpTexture : getFancyPantsArmorTextures()) {
            Validation.scope(
                    "FP texture " + fpTexture.getName(),
                    () -> fpTexture.validate(this, fpTexture.getId())
            );
        }
        validateUniqueIDs("FP texture ID", fancyPantsArmorTextures, fpTexture -> fpTexture.getValues().getId());
        validateUniqueIDs("FP texture name", fancyPantsArmorTextures, fpTexture -> fpTexture.getValues().getName());
        validateUniqueIDs("FP texture RGB value", fancyPantsArmorTextures, fpTexture -> fpTexture.getValues().getRgb());

        for (CustomItem item : items) {
            Validation.scope(
                    "Item " + item.getValues().getName(),
                    () -> item.getValues().validateComplete(this, item.getValues().getName())
            );
        }
        validateUniqueIDs("item name", items, item -> item.getValues().getName());

        for (EquipmentSet equipmentSet : equipmentSets) {
            Validation.scope(
                    "Equipment set " + equipmentSet.getValues(),
                    equipmentSet.getValues()::validate, this
            );
        }

        for (CustomDamageSourceValues damageSource : getDamageSources()) {
            Validation.scope(
                    "Damage source " + damageSource.getName(),
                    () -> damageSource.validateComplete(this, damageSource.getId())
            );
        }
        validateUniqueIDs("Damage sources", damageSources, damageSource -> damageSource.getValues().getId());

        for (CustomCraftingRecipe recipe : craftingRecipes) {
            Validation.scope(
                    "Recipe for " + recipe.getValues().getResult(),
                    () -> recipe.getValues().validate(this, new CraftingRecipeReference(recipe))
            );
        }

        for (UpgradeValues upgrade : getUpgrades()) {
            Validation.scope(
                    "Upgrade " + upgrade.getName(),
                    () -> upgrade.validateComplete(this, upgrade.getId())
            );
        }
        validateUniqueIDs("upgrade id", upgrades, upgrade -> upgrade.getValues().getId());

        for (BlockDrop blockDrop : blockDrops) {
            Validation.scope(
                    "Block drop for " + blockDrop.getValues().getBlockType(),
                    () -> blockDrop.getValues().validate(this)
            );
        }

        for (MobDrop mobDrop : mobDrops) {
            Validation.scope(
                    "Mob drop for " + mobDrop.getValues().getEntityType(),
                    () -> mobDrop.getValues().validate(this)
            );
        }

        for (CustomProjectile projectile : projectiles) {
            Validation.scope(
                    "Projectile " + projectile.getValues().getName(),
                    () -> projectile.getValues().validate(this, projectile.getValues().getName())
            );
        }
        validateUniqueIDs("projectile name", projectiles, projectile -> projectile.getValues().getName());

        for (ProjectileCover projectileCover : projectileCovers) {
            Validation.scope(
                    "Projectile cover " + projectileCover.getValues().getName(),
                    () -> projectileCover.getValues().validate(this, projectileCover.getValues().getName())
            );
        }
        validateUniqueIDs("projectile cover name", projectileCovers, projectileCover -> projectileCover.getValues().getName());

        for (CustomContainer container : containers) {
            Validation.scope(
                    "Container " + container.getValues().getName(),
                    () -> container.getValues().validate(this, container.getValues().getName())
            );
        }
        validateUniqueIDs("container name", containers, container -> container.getValues().getName());

        for (CustomFuelRegistry fuelRegistry : fuelRegistries) {
            Validation.scope(
                    "Fuel registry " + fuelRegistry.getValues().getName(),
                    () -> fuelRegistry.getValues().validate(this, fuelRegistry.getValues().getName())
            );
        }
        validateUniqueIDs("fuel registry name", fuelRegistries, fuelRegistry -> fuelRegistry.getValues().getName());

        for (EnergyTypeValues energyType : getEnergyTypes()) {
            Validation.scope(
                    "Energy type " + energyType.getName(),
                    () -> energyType.validateComplete(this, energyType.getId())
            );
        }
        validateUniqueIDs("energy type id", energyTypes, energyType -> energyType.getValues().getId());
        validateUniqueIDs("energy type name", energyTypes, energyType -> energyType.getValues().getName());

        for (CustomSoundTypeValues soundType : getSoundTypes()) {
            Validation.scope(
                    "Sound type " + soundType.getName(),
                    () -> soundType.validate(this, soundType.getId())
            );
        }
        validateUniqueIDs("sound type id", soundTypes, soundType -> soundType.getValues().getId());
        validateUniqueIDs("sound type name", soundTypes, soundType -> soundType.getValues().getName());

        for (CustomBlock block : blocks) {
            Validation.scope(
                    "Block " + block.getValues().getName(),
                    () -> block.getValues().validateComplete(this, block.getValues().getInternalID())
            );
        }
        validateUniqueIDs("block id", blocks, block -> block.getValues().getInternalID());
        validateUniqueIDs("block name", blocks, block -> block.getValues().getName());

        for (OreVeinGenerator oreVeinGenerator : oreVeinGenerators) {
            Validation.scope(
                    "Ore vein generator " + oreVeinGenerator,
                    () -> oreVeinGenerator.getValues().validate(this)
            );
        }

        for (TreeGenerator treeGenerator : treeGenerators) {
            Validation.scope(
                    "Tree generator " + treeGenerator,
                    () -> treeGenerator.getValues().validate(this)
            );
        }
    }

    public void setExportSettings(ExportSettingsValues newExportSettings) throws ValidationException, ProgrammingValidationException {
        newExportSettings.validate();
        this.exportSettings = newExportSettings;
        maybeCreateBackup();
    }

    public void addCombinedResourcepack(CombinedResourcepackValues newPack) throws ValidationException, ProgrammingValidationException {
        newPack.validate(this, null, null);
        this.combinedResourcepacks.add(new CombinedResourcepack(newPack));
        maybeCreateBackup();
    }

    public void changeCombinedResourcepack(
            CombinedResourcepackReference packToChange, CombinedResourcepackValues newPackValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(packToChange)) throw new ProgrammingValidationException("Pack to change is invalid");
        newPackValues.validate(this, packToChange.get().getName(), packToChange.get().getPriority());
        packToChange.getModel().setValues(newPackValues);
        maybeCreateBackup();
    }

    public void addTexture(BaseTextureValues newTexture) throws ValidationException, ProgrammingValidationException {
        newTexture.validateComplete(this, null);
        this.textures.add(new CustomTexture(newTexture));
        maybeCreateBackup();
    }

    public void changeTexture(TextureReference textureToChange, BaseTextureValues newTextureValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(textureToChange)) throw new ProgrammingValidationException("Texture to change is invalid");
        newTextureValues.validateComplete(this, textureToChange.get().getName());
        textureToChange.getModel().setValues(newTextureValues);
        maybeCreateBackup();
    }

    public void addArmorTexture(ArmorTextureValues newTexture) throws ValidationException, ProgrammingValidationException {
        newTexture.validate(this, null);
        this.armorTextures.add(new ArmorTexture(newTexture));
        maybeCreateBackup();
    }

    public void changeArmorTexture(
            ArmorTextureReference textureToChange, ArmorTextureValues newTextureValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(textureToChange)) throw new ProgrammingValidationException("Armor texture to change is invalid");
        newTextureValues.validate(this, textureToChange.get().getName());
        textureToChange.getModel().setValues(newTextureValues);
        maybeCreateBackup();
    }

    public int findFreeFancyPantsArmorRgb() {
        int candidateRgb = 0;
        whileLoop:
        while (true) {
            for (FancyPantsArmorTextureValues existing : getFancyPantsArmorTextures()) {
                if (existing.getRgb() == candidateRgb) {
                    candidateRgb += 1;
                    continue whileLoop;
                }
            }
            return candidateRgb;
        }
    }

    public void addFancyPantsArmorTexture(FancyPantsArmorTextureValues newTexture) throws ValidationException, ProgrammingValidationException {
        newTexture.validate(this, null);
        this.fancyPantsArmorTextures.add(new FancyPantsArmorTexture(newTexture));
        maybeCreateBackup();
    }

    public void changeFancyPantsArmorTexture(
            FancyPantsArmorTextureReference textureToChange, FancyPantsArmorTextureValues newTextureValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(textureToChange)) throw new ProgrammingValidationException("FP texture to change is invalid");
        newTextureValues.validate(this, textureToChange.get().getId());
        textureToChange.getModel().setValues(newTextureValues);
        maybeCreateBackup();
    }

    public void addItem(CustomItemValues newItem) throws ValidationException, ProgrammingValidationException {
        newItem.validateComplete(this, null);
        this.items.add(new CustomItem(newItem));
        maybeCreateBackup();
    }

    public void changeItem(ItemReference itemToChange, CustomItemValues newItemValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(itemToChange)) throw new ProgrammingValidationException("Item to change is invalid");
        newItemValues.validateComplete(this, itemToChange.get().getName());
        itemToChange.getModel().setValues(newItemValues);
        maybeCreateBackup();
    }

    public void addEquipmentSet(EquipmentSetValues newEquipmentSet) throws ValidationException, ProgrammingValidationException {
        newEquipmentSet.validate(this);
        this.equipmentSets.add(new EquipmentSet(newEquipmentSet));
        maybeCreateBackup();
    }

    public void changeEquipmentSet(
            EquipmentSetReference setToChange, EquipmentSetValues newSetValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(setToChange)) throw new ProgrammingValidationException("Equipment set is invalid");
        newSetValues.validate(this);
        setToChange.getModel().setValues(newSetValues);
        maybeCreateBackup();
    }

    public void addDamageSource(CustomDamageSourceValues newDamageSource) throws ValidationException, ProgrammingValidationException {
        newDamageSource.validateComplete(this, null);
        this.damageSources.add(new CustomDamageSource(newDamageSource));
        maybeCreateBackup();
    }

    public void changeDamageSource(
            CustomDamageSourceReference sourceToChange, CustomDamageSourceValues newSourceValues
    ) throws ValidationException, ProgrammingValidationException {
        newSourceValues.validateComplete(this, sourceToChange.get().getId());
        sourceToChange.getModel().setValues(newSourceValues);
        maybeCreateBackup();
    }

    public void addRecipe(CraftingRecipeValues newRecipe) throws ValidationException, ProgrammingValidationException {
        newRecipe.validate(this, null);
        this.craftingRecipes.add(new CustomCraftingRecipe(newRecipe));
        maybeCreateBackup();
    }

    public void changeRecipe(
            CraftingRecipeReference recipeToChange, CraftingRecipeValues newRecipeValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(recipeToChange)) throw new ProgrammingValidationException("Recipe to change is invalid");
        newRecipeValues.validate(this, recipeToChange);
        recipeToChange.getModel().setValues(newRecipeValues);
        maybeCreateBackup();
    }

    public void addUpgrade(UpgradeValues newUpgrade) throws ValidationException, ProgrammingValidationException {
        newUpgrade.validateComplete(this, null);
        this.upgrades.add(new Upgrade(newUpgrade));
        maybeCreateBackup();
    }

    public void changeUpgrade(
            UpgradeReference upgradeToChange, UpgradeValues newUpgradeValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(upgradeToChange)) throw new ProgrammingValidationException("Upgrade to change is invalid");
        newUpgradeValues.validateComplete(this, upgradeToChange.get().getId());
        upgradeToChange.getModel().setValues(newUpgradeValues);
        maybeCreateBackup();
    }

    public void addBlockDrop(BlockDropValues newBlockDrop) throws ValidationException, ProgrammingValidationException {
        newBlockDrop.validate(this);
        blockDrops.add(new BlockDrop(newBlockDrop));
        maybeCreateBackup();
    }

    public void changeBlockDrop(
            BlockDropReference toChange, BlockDropValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(toChange)) throw new ProgrammingValidationException("Block drop to change is invalid");
        newValues.validate(this);
        toChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addMobDrop(MobDropValues dropToAdd) throws ValidationException, ProgrammingValidationException {
        dropToAdd.validate(this);
        this.mobDrops.add(new MobDrop(dropToAdd));
        maybeCreateBackup();
    }

    public void changeMobDrop(MobDropReference dropToChange, MobDropValues newValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(dropToChange)) throw new ProgrammingValidationException("Mob drop to be changed is invalid");
        newValues.validate(this);
        dropToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addProjectile(CustomProjectileValues projectileToAdd) throws ValidationException, ProgrammingValidationException {
        projectileToAdd.validate(this, null);
        this.projectiles.add(new CustomProjectile(projectileToAdd));
        maybeCreateBackup();
    }

    public void changeProjectile(ProjectileReference projectileToChange, CustomProjectileValues newValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(projectileToChange)) throw new ProgrammingValidationException("Projectile to be changed is invalid");
        newValues.validate(this, projectileToChange.get().getName());
        projectileToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addProjectileCover(ProjectileCoverValues coverToAdd) throws ValidationException, ProgrammingValidationException {
        coverToAdd.validate(this, null);
        this.projectileCovers.add(new ProjectileCover(coverToAdd));
        maybeCreateBackup();
    }

    public void changeProjectileCover(
            ProjectileCoverReference coverToChange, ProjectileCoverValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(coverToChange)) throw new ProgrammingValidationException("Projectile cover to change is invalid");
        newValues.validate(this, coverToChange.get().getName());
        coverToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addContainer(CustomContainerValues containerToAdd) throws ValidationException, ProgrammingValidationException {
        containerToAdd.validate(this, null);
        this.containers.add(new CustomContainer(containerToAdd));
        maybeCreateBackup();
    }

    public void changeContainer(
            ContainerReference containerToChange, CustomContainerValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(containerToChange)) throw new ProgrammingValidationException("Container to change is invalid");
        newValues.validate(this, containerToChange.get().getName());
        containerToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addFuelRegistry(FuelRegistryValues registryToAdd) throws ValidationException, ProgrammingValidationException {
        registryToAdd.validate(this, null);
        this.fuelRegistries.add(new CustomFuelRegistry(registryToAdd));
        maybeCreateBackup();
    }

    public void changeFuelRegistry(
            FuelRegistryReference registryToChange, FuelRegistryValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(registryToChange)) throw new ProgrammingValidationException("Fuel registry to change is invalid");
        newValues.validate(this, registryToChange.get().getName());
        registryToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addEnergyType(EnergyTypeValues energyToAdd) throws ValidationException, ProgrammingValidationException {
        energyToAdd.validateComplete(this, null);
        this.energyTypes.add(new EnergyType(energyToAdd));
        maybeCreateBackup();
    }

    public void changeEnergyType(
            EnergyTypeReference energyToChange, EnergyTypeValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(energyToChange)) throw new ProgrammingValidationException("Energy type to change is invalid");
        newValues.validateComplete(this, energyToChange.get().getId());
        energyToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addSoundType(CustomSoundTypeValues soundToAdd) throws ValidationException, ProgrammingValidationException {
        soundToAdd.validate(this, null);
        this.soundTypes.add(new CustomSoundType(soundToAdd));
        maybeCreateBackup();
    }

    public void changeSoundType(
            SoundTypeReference soundToChange, CustomSoundTypeValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(soundToChange)) throw new ProgrammingValidationException("Sound type to change is invalid");
        newValues.validate(this, soundToChange.get().getId());
        soundToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    private int findFreeBlockId() throws ValidationException {
        for (int candidateId = BlockConstants.MIN_BLOCK_ID; candidateId <= BlockConstants.MAX_BLOCK_ID; candidateId++) {
            if (!this.getBlock(candidateId).isPresent()) return candidateId;
        }
        throw new ValidationException("Maximum number of custom blocks has been reached");
    }

    public void addBlock(CustomBlockValues newBlock) throws ValidationException, ProgrammingValidationException {
        newBlock.setInternalId(this.findFreeBlockId());
        newBlock.validateComplete(this, null);
        this.blocks.add(new CustomBlock(newBlock));
        maybeCreateBackup();
    }

    public void changeBlock(BlockReference blockToChange, CustomBlockValues newBlockValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(blockToChange)) throw new ProgrammingValidationException("Block to change is invalid");
        newBlockValues.validateComplete(this, blockToChange.get().getInternalID());
        blockToChange.getModel().setValues(newBlockValues);
        maybeCreateBackup();
    }

    public void addOreVeinGenerator(OreVeinGeneratorValues toAdd) throws ValidationException, ProgrammingValidationException {
        toAdd.validate(this);
        this.oreVeinGenerators.add(new OreVeinGenerator(toAdd));
        maybeCreateBackup();
    }

    public void changeOreVeinGenerator(
            OreVeinGeneratorReference generatorToChange, OreVeinGeneratorValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(generatorToChange)) throw new ProgrammingValidationException("Generator to change is invalid");
        newValues.validate(this);
        generatorToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    public void addTreeGenerator(TreeGeneratorValues toAdd) throws ValidationException, ProgrammingValidationException {
        toAdd.validate(this);
        this.treeGenerators.add(new TreeGenerator(toAdd));
        maybeCreateBackup();
    }

    public void changeTreeGenerator(
            TreeGeneratorReference generatorToChange, TreeGeneratorValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(generatorToChange)) throw new ProgrammingValidationException("Generator to change is invalid");
        newValues.validate(this);
        generatorToChange.getModel().setValues(newValues);
        maybeCreateBackup();
    }

    private <T> void removeModel(Collection<T> collection, T model) throws ValidationException, ProgrammingValidationException {
        if (model == null) throw new ProgrammingValidationException("Model is invalid");
        String errorMessage = null;

        if (!collection.remove(model)) throw new ProgrammingValidationException("Model no longer exists");
        try {
            this.validate();
        } catch (ValidationException | ProgrammingValidationException validation) {
            errorMessage = validation.getMessage();
        }

        if (errorMessage != null) {
            collection.add(model);
            throw new ValidationException(errorMessage);
        }

        maybeCreateBackup();
    }

    public void removeCombinedResourcepack(CombinedResourcepackReference packToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.combinedResourcepacks, packToRemove.getModel());
    }

    public void removeTexture(TextureReference textureToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.textures, textureToRemove.getModel());
    }

    public void removeArmorTexture(ArmorTextureReference textureToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.armorTextures, textureToRemove.getModel());
    }

    public void removeFancyPantsArmorTexture(
            FancyPantsArmorTextureReference textureToRemove
    ) throws ValidationException, ProgrammingValidationException {
        removeModel(this.fancyPantsArmorTextures, textureToRemove.getModel());
    }

    public void removeItem(ItemReference itemToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.items, itemToRemove.getModel());
        this.removedItemNames.add(itemToRemove.getModel().getValues().getName());
    }

    public void removeEquipmentSet(EquipmentSetReference setToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.equipmentSets, setToRemove.getModel());
    }

    public void removeDamageSource(CustomDamageSourceReference sourceToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.damageSources, sourceToRemove.getModel());
    }

    public void removeCraftingRecipe(CraftingRecipeReference recipeToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.craftingRecipes, recipeToRemove.getModel());
    }

    public void removeUpgrade(UpgradeReference upgradeToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.upgrades, upgradeToRemove.getModel());
    }

    public void removeBlockDrop(BlockDropReference blockDropToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.blockDrops, blockDropToRemove.getModel());
    }

    public void removeMobDrop(MobDropReference mobDropToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.mobDrops, mobDropToRemove.getModel());
    }

    public void removeProjectile(ProjectileReference projectileToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.projectiles, projectileToRemove.getModel());
    }

    public void removeProjectileCover(ProjectileCoverReference coverToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.projectileCovers, coverToRemove.getModel());
    }

    public void removeContainer(ContainerReference containerToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.containers, containerToRemove.getModel());
    }

    public void removeFuelRegistry(FuelRegistryReference registryToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.fuelRegistries, registryToRemove.getModel());
    }

    public void removeOreVeinGenerator(OreVeinGeneratorReference generatorToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.oreVeinGenerators, generatorToRemove.getModel());
    }

    public void removeTreeGenerator(TreeGeneratorReference generatorToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.treeGenerators, generatorToRemove.getModel());
    }

    public void removeEnergyType(EnergyTypeReference energyToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.energyTypes, energyToRemove.getModel());
    }

    public void removeSoundType(SoundTypeReference soundToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.soundTypes, soundToRemove.getModel());
    }

    public void removeBlock(BlockReference blockToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.blocks, blockToRemove.getModel());
    }

    public enum Side {
        EDITOR,
        PLUGIN
    }
}
