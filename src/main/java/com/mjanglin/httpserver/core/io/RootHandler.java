package com.mjanglin.httpserver.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mjanglin.httpserver.util.MimeTypes;

public class RootHandler {
    private final File root;
    private final MimeTypes mimeTypes = new MimeTypes();

    public RootHandler(String rootPath) throws RootNotFoundException {
        root = new File(rootPath);

        if (!root.exists() || !root.isDirectory()) {
            throw new RootNotFoundException("Root not found: " + rootPath);
        }
    }

    private boolean checkIfEndsWithSlash(String path) {
        return path.endsWith("/");
    }

    /**
     * This method checks to see if the relative path provided exists inside root
     */
    private boolean checkIfProvidedRelativePathExistsInsideRoot(String relativePath) {
        File file = new File(root, relativePath);

        if (!file.exists()) {
            return false;
        }

        try {
            if (file.getCanonicalPath().startsWith(root.getCanonicalPath())) {
                return true;
            } else if (file.isDirectory()) {
                file = new File(file, "index.html");

                if (file.exists()) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }

        return false;
    }

    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if (checkIfEndsWithSlash(relativePath)) {
            relativePath += "index.html"; // default file to serve if it exists
        }

        if (!checkIfProvidedRelativePathExistsInsideRoot(relativePath)) {
            
        }

        File file = new File(root, relativePath);

        String fileName = file.getName();

        String mimeType = mimeTypes.getMimeType(fileName);

        if (mimeType == null) {
            return "application/octet-stream"; /**
                                                * https://datatracker.ietf.org/doc/html/rfc7231
                                                */
        }

        return mimeType;
    }

    /**
     * This method returns the byte array of the file
     * 
     * Todo - Add strategy for large files
     * 
     * @param relativePath
     * @return a byte array of the file
     * @throws FileNotFoundException
     * @throws ReadFileException
     */

    public byte[] getFileByteArrayData(String relativePath) throws FileNotFoundException, ReadFileException {
        if (checkIfEndsWithSlash(relativePath)) {
            relativePath += "index.html";
        }

        if (!checkIfProvidedRelativePathExistsInsideRoot(relativePath)) {
            throw new FileNotFoundException("File not found: " + relativePath);
        }

        File file = new File(root, relativePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];

        try {
            fileInputStream.read(fileBytes);
            fileInputStream.close();
        } catch (IOException e) {
            throw new ReadFileException(e);
        }

        return fileBytes;
    }
}