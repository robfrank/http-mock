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
        this(file, new MimeTypes().getMimeByExtension(file.getName()));
    }

    public FileContentResponder(final File file, final String mime) {
        this.file = file;
        this.mime = mime;
    }

    @Override
    public void reply(final HttpServletResponse response) throws IOException, ServletException {
        response.setContentType(mime);
        final ServletOutputStream outputStream = response.getOutputStream();

        Files.copy(file, outputStream);
    }

    @Override
    public String toString() {
        return "file responder:: " + file.getName();
    }

}
