package nl.knokko.customitems.itemset;

import nl.knokko.customitems.bithelper.*;
import nl.knokko.customitems.container.KciContainer;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.durability.ItemDurabilityAssignments;
import nl.knokko.customitems.item.durability.ItemDurabilityClaim;
import nl.knokko.customitems.item.model.DefaultItemModel;
import nl.knokko.customitems.item.model.DefaultModelType;
import nl.knokko.customitems.item.model.ItemModel;
import nl.knokko.customitems.projectile.cover.ProjectileCover;
import nl.knokko.customitems.settings.ExportSettings;
import nl.knokko.customitems.texture.*;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.OutdatedItemSetException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemSet {

    static final Field[] MANAGER_FIELDS;

    static {
        List<Field> managerFields = new ArrayList<>();
        Field[] fields = ItemSet.class.getDeclaredFields();
        for (Field field : fields) {
            if (ModelManager.class.isAssignableFrom(field.getType())) managerFields.add(field);
        }

        MANAGER_FIELDS = managerFields.toArray(new Field[0]);
    }

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

        for (Field field : MANAGER_FIELDS) {
            try {
                ModelManager<?, ?> destination = (ModelManager<?, ?>) field.get(result);
                ModelManager<?, ?> primaryManager = (ModelManager<?, ?>) field.get(primary);
                ModelManager<?, ?> secondaryManager = (ModelManager<?, ?>) field.get(secondary);
                destination.combineUnchecked(primaryManager, secondaryManager);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        for (String deletedItem : primary.removedItemNames) {
            if (secondary.items.get(deletedItem).isPresent()) {
                throw new ValidationException("The secondary set has item " + deletedItem + ", which is removed in the primary set");
            }
        }
        for (String deletedItem : secondary.removedItemNames) {
            if (primary.items.get(deletedItem).isPresent()) {
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
    Collection<IntBasedReference<?>> intReferences;
    Collection<StringBasedReference<?>> stringReferences;
    Collection<UUIDBasedReference<?>> uuidReferences;
    boolean finishedLoading;
    byte loadEncoding;
    // <---- END OF INTERNAL USE ---->

    private long exportTime;

    private ExportSettings exportSettings = new ExportSettings(false);
    public final CombinedResourcepackManager combinedResourcepacks = new CombinedResourcepackManager(this);
    public final TextureManager textures = new TextureManager(this);
    public final ArmorTextureManager armorTextures = new ArmorTextureManager(this);
    public final FancyPantsManager fancyPants = new FancyPantsManager(this);
    public final ItemManager items = new ItemManager(this);
    public final EquipmentSetManager equipmentSets = new EquipmentSetManager(this);
    public final DamageSourceManager damageSources = new DamageSourceManager(this);
    public final CraftingRecipeManager craftingRecipes = new CraftingRecipeManager(this);
    public final CookingRecipeManager cookingRecipes = new CookingRecipeManager(this);
    public final SmithingRecipeManager smithingRecipes = new SmithingRecipeManager(this);
    public final UpgradeManager upgrades = new UpgradeManager(this);
    public final BlockDropManager blockDrops = new BlockDropManager(this);
    public final MobDropManager mobDrops = new MobDropManager(this);
    public final ContainerManager containers = new ContainerManager(this);
    public final FuelRegistryManager fuelRegistries = new FuelRegistryManager(this);
    public final EnergyTypeManager energyTypes = new EnergyTypeManager(this);
    public final SoundTypeManager soundTypes = new SoundTypeManager(this);
    public final ProjectileManager projectiles = new ProjectileManager(this);
    public final ProjectileCoverManager projectileCovers = new ProjectileCoverManager(this);
    public final BlockManager blocks = new BlockManager(this);
    public final OreGeneratorManager oreGenerators = new OreGeneratorManager(this);
    public final TreeGeneratorManager treeGenerators = new TreeGeneratorManager(this);
    Collection<String> removedItemNames = new ArrayList<>();

    // When non-null, this function should be called occasionally
    public Consumer<ItemSet> createBackup;

    final Side side;

    public ItemSet(Side side) {
        this.side = Objects.requireNonNull(side);
        this.finishedLoading = true;
    }

    public ItemSet(
            BitInput input, Side side, boolean allowOutdated
    ) throws IntegrityException, UnknownEncodingException, OutdatedItemSetException {
        Checks.notNull(side);
        this.side = side;
        load(input, allowOutdated);
    }

    private Collection<ModelManager<?, ?>> getAllManagers() {
        return Arrays.stream(MANAGER_FIELDS).map(field -> {
            try {
                return (ModelManager<?, ?>) field.get(this);
            } catch (IllegalAccessException e) {
                throw new Error(e);
            }
        }).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        if (!removedItemNames.isEmpty()) return false;
        for (ModelManager<?, ?> manager : getAllManagers()) {
            if (!manager.isEmpty()) return false;
        }

        return true;
    }

    void maybeCreateBackup() {
        Consumer<ItemSet> createBackup = this.createBackup;
        if (createBackup != null && Math.random() < 0.2) {
            createBackup.accept(this);
        }
    }

    public void assignContainerOverlayCharacters() {
        int nextOverlayChar = KciContainer.OVERLAY_BASE_CHAR;

        for (KciContainer container : containers) {
            if (container.getOverlayTexture() != null) {
                nextOverlayChar += 1;
                container.setOverlayChar((char) nextOverlayChar);
            }
        }
    }

    public Map<KciItemType, ItemDurabilityAssignments> assignInternalItemDamages() throws ValidationException {
        Map<KciItemType, ItemDurabilityAssignments> assignmentMap = new EnumMap<>(KciItemType.class);

        Map<KciItemType, Set<Short>> lockedDamageAssignments = new EnumMap<>(KciItemType.class);
        for (KciItem item : items) {
            if (!item.shouldUpdateAutomatically() && item.getItemDamage() > 0) {
                Set<Short> lockedAssignments = lockedDamageAssignments.computeIfAbsent(item.getItemType(), k -> new HashSet<>());
                if (!lockedAssignments.contains(item.getItemDamage())) {
                    ItemDurabilityAssignments assignments = assignmentMap.computeIfAbsent(item.getItemType(), k -> new ItemDurabilityAssignments());

                    List<BowTextureEntry> pullTextures = null;
                    if (item.getTexture() instanceof BowTexture) {
                        pullTextures = ((BowTexture) item.getTexture()).getPullTextures();
                    } else if (item.getTexture() instanceof CrossbowTexture) {
                        pullTextures = ((CrossbowTexture) item.getTexture()).getPullTextures();
                    }

                    ItemDurabilityClaim lockedClaim = new ItemDurabilityClaim(
                            "customitems/" + item.getName(), item.getItemDamage(),
                            pullTextures, !(item.getModel() instanceof DefaultItemModel)
                    );
                    assignments.claimList.add(lockedClaim);
                    lockedAssignments.add(item.getItemDamage());
                }
            }
        }

        for (KciItem originalItem : items) {
            KciItem item = originalItem.copy(true);
            if (item.shouldUpdateAutomatically() || item.getItemDamage() <= 0) {
                KciItemType itemType = item.getItemType();
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
                    if (itemType == KciItemType.BOW) {
                        pullTextures = ((BowTexture) item.getTexture()).getPullTextures();
                    } else if (itemType == KciItemType.CROSSBOW) {
                        pullTextures = ((CrossbowTexture) item.getTexture()).getPullTextures();
                    }

                    assignments.claimList.add(new ItemDurabilityClaim(
                            resourcePath, nextItemDamage, pullTextures, !(item.getModel() instanceof DefaultItemModel)
                    ));

                    if (canReuseModel) {
                        assignments.textureReuseMap.put(item.getTexture().getName(), nextItemDamage);
                    }
                }

                this.items.getReference(originalItem.getName()).getModel().setValues(item);
            }
        }

        for (ProjectileCover originalCover : projectileCovers) {
            ProjectileCover cover = originalCover.copy(true);
            KciItemType itemType = cover.getItemType();

            ItemDurabilityAssignments assignments = assignmentMap.get(itemType);
            if (assignments == null) {
                assignments = new ItemDurabilityAssignments();
                assignmentMap.put(itemType, assignments);
            }

            short itemDamage = assignments.getNextItemDamage(itemType, exportSettings.getMcVersion());
            cover.setItemDamage(itemDamage);
            String resourcePath = "customprojectiles/" + cover.getName();
            assignments.claimList.add(new ItemDurabilityClaim(resourcePath, itemDamage, null, true));

            projectileCovers.getReference(originalCover.getName()).getModel().setValues(cover);
        }

        for (ItemDurabilityAssignments assignments : assignmentMap.values()) {
            assignments.claimList.sort(Comparator.comparingInt(a -> a.itemDamage));
        }

        return assignmentMap;
    }

    public void save(BitOutput output, Side targetSide) {
        output.addByte(SetEncoding.ENCODING_13);

        ByteArrayBitOutput checkedOutput = new ByteArrayBitOutput();
        saveContent(checkedOutput, targetSide);
        byte[] contentArray = checkedOutput.getBytes();
        long hash = computeHash(contentArray);

        output.addLong(hash);
        output.addByteArray(contentArray);
    }

    private void saveContent(BitOutput output, Side targetSide) {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);

        output.addLong(System.currentTimeMillis());
        exportSettings.save(output);

        textures.save(output, threadPool, targetSide);
        armorTextures.save(output, threadPool, targetSide);
        fancyPants.save(output, threadPool, targetSide);
        combinedResourcepacks.save(output, threadPool, targetSide);
        projectileCovers.save(output, threadPool, targetSide);
        projectiles.save(output, threadPool, targetSide);
        items.save(output, threadPool, targetSide);
        equipmentSets.save(output, threadPool, targetSide);
        damageSources.save(output, threadPool, targetSide);
        blocks.save(output, threadPool, targetSide);
        oreGenerators.save(output, threadPool, targetSide);
        treeGenerators.save(output, threadPool, targetSide);
        craftingRecipes.save(output, threadPool, targetSide);
        upgrades.save(output, threadPool, targetSide);
        blockDrops.save(output, threadPool, targetSide);
        mobDrops.save(output, threadPool, targetSide);
        fuelRegistries.save(output, threadPool, targetSide);
        energyTypes.save(output, threadPool, targetSide);
        soundTypes.save(output, threadPool, targetSide);
        containers.save(output, threadPool, targetSide);
        output.addInt(removedItemNames.size());
        for (String removed : removedItemNames) output.addString(removed);
        cookingRecipes.save(output, threadPool, targetSide);
        smithingRecipes.save(output, threadPool, targetSide);

        threadPool.shutdown();
    }

    private void load(
            BitInput input, boolean allowOutdated
    ) throws IntegrityException, UnknownEncodingException, OutdatedItemSetException {
        this.intReferences = new ArrayList<>();
        this.stringReferences = new ArrayList<>();
        this.uuidReferences = new ArrayList<>();

        byte encoding = input.readByte();
        this.loadEncoding = encoding;
        if (!allowOutdated && encoding < SetEncoding.ENCODING_11) throw new OutdatedItemSetException();
        if (encoding == SetEncoding.ENCODING_1) {
            load1(input);
        } else if (encoding == SetEncoding.ENCODING_2) {
            load2(input);
        } else if (encoding == SetEncoding.ENCODING_3) {
            load3(input);
        } else if (encoding == SetEncoding.ENCODING_4) {
            load4(input);
        } else if (encoding == SetEncoding.ENCODING_5) {
            load5(input);
        } else if (encoding == SetEncoding.ENCODING_6) {
            load6(input);
        } else if (encoding == SetEncoding.ENCODING_7) {
            load7(input);
        } else if (encoding == SetEncoding.ENCODING_8) {
            load8(input);
        } else if (encoding == SetEncoding.ENCODING_9) {
            load9(input);
        } else if (encoding == SetEncoding.ENCODING_10) {
            load10(input);
        } else if (encoding == SetEncoding.ENCODING_11) {
            load11(input);
        } else if (encoding == SetEncoding.ENCODING_12) {
            load12(input);
        } else if (encoding == SetEncoding.ENCODING_13) {
            load13(input);
        } else {
            throw new UnknownEncodingException("ItemSet", encoding);
        }
        finishedLoading = true;
        this.loadEncoding = -1;

        // Ensure that all references find their model (this must happen before the user can rename models)
        for (IntBasedReference<?> intReference : intReferences) intReference.get();
        for (StringBasedReference<?> stringReference : stringReferences) stringReference.get();
        for (UUIDBasedReference<?> uuidReference : uuidReferences) uuidReference.get();
        intReferences = null;
        stringReferences = null;
        uuidReferences = null;
    }

    private void loadExportTime(BitInput input) {
        if (side == Side.PLUGIN) {
            this.exportTime = input.readLong();
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
        exportTime = generateFakeExportTime();
        if (side == Side.EDITOR) textures.load(input, false, false);
        items.loadWithoutModel(input);
        craftingRecipes.load(input);
    }

    private void load2(BitInput input) throws UnknownEncodingException {
        exportTime = generateFakeExportTime();
        if (side == Side.EDITOR) textures.load(input, true, false);
        items.loadWithoutModel(input);
        craftingRecipes.load(input);
    }

    private void load3(BitInput input) throws UnknownEncodingException {
        load2(input);
        blockDrops.load(input);
        mobDrops.load(input);
    }

    private void load4(BitInput input) throws UnknownEncodingException {
        exportTime = generateFakeExportTime();
        if (side == Side.EDITOR) textures.load(input, true, false);
        items.load(input);
        craftingRecipes.load(input);
        blockDrops.load(input);
        mobDrops.load(input);
    }

    private void load5(BitInput input) throws UnknownEncodingException {
        exportTime = generateFakeExportTime();
        if (side == Side.EDITOR) textures.load(input, true, false);
        projectileCovers.load(input);
        projectiles.load(input);
        items.load(input);
        craftingRecipes.load(input);
        blockDrops.load(input);
        mobDrops.load(input);
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
            fuelRegistries.load(input);
            containers.load(input);
        });
    }

    private void load8(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadExportTime(input);
            if (side == Side.EDITOR) {
                textures.load(input, true, true);
                armorTextures.load(input);
            }
            projectileCovers.load(input);
            projectiles.load(input);
            items.load(input);
            craftingRecipes.load(input);
            blockDrops.load(input);
            mobDrops.load(input);
            fuelRegistries.load(input);
            containers.load(input);
            loadDeletedItemNames(input);
        });
    }

    private void load9(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadExportTime(input);
            if (side == Side.EDITOR) {
                textures.load(input, true, true);
                armorTextures.load(input);
            }
            projectileCovers.load(input);
            projectiles.load(input);
            items.load(input);
            blocks.load(input);
            craftingRecipes.load(input);
            blockDrops.load(input);
            mobDrops.load(input);
            fuelRegistries.load(input);
            containers.load(input);
            loadDeletedItemNames(input);
        });
    }

    private void load10(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadExportTime(input);
            if (side == Side.EDITOR) {
                textures.load(input, true, true);
                armorTextures.load(input);
            }
            projectileCovers.load(input);
            projectiles.load(input);
            items.load(input);
            equipmentSets.load(input);
            blocks.load(input);
            oreGenerators.load(input);
            treeGenerators.load(input);
            craftingRecipes.load(input);
            blockDrops.load(input);
            mobDrops.load(input);
            fuelRegistries.load(input);
            energyTypes.load(input);
            soundTypes.load(input);
            containers.load(input);
            loadDeletedItemNames(input);
        });
    }

    private void loadContent11(BitInput input) throws UnknownEncodingException {
        this.exportSettings = ExportSettings.load(input);
        loadExportTime(input);
        if (side == Side.EDITOR) {
            combinedResourcepacks.load(input);
            textures.load(input, true, true);
            armorTextures.load(input);
        }
        fancyPants.load(input);
        projectileCovers.load(input);
        projectiles.load(input);
        items.load(input);
        equipmentSets.load(input);
        damageSources.load(input);
        blocks.load(input);
        oreGenerators.load(input);
        treeGenerators.load(input);
        craftingRecipes.load(input);
        upgrades.load(input);
        blockDrops.load(input);
        mobDrops.load(input);
        fuelRegistries.load(input);
        energyTypes.load(input);
        soundTypes.load(input);
        containers.load(input);
        loadDeletedItemNames(input);
    }

    private void load11(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadContent11(input);
        });
    }

    private void loadContent12(BitInput input) throws UnknownEncodingException {
        loadContent11(input);

        cookingRecipes.load(input);
        smithingRecipes.load(input);
    }

    private void load12(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadContent12(input);
        });
    }

    private void loadContent13(BitInput input) throws UnknownEncodingException {
        exportTime = input.readLong();
        exportSettings = ExportSettings.load(input);
        textures.load(input, true, true);
        armorTextures.load(input);
        fancyPants.load(input);
        combinedResourcepacks.load(input);
        projectileCovers.load(input);
        projectiles.load(input);
        items.load(input);
        equipmentSets.load(input);
        damageSources.load(input);
        blocks.load(input);
        oreGenerators.load(input);
        treeGenerators.load(input);
        craftingRecipes.load(input);
        upgrades.load(input);
        blockDrops.load(input);
        mobDrops.load(input);
        fuelRegistries.load(input);
        energyTypes.load(input);
        soundTypes.load(input);
        containers.load(input);
        loadDeletedItemNames(input);
        cookingRecipes.load(input);
        smithingRecipes.load(input);
    }

    private void load13(BitInput rawInput) throws IntegrityException, UnknownEncodingException {
        loadWithIntegrityCheck(rawInput, (input, hash) -> {
            loadContent13(input);
        });
    }

    public Side getSide() {
        return side;
    }

    public ExportSettings getExportSettings() {
        return exportSettings;
    }

    public long getExportTime() {
        return exportTime;
    }

    public Set<String> getRemovedItemNames() {
        return new HashSet<>(removedItemNames);
    }

    public boolean hasItemBeenDeleted(String itemName) {
        return removedItemNames.contains(itemName);
    }

    public void validateExportVersion(int version) throws ValidationException, ProgrammingValidationException {
        // Avoid annoying NullPointerException's by first doing a general validation check
        validate();

        for (ModelManager<?, ?> manager : getAllManagers()) manager.validateExportVersion(version);
    }

    void validate() throws ValidationException, ProgrammingValidationException {
        for (ModelManager<?, ?> manager : getAllManagers()) manager.validate();
    }

    public void setExportSettings(ExportSettings newExportSettings) throws ValidationException, ProgrammingValidationException {
        newExportSettings.validate();
        this.exportSettings = newExportSettings;
        maybeCreateBackup();
    }

    public enum Side {
        EDITOR,
        PLUGIN
    }
}
