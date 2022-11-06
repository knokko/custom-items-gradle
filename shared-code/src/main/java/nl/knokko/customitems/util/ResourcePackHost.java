package nl.knokko.customitems.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ResourcePackHost {

    public static final String PREFIX = "http://49.12.188.159/";

    public static String upload(
            UploadFunction uploadFunction,
            UploadFailFunction uploadFailFunction
    ) throws IOException, ValidationException, ProgrammingValidationException {
        URL url = new URL(PREFIX + "upload-resource-pack/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.connect();

        OutputStream uploadOutput = connection.getOutputStream();
        long fileId = System.nanoTime() + System.currentTimeMillis();
        PrintWriter uploadTextOutput = new PrintWriter(uploadOutput);
        uploadTextOutput.print("-----------------------------" + fileId + "\r\n");
        uploadTextOutput.print("Content-Disposition: form-data; name=\"resource-pack\"; filename=\"resource-pack.zip\"\r\n");
        uploadTextOutput.print("Content-Type: application/x-zip-compressed\r\n\r\n");
        uploadTextOutput.flush();

        uploadFunction.sendOutput(uploadOutput);

        uploadTextOutput = new PrintWriter(uploadOutput);
        uploadTextOutput.print("\r\n-----------------------------" + fileId + "--\r\n");
        uploadTextOutput.flush();

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            List<String> response = new ArrayList<>();
            Scanner errorScanner = new Scanner(connection.getInputStream());
            while (errorScanner.hasNextLine()) {
                response.add(errorScanner.nextLine());
            }
            errorScanner.close();
            connection.disconnect();

            uploadFailFunction.handleFailure(responseCode, connection.getResponseMessage(), response);
            return null;
        }

        String hashPrefix = "/resourcepack changeid ";
        String resourcePackHash = null;
        Scanner responseReader = new Scanner(connection.getInputStream());
        while (responseReader.hasNextLine()) {
            String nextLine = responseReader.nextLine();
            int indexPrefix = nextLine.indexOf(hashPrefix);
            if (indexPrefix != -1) {
                int endIndex = indexPrefix + hashPrefix.length();
                while (endIndex < nextLine.length() && Character.isLetterOrDigit(nextLine.charAt(endIndex))) endIndex += 1;
                resourcePackHash = nextLine.substring(indexPrefix + hashPrefix.length(), endIndex);
            }
        }
        responseReader.close();

        connection.disconnect();

        if (resourcePackHash == null) throw new IOException("Can't find resource pack hash");
        return resourcePackHash;
    }

    @FunctionalInterface
    public interface UploadFunction {
        void sendOutput(OutputStream output) throws IOException, ValidationException, ProgrammingValidationException;
    }

    @FunctionalInterface
    public interface UploadFailFunction {
        void handleFailure(int responseCode, String responseMessage, List<String> responseBody) throws IOException;
    }
}
