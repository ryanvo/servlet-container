package edu.upenn.cis.cis555.webserver.util;

import edu.upenn.cis.cis455.webserver.exception.file.IllegalFilePathException;
import edu.upenn.cis.cis455.webserver.util.FileUtil;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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


    @Test
    public void shouldDetermineMimeTypeFromPath() throws Exception {

        String path = "/path/to/util.jpg";
        assertThat(FileUtil.probeContentType(path), is("image/jpeg"));

        path = "/path/to/util.jpeg";
        assertThat(FileUtil.probeContentType(path), is("image/jpeg"));

        path = "/path/to/util.gif";
        assertThat(FileUtil.probeContentType(path), is("image/gif"));

        path = "/path/to/util.png";
        assertThat(FileUtil.probeContentType(path), is("image/png"));

        path = "/path/to/util.txt";
        assertThat(FileUtil.probeContentType(path), is("text/plain"));

        path = "/path/to/util.html";
        assertThat(FileUtil.probeContentType(path), is("text/html"));

        path = "/path/to/util.htm";
        assertThat(FileUtil.probeContentType(path), is("text/html"));
    }

    @Test
    public void shouldDetermineMimeTypeFromPathCaseInsensitive() throws Exception {

        String path = "/path/to/util.JPG";
        assertThat(FileUtil.probeContentType(path), is("image/jpeg"));

        path = "/path/to/util.JPEG";
        assertThat(FileUtil.probeContentType(path), is("image/jpeg"));

        path = "/path/to/util.GIF";
        assertThat(FileUtil.probeContentType(path), is("image/gif"));

        path = "/path/to/util.PNG";
        assertThat(FileUtil.probeContentType(path), is("image/png"));

        path = "/path/to/util.TXT";
        assertThat(FileUtil.probeContentType(path), is("text/plain"));

        path = "/path/to/util.HTML";
        assertThat(FileUtil.probeContentType(path), is("text/html"));

        path = "/path/to/util.HTM";
        assertThat(FileUtil.probeContentType(path), is("text/html"));

    }

    @Test
    public void shouldReturnOctetStreamMimeTypeIfNotRecognized() throws Exception {

        String path = "/path/to/util.bin";
        assertThat(FileUtil.probeContentType(path), is("application/octet-stream"));

        path = "/path/to/util.BIN";
        assertThat(FileUtil.probeContentType(path), is("application/octet-stream"));

    }



}
