package nl.knokko.gui.component.menu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.image.SimpleImageComponent;
import nl.knokko.gui.component.simple.SimpleColorComponent;
import nl.knokko.gui.component.text.ConditionalTextButton;
import nl.knokko.gui.component.text.EagerTextEditField;
import nl.knokko.gui.component.text.TextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextButton;
import nl.knokko.gui.component.text.dynamic.DynamicTextComponent;
import nl.knokko.gui.util.TextBuilder.Properties;

public class FileChooserMenu extends GuiMenu {

    protected final Consumer<File> onSelect;
    protected final Predicate<File> filter;
    protected final GuiComponent returnMenu;
    protected FileList list;

    protected File selectedFile;
    protected File directory;
    protected File parentDirectory;

    protected final Properties cancelProps, cancelHover, selectProps, selectHover;
    protected final GuiColor background, listBackground;

    public FileChooserMenu(GuiComponent returnMenu, Consumer<File> onSelect, Predicate<File> filter,
                           Properties cancelProps, Properties cancelHover, Properties selectProps, Properties selectHover,
                           GuiColor background, GuiColor listBackground) {
        this.returnMenu = returnMenu;
        this.onSelect = onSelect;
        this.filter = filter;
        this.directory = new File("").getAbsoluteFile();
        this.parentDirectory = directory.getParentFile();

        this.cancelProps = cancelProps;
        this.cancelHover = cancelHover;
        this.selectProps = selectProps;
        this.selectHover = selectHover;
        this.background = background;
        this.listBackground = listBackground;
    }

    @Override
    protected void addComponents() {
        list = new FileList();
        addComponent(list, 0f, 0.14f, 1f, 0.86f);
        addComponent(new SimpleColorComponent(background), 0f, 0f, 1f, 0.14f);
        addComponent(new TextButton("Cancel", cancelProps, cancelHover, () -> {
            state.getWindow().setMainComponent(returnMenu);
        }), 0.2f, 0.02f, 0.35f, 0.12f);
        addComponent(new ConditionalTextButton("Select", selectProps, selectHover, () -> {
            state.getWindow().setMainComponent(returnMenu);
            onSelect.accept(selectedFile);
        }, () -> {
            return selectedFile != null;
        }), 0.5f, 0.02f, 0.65f, 0.12f);
        addComponent(new DynamicTextComponent("Search:", Properties.createLabel()),
                0.7f, 0.02f, 0.8f, 0.12f);
        addComponent(new EagerTextEditField("",
                Properties.createEdit(), Properties.createEdit(Color.GREEN), newText -> {
            this.filterText = newText;
            list.setDirectory();
        }), 0.825f, 0.02f, 0.975f, 0.12f);
        addComponent(new SimpleColorComponent(background), 0f, 0.86f, 1f, 1f);
        addComponent(new ConditionalTextButton("Go up", cancelProps, cancelHover, () -> {
            setDirectory(parentDirectory);
        }, () -> {
            return parentDirectory != null;
        }), 0.25f, 0.88f, 0.35f, 0.98f);
    }

    protected void setDirectory(File newDirectory) {
        directory = newDirectory;
        parentDirectory = directory.getParentFile();
        list.setDirectory();
        state.getWindow().markChange();
    }

    private String filterText = "";

    public static final Properties FILE_NAME_PROPERTIES = Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
    public static final Properties FILE_NAME_HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50),
            new Color(150, 150, 255), 512, 128);
    public static final Properties FOLDER_NAME_PROPERTIES = Properties.createLabel(Color.BLACK, Color.WHITE, 512, 128);
    public static final Properties FOLDER_NAME_HOVER_PROPERTIES = Properties.createLabel(new Color(50, 50, 50),
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
                if ((file.isDirectory() || filter.test(file))
                        && file.getName().toLowerCase(Locale.ROOT)
                        .contains(filterText.toLowerCase(Locale.ROOT))) {

                    Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
                    BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = image.createGraphics();
                    icon.paintIcon(null, g, 0, 0);
                    g.dispose();

                    addComponent(new SimpleImageComponent(state.getWindow().getTextureLoader().loadTexture(image)), 0f,
                            0.9f - index * 0.1f, 0.1f, 1f - index * 0.1f);
                    if (file.isDirectory()) {
                        addComponent(new DynamicTextButton(file.getName(), FILE_NAME_PROPERTIES,
                                        FILE_NAME_HOVER_PROPERTIES, () -> {
                                    FileChooserMenu.this.setDirectory(file);
                                }), 0.15f, 0.9f - index * 0.1f, Math.min(1f, 0.15f + file.getName().length() * 0.02f),
                                1f - index * 0.1f);
                    } else {
                        addComponent(new DynamicTextButton(file.getName(), FOLDER_NAME_PROPERTIES,
                                        FOLDER_NAME_HOVER_PROPERTIES, () -> {
                                    selectedFile = file;
                                    state.getWindow().markChange();
                                }), 0.15f, 0.9f - index * 0.1f, Math.min(1f, 0.15f + file.getName().length() * 0.02f),
                                1f - index * 0.1f);
                    }
                    index++;
                }
            }
            this.screenCenterY = 0f;
        }
    }
}
