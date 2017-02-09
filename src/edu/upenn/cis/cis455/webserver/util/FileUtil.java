package edu.upenn.cis.cis455.webserver.util;

import edu.upenn.cis.cis455.webserver.exception.file.IllegalFilePathException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author rtv
 */
public class FileUtil {


    public static String normalizePath(String path) throws IllegalFilePathException
    {
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

    public static String probeContentType(String filePath) {

        filePath = filePath.toLowerCase();

        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {

            return "image/jpeg";

        } else if (filePath.endsWith(".gif")) {

            return "image/gif";

        } else if (filePath.endsWith(".png")) {

            return "image/png";

        } else if (filePath.endsWith(".txt")) {

            return "text/plain";

        } else if (filePath.endsWith(".html") || filePath.endsWith(".htm")) {

            return "text/html";

        } else {

            return "application/octet-stream";
        }

    }


}
