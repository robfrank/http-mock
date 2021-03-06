package it.celi.test.httpMock;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class ProgrammableHandler extends AbstractHandler {

    private final Function<String, HttpResponder> responders;

    private final Map<String, HttpResponder> urlToHttpResponder;

    private final Joiner urlQueryJoiner;

    public ProgrammableHandler() {
        urlToHttpResponder = new HashMap<>();
        urlQueryJoiner = Joiner.on('?').skipNulls();

        final HttpResponder defaultResponder = new StatusCodeResponder(SC_NOT_FOUND);

        responders = Functions.forMap(urlToHttpResponder, defaultResponder);
    }

    @Override
    public void handle(final String url, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        baseRequest.setHandled(true);


        final String lookup = urlQueryJoiner.join(url, baseRequest.getQueryString());

        final HttpResponder responder = responders.apply(lookup);

        responder.reply(response);

    }

    public ProgrammableHandler handle(final String url, final HttpResponder responder) {
        urlToHttpResponder.put(url, responder);
        return this;
    }

    public ProgrammableHandler handle(final String url, final File file) {
        urlToHttpResponder.put(url, new FileContentResponder(file));
        return this;
    }

    public ProgrammableHandler handle(final String url, final FileContentResponder fileResponder) {
        urlToHttpResponder.put(url, fileResponder);
        return this;
    }

    public ProgrammableHandler handle(final String url, final Path path) {
        urlToHttpResponder.put(url, new PathContentResponder(path));
        return this;
    }

    public ProgrammableHandler handle(final String url, final PathContentResponder pathResponder) {
        urlToHttpResponder.put(url, pathResponder);
        return this;
    }

    public ProgrammableHandler handle(final String url, final Path dir, final String glob) {

        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, glob);
            directoryStream.forEach(p -> handle(url + p.getFileName(), new PathContentResponder(p)));

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        urlToHttpResponder.entrySet().forEach(e -> System.out.println(e.getKey() + "::" + e.getValue()));
        return this;
    }

    public ProgrammableHandler handle(final String url, final Path dir, final String glob, final String mime) {

        try {
            final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, glob);
            directoryStream.forEach(p -> handle(url + p.getFileName(), new PathContentResponder(p, mime)));

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public ProgrammableHandler handle(final String url, final int status) {
        urlToHttpResponder.put(url, new StatusCodeResponder(status));
        return this;
    }

}
