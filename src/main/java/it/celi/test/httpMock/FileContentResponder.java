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

	public FileContentResponder(File file) {
		super();
		this.file = file;

		MimeTypes mimeTypes = new MimeTypes();
		mimeTypes.addMimeMapping("json", "application/json");
		mime = mimeTypes.getMimeByExtension(file.getName());

	}

	@Override
	public void reply(HttpServletResponse response) throws IOException, ServletException {
		response.setContentType(mime);
		ServletOutputStream outputStream = response.getOutputStream();

		Files.copy(file, outputStream);
	}

}
