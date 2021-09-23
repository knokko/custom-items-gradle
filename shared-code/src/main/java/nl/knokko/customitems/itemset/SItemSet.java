package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.*;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomItemsView;
import nl.knokko.customitems.item.SCustomItem;
import nl.knokko.customitems.projectile.CustomProjectileValues;
import nl.knokko.customitems.projectile.SCustomProjectile;
import nl.knokko.customitems.texture.BaseTextureValues;
import nl.knokko.customitems.texture.CustomTexture;
import nl.knokko.customitems.texture.CustomTexturesView;
import nl.knokko.customitems.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SItemSet {

    Collection<CustomTexture> textures;
    Collection<SCustomItem> items;
    Collection<CustomBlock> blocks;
    Collection<SCustomProjectile> projectiles;

    Collection<String> removedItemNames;

    boolean finishedLoading;
    final Side side;

    public SItemSet(Side side) {
        Checks.notNull(side);
        this.side = side;
    }

    public void initialize() {
        textures = new ArrayList<>();
        items = new ArrayList<>();
        blocks = new ArrayList<>();
        projectiles = new ArrayList<>();

        removedItemNames = new ArrayList<>();

        finishedLoading = true;
    }

    public Side getSide() {
        return side;
    }

    public CustomTexturesView getTextures() {
        return new CustomTexturesView(textures);
    }

    public CustomItemsView getItems() {
        return new CustomItemsView(items);
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

    public ProjectileReference getProjectileReference(String projectileName) throws NoSuchElementException {
        if (finishedLoading) {
            return new ProjectileReference(CollectionHelper.find(projectiles, projectile -> projectile.getValues().getName(), projectileName).get());
        } else {
            return new ProjectileReference(projectileName, this);
        }
    }

    public Optional<BaseTextureValues> getTexture(String textureName) {
        return CollectionHelper.find(textures, texture -> texture.getValues().getName(), textureName).map(CustomTexture::getValues);
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

    public Optional<CustomProjectileValues> getProjectile(String projectileName) {
        return CollectionHelper.find(projectiles, projectile -> projectile.getValues().getName(), projectileName).map(SCustomProjectile::getValues);
    }

    private <T> boolean isReferenceValid(Collection<T> collection, T model) {
        if (model == null) throw new IllegalStateException("Too early for validity checks");
        return collection.contains(model);
    }

    public boolean isReferenceValid(TextureReference reference) {
        return isReferenceValid(textures, reference.model);
    }

    public boolean isReferenceValid(ItemReference reference) {
        return isReferenceValid(items, reference.model);
    }

    public boolean isReferenceValid(BlockReference reference) {
        return isReferenceValid(blocks, reference.model);
    }

    public boolean isReferenceValid(ProjectileReference reference) {
        return isReferenceValid(projectiles, reference.model);
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
        for (SCustomItem item : items) {
            Validation.scope(
                    "Item " + item.getValues().getName(),
                    () -> item.getValues().validateComplete(this, item.getValues().getName())
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
        textureToChange.model.setValues(newTextureValues);
    }

    public void addItem(CustomItemValues newItem) throws ValidationException, ProgrammingValidationException {
        newItem.validateComplete(this, null);
        this.items.add(new SCustomItem(newItem));
    }

    public void changeItem(ItemReference itemToChange, CustomItemValues newItemValues) throws ValidationException, ProgrammingValidationException {
        if (!isReferenceValid(itemToChange)) throw new ProgrammingValidationException("Item to change is invalid");
        newItemValues.validateComplete(this, itemToChange.get().getName());
        itemToChange.model.setValues(newItemValues);
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
        blockToChange.model.setValues(newBlockValues);
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
        removeModel(this.textures, textureToRemove.model);
    }

    public void removeItem(ItemReference itemToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.items, itemToRemove.model);
        this.removedItemNames.add(itemToRemove.model.getValues().getName());
    }

    public void removeBlock(BlockReference blockToRemove) throws ValidationException, ProgrammingValidationException {
        removeModel(this.blocks, blockToRemove.model);
    }

    public enum Side {
        EDITOR,
        PLUGIN
    }
}
