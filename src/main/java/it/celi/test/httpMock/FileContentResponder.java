package it.celi.test.httpMock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Buffer;

import com.google.common.io.Files;


public class FileContentResponder implements HttpResponder {

	private final File file;
	private Buffer mimeByExtension;

	public FileContentResponder(File file) {
		super();
		this.file = file;
		
		MimeTypes mime = new MimeTypes();
		
		String name = file.getName();
		System.out.println( name);
		mimeByExtension = mime.getMimeByExtension(name);
		System.out.println(mimeByExtension);
		System.out.println( mimeByExtension.toString(Charset.defaultCharset()));
	}

	@Override
	public void reply(HttpServletResponse response) throws IOException, ServletException {

		response.setContentType(mimeByExtension.toString(Charset.defaultCharset()));
		ServletOutputStream outputStream = response.getOutputStream();
		Files.copy(file, outputStream);
	}

}
