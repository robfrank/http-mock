package it.celi.test.httpMock;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.Files;


public class FileContentResponder implements HttpResponder {

	private final File file;

	public FileContentResponder(File file) {
		super();
		this.file = file;
	}

	@Override
	public void reply(HttpServletResponse response) throws IOException, ServletException {
		ServletOutputStream outputStream = response.getOutputStream();
		Files.copy(file, outputStream);
	}

}
