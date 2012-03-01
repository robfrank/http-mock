package it.celi.test.httpMock;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.base.Joiner;

public class ProgrammableHandler extends AbstractHandler {

	private final Map<String, HttpResponder> urlToHttpResponder;

	private final HttpResponder defaultResponder;

	public ProgrammableHandler() {
		urlToHttpResponder = new HashMap<String, HttpResponder>();
		defaultResponder = new StatusCodeResponder(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public void handle(String url, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		baseRequest.setHandled(true);
		
		String lookup = Joiner.on('?').skipNulls().join(url, baseRequest.getQueryString());

		HttpResponder responder = lookupResponder(lookup);

		responder.reply(response);
		
	}

	private HttpResponder lookupResponder(String url) {
		HttpResponder responder = urlToHttpResponder.get(url);

		if (responder == null) return defaultResponder;

		return responder;

	}

	public ProgrammableHandler map(String url, HttpResponder responder) {
		urlToHttpResponder.put(url, responder);
		return this;
	}

	public ProgrammableHandler map(String url, File file) {
		urlToHttpResponder.put(url, new FileContentResponder(file));
		return this;
	}

	public ProgrammableHandler map(String url, int status) {
		urlToHttpResponder.put(url, new StatusCodeResponder(status));
		return this;
	}

}
