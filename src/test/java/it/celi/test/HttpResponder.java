package it.csi.indexer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author franchini@celi.it
 * 
 */
public interface HttpResponder {

	void reply(HttpServletResponse response) throws IOException, ServletException;
}
