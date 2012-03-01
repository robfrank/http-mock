package it.celi.test.httpMock;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProgrammableHandlerTest {

	private static Server httpServer;
	private static File html;
	private static HttpClient httpClient;

	@BeforeClass
	public static void startServerAndClient() throws Exception {

		html = new File("./src/test/resources/ProgrammableHandlerTest.html");
		ProgrammableHandler handler = new ProgrammableHandler()
			.map("/index.html", html)
			.map("/index.php", HttpServletResponse.SC_NOT_FOUND);

		httpServer = new Server(8888);
		httpServer.addConnector(new SelectChannelConnector());
		httpServer.setHandler(handler);
		httpServer.start();

		httpClient = new HttpClient();
		httpClient.start();

	}

	@AfterClass
	public static void shutdownServerAndClient() throws Exception {
		httpServer.stop();
		httpClient.stop();
	}

	@Test
	public void souldGetContentFromHttp() throws Exception {

		ContentExchange content = new ContentExchange();
		content.setURL("http://localhost:8888/index.html");

		httpClient.send(content);

		content.waitForDone();

		assertEquals(HttpStatus.OK_200, content.getResponseStatus());

		String contentFromHttp = content.getResponseContent();

		String contentFromFile = IOUtils.toString(new FileInputStream(html), "UTF-8");

		assertEquals(contentFromFile, contentFromHttp);

	}

	@Test
	public void souldGet404FromWrongURL() throws Exception {

		ContentExchange content = new ContentExchange();
		content.setURL("http://localhost:8888/index.php");

		httpClient.send(content);

		content.waitForDone();

		assertEquals(HttpStatus.NOT_FOUND_404, content.getResponseStatus());

	}

}
