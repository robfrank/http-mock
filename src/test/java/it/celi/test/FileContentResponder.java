package it.csi.indexer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class FileContentResponder implements HttpResponder {

	private final File file;

	public FileContentResponder(File file) {
		super();
		this.file = file;
	}

	@Override
	public void reply(HttpServletResponse response) throws IOException, ServletException {
		ServletOutputStream outputStream = response.getOutputStream();

		IOUtils.copy(new FileReader(file), outputStream);
		outputStream.flush();
	}

}
