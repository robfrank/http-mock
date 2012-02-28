package it.csi.indexer;

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

import static it.celi.util.ObjectUtil.isNull;

public class ProgrammableHandler extends AbstractHandler {

	private final Map<String, HttpResponder> urlToFile;

	private final HttpResponder defaultResponder;

	public ProgrammableHandler() {
		urlToFile = new HashMap<String, HttpResponder>();
		defaultResponder = new StatusCodeResponder(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public void handle(String url, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		baseRequest.setHandled(true);
		String lookup = Joiner.on('?').skipNulls().join(url, baseRequest.getQueryString());

		HttpResponder responder = urlToFile.get(lookup);

		if (isNull(responder)) defaultResponder.reply(response);

		else responder.reply(response);

		baseRequest.setHandled(true);
		
	}

	public ProgrammableHandler map(String url, HttpResponder responder) {
		urlToFile.put(url, responder);
		return this;
	}

	public ProgrammableHandler map(String url, File file) {
		urlToFile.put(url, new FileContentResponder(file));
		return this;
	}

	public ProgrammableHandler map(String url, int status) {
		urlToFile.put(url, new StatusCodeResponder(status));
		return this;
	}

}
