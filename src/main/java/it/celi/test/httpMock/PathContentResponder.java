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
        this(path, new MimeTypes().getMimeByExtension(path.getFileName().toString()));
    }

    public PathContentResponder(final Path path, final String mime) {
        this.path = path;
        this.mime = mime;
    }


    @Override
    public void reply(final HttpServletResponse response) throws IOException, ServletException {
        response.setContentType(mime);
        final ServletOutputStream outputStream = response.getOutputStream();

        Files.copy(path, outputStream);
    }

    @Override
    public String toString() {
        return "path responder:: " + path.getFileName();
    }

}
