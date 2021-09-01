package nl.knokko.customitems.itemset;

import nl.knokko.customitems.block.CustomBlock;
import nl.knokko.customitems.block.CustomBlocksView;
import nl.knokko.customitems.item.CustomItemValues;
import nl.knokko.customitems.item.CustomItemsView;
import nl.knokko.customitems.item.SCustomItem;
import nl.knokko.customitems.texture.CustomTexture;
import nl.knokko.customitems.texture.CustomTexturesView;
import nl.knokko.customitems.util.CollectionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SItemSet {

    Collection<CustomTexture> textures;
    Collection<SCustomItem> items;
    Collection<CustomBlock> blocks;

    boolean finishedLoading;

    public SItemSet() {
    }

    public void initialize() {
        textures = new ArrayList<>();
        items = new ArrayList<>();
        blocks = new ArrayList<>();

        finishedLoading = true;
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
}
