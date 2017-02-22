package edu.upenn.cis455.webserver.util;

import edu.upenn.cis455.webserver.servlet.exception.file.IllegalFilePathException;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author rtv
 */
public class FileUtil {

    public static String relativizePath(String absPath, String absRoot) {
        String relativePath = absPath.replaceFirst(absRoot, "");

        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        return relativePath;
    }

    public static String getUrlPath(String url) {

        final String protocol = "http://";

        int indexOfHost;
        if (url.startsWith(protocol)) {
            indexOfHost = protocol.length();
        } else {
            indexOfHost = 0;
        }

        String urlWithoutProtocol = url.substring(indexOfHost);
        int indexOfUrlPath = urlWithoutProtocol.indexOf('/');
        if (indexOfUrlPath == -1) {
            return "/";
        }

        return urlWithoutProtocol.substring(indexOfUrlPath);
    }

    public static String normalizePath(String path) throws IllegalFilePathException {
        if (path.isEmpty()) {
            throw new IllegalFilePathException();
        }

        List<String> pathStack = new ArrayList<>();

        /* Handle absolute path */
        if (path.startsWith("/")) {
            pathStack.add("/");
        }

        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        String token;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token.equals("..")) {
                if (pathStack.isEmpty() || pathStack.get(pathStack.size() - 1).equals("..")) {
                    pathStack.add(token);
                } else {

                    if (pathStack.get(pathStack.size() - 1).equals("/")) {
                        throw new IllegalFilePathException();
                    }
                    pathStack.remove(pathStack.size() - 1);
                }
            } else if (!token.equals(".") && !token.isEmpty()) {
                pathStack.add(token);
            }
        }

        if (pathStack.isEmpty()) {
            throw new IllegalFilePathException();
        }

        String normalizedPath;
        normalizedPath = pathStack.get(0);
        for (int i = 1; i < pathStack.size(); i++) {
            if (i == 1 && normalizedPath.equals("/")) {
                normalizedPath += pathStack.get(i);
            } else {
                normalizedPath += "/" + pathStack.get(i);
            }
        }

        /* Add trailing '/' if original path was to a directory */
        if (normalizedPath.length() > 1 && path.endsWith("/")) {
            normalizedPath += "/";
        }

        return normalizedPath;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[0x1000];
        int bytesRead;
        while ((bytesRead = in.read(buf, 0, buf.length)) > 0) {
            out.write(buf, 0, bytesRead);
        }
    }

    public static void copy(File file, OutputStream out) throws IOException {
        int len = Long.valueOf(file.length()).intValue();
        InputStream fileInputStream = new FileInputStream(file);
        byte[] buf = new byte[len];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buf, 0, buf.length)) > 0) {
            out.write(buf, 0, bytesRead);
        }
    }

    public static ZonedDateTime getLastModifiedGmt(File file) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("GMT"));
    }

}
