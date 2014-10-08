package it.celi.test.httpMock;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

public class ProgrammableHandlerTest {

    private static Server httpServer;
    private static File html;
    private static HttpClient httpClient;
    private static File json;

    @BeforeClass
    public static void startServerAndClient() throws Exception {

        // file to be served as response
        html = new File("./src/test/resources/ProgrammableHandlerTest.html");
        json = new File("./src/test/resources/ProgrammableHandlerTest.json");

        final ProgrammableHandler handler = new ProgrammableHandler()
                .handle("/index.html", html)
                .handle("/data.json", json)
                .handle("/index.php", HttpServletResponse.SC_NOT_FOUND);

        // start the server
        httpServer = new Server(8888);
        // httpServer.addConnector(new SelectChannelConnector());
        httpServer.setHandler(handler);
        httpServer.start();
        // start the client
        httpClient = new HttpClient();
        httpClient.start();

    }

    @AfterClass
    public static void shutdownServerAndClient() throws Exception {
        httpClient.stop();
        httpServer.stop();
    }

    @Test
    public void shouldGetContentFromHttp() throws Exception {

        final ContentResponse response = httpClient.GET("http://localhost:8888/index.html");

        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200));

        assertThat(response.getHeaders().get(HttpHeader.CONTENT_TYPE), equalTo("text/html"));
        final String contentFromHttp = response.getContentAsString();

        final String contentFromFile = Files.toString(html, Charset.forName("UTF-8"));

        assertThat(contentFromHttp, equalTo(contentFromFile));

    }


    @Test
    public void shouldGetJsonDataFromHttp() throws Exception {

        final ContentResponse response = httpClient.GET("http://localhost:8888/data.json");

        assertThat(response.getStatus(), equalTo(HttpStatus.OK_200));

        assertThat(response.getHeaders().get(HttpHeader.CONTENT_TYPE), equalTo("application/json"));
        final String contentFromHttp = response.getContentAsString();

        final String contentFromFile = Files.toString(json, Charset.forName("UTF-8"));

        assertThat(contentFromHttp, equalTo(contentFromFile));

    }

    @Test
    public void souldGet404From404MapperUrl() throws Exception {

        final ContentResponse response = httpClient.GET("http://localhost:8888/index.php");

        assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND_404));

    }

    @Test
    public void souldGet404FromWrongURL() throws Exception {

        final ContentResponse response = httpClient.GET("http://localhost:8888/wrong.html");

        assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND_404));

    }

}
