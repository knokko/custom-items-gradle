package nl.knokko.customitems.editor.resourcepack;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PriorityZipOutputStream extends ZipOutputStream {

    private final Set<String> existingEntries = new HashSet<>();
    private boolean isSkipping;

    public PriorityZipOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void putNextEntry(ZipEntry entry) throws IOException {
        if (existingEntries.contains(entry.getName())) {
            isSkipping = true;
            return;
        }

        super.putNextEntry(entry);
        existingEntries.add(entry.getName());
        isSkipping = false;
    }

    @Override
    public void write(byte[] bytes, int offset, int length) throws IOException {
        if (!isSkipping) super.write(bytes, offset, length);
    }

    @Override
    public void closeEntry() throws IOException {
        if (!isSkipping) super.closeEntry();
    }
}
