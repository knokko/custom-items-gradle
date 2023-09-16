package nl.knokko.customitems.editor.menu.edit.export;

import nl.knokko.customitems.MCVersions;
import nl.knokko.customitems.editor.menu.edit.CollectionSelect;
import nl.knokko.customitems.editor.menu.edit.EnumSelect;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.itemset.ItemSet;
import nl.knokko.customitems.settings.ExportSettingsValues;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.WrapperComponent;
import nl.knokko.gui.component.image.CheckboxComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class ExportMenu extends GuiMenu {

    private static final List<Integer> MC_VERSIONS = new ArrayList<>(10);

    static {
        for (int version = MCVersions.FIRST_VERSION; version <= MCVersions.LAST_VERSION; version++) {
            MC_VERSIONS.add(version);
        }
    }

    private final ItemSet itemSet;
    private final GuiComponent returnMenu;
    private final String fileName;
    private final ExportSettingsValues exportSettings;

    public ExportMenu(ItemSet itemSet, GuiComponent returnMenu, String fileName) {
        this.itemSet = itemSet;
        this.returnMenu = returnMenu;
        this.fileName = fileName;
        this.exportSettings = itemSet.getExportSettings().copy(true);
    }

    @Override
    protected void addComponents() {
        DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);
        addComponent(errorComponent, 0.05f, 0.9f, 0.975f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        addComponent(new DynamicTextComponent("MC version:", LABEL), 0.225f, 0.8f, 0.355f, 0.9f);
        addComponent(CollectionSelect.createButton(
                MC_VERSIONS, exportSettings::setMcVersion, MCVersions::createString,
                exportSettings.getMcVersion(), false
        ), 0.375f, 0.8f, 0.425f, 0.9f);

        addComponent(new DynamicTextComponent("Export mode:", LABEL), 0.525f, 0.8f, 0.655f, 0.9f);
        addComponent(EnumSelect.createSelectButton(
                ExportSettingsValues.Mode.class, exportSettings::setMode, exportSettings.getMode()
        ), 0.675f, 0.8f, 0.775f, 0.9f);

        addComponent(new DynamicTextButton("Continue", SAVE_BASE, SAVE_HOVER, () -> {
            errorComponent.setText("");

            ExportProgress progress = new ExportProgress();
            new Thread(() -> {
                Exporter.attemptToExport(itemSet, fileName, exportSettings, returnMenu, progress);
            }).start();
            state.getWindow().setMainComponent(new ExportLoadingScreen(this, errorComponent, progress));
        }), 0.825f, 0.8f, 0.975f, 0.9f);

        addComponent(new WrapperComponent<GuiMenu>(new AutomaticSettings()) {
            @Override
            public boolean isActive() {
                return exportSettings.getMode() == ExportSettingsValues.Mode.AUTOMATIC
                        || exportSettings.getMode() == ExportSettingsValues.Mode.MIXED;
            }
        }, 0f, 0f, 1f, 0.7f);

        HelpButtons.addHelpLink(this, "edit menu/export.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class AutomaticSettings extends GuiMenu {

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent("Reload message:", LABEL), 0.025f, 0.9f, 0.2f, 1f);
            addComponent(new EagerTextEditField(
                    exportSettings.getReloadMessage(), LONG_EDIT_BASE, LONG_EDIT_ACTIVE, exportSettings::setReloadMessage
            ), 0.025f, 0.8f, 0.975f, 0.9f);

            addComponent(new CheckboxComponent(
                    exportSettings.shouldKickUponReject(), exportSettings::setKickUponReject
            ), 0.01f, 0.675f, 0.025f, 0.7f);
            addComponent(new DynamicTextComponent(
                    "Kick players who reject the resource pack", LABEL
            ), 0.05f, 0.65f, 0.6f, 0.75f);

            addCheckboxBasedInput(new MessageSettings(
                    "Kick message:", exportSettings.getForceRejectMessage(), exportSettings::setForceRejectMessage
            ), exportSettings::shouldKickUponReject, false, 0.45f);
            addCheckboxBasedInput(new MessageSettings(
                    "Warning message:", exportSettings.getOptionalRejectMessage(), exportSettings::setOptionalRejectMessage
            ), exportSettings::shouldKickUponReject, true, 0.45f);

            addComponent(new CheckboxComponent(
                    exportSettings.shouldKickUponFailedDownload(), exportSettings::setKickUponFailedDownload
            ), 0.01f, 0.275f, 0.025f, 0.3f);
            addComponent(new DynamicTextComponent(
                    "Kick players who fail to download the resource pack", LABEL
            ), 0.05f, 0.25f, 0.7f, 0.35f);

            addCheckboxBasedInput(new MessageSettings(
                    "Kick message:", exportSettings.getForceFailedMessage(), exportSettings::setForceFailedMessage
            ), exportSettings::shouldKickUponFailedDownload, false, 0.05f);
            addCheckboxBasedInput(new MessageSettings(
                    "Warning message:", exportSettings.getOptionalFailedMessage(), exportSettings::setOptionalFailedMessage
            ), exportSettings::shouldKickUponFailedDownload, true, 0.05f);
        }

        private void addCheckboxBasedInput(MessageSettings message, BooleanSupplier isActive, boolean invert, float minY) {
            addComponent(new WrapperComponent<MessageSettings>(message) {
                @Override
                public boolean isActive() {
                    return isActive.getAsBoolean() != invert;
                }
            }, 0f, minY, 1f, minY + 0.2f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND;
        }
    }

    private static class MessageSettings extends GuiMenu {

        final String description;
        final String currentMessage;
        final Consumer<String> changeMessage;

        MessageSettings(String description, String currentMessage, Consumer<String> changeMessage) {
            this.description = description;
            this.currentMessage = currentMessage;
            this.changeMessage = changeMessage;
        }

        @Override
        protected void addComponents() {
            addComponent(new DynamicTextComponent(description, LABEL), 0.025f, 0.5f, 0.25f, 1f);
            addComponent(new EagerTextEditField(
                    currentMessage, LONG_EDIT_BASE, LONG_EDIT_ACTIVE, changeMessage
            ), 0.025f, 0f, 0.975f, 0.5f);
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND;
        }
    }
}
