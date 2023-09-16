package nl.knokko.customitems.plugin.set.loading;

import nl.knokko.customitems.util.ProgrammingValidationException;
import nl.knokko.customitems.util.ResourcePackHost;
import nl.knokko.customitems.util.ValidationException;
import org.bukkit.ChatColor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class ResourcePackIO {

    static final String GET_RESOURCE_PACK_PREFIX = ResourcePackHost.PREFIX + "get-resource-pack/";

    static ResourcePackHashes computeHashes(File file) throws IOException, NoSuchAlgorithmException {
        DigestInputStream sha1Stream = new DigestInputStream(
                Files.newInputStream(file.toPath()), MessageDigest.getInstance("SHA-1")
        );
        DigestInputStream sha256Stream = new DigestInputStream(sha1Stream, MessageDigest.getInstance("SHA-256"));
        byte[] digestBuffer = new byte[100_000];
        int numReadBytes;
        do { numReadBytes = sha256Stream.read(digestBuffer); } while (numReadBytes != -1);
        sha256Stream.close();
        return new ResourcePackHashes(sha1Stream.getMessageDigest().digest(), sha256Stream.getMessageDigest().digest());
    }

    private static void propagate(
            InputStream source, boolean closeSource, OutputStream destination, boolean closeDestination,
            Consumer<String> sendMessage, long totalLength
    ) throws IOException {

        byte[] buffer = new byte[100_000];
        long totalNumReadBytes = 0;
        while (true) {
            int numReadBytes = source.read(buffer);
            if (numReadBytes == -1) break;

            destination.write(buffer, 0, numReadBytes);

            long oldMillion = totalNumReadBytes / 1_000_000;
            totalNumReadBytes += numReadBytes;
            long newMillion = totalNumReadBytes / 1_000_000;
            if (sendMessage != null && oldMillion != newMillion) {
                sendMessage.accept(ChatColor.AQUA + "Progress: " + String.format("%.1f", 100.0 * totalNumReadBytes / totalLength) + "%");
            }
        }

        if (closeSource) source.close();
        destination.flush();
        if (closeDestination) destination.close();
    }

    static boolean checkStatus(String sha256Hex) throws IOException {
        URL url = new URL(GET_RESOURCE_PACK_PREFIX + sha256Hex);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(ResourcePackHost.TIMEOUT);
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) return true;
        else if (responseCode == 404) return false;
        else throw new IOException("Resource pack server returned " + responseCode + " (" + connection.getResponseMessage() + ")");
    }

    static void downloadResourcePackPlusItems(File dataFolder, String sha256Hex) throws IOException {
        URL url = new URL(GET_RESOURCE_PACK_PREFIX + sha256Hex);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(ResourcePackHost.TIMEOUT);
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == 404) throw new IOException("Content is missing or expired. Please export again.");
        else if (responseCode != 200) throw new IOException("Resource pack server returned " + responseCode + " (" + connection.getResponseMessage() + ")");

        ZipInputStream contentStream = new ZipInputStream(connection.getInputStream());
        ZipOutputStream resourcePackStream = new ZipOutputStream(Files.newOutputStream(new File(dataFolder + "/resource-pack.zip").toPath()));
        boolean foundItems = false;

        ZipEntry zipEntry = contentStream.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                zipEntry = contentStream.getNextEntry();
                continue;
            }

            if (zipEntry.getName().equals("items.cis.txt")) {
                OutputStream itemsOutput = Files.newOutputStream(new File(dataFolder + "/items.cis.txt").toPath());
                propagate(contentStream, false, itemsOutput, true, null, 0);
                foundItems = true;
            } else {
                resourcePackStream.putNextEntry(new ZipEntry(zipEntry.getName()));
                propagate(contentStream, false, resourcePackStream, false, null, 0);
                resourcePackStream.closeEntry();
            }

            zipEntry = contentStream.getNextEntry();
        }

        contentStream.close();
        resourcePackStream.flush();
        resourcePackStream.close();

        if (!foundItems) throw new IOException("Couldn't find items.cis.txt");
    }

    static void upload(File file, Consumer<String> sendMessage) throws IOException {
        try {
            ResourcePackHost.upload(
                    uploadOutput -> {
                        sendMessage.accept(ChatColor.BLUE + "Uploading resource pack to the resource pack server...");
                        propagate(
                                Files.newInputStream(file.toPath()), true, uploadOutput,
                                false, sendMessage, file.length()
                        );
                        sendMessage.accept(ChatColor.BLUE + "Finished uploading resource pack to the resource pack server");
                    }, (responseCode, responseMessage, responseBody) -> {
                        throw new IOException("Host returned " + responseCode + " (" + responseMessage + ")");
                    }
            );
        } catch (ValidationException | ProgrammingValidationException e) {
            throw new Error("No validation should be done at this point", e);
        }
    }
}
