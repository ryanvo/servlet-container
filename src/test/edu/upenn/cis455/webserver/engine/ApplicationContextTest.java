package edu.upenn.cis455.webserver.engine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author rtv
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ApplicationContextTest {

    @Mock private ServletContextBuilder mockBuilder;
    @Mock private Map<String, String> contextParams;

    @Test
    public void shouldReturnMimeType() throws Exception {

        when(mockBuilder.getContextParams()).thenReturn(contextParams);
        when(mockBuilder.getRealPath()).thenReturn("mockPath");

        ApplicationContext context = new ApplicationContext(mockBuilder);

        String path = "/path/to/util.jpg";
        assertThat(context.getMimeType(path), is("image/jpeg"));

        path = "/path/to/util.jpeg";
        assertThat(context.getMimeType(path), is("image/jpeg"));

        path = "/path/to/util.gif";
        assertThat(context.getMimeType(path), is("image/gif"));

        path = "/path/to/util.png";
        assertThat(context.getMimeType(path), is("image/png"));

        path = "/path/to/util.txt";
        assertThat(context.getMimeType(path), is("text/plain"));

        path = "/path/to/util.html";
        assertThat(context.getMimeType(path), is("text/html"));

        path = "/path/to/util.htm";
        assertThat(context.getMimeType(path), is("text/html"));
    }

    @Test
    public void shouldDetermineMimeTypeFromPathCaseInsensitive() throws Exception {
        when(mockBuilder.getContextParams()).thenReturn(contextParams);
        when(mockBuilder.getRealPath()).thenReturn("mockPath");

        ApplicationContext context = new ApplicationContext(mockBuilder);

        String path = "/path/to/util.JPG";
        assertThat(context.getMimeType(path), is("image/jpeg"));

        path = "/path/to/util.JPEG";
        assertThat(context.getMimeType(path), is("image/jpeg"));

        path = "/path/to/util.GIF";
        assertThat(context.getMimeType(path), is("image/gif"));

        path = "/path/to/util.PNG";
        assertThat(context.getMimeType(path), is("image/png"));

        path = "/path/to/util.TXT";
        assertThat(context.getMimeType(path), is("text/plain"));

        path = "/path/to/util.HTML";
        assertThat(context.getMimeType(path), is("text/html"));

        path = "/path/to/util.HTM";
        assertThat(context.getMimeType(path), is("text/html"));

    }

    @Test
    public void shouldReturnOctetStreamMimeTypeIfNotRecognized() throws Exception {

        when(mockBuilder.getContextParams()).thenReturn(contextParams);
        when(mockBuilder.getRealPath()).thenReturn("mockPath");

        ApplicationContext context = new ApplicationContext(mockBuilder);

        String path = "/path/to/util.bin";
        assertThat(context.getMimeType(path), is("application/octet-stream"));

        path = "/path/to/util.BIN";
        assertThat(context.getMimeType(path), is("application/octet-stream"));

    }

}
