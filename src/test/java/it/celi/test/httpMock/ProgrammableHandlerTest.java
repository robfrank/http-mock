package it.celi.test.httpMock;

import com.google.common.io.Files;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jetty.http.HttpHeader.CONTENT_TYPE;
import static org.eclipse.jetty.http.HttpStatus.OK_200;

public class ProgrammableHandlerTest {

    private static Server httpServer;
    private static File html;
    private static HttpClient httpClient;
    private static File json;
    private static List<File> xmls;

    @BeforeClass
    public static void startServerAndClient() throws Exception {

        // file to be served as response
        html = new File("./src/test/resources/ProgrammableHandlerTest.html");
        json = new File("./src/test/resources/ProgrammableHandlerTest.json");

        xmls = asList(new File("./src/test/resources/data1.xml")
                , new File("./src/test/resources/data2.xml")
                , new File("./src/test/resources/data3.xml"));

        //configure handler
        final ProgrammableHandler handler = new ProgrammableHandler()
                .handle("/index.html", html)
                .handle("/data.json", json)
                .handle("/", Paths.get("./src/test/resources/"), "*.xml")
                .handle("/service?file=", Paths.get("./src/test/resources/"), "*.xml")
                //force to return xml files as json 
                .handle("/serviceForced?file=", Paths.get("./src/test/resources/"), "*.xml", new MimeTypes().getMimeByExtension("forceMimeTo.json"))
                .handle("/index.php", HttpServletResponse.SC_NOT_FOUND);

        // start the server
        httpServer = new Server(8888);
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

        assertThat(response.getStatus()).isEqualTo((OK_200));

        assertThat(response.getHeaders().get(CONTENT_TYPE)).isEqualTo(("text/html"));

        assertThat(response.getContentAsString()).isEqualTo((Files.toString(html, UTF_8)));

    }


    @Test
    public void shouldGetJsonDataFromHttp() throws Exception {

        final ContentResponse response = httpClient.GET("http://localhost:8888/data.json");

        assertThat(response.getStatus()).isEqualTo((OK_200));

        assertThat(response.getHeaders().get(CONTENT_TYPE)).isEqualTo(("application/json"));

        assertThat(response.getContentAsString()).isEqualTo((Files.toString(json, UTF_8)));

    }

    @Test
    public void shouldGetXmlsDataFromHttp() throws Exception {

        for (final File xml : xmls) {

            final ContentResponse response = httpClient.GET("http://localhost:8888/" + xml.getName());

            assertThat(response.getStatus()).isEqualTo((OK_200));

            assertThat(response.getHeaders().get(CONTENT_TYPE)).isEqualTo(("application/xml"));

            assertThat(response.getContentAsString()).isEqualTo((Files.toString(xml, UTF_8)));
        }
    }

    @Test
    public void shouldGetXmlsDataFromHttpGetParam() throws Exception {

        for (final File xml : xmls) {

            final ContentResponse response = httpClient.GET("http://localhost:8888/service?file=" + xml.getName());

            assertThat(response.getStatus()).isEqualTo((OK_200));

            assertThat(response.getHeaders().get(CONTENT_TYPE)).isEqualTo(("application/xml"));

            assertThat(response.getContentAsString()).isEqualTo((Files.toString(xml, UTF_8)));
        }

    }

    @Test
    public void shouldGetFocedMimeToJsonDataFromHttpGetParam() throws Exception {

        for (final File xml : xmls) {

            final ContentResponse response = httpClient.GET("http://localhost:8888/serviceForced?file=" + xml.getName());

            assertThat(response.getStatus()).isEqualTo((OK_200));

            //xml files are forced to be json
            assertThat(response.getHeaders().get(CONTENT_TYPE)).isEqualTo(("application/json"));

            assertThat(response.getContentAsString()).isEqualTo((Files.toString(xml, UTF_8)));
        }

    }


    @Test
    public void souldGet404From404MapperUrl() throws Exception {

        final ContentResponse response = httpClient.GET("http://localhost:8888/index.php");

        assertThat(response.getStatus()).isEqualTo((HttpStatus.NOT_FOUND_404));

    }

    @Test
    public void souldGet404FromWrongURL() throws Exception {

        final ContentResponse response = httpClient.GET("http://localhost:8888/wrong.html");

        assertThat(response.getStatus()).isEqualTo((HttpStatus.NOT_FOUND_404));

    }

}
