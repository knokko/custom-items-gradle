package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.*;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.SCustomContainer;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.fuel.SFuelRegistry;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.encoding.SetEncoding;
import nl.knokko.customitems.item.*;
import nl.knokko.customitems.item.durability.ItemDurabilityAssignments;
import nl.knokko.customitems.item.durability.ItemDurabilityClaim;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.SCustomProjectile;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.SProjectileCover;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.CustomCraftingRecipe;
import nl.knokko.customitems.texture.*;
import nl.knokko.customitems.trouble.IntegrityException;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.*;
import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.bithelper.ByteArrayBitInput;
import nl.knokko.customitems.bithelper.ByteArrayBitOutput;

import java.util.*;
import java.util.function.Function;

public class SItemSet {

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

    public static SItemSet combine(SItemSet primary, SItemSet secondary) throws ValidationException {

        SItemSet result = new SItemSet(Side.EDITOR);

        result.textures.addAll(primary.textures);
        result.textures.addAll(secondary.textures);
        result.armorTextures.addAll(primary.armorTextures);
        result.armorTextures.addAll(secondary.armorTextures);

        result.items.addAll(primary.items);
        result.items.addAll(secondary.items);

        result.craftingRecipes.addAll(primary.craftingRecipes);
        result.craftingRecipes.addAll(secondary.craftingRecipes);

        result.blockDrops.addAll(primary.blockDrops);
        result.blockDrops.addAll(secondary.blockDrops);
        result.mobDrops.addAll(primary.mobDrops);
        result.mobDrops.addAll(secondary.mobDrops);

        result.containers.addAll(primary.containers);
        result.containers.addAll(secondary.containers);
        result.fuelRegistries.addAll(primary.fuelRegistries);
        result.fuelRegistries.addAll(secondary.fuelRegistries);

        result.projectiles.addAll(primary.projectiles);
        result.projectiles.addAll(secondary.projectiles);
        result.projectileCovers.addAll(primary.projectileCovers);
        result.projectileCovers.addAll(secondary.projectileCovers);

        result.blocks.addAll(primary.blocks);
        if (!secondary.blocks.isEmpty()) throw new ValidationException("The secondary item set can't have blocks");

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
    boolean finishedLoading;
    // <---- END OF INTERNAL USE ---->

    private long exportTime;

    Collection<CustomTexture> textures;
    Collection<ArmorTexture> armorTextures;
    Collection<SCustomItem> items;
    Collection<CustomCraftingRecipe> craftingRecipes;
    Collection<SBlockDrop> blockDrops;
    Collection<MobDrop> mobDrops;
    Collection<SCustomContainer> containers;
    Collection<SFuelRegistry> fuelRegistries;
    Collection<SCustomProjectile> projectiles;
    Collection<SProjectileCover> projectileCovers;
    Collection<CustomBlock> blocks;

    Collection<String> removedItemNames;

    final Side side;

    public SItemSet(Side side) {
        Checks.notNull(side);
        this.side = side;
        initialize();
    }

    public SItemSet(BitInput input, Side side) throws IntegrityException, UnknownEncodingException {
        Checks.notNull(side);
        this.side = side;
        load(input);
    }

    private void initialize() {
        textures = new ArrayList<>();
        armorTextures = new ArrayList<>();
        items = new ArrayList<>();
        craftingRecipes = new ArrayList<>();
        blockDrops = new ArrayList<>();
        mobDrops = new ArrayList<>();
        blocks = new ArrayList<>();
        containers = new ArrayList<>();
        fuelRegistries = new ArrayList<>();
        projectiles = new ArrayList<>();
        projectileCovers = new ArrayList<>();

        removedItemNames = new ArrayList<>();

        finishedLoading = true;
    }

    public Map<CustomItemType, ItemDurabilityAssignments> assignInternalItemDamages() throws ValidationException {
        Map<CustomItemType, ItemDurabilityAssignments> assignmentMap = new EnumMap<>(CustomItemType.class);

        for (SCustomItem itemModel : items) {
            CustomItemValues item = itemModel.cloneValues();
            CustomItemType itemType = item.getItemType();
            ItemDurabilityAssignments assignments = assignmentMap.get(itemType);
            if (assignments == null) {
                assignments = new ItemDurabilityAssignments();
                assignmentMap.put(itemType, assignments);
            }

            boolean canReuseModel = item.getCustomModel() == null && itemType.hasSimpleModel;
            boolean reuseExistingModel = false;
            if (canReuseModel) {
                Short existingItemDamage = assignments.textureReuseMap.get(item.getTexture().getName());
                if (existingItemDamage != null) {
                    item.setItemDamage(existingItemDamage);
                    reuseExistingModel = true;
                }
            }

            if (!reuseExistingModel) {
                short nextItemDamage = assignments.getNextItemDamage(itemType);
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

        for (SProjectileCover coverModel : projectileCovers) {
            ProjectileCoverValues cover = coverModel.cloneValues();
            CustomItemType itemType = cover.getItemType();

            ItemDurabilityAssignments assignments = assignmentMap.get(itemType);
            if (assignments == null) {
                assignments = new ItemDurabilityAssignments();
                assignmentMap.put(itemType, assignments);
            }

            short itemDamage = assignments.getNextItemDamage(itemType);
            cover.setItemDamage(itemDamage);
            String resourcePath = "customprojectiles/" + cover.getName();
            assignments.claimList.add(new ItemDurabilityClaim(resourcePath, itemDamage, null));

            coverModel.setValues(cover);
        }

        return assignmentMap;
    }

    public void save(BitOutput output, Side targetSide) {
        output.addByte(SetEncoding.ENCODING_9);

        ByteArrayBitOutput checkedOutput = new ByteArrayBitOutput();
        saveContent(checkedOutput, targetSide);
        byte[] contentArray = checkedOutput.getBytes();
        long hash = computeHash(contentArray);

        output.addLong(hash);
        output.addByteArray(contentArray);
    }

    private void saveContent(BitOutput output, Side targetSide) {
        if (targetSide == Side.EDITOR) {
            output.addInt(textures.size());
            for (CustomTexture texture : textures) {
                texture.getValues().save(output);
            }

            output.addInt(armorTextures.size());
            for (ArmorTexture armorTexture : armorTextures) {
                armorTexture.getValues().save(output);
            }
        } else {
            output.addLong(System.currentTimeMillis());
        }

        output.addInt(projectileCovers.size());
        for (SProjectileCover projectileCover : projectileCovers) {
            projectileCover.getValues().save(output, targetSide);
        }

        output.addInt(projectiles.size());
        for (SCustomProjectile projectile : projectiles) {
            projectile.getValues().save(output);
        }

        output.addInt(items.size());
        for (SCustomItem item : items) {
            item.getValues().save(output, targetSide);
        }

        output.addInt(blocks.size());
        for (CustomBlock block : blocks) {
            output.addInt(block.getValues().getInternalID());
            block.getValues().save(output);
        }

        output.addInt(craftingRecipes.size());
        for (CustomCraftingRecipe recipe : craftingRecipes) {
            recipe.getValues().save(output);
        }

        output.addInt(blockDrops.size());
        for (SBlockDrop drop : blockDrops) {
            drop.getValues().save(output);
        }

        output.addInt(mobDrops.size());
        for (MobDrop drop : mobDrops) {
            drop.getValues().save(output);
        }

        output.addInt(fuelRegistries.size());
        for (SFuelRegistry fuelRegistry : fuelRegistries) {
            fuelRegistry.getValues().save(output);
        }

        output.addInt(containers.size());
        for (SCustomContainer container : containers) {
            container.getValues().save(output);
        }

        output.addInt(removedItemNames.size());
        for (String removedItemName : removedItemNames) {
            output.addString(removedItemName);
        }
    }

    private void load(BitInput input) throws IntegrityException, UnknownEncodingException {
        this.intReferences = new ArrayList<>();
        this.stringReferences = new ArrayList<>();

        byte encoding = input.readByte();
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
        intReferences = null;
        stringReferences = null;
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
        // Nothing to be done until the next encoding is known
    }

    private void loadExportTime(BitInput input) {
        if (side == Side.PLUGIN) {
            this.exportTime = input.readLong();
        }
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

    private void loadItems(BitInput input, boolean checkCustomModel) throws UnknownEncodingException {
        int numItems = input.readInt();
        this.items = new ArrayList<>(numItems);
        for (int counter = 0; counter < numItems; counter++) {
            this.items.add(new SCustomItem(CustomItemValues.load(input, this, checkCustomModel)));
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
            this.blockDrops.add(new SBlockDrop(BlockDropValues.load(input, this)));
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
            this.projectileCovers.add(new SProjectileCover(ProjectileCoverValues.load(input, this)));
        }
    }

    private void loadProjectiles(BitInput input) throws UnknownEncodingException {
        int numProjectiles = input.readInt();
        this.projectiles = new ArrayList<>(numProjectiles);
        for (int counter = 0; counter < numProjectiles; counter++) {
            this.projectiles.add(new SCustomProjectile(CustomProjectileValues.load(input, this)));
        }
    }

    private void loadFuelRegistries(BitInput input) throws UnknownEncodingException {
        int numFuelRegistries = input.readInt();
        this.fuelRegistries = new ArrayList<>(numFuelRegistries);
        for (int counter = 0; counter < numFuelRegistries; counter++) {
            this.fuelRegistries.add(new SFuelRegistry(FuelRegistryValues.load(input, this)));
        }
    }

    private void loadContainers(BitInput input) throws UnknownEncodingException {
        int numContainers = input.readInt();
        this.containers = new ArrayList<>(numContainers);
        for (int counter = 0; counter < numContainers; counter++) {
            this.containers.add(new SCustomContainer(CustomContainerValues.load(input, this)));
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

    public Side getSide() {
        return side;
    }

    public long getExportTime() {
        return exportTime;
    }

    public CustomTexturesView getTextures() {
        return new CustomTexturesView(textures);
    }

    public ArmorTexturesView getArmorTextures() {
        return new ArmorTexturesView(armorTextures);
    }

    public CustomItemsView getItems() {
        return new CustomItemsView(items);
    }

    public CustomRecipesView getCraftingRecipes() {
        return new CustomRecipesView(craftingRecipes);
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

    public CustomBlocksView getBlocks() {
        return new CustomBlocksView(blocks);
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

    public ItemReference getItemReference(String itemName) throws NoSuchElementException {
        if (finishedLoading) {
            return new ItemReference(CollectionHelper.find(items, item -> item.getValues().getName(), itemName).get());
        } else {
            return new ItemReference(itemName, this);
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

    public Optional<BaseTextureValues> getTexture(String textureName) {
        return CollectionHelper.find(textures, texture -> texture.getValues().getName(), textureName).map(CustomTexture::getValues);
    }

    public Optional<ArmorTextureValues> getArmorTexture(String textureName) {
        return CollectionHelper.find(armorTextures, texture -> texture.getValues().getName(), textureName).map(ArmorTexture::getValues);
    }

    public Optional<CustomItemValues> getItem(String itemName) {
        return CollectionHelper.find(items, item -> item.getValues().getName(), itemName).map(SCustomItem::getValues);
    }

    public Optional<CustomBlockValues> getBlock(int blockInternalId) {
        return CollectionHelper.find(blocks, block -> block.getValues().getInternalID(), blockInternalId).map(CustomBlock::getValues);
    }

    public Optional<CustomBlockValues> getBlock(String blockName) {
        return CollectionHelper.find(blocks, block -> block.getValues().getName(), blockName).map(CustomBlock::getValues);
    }

    public Optional<CustomContainerValues> getContainer(String containerName) {
        return CollectionHelper.find(containers, container -> container.getValues().getName(), containerName).map(SCustomContainer::getValues);
    }

    public Optional<FuelRegistryValues> getFuelRegistry(String registryName) {
        return CollectionHelper.find(fuelRegistries, registry -> registry.getValues().getName(), registryName).map(SFuelRegistry::getValues);
    }

    public Optional<CustomProjectileValues> getProjectile(String projectileName) {
        return CollectionHelper.find(projectiles, projectile -> projectile.getValues().getName(), projectileName).map(SCustomProjectile::getValues);
    }

    public Optional<ProjectileCoverValues> getProjectileCover(String coverName) {
        return CollectionHelper.find(projectileCovers, cover -> cover.getValues().getName(), coverName).map(SProjectileCover::getValues);
    }

    private <T> boolean isReferenceValid(Collection<T> collection, T model) {
        if (model == null) return false;
        return collection.contains(model);
    }

    public boolean isReferenceValid(TextureReference reference) {
        return isReferenceValid(textures, reference.getModel());
    }

    public boolean isReferenceValid(ArmorTextureReference reference) {
        return isReferenceValid(armorTextures, reference.getModel());
    }

    public boolean isReferenceValid(ItemReference reference) {
        return isReferenceValid(items, reference.getModel());
    }

    public boolean isReferenceValid(CraftingRecipeReference reference) {
        return isReferenceValid(craftingRecipes, reference.getModel());
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

    public boolean isReferenceValid(ContainerReference reference) {
        return isReferenceValid(containers, reference.getModel());
    }

    public boolean isReferenceValid(FuelRegistryReference reference) {
        return isReferenceValid(fuelRegistries, reference.getModel());
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

        for (CustomItemValues item : getItems()) {
            Validation.scope(
                    "Item " + item.getName(),
                    () -> item.validateExportVersion(version)
            );
        }

        for (CraftingRecipeValues recipe : getCraftingRecipes()) {
            Validation.scope(
                    "Recipe for " + recipe.getResult(),
                    () -> recipe.validateExportVersion(version)
            );
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

        for (SCustomItem item : items) {
            Validation.scope(
                    "Item " + item.getValues().getName(),
                    () -> item.getValues().validateComplete(this, item.getValues().getName())
            );
        }
        validateUniqueIDs("item name", items, item -> item.getValues().getName());

        for (CustomCraftingRecipe recipe : craftingRecipes) {
            Validation.scope(
                    "Recipe for " + recipe.getValues().getResult(),
                    () -> recipe.getValues().validate(this, new CraftingRecipeReference(recipe))
            );
        }

        for (SBlockDrop blockDrop : blockDrops) {
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

        for (SCustomProjectile projectile : projectiles) {
            Validation.scope(
                    "Projectile " + projectile.getValues().getName(),
                    () -> projectile.getValues().validate(this, projectile.getValues().getName())
            );
        }
        validateUniqueIDs("projectile name", projectiles, projectile -> projectile.getValues().getName());

        for (SProjectileCover projectileCover : projectileCovers) {
            Validation.scope(
                    "Projectile cover " + projectileCover.getValues().getName(),
                    () -> projectileCover.getValues().validate(this, projectileCover.getValues().getName())
            );
        }
        validateUniqueIDs("projectile cover name", projectileCovers, projectileCover -> projectileCover.getValues().getName());

        for (SCustomContainer container : containers) {
            Validation.scope(
                    "Container " + container.getValues().getName(),
                    () -> container.getValues().validate(this, container.getValues().getName())
            );
        }
        validateUniqueIDs("container name", containers, container -> container.getValues().getName());

        for (SFuelRegistry fuelRegistry : fuelRegistries) {
            Validation.scope(
                    "Fuel registry " + fuelRegistry.getValues().getName(),
                    () -> fuelRegistry.getValues().validate(this, fuelRegistry.getValues().getName())
            );
        }
        validateUniqueIDs("fuel registry name", fuelRegistries, fuelRegistry -> fuelRegistry.getValues().getName());

        for (CustomBlock block : blocks) {
            Validation.scope(
                    "Block " + block.getValues().getName(),
                    () -> block.getValues().validateComplete(this, block.getValues().getInternalID())
            );
        }
        validateUniqueIDs("block id", blocks, block -> block.getValues().getInternalID());
        validateUniqueIDs("block name", blocks, block -> block.getValues().getName());
    }

    public void addTexture(BaseTextureValues newTexture) throws ValidationException, ProgrammingValidationException {
        newTexture.validateComplete(this, null);
        this.textures.add(new CustomTexture(newTexture));
    }

    public void changeTexture(TextureReference textureToChange, BaseTextureValues newTextureValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(textureToChange)) throw new ProgrammingValidationException("Texture to change is invalid");
        newTextureValues.validateComplete(this, textureToChange.get().getName());
        textureToChange.getModel().setValues(newTextureValues);
    }

    public void addArmorTexture(ArmorTextureValues newTexture) throws ValidationException, ProgrammingValidationException {
        newTexture.validate(this, null);
        this.armorTextures.add(new ArmorTexture(newTexture));
    }

    public void changeArmorTexture(
            ArmorTextureReference textureToChange, ArmorTextureValues newTextureValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(textureToChange)) throw new ProgrammingValidationException("Armor texture to change is invalid");
        newTextureValues.validate(this, textureToChange.get().getName());
        textureToChange.getModel().setValues(newTextureValues);
    }

    public void addItem(CustomItemValues newItem) throws ValidationException, ProgrammingValidationException {
        newItem.validateComplete(this, null);
        this.items.add(new SCustomItem(newItem));
    }

    public void changeItem(ItemReference itemToChange, CustomItemValues newItemValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(itemToChange)) throw new ProgrammingValidationException("Item to change is invalid");
        newItemValues.validateComplete(this, itemToChange.get().getName());
        itemToChange.getModel().setValues(newItemValues);
    }

    public void addRecipe(CraftingRecipeValues newRecipe) throws ValidationException, ProgrammingValidationException {
        newRecipe.validate(this, null);
        this.craftingRecipes.add(new CustomCraftingRecipe(newRecipe));
    }

    public void changeRecipe(
            CraftingRecipeReference recipeToChange, CraftingRecipeValues newRecipeValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(recipeToChange)) throw new ProgrammingValidationException("Recipe to change is invalid");
        newRecipeValues.validate(this, recipeToChange);
        recipeToChange.getModel().setValues(newRecipeValues);
    }

    public void addBlockDrop(BlockDropValues newBlockDrop) throws ValidationException, ProgrammingValidationException {
        newBlockDrop.validate(this);
        blockDrops.add(new SBlockDrop(newBlockDrop));
    }

    public void changeBlockDrop(
            BlockDropReference toChange, BlockDropValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(toChange)) throw new ProgrammingValidationException("Block drop to change is invalid");
        newValues.validate(this);
        toChange.getModel().setValues(newValues);
    }

    public void addMobDrop(MobDropValues dropToAdd) throws ValidationException, ProgrammingValidationException {
        dropToAdd.validate(this);
        this.mobDrops.add(new MobDrop(dropToAdd));
    }

    public void changeMobDrop(MobDropReference dropToChange, MobDropValues newValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(dropToChange)) throw new ProgrammingValidationException("Mob drop to be changed is invalid");
        newValues.validate(this);
        dropToChange.getModel().setValues(newValues);
    }

    public void addProjectile(CustomProjectileValues projectileToAdd) throws ValidationException, ProgrammingValidationException {
        projectileToAdd.validate(this, null);
        this.projectiles.add(new SCustomProjectile(projectileToAdd));
    }

    public void changeProjectile(ProjectileReference projectileToChange, CustomProjectileValues newValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(projectileToChange)) throw new ProgrammingValidationException("Projectile to be changed is invalid");
        newValues.validate(this, projectileToChange.get().getName());
        projectileToChange.getModel().setValues(newValues);
    }

    public void addProjectileCover(ProjectileCoverValues coverToAdd) throws ValidationException, ProgrammingValidationException {
        coverToAdd.validate(this, null);
        this.projectileCovers.add(new SProjectileCover(coverToAdd));
    }

    public void changeProjectileCover(
            ProjectileCoverReference coverToChange, ProjectileCoverValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(coverToChange)) throw new ProgrammingValidationException("Projectile cover to change is invalid");
        newValues.validate(this, coverToChange.get().getName());
        coverToChange.getModel().setValues(newValues);
    }

    public void addContainer(CustomContainerValues containerToAdd) throws ValidationException, ProgrammingValidationException {
        containerToAdd.validate(this, null);
        this.containers.add(new SCustomContainer(containerToAdd));
    }

    public void changeContainer(
            ContainerReference containerToChange, CustomContainerValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(containerToChange)) throw new ProgrammingValidationException("Container to change is invalid");
        newValues.validate(this, containerToChange.get().getName());
        containerToChange.getModel().setValues(newValues);
    }

    public void addFuelRegistry(FuelRegistryValues registryToAdd) throws ValidationException, ProgrammingValidationException {
        registryToAdd.validate(this, null);
        this.fuelRegistries.add(new SFuelRegistry(registryToAdd));
    }

    public void changeFuelRegistry(
            FuelRegistryReference registryToChange, FuelRegistryValues newValues
    ) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(registryToChange)) throw new ProgrammingValidationException("Fuel registry to change is invalid");
        newValues.validate(this, registryToChange.get().getName());
        registryToChange.getModel().setValues(newValues);
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
    }

    public void changeBlock(BlockReference blockToChange, CustomBlockValues newBlockValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(blockToChange)) throw new ProgrammingValidationException("Block to change is invalid");
        newBlockValues.validateComplete(this, blockToChange.get().getInternalID());
        blockToChange.getModel().setValues(newBlockValues);
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
    }

    public void removeTexture(TextureReference textureToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.textures, textureToRemove.getModel());
    }

    public void removeArmorTexture(ArmorTextureReference textureToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.armorTextures, textureToRemove.getModel());
    }

    public void removeItem(ItemReference itemToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.items, itemToRemove.getModel());
        this.removedItemNames.add(itemToRemove.getModel().getValues().getName());
    }

    public void removeCraftingRecipe(CraftingRecipeReference recipeToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.craftingRecipes, recipeToRemove.getModel());
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

    public void removeBlock(BlockReference blockToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.blocks, blockToRemove.getModel());
    }

    public enum Side {
        EDITOR,
        PLUGIN
    }
}
