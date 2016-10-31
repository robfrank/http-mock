# HTTP mock

Scope of this tool is to help in writing tests against third-party REST api allowing easy mocking.
http-mock is a programmable http server written in Java. It uses the [Jetty](http://www.eclipse.org/jetty/) servlet container. 

# Configure server

```java
// file to be serverd
File html = new File("./src/test/resources/ProgrammableHandlerTest.html");

ProgrammableHandler handler = new ProgrammableHandler()
        .handle("/index.html", html)
        .handle("/xmls/", Paths.get("/path/to/dir/of/resources/"), "*.xml")
        .handle("/index.php", HttpServletResponse.SC_NOT_FOUND);

// start the server
Server httpServer = new Server(8888);
httpServer.setHandler(handler);
httpServer.start();
```
 
So the server will return the content of file when called on `localhost:8888/index.html`.
Calling it on `localhost:8888/index.php` will return a 404 error code. 
`Not found (404)` error code is the default response code for non-mapped urls.

# Query the server

```java
HttpClient httpClient = new HttpClient();

final ContentResponse response = httpClient.GET("http://localhost:8888/index.html");

assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

assertThat(response.getHeaders().get(HttpHeader.CONTENT_TYPE))isEqualTo("text/html");

```

