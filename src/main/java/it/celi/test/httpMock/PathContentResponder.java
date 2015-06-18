package it.celi.test.httpMock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;

public class PathContentResponder implements HttpResponder {

    private final Path path;
    private final String mime;

    public PathContentResponder(final Path path) {
        super();
        this.path = path;

        final MimeTypes mimeTypes = new MimeTypes();
        mime = mimeTypes.getMimeByExtension(path.getFileName().toString());

    }

    @Override
    public void reply(final HttpServletResponse response) throws IOException, ServletException {
        response.setContentType(mime);
        final ServletOutputStream outputStream = response.getOutputStream();

        Files.copy(path, outputStream);
    }

}
