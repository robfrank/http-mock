package it.csi.indexer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class StatusCodeResponder implements HttpResponder {

	private final int status;

	public StatusCodeResponder(int status) {
		super();
		this.status = status;
	}

	@Override
	public void reply(HttpServletResponse response) throws IOException, ServletException {

		response.setStatus(status);
	}

}
