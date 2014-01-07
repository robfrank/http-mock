package it.celi.test.httpMock;

import java.io.File;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ProgrammableHandlerTest {

	private static Server httpServer;
	private static File html;
	private static HttpClient httpClient;

	@BeforeClass
	public static void startServerAndClient() throws Exception {

		// file to be served as response
		html = new File("./src/test/resources/ProgrammableHandlerTest.html");

		ProgrammableHandler handler = new ProgrammableHandler()
				.handle("/index.html", html)
				.handle("/index.php", HttpServletResponse.SC_NOT_FOUND);

		// start the server
		httpServer = new Server(8888);
		httpServer.addConnector(new SelectChannelConnector());
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

		ContentExchange exchange = new ContentExchange(true);
		exchange.setMethod(HttpMethods.GET);
		exchange.setURL("http://localhost:8888/index.html");

		httpClient.send(exchange);

		assertThat(exchange.waitForDone(), equalTo(HttpExchange.STATUS_COMPLETED));

		assertThat(exchange.getResponseStatus(), equalTo(HttpStatus.OK_200));

		String contentFromHttp = exchange.getResponseContent();

		String contentFromFile = Files.toString(html, Charset.forName("UTF-8"));

		assertThat(contentFromHttp, equalTo(contentFromFile));

		assertThat(exchange.getResponseFields().getStringField(HttpHeaders.CONTENT_TYPE), equalTo("text/html"));

	}

	@Test
	public void souldGet404From404MapperUrl() throws Exception {

		ContentExchange content = new ContentExchange();
		content.setURL("http://localhost:8888/index.php");

		httpClient.send(content);

		assertThat(content.waitForDone(), equalTo(HttpExchange.STATUS_COMPLETED));

		assertThat(content.getResponseStatus(), equalTo(HttpStatus.NOT_FOUND_404));

	}

	@Test
	public void souldGet404FromWrongURL() throws Exception {

		ContentExchange content = new ContentExchange();
		content.setURL("http://localhost:8888/wrong.html");

		httpClient.send(content);

		assertThat(content.waitForDone(), equalTo(HttpExchange.STATUS_COMPLETED));

		assertThat(content.getResponseStatus(), equalTo(HttpStatus.NOT_FOUND_404));

	}

}
