package nl.knokko.customitems.editor.menu.main;

import nl.knokko.customitems.editor.EditorFileManager;
import nl.knokko.customitems.editor.util.HelpButtons;
import nl.knokko.customitems.editor.util.ItemSetBackups;
import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.menu.GuiMenu;
import nl.knokko.gui.component.text.ActivatableTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.List;

import static nl.knokko.customitems.editor.menu.edit.EditProps.*;

public class BackupMenu extends GuiMenu {

    private final GuiComponent returnMenu;

    private final Collection<ItemSetBackups> allBackups = EditorFileManager.getAllBackups();
    private final SavesList savesList = new SavesList();
    private final DynamicTextComponent errorComponent = new DynamicTextComponent("", ERROR);

    private ItemSetBackups selectedItemSet;

    public BackupMenu(GuiComponent returnMenu) {
        this.returnMenu = returnMenu;
    }

    @Override
    protected void addComponents() {
        addComponent(errorComponent, 0.1f, 0.9f, 1f, 1f);

        addComponent(new DynamicTextButton("Cancel", CANCEL_BASE, CANCEL_HOVER, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.025f, 0.8f, 0.175f, 0.9f);

        int index = 0;
        for (ItemSetBackups backup : allBackups) {
            addComponent(new ActivatableTextButton(backup.name, SELECT_BASE, SELECT_HOVER, SELECT_ACTIVE, () -> {
                selectedItemSet = backup;
                savesList.clearComponents();
                savesList.addComponents();
            }, () -> selectedItemSet == backup
            ), 0.2f, 0.8f - index * 0.12f, 0.45f, 0.9f - index * 0.12f);
            index++;
        }

        addComponent(savesList, 0.5f, 0f, 1f, 0.9f);

        HelpButtons.addHelpLink(this, "main menu/edit/backup.html");
    }

    @Override
    public GuiColor getBackgroundColor() {
        return BACKGROUND;
    }

    private class SavesList extends GuiMenu {

        @Override
        protected void addComponents() {
            if (selectedItemSet != null) {
                List<Long> saveTimes = selectedItemSet.getSaveTimes();
                for (int index = 0; index < saveTimes.size(); index++) {
                    long saveTime = saveTimes.get(index);
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM);
                    String dateTime = formatter.format(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(saveTime), ZoneId.systemDefault()
                    ));
                    float maxY = 1f - index * 0.12f;
                    float minY = maxY - 0.1f;
                    addComponent(
                            new DynamicTextComponent(dateTime, LABEL),
                            0.01f, minY, 0.85f, maxY
                    );
                    addComponent(new DynamicTextButton("Load", BUTTON, HOVER, () -> {
                        File backupFile = EditorFileManager.getBackupFile(selectedItemSet.name, saveTime);
                        LoadMenu.loadSave(backupFile, errorComponent, true);
                    }), 0.875f, minY, 0.99f, maxY);
                }
            }
        }

        @Override
        public GuiColor getBackgroundColor() {
            return BACKGROUND2;
        }
    }
}
