package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.set.ItemSet;
import nl.knokko.customitems.editor.set.item.texture.CrossbowTextures;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.TextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CrossbowTextureEdit extends GuiMenu {

    private final ItemSet set;
    private final GuiComponent returnMenu;
    private final Consumer<CrossbowTextures> afterSave;

    private final CrossbowTextures toModify;

    private final DynamicTextComponent errorComponent = new DynamicTextComponent("", EditProps.ERROR);
    private PullTextureMenu pullTextureMenu = new PullTextureMenu();

    private final TextEditField nameField;
    private BufferedImage standbyImage;
    private BufferedImage arrowImage;
    private BufferedImage fireworkImage;

    private List<CrossbowTextures.PullTexture> pullTextures;

    public CrossbowTextureEdit(
            EditMenu menu, Consumer<CrossbowTextures> afterSave,
            CrossbowTextures oldValue, CrossbowTextures toModify
    ) {
        this(menu.getSet(), menu.getTextureOverview(), afterSave, oldValue, toModify);
    }

    public CrossbowTextureEdit(
            ItemSet set, GuiComponent returnMenu, Consumer<CrossbowTextures> afterSave,
            CrossbowTextures oldValue, CrossbowTextures toModify
    ) {
        this.set = set;
        this.returnMenu = returnMenu;
        this.afterSave = afterSave;
        this.toModify = toModify;

        if (oldValue == null) {
            this.nameField = new TextEditField("", EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
            this.pullTextures = new ArrayList<>(3);
            this.pullTextures.add(new CrossbowTextures.PullTexture(null, 0.0));
            this.pullTextures.add(new CrossbowTextures.PullTexture(null, 0.58));
            this.pullTextures.add(new CrossbowTextures.PullTexture(null, 1.0));
        } else {
            this.nameField = new TextEditField(oldValue.getName(), EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE);
            this.standbyImage = oldValue.getImage();
            this.arrowImage = oldValue.getArrowImage();
            this.fireworkImage = oldValue.getFireworkImage();
            this.pullTextures = oldValue.getPullTextures();
        }
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.2f, 0.8f);

        addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1.0f);
        addComponent(pullTextureMenu, 0.7f, 0f, 0.975f, 1.0f);

        if (toModify == null) {
            addComponent(new DynamicTextButton("Create", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                CrossbowTextures toAdd = new CrossbowTextures(
                        nameField.getText(), pullTextures, standbyImage, arrowImage, fireworkImage
                );
                // TODO Add it to the item set
            }), 0.025f, 0.2f, 0.2f, 0.3f);
        } else {
            addComponent(new DynamicTextButton("Apply", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                // TODO Change it in the item set
            }), 0.025f, 0.2f, 0.2f, 0.3f);
        }

        addComponent(new DynamicTextComponent("Name:", EditProps.LABEL), 0.25f, 0.6f, 0.35f, 0.7f);
        addComponent(nameField, 0.375f, 0.6f, 0.5f, 0.7f);

        addComponent(new DynamicTextComponent("Standby image:", EditProps.LABEL), 0.25f, 0.45f, 0.45f, 0.55f);
        WrapperComponent<SimpleImageComponent> standbyImageWrapper = new WrapperComponent<>(null);
        addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, (BufferedImage texture, String imageName) -> {
            standbyImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture)));
            standbyImage = texture;
            if (nameField.getText().isEmpty()) {
                if (imageName.endsWith("_standby")) {
                    nameField.setText(imageName.substring(0, imageName.length() - "_standby".length()));
                } else {
                    nameField.setText(imageName);
                }
            }
            return this;
        }), errorComponent, this), 0.475f, 0.45f, 0.55f, 0.55f);
        addComponent(standbyImageWrapper, 0.575f, 0.45f, 0.65f, 0.55f);

        addComponent(new DynamicTextComponent("Arrow image:", EditProps.LABEL), 0.25f, 0.3f, 0.45f, 0.4f);
        WrapperComponent<SimpleImageComponent> arrowImageWrapper = new WrapperComponent<>(null);
        addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, (BufferedImage texture, String imageName) -> {
            arrowImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture)));
            arrowImage = texture;
            return this;
        }), errorComponent, this), 0.475f, 0.3f, 0.55f, 0.4f);
        addComponent(arrowImageWrapper, 0.575f, 0.3f, 0.65f, 0.4f);

        addComponent(new DynamicTextComponent("Firework image:", EditProps.LABEL), 0.25f, 0.15f, 0.45f, 0.25f);
        WrapperComponent<SimpleImageComponent> fireworkImageWrapper = new WrapperComponent<>(null);
        addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, (BufferedImage texture, String imageName) -> {
            fireworkImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture)));
            fireworkImage = texture;
            return this;
        }), errorComponent, this), 0.475f, 0.15f, 0.55f, 0.25f);
        addComponent(fireworkImageWrapper, 0.575f, 0.15f, 0.65f, 0.25f);

        // TODO Create help menu
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }

    private class PullTextureMenu extends GuiMenu {

        @Override
        protected void addComponents() {
            int index = 0;
            for (CrossbowTextures.PullTexture pullTexture : pullTextures) {
                addComponent(new PullTextureComponent(pullTexture),
                        0f, 0.65f - 0.15f * index, 1f, 0.75f - 0.15f * index
                );
                index++;
            }

            addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                pullTextures.add(new CrossbowTextures.PullTexture(null, 1.0));
                clearComponents();
                addComponents();
            }), 0f, 0.65f - 0.15f * index, 0.3f, 0.75f - 0.15f * index);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }

    private class PullTextureComponent extends GuiMenu {

        private final CrossbowTextures.PullTexture pullTexture;

        PullTextureComponent(CrossbowTextures.PullTexture pullTexture) {
            this.pullTexture = pullTexture;
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Pull:", EditProps.LABEL), 0.1f, 0.55f, 0.5f, 0.95f);
            addComponent(new EagerFloatEditField(
                    pullTexture.getPull(), 0.0, EditProps.EDIT_BASE, EditProps.EDIT_ACTIVE,
                    pullTexture::setPull
            ), 0.55f, 0.55f, 0.8f, 0.95f);
            addComponent(new DynamicTextButton("X", EditProps.QUIT_BASE, EditProps.QUIT_HOVER, () -> {
                pullTextures.remove(pullTexture);
                pullTextureMenu.clearComponents();
                pullTextureMenu.addComponents();
            }), 0.825f, 0.55f, 0.975f, 0.95f);

            addComponent(new DynamicTextComponent("Image:", EditProps.LABEL), 0.1f, 0.05f, 0.55f, 0.45f);
            WrapperComponent<SimpleImageComponent> pullImageWrapper = new WrapperComponent<>(
                    pullTexture.getImage() == null ? null :
                            new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(pullTexture.getImage()))
            );
            addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, (BufferedImage texture, String imageName) -> {
                pullImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(texture)));
                pullTexture.setImage(texture);
                return CrossbowTextureEdit.this;
            }), errorComponent, this), 0.6f, 0.05f, 0.775f, 0.45f);
            addComponent(pullImageWrapper, 0.8f, 0.05f, 1.0f, 0.45f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND2;
        }
    }
}
