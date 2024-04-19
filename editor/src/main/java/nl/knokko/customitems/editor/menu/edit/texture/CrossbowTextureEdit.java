package nl.knokko.customitems.editor.menu.edit.texture;

import nl.knokko.customitems.editor.menu.edit.EditMenu;
import nl.knokko.customitems.editor.menu.edit.EditProps;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.Validation;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.itemset.TextureReference;
import nl.knokko.customitems.texture.BowTextureEntry;
import nl.knokko.customitems.texture.CrossbowTexture;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerFloatEditField;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.Comparator;
import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class CrossbowTextureEdit extends GuiMenu {

    private final ItemSet set;
    private final GuiComponent returnMenu;

    private final TextureReference toModify;
    private final CrossbowTexture currentValues;

    private final DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
    private final PullTextureMenu pullTextureMenu = new PullTextureMenu();

    public CrossbowTextureEdit(
            EditMenu menu, TextureReference toModify, CrossbowTexture oldValues
    ) {
        this(menu.getSet(), new TextureCollectionEdit(menu), toModify, oldValues);
    }

    public CrossbowTextureEdit(
            ItemSet set, GuiComponent returnMenu, TextureReference toModify, CrossbowTexture oldValues
    ) {
        this.set = set;
        this.returnMenu = returnMenu;
        this.toModify = toModify;
        this.currentValues = oldValues.copy(true);
    }

    @Override
    protected void addComponents() {
        addComponent(new DynamicTextButton("Cancel", EditProps.CANCEL_BASE, EditProps.CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.7f, 0.2f, 0.8f);

        addComponent(errorComponent, 0.05f, 0.9f, 0.95f, 1.0f);
        addComponent(pullTextureMenu, 0.7f, 0f, 0.975f, 0.9f);

        addComponent(new DynamicTextButton(toModify == null ? "Create" : "Apply", SAVE_BASE, SAVE_HOVER, () -> {
            List<BowTextureEntry> pulls = currentValues.getPullTextures();
            pulls.sort(Comparator.comparingDouble(BowTextureEntry::getPull));
            currentValues.setPullTextures(pulls);

            String error;
            if (toModify == null) error = Validation.toErrorString(() -> set.textures.add(currentValues));
            else error = Validation.toErrorString(() -> set.textures.change(toModify, currentValues));

            if (error == null) state.getWindow().setMainComponent(returnMenu);
            else errorComponent.setText(error);
        }), 0.025f, 0.2f, 0.2f, 0.3f);

        EagerTextEditField nameField = new EagerTextEditField(currentValues.getName(), EDIT_BASE, EDIT_ACTIVE, currentValues::setName);

        addComponent(
                new DynamicTextComponent("Name:", EditProps.LABEL),
                0.25f, 0.6f, 0.35f, 0.7f
        );
        addComponent(nameField, 0.375f, 0.6f, 0.5f, 0.7f);

        addComponent(
                new DynamicTextComponent("Standby image:", EditProps.LABEL),
                0.25f, 0.45f, 0.45f, 0.55f
        );
        WrapperComponent<SimpleImageComponent> standbyImageWrapper = new WrapperComponent<>(
                currentValues.getImage() == null ? null : new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(currentValues.getImage()))
        );
        addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, chosenTexture -> {
            standbyImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(chosenTexture.getImage())));
            currentValues.setImage(chosenTexture.getImage());
            if (nameField.getText().isEmpty()) {
                if (chosenTexture.getName().endsWith("_standby")) {
                    nameField.setText(chosenTexture.getName().substring(0, chosenTexture.getName().length() - "_standby".length()));
                } else {
                    nameField.setText(chosenTexture.getName());
                }
            }
        }), errorComponent), 0.475f, 0.45f, 0.55f, 0.55f);
        addComponent(standbyImageWrapper, 0.575f, 0.45f, 0.65f, 0.55f);

        addComponent(
                new DynamicTextComponent("Arrow image:", EditProps.LABEL),
                0.25f, 0.3f, 0.45f, 0.4f
        );
        WrapperComponent<SimpleImageComponent> arrowImageWrapper = new WrapperComponent<>(
                currentValues.getArrowImage() == null ? null : new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(currentValues.getArrowImage()))
        );
        addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, chosenTexture -> {
            arrowImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(chosenTexture.getImage())));
            currentValues.setArrowImage(chosenTexture.getImage());
        }), errorComponent), 0.475f, 0.3f, 0.55f, 0.4f);
        addComponent(arrowImageWrapper, 0.575f, 0.3f, 0.65f, 0.4f);

        addComponent(
                new DynamicTextComponent("Firework image:", EditProps.LABEL),
                0.25f, 0.15f, 0.45f, 0.25f
        );
        WrapperComponent<SimpleImageComponent> fireworkImageWrapper = new WrapperComponent<>(
                currentValues.getFireworkImage() == null ? null : new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(currentValues.getFireworkImage()))
        );
        addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(this, chosenTexture -> {
            fireworkImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(chosenTexture.getImage())));
            currentValues.setFireworkImage(chosenTexture.getImage());
        }), errorComponent), 0.475f, 0.15f, 0.55f, 0.25f);
        addComponent(fireworkImageWrapper, 0.575f, 0.15f, 0.65f, 0.25f);

        HelpButtons.addHelpLink(this, "edit%20menu/textures/crossbow%20edit.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return EditProps.BACKGROUND;
    }

    private class PullTextureMenu extends GuiMenu {

        @Override
        protected void addComponents() {
            List<BowTextureEntry> pullTextures = currentValues.getPullTextures();
            for (int index = 0; index < pullTextures.size(); index++) {
                addComponent(new PullTextureComponent(index),
                        0f, 0.65f - 0.15f * index, 1f, 0.75f - 0.15f * index
                );
            }

            addComponent(new DynamicTextButton("+", EditProps.SAVE_BASE, EditProps.SAVE_HOVER, () -> {
                pullTextures.add(BowTextureEntry.createQuick(null, 1.0));
                clearComponents();
                addComponents();
            }), 0f, 0.65f - 0.15f * pullTextures.size(), 0.3f, 0.75f - 0.15f * pullTextures.size());
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND;
        }
    }

    private class PullTextureComponent extends GuiMenu {

        private final int index;

        PullTextureComponent(int index) {
            this.index = index;
        }

        @Override
        protected void addComponents() {
            BowTextureEntry oldEntry = currentValues.getPullTextures().get(index);

            addComponent(
                    new DynamicTextComponent("Pull:", LABEL),
                    0.1f, 0.55f, 0.5f, 0.95f
            );
            addComponent(new EagerFloatEditField(oldEntry.getPull(), 0.0, EDIT_BASE, EDIT_ACTIVE, newPull -> {
                List<BowTextureEntry> pulls = currentValues.getPullTextures();
                BowTextureEntry newEntry = pulls.get(index).copy(true);
                newEntry.setPull(newPull);
                pulls.set(index, newEntry);
                currentValues.setPullTextures(pulls);
            }), 0.55f, 0.55f, 0.8f, 0.95f);
            addComponent(new DynamicTextButton("X", QUIT_BASE, QUIT_HOVER, () -> {
                List<BowTextureEntry> pulls = currentValues.getPullTextures();
                pulls.remove(index);
                currentValues.setPullTextures(pulls);
                pullTextureMenu.clearComponents();
                pullTextureMenu.addComponents();
            }), 0.825f, 0.55f, 0.975f, 0.95f);

            addComponent(
                    new DynamicTextComponent("Image:", EditProps.LABEL),
                    0.1f, 0.05f, 0.55f, 0.45f
            );
            WrapperComponent<SimpleImageComponent> pullImageWrapper = new WrapperComponent<>(
                    oldEntry.getImage() == null ? null :
                            new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(oldEntry.getImage()))
            );
            addComponent(TextureEdit.createImageSelect(new TextureEdit.PartialTransparencyFilter(CrossbowTextureEdit.this, chosenTexture -> {
                pullImageWrapper.setComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(chosenTexture.getImage())));
                List<BowTextureEntry> pulls = currentValues.getPullTextures();
                BowTextureEntry newEntry = pulls.get(index).copy(true);
                newEntry.setImage(chosenTexture.getImage());
                pulls.set(index, newEntry);
                currentValues.setPullTextures(pulls);
            }), errorComponent), 0.6f, 0.05f, 0.775f, 0.45f);
            addComponent(pullImageWrapper, 0.8f, 0.05f, 1.0f, 0.45f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return EditProps.BACKGROUND2;
        }
    }
}
