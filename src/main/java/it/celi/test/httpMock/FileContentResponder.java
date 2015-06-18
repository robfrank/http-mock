package it.celi.test.httpMock;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;

import com.google.common.io.Files;

public class FileContentResponder implements HttpResponder {

	private final File file;
	private final String mime;

	public FileContentResponder(final File file) {
		super();
		this.file = file;

		final MimeTypes mimeTypes = new MimeTypes();
		mime = mimeTypes.getMimeByExtension(file.getName());

	}

	@Override
	public void reply(final HttpServletResponse response) throws IOException, ServletException {
		response.setContentType(mime);
		final ServletOutputStream outputStream = response.getOutputStream();

		Files.copy(file, outputStream);
	}

}
