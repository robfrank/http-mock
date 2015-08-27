package it.celi.test.httpMock;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class StatusCodeResponder implements HttpResponder {

	private final int status;

	public StatusCodeResponder(final int status) {
		super();
		this.status = status;
	}

	@Override
	public void reply(final HttpServletResponse response) throws IOException, ServletException {

		response.setStatus(status);
	}

    @Override
    public String toString() {
        return "status responder:: " + status;
    }

	
}
