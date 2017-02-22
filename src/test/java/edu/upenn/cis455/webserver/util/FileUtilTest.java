package edu.upenn.cis455.webserver.util;

import edu.upenn.cis455.webserver.servlet.exception.file.IllegalFilePathException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Ryan Vo
 */
public class FileUtilTest {

    @Test
    public void shouldNormalizePath() throws Exception {

        String path = "//././path/to/../../folder/from/";

        String actual = FileUtil.normalizePath(path);

        String expected = "/folder/from/";

        assertThat(actual, is(expected));

   }


    @Test (expected = IllegalFilePathException.class)
    public void shouldThrowIfPathIsIllegal() throws Exception {

        String path = "//././path/to/../.././../";

        FileUtil.normalizePath(path);

    }

    @Test
    public void shouldRelativizePath() throws Exception {

        String root = "/home/rtv/folder/";
        String pathToRelativize = "/home/rtv/folder/path/to";

        String relativePath = FileUtil.relativizePath(pathToRelativize, root);

        assertThat(relativePath, is("path/to"));
    }

    @Test
    public void shouldGetPathFromUrl() throws Exception {

        String url = "http://localhost:8080/path/to/foo";
        String expected = "/path/to/foo";
        String actual = FileUtil.getUrlPath(url);
        assertThat(actual, is(expected));

        url = "http://www.somehost.com/path/file.html";
        expected = "/path/file.html";
        actual = FileUtil.getUrlPath(url);
        assertThat(actual, is(expected));
    }

    @Test
    public void shouldGetPathFromUrlWithNoForwardSlash() throws Exception {

        String url = "http://localhost:8080";
        String expected = "/";
        String actual = FileUtil.getUrlPath(url);
        assertThat(actual, is(expected));

    }

    @Test
    public void shouldGetPathFromUrlWithNoProtocol() throws Exception {
        String url = "localhost:8080/path/to/foo";
        String expected = "/path/to/foo";
        String actual = FileUtil.getUrlPath(url);
        assertThat(actual, is(expected));
    }







}
