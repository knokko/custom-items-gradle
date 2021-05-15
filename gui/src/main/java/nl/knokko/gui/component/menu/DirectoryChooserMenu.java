package nl.knokko.gui.component.menu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.color.SimpleGuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.simple.SimpleColorComponent;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.TextBuilder;

public class DirectoryChooserMenu extends GuiMenu {

    public static final TextBuilder.Properties DEFAULT_CANCEL_PROPERTIES = TextBuilder.Properties.createButton(new Color(200, 200, 200),
            new Color(150, 150, 250));
    public static final TextBuilder.Properties DEFAULT_CANCEL_HOVER_PROPERTIES = TextBuilder.Properties.createButton(Color.WHITE,
            new Color(200, 200, 255));

    public static final TextBuilder.Properties DEFAULT_SELECT_PROPERTIES = TextBuilder.Properties.createButton(new Color(150, 150, 200),
            new Color(120, 120, 250));
    public static final TextBuilder.Properties DEFAULT_SELECT_HOVER_PROPERTIES = TextBuilder.Properties.createButton(new Color(100, 100, 255),
            Color.BLUE);

    protected final Function<List<File>,GuiComponent> listener;
    protected final Predicate<File> filter;
    protected final GuiComponent cancelMenu;
    protected FileList list;

    protected List<File> selectedFiles;
    protected File directory;
    protected File parentDirectory;

    protected final TextBuilder.Properties cancelProps, cancelHover, selectProps, selectHover;
    protected final GuiColor background, listBackground;

    public DirectoryChooserMenu(GuiComponent cancelMenu, Function<List<File>,GuiComponent> listener, Predicate<File> filter,
                           TextBuilder.Properties cancelProps, TextBuilder.Properties cancelHover, TextBuilder.Properties selectProps, TextBuilder.Properties selectHover,
                           GuiColor background, GuiColor listBackground) {
        this.cancelMenu = cancelMenu;
        this.listener = listener;
        this.filter = filter;
        this.directory = new File("").getAbsoluteFile();
        this.selectedFiles = new ArrayList<>();
        updateSelectedFiles();
        this.parentDirectory = directory.getParentFile();

        this.cancelProps = cancelProps;
        this.cancelHover = cancelHover;
        this.selectProps = selectProps;
        this.selectHover = selectHover;
        this.background = background;
        this.listBackground = listBackground;
    }

    public DirectoryChooserMenu(GuiComponent returnMenu, Function<List<File>,GuiComponent> listener, Predicate<File> filter) {
        this(returnMenu, listener, filter, DEFAULT_CANCEL_PROPERTIES, DEFAULT_CANCEL_HOVER_PROPERTIES,
                DEFAULT_SELECT_PROPERTIES, DEFAULT_SELECT_HOVER_PROPERTIES,
                SimpleGuiColor.BLUE, DEFAULT_LIST_BACKGROUND);
    }

    protected void updateSelectedFiles() {
        File[] allFiles = directory.listFiles();
        selectedFiles.clear();
        if (allFiles != null) {
            for (File file : allFiles) {
                if (file.isFile() && filter.test(file)) {
                    selectedFiles.add(file);
                }
            }
        }
    }

    @Override
    protected void addComponents() {
        list = new FileList();
        addComponent(list, 0f, 0.14f, 1f, 0.86f);
        addComponent(new SimpleColorComponent(background), 0f, 0f, 1f, 0.14f);
        addComponent(new TextButton("Cancel", cancelProps, cancelHover, () -> {
            state.getWindow().setMainComponent(cancelMenu);
        }), 0.2f, 0.02f, 0.35f, 0.12f);
        addComponent(new ConditionalTextButton("Select", selectProps, selectHover,
                () -> state.getWindow().setMainComponent(listener.apply(selectedFiles)),
                () -> !selectedFiles.isEmpty()), 0.8f, 0.02f, 0.95f, 0.12f);
        addComponent(new SimpleColorComponent(background), 0f, 0.86f, 1f, 1f);
        addComponent(new ConditionalTextButton("Go up", cancelProps, cancelHover, () -> {
            setDirectory(parentDirectory);
        }, () -> parentDirectory != null), 0.25f, 0.88f, 0.35f, 0.98f);
    }

    protected void setDirectory(File newDirectory) {
        directory = newDirectory;
        updateSelectedFiles();
        parentDirectory = directory.getParentFile();
        list.setDirectory();
        state.getWindow().markChange();
    }

    private static final GuiColor DEFAULT_LIST_BACKGROUND = new SimpleGuiColor(0, 0, 150);

    public static final TextBuilder.Properties FILE_NAME_PROPERTIES = TextBuilder.Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
    public static final TextBuilder.Properties FILE_NAME_HOVER_PROPERTIES = TextBuilder.Properties.createLabel(new Color(50, 50, 50),
            new Color(150, 150, 255), 512, 128);
    public static final TextBuilder.Properties FOLDER_NAME_PROPERTIES = TextBuilder.Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
    public static final TextBuilder.Properties FOLDER_NAME_HOVER_PROPERTIES = TextBuilder.Properties.createLabel(new Color(50, 50, 50),
            new Color(150, 150, 255), 512, 128);

    protected class FileList extends GuiMenu {

        @Override
        protected void addComponents() {
            setDirectory();
        }

        @Override
        public GuiColor getBackgroundColor() {
            return listBackground;
        }

        protected void setDirectory() {
            clearComponents();
            File[] files = directory.listFiles();
            if (files != null) {
                Arrays.sort(files, (a, b) -> {
                    if (a.isHidden() && !b.isHidden())
                        return 1;
                    if (!a.isHidden() && b.isHidden())
                        return -1;
                    return a.getName().toLowerCase(Locale.ROOT)
                            .compareTo(b.getName().toLowerCase(Locale.ROOT));
                });
                int index = 0;
                for (File file : files) {
                    if (file.isDirectory() || filter.test(file)) {

                        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
                        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                                BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = image.createGraphics();
                        icon.paintIcon(null, g, 0, 0);
                        g.dispose();

                        addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)), 0f,
                                0.9f - index * 0.1f, 0.1f, 1f - index * 0.1f);
                        if (file.isDirectory()) {
                            addComponent(new DynamicTextButton(file.getName(), FOLDER_NAME_PROPERTIES,
                                            FOLDER_NAME_HOVER_PROPERTIES, () -> {
                                        DirectoryChooserMenu.this.setDirectory(file);
                                    }), 0.15f, 0.9f - index * 0.1f, Math.min(1f, 0.15f + file.getName().length() * 0.02f),
                                    1f - index * 0.1f);
                        } else {
                            addComponent(new DynamicTextComponent(file.getName(), FILE_NAME_PROPERTIES),
                                    0.15f, 0.9f - index * 0.1f,
                                    Math.min(1f, 0.15f + file.getName().length() * 0.02f),1f - index * 0.1f);
                        }
                        index++;
                    }
                }
            }
            this.screenCenterY = 0f;
        }
    }
}