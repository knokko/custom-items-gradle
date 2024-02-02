package nl.knokko.customitems.item;

import nl.knokko.customitems.bithelper.BitInput;
import nl.knokko.customitems.bithelper.BitOutput;
import nl.knokko.customitems.model.ModelValues;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.customitems.util.CollectionHelper;
import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TranslationEntry extends ModelValues {

    public static TranslationEntry load(BitInput input, boolean mutable) throws UnknownEncodingException {
        byte encoding = input.readByte();

        if (encoding != 1) throw new UnknownEncodingException("TranslationEntry", encoding);

        TranslationEntry entry = new TranslationEntry(mutable);
        entry.language = input.readString();
        entry.displayName = input.readString();
        entry.lore = CollectionHelper.load(input, innerInput -> innerInput.readString());

        return entry;
    }

    private String language;
    private String displayName;
    private List<String> lore;

    public TranslationEntry(boolean mutable) {
        super(mutable);
        this.language = "en_us";
        this.displayName = "";
        this.lore = Collections.emptyList();
    }

    public TranslationEntry(TranslationEntry toCopy, boolean mutable) {
        super(mutable);
        this.language = toCopy.getLanguage();
        this.displayName = toCopy.getDisplayName();
        this.lore = toCopy.getLore();
    }

    public void save(BitOutput output) {
        output.addByte((byte) 1);

        output.addString(language);
        output.addString(displayName);
        CollectionHelper.save(lore, output::addString, output);
    }

    @Override
    public TranslationEntry copy(boolean mutable) {
        return new TranslationEntry(this, mutable);
    }

    public String getLanguage() {
        return language;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLanguage(String language) {
        assertMutable();
        this.language = Objects.requireNonNull(language);
    }

    public void setDisplayName(String displayName) {
        assertMutable();
        this.displayName = Objects.requireNonNull(displayName);
    }

    public void setLore(List<String> lore) {
        assertMutable();
        this.lore = Collections.unmodifiableList(lore);
    }

    public void validate() throws ValidationException, ProgrammingValidationException {
        if (language == null) throw new ProgrammingValidationException("No language");
        if (language.isEmpty()) throw new ValidationException("Language can't be empty");
        if (language.contains(" ")) throw new ValidationException("Language can't contain spaces");
        if (!language.equals(language.toLowerCase(Locale.ROOT))) throw new ValidationException("Language must be lower case");

        if (displayName == null) throw new ProgrammingValidationException("No display name");

        if (lore == null) throw new ProgrammingValidationException("No lore");
        for (String line : lore) {
            if (line == null) throw new ProgrammingValidationException("Missing lore line");
        }
    }
}
