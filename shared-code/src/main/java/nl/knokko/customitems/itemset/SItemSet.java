package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.*;
import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.CustomContainerView;
import nl.knokko.customitems.container.SCustomContainer;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.fuel.SFuelRegistry;
import nl.knokko.customitems.drops.*;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomItemsView;
import nl.knokko.customitems.item.SCustomItem;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.CustomProjectilesView;
import nl.knokko.customitems.projectile.SCustomProjectile;
import nl.knokko.customitems.projectile.cover.ProjectileCoverValues;
import nl.knokko.customitems.projectile.cover.ProjectileCoversView;
import nl.knokko.customitems.projectile.cover.SProjectileCover;
import nl.knokko.customitems.recipe.CraftingRecipeValues;
import nl.knokko.customitems.recipe.CustomCraftingRecipe;
import nl.knokko.customitems.recipe.CustomRecipesView;
import nl.knokko.customitems.texture.*;
import nl.knokko.customitems.util.*;
import nl.knokko.util.bits.BitInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

public class SItemSet {

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

    boolean finishedLoading;
    final Side side;

    public SItemSet(Side side) {
        Checks.notNull(side);
        this.side = side;
    }

    public void initialize() {
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

    private void load(BitInput input) {
        // TODO The actual loading
        finishedLoading = true;
        // TODO Ensure that all references find their model (this must happen before the user can rename models)
    }

    public Side getSide() {
        return side;
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

    public Stream<CraftingRecipeReference> getCraftingRecipeReferences() {
        return craftingRecipes.stream().map(CraftingRecipeReference::new);
    }

    public BlockDropsView getBlockDrops() {
        return new BlockDropsView(blockDrops);
    }

    public Stream<BlockDropReference> getBlockDropReferences() {
        return blockDrops.stream().map(BlockDropReference::new);
    }

    public MobDropsView getMobDrops() {
        return new MobDropsView(mobDrops);
    }

    public Stream<MobDropReference> getMobDropReferences() {
        return mobDrops.stream().map(MobDropReference::new);
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

    private void validate() throws ValidationException, ProgrammingValidationException {
        for (CustomTexture texture : textures) {
            Validation.scope(
                    "Texture " + texture.getValues().getName(),
                    () -> texture.getValues().validateComplete(this, texture.getValues().getName())
            );
        }
        for (ArmorTexture texture : armorTextures) {
            Validation.scope(
                    "Armor texture " + texture.getValues().getName(),
                    () -> texture.getValues().validate(this, texture.getValues().getName())
            );
        }
        for (SCustomItem item : items) {
            Validation.scope(
                    "Item " + item.getValues().getName(),
                    () -> item.getValues().validateComplete(this, item.getValues().getName())
            );
        }
        for (CustomCraftingRecipe recipe : craftingRecipes) {
            Validation.scope(
                    "Recipe for " + recipe.getValues().getResult().toString(),
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
        for (SProjectileCover projectileCover : projectileCovers) {
            Validation.scope(
                    "Projectile cover " + projectileCover.getValues().getName(),
                    () -> projectileCover.getValues().validate(this, projectileCover.getValues().getName())
            );
        }
        for (SCustomContainer container : containers) {
            Validation.scope(
                    "Container " + container.getValues().getName(),
                    () -> container.getValues().validate(this, container.getValues().getName())
            );
        }
        for (CustomBlock block : blocks) {
            Validation.scope(
                    "Block " + block.getValues().getName(),
                    () -> block.getValues().validateComplete(this, block.getValues().getInternalID())
            );
        }

        // TODO Validate the rest of the models after I add them
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

    public void removeBlock(BlockReference blockToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.blocks, blockToRemove.getModel());
    }

    public enum Side {
        EDITOR,
        PLUGIN
    }
}
